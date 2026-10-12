package org.example.web.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Alojamiento;
import org.example.AlojamientoDAO;
import org.example.EstadoReserva;
import org.example.ReservaDao;
import org.example.ReservaDetalle;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Caso de uso: consultar la disponibilidad de un alojamiento (calendario mensual, solo lectura).
 * Una noche cuenta como ocupada desde el día de ingreso hasta el día anterior a la salida:
 * el día de salida queda libre para un nuevo ingreso.
 */
@WebServlet(AdminSesion.BASE + "/disponibilidad")
public class AdminDisponibilidadServlet extends AdminServlet {

    private static final Locale ES = Locale.forLanguageTag("es-UY");

    private final AlojamientoDAO alojamientoDAO = new AlojamientoDAO();
    private final ReservaDao reservaDao = new ReservaDao();

    /** Una celda del calendario. estado: "libre", "ocupado" o "pendiente". */
    public static class Dia {
        private final int numero;
        private final boolean delMes;
        private final boolean hoy;
        private final String estado;

        Dia(int numero, boolean delMes, boolean hoy, String estado) {
            this.numero = numero;
            this.delMes = delMes;
            this.hoy = hoy;
            this.estado = estado;
        }

        public int getNumero() { return numero; }
        public boolean isDelMes() { return delMes; }
        public boolean isHoy() { return hoy; }
        public String getEstado() { return estado; }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer alojamientoId = entero(request, "alojamiento");
        YearMonth mes = mes(texto(request, "mes"));

        try {
            List<Alojamiento> alojamientos = alojamientoDAO.listarAlojamientos();
            request.setAttribute("alojamientos", alojamientos);

            Alojamiento elegido = alojamientoId == null ? null : alojamientoDAO.buscarPorIdALO(alojamientoId);
            if (elegido != null) {
                List<ReservaDetalle> reservas = reservaDao.listarDetalle(null, alojamientoId, null, null);
                List<ReservaDetalle> vigentes = new ArrayList<>();
                for (ReservaDetalle r : reservas) {
                    if (r.getEstado() != EstadoReserva.CANCELADA) {
                        vigentes.add(r);
                    }
                }

                request.setAttribute("alojamiento", elegido);
                request.setAttribute("semanas", calendario(mes, vigentes));
                request.setAttribute("proximas", proximas(vigentes));
            }
        } catch (SQLException e) {
            log("Error al cargar la disponibilidad", e);
            avisoError(request, "No pudimos cargar la disponibilidad. Intentá de nuevo en unos minutos.");
        }

        request.setAttribute("alojamientoSel", alojamientoId);
        request.setAttribute("mesTexto", mes.getMonth().getDisplayName(TextStyle.FULL, ES) + " " + mes.getYear());
        request.setAttribute("mesAnterior", mes.minusMonths(1).toString());
        request.setAttribute("mesSiguiente", mes.plusMonths(1).toString());
        mostrar(request, response, "disponibilidad", "disponibilidad", "Disponibilidad");
    }

    /** Semanas (lunes a domingo) del mes, con el estado de cada día. */
    private static List<List<Dia>> calendario(YearMonth mes, List<ReservaDetalle> reservas) {
        LocalDate hoy = LocalDate.now();
        LocalDate dia = mes.atDay(1);
        dia = dia.minusDays(dia.getDayOfWeek().getValue() - 1L); // retrocede hasta el lunes

        List<List<Dia>> semanas = new ArrayList<>();
        while (!dia.isAfter(mes.atEndOfMonth())) {
            List<Dia> semana = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                semana.add(new Dia(dia.getDayOfMonth(), YearMonth.from(dia).equals(mes),
                        dia.equals(hoy), estadoDelDia(dia, reservas)));
                dia = dia.plusDays(1);
            }
            semanas.add(semana);
        }
        return semanas;
    }

    private static String estadoDelDia(LocalDate dia, List<ReservaDetalle> reservas) {
        String estado = "libre";
        for (ReservaDetalle r : reservas) {
            boolean dentro = !dia.isBefore(r.getFechaIngreso().toLocalDate()) && dia.isBefore(r.getFechaSalida().toLocalDate());
            if (!dentro) {
                continue;
            }
            if (r.getEstado() == EstadoReserva.CONFIRMADA) {
                return "ocupado";
            }
            estado = "pendiente";
        }
        return estado;
    }

    /** Reservas vigentes que todavía no terminaron, ordenadas por fecha de ingreso. */
    private static List<ReservaDetalle> proximas(List<ReservaDetalle> reservas) {
        LocalDate hoy = LocalDate.now();
        List<ReservaDetalle> lista = new ArrayList<>();
        for (ReservaDetalle r : reservas) {
            if (!r.getFechaSalida().toLocalDate().isBefore(hoy)) {
                lista.add(r);
            }
        }
        lista.sort((a, b) -> a.getFechaIngreso().compareTo(b.getFechaIngreso()));
        return lista;
    }

    private static YearMonth mes(String texto) {
        try {
            return YearMonth.parse(texto);
        } catch (RuntimeException e) {
            return YearMonth.now();
        }
    }
}
