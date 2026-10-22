package org.example.web.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Alojamiento;
import org.example.AlojamientoDAO;
import org.example.ClienteDAO;
import org.example.EstadoReserva;
import org.example.PlanillaDiaDAO;
import org.example.ReservaDao;
import org.example.ReservaDetalle;
import org.example.ReservaPlanilla;
import org.example.web.LoginServlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Caso de uso: consultar la disponibilidad.
 * - Sin alojamiento elegido: planilla de TODAS las casas del mes (como el Excel), donde además
 *   se puede crear, confirmar o cancelar una reserva.
 * - Con alojamiento elegido: calendario mensual de esa casa y sus próximas reservas.
 * En "Todas las casas" también se anota el precio por noche y un color para cada casa y día
 * (POST accion=dias), como hacía la administradora en la planilla de Excel.
 * Las dos vistas usan la misma consulta (ReservaDao.listarParaPlanilla) y la misma regla:
 * un día está ocupado desde el ingreso hasta el día anterior a la salida; con medio día extra,
 * el día de salida también queda ocupado.
 */
@WebServlet(AdminSesion.BASE + "/disponibilidad")
public class AdminDisponibilidadServlet extends AdminServlet {

    private static final Locale ES = Locale.forLanguageTag("es-UY");

    private final AlojamientoDAO alojamientoDAO = new AlojamientoDAO();
    private final ReservaDao reservaDao = new ReservaDao();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final PlanillaDiaDAO planillaDiaDAO = new PlanillaDiaDAO();

    /** Máximo de días que se pueden cambiar de una vez (una temporada entera entra de sobra). */
    private static final int MAX_DIAS = 400;

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
                // ---- Vista de una casa: calendario ----
                LocalDate desde = lunesAnterior(mes.atDay(1));
                LocalDate hasta = mes.atEndOfMonth().plusDays(7);
                List<ReservaPlanilla> delMes = new ArrayList<>();
                for (ReservaPlanilla r : reservaDao.listarParaPlanilla(desde, hasta)) {
                    if (r.getAlojamientoId() == alojamientoId) {
                        delMes.add(r);
                    }
                }

                List<ReservaDetalle> reservas = reservaDao.listarDetalle(null, alojamientoId, null, null);
                List<ReservaDetalle> vigentes = new ArrayList<>();
                for (ReservaDetalle r : reservas) {
                    if (r.getEstado() != EstadoReserva.CANCELADA) {
                        vigentes.add(r);
                    }
                }

                request.setAttribute("alojamiento", elegido);
                request.setAttribute("semanas", calendario(mes, delMes));
                request.setAttribute("proximas", proximas(vigentes));
            } else {
                // ---- Vista de todas las casas: planilla ----
                alojamientoId = null;
                List<ReservaPlanilla> reservas =
                        reservaDao.listarParaPlanilla(mes.atDay(1), mes.atEndOfMonth().plusDays(1));
                PlanillaOcupacion.cargar(request, mes, alojamientos, reservas,
                        planillaDiaDAO.listar(mes.atDay(1), mes.atEndOfMonth().plusDays(1)));

                // Para el formulario "Nueva reserva" (el mismo de la sección Reservas)
                request.setAttribute("clientes", clienteDAO.listarClientes());
                request.setAttribute("procedencias", LoginServlet.PROCEDENCIAS);
                request.setAttribute("hoy", LocalDate.now().toString());
            }
        } catch (SQLException e) {
            log("Error al cargar la disponibilidad", e);
            avisoError(request, "No pudimos cargar la disponibilidad. Intentá de nuevo en unos minutos.");
        }

        request.setAttribute("alojamientoSel", alojamientoId);
        request.setAttribute("mesValor", mes.toString());
        request.setAttribute("mesTexto", mes.getMonth().getDisplayName(TextStyle.FULL, ES) + " " + mes.getYear());
        request.setAttribute("mesAnterior", mes.minusMonths(1).toString());
        request.setAttribute("mesSiguiente", mes.plusMonths(1).toString());
        mostrar(request, response, "disponibilidad", "disponibilidad", "Disponibilidad");
    }

    /* ===================== Precios y colores (POST) ===================== */

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        YearMonth mes = mes(texto(request, "mes"));

        if ("dias".equals(texto(request, "accion"))) {
            try {
                guardarDias(request);
            } catch (SQLException | RuntimeException e) {
                log("Error al guardar precios y colores", e);
                avisoError(request, "No pudimos guardar los cambios. Intentá de nuevo en unos minutos.");
            }
        } else {
            avisoError(request, "Acción no reconocida.");
        }

        redirigir(request, response, "/disponibilidad?mes=" + mes);
    }

    private void guardarDias(HttpServletRequest request) throws SQLException {
        // Casas elegidas
        Set<Integer> casas = new LinkedHashSet<>();
        String[] elegidas = request.getParameterValues("casa");
        if (elegidas != null) {
            for (String valor : elegidas) {
                try {
                    casas.add(Integer.valueOf(valor.trim()));
                } catch (NumberFormatException e) {
                    // se ignora un valor inválido
                }
            }
        }

        LocalDate desde = fecha(texto(request, "desde"));
        LocalDate hasta = fecha(texto(request, "hasta"));

        // Precio: "igual" (no tocar), "nuevo" (el del campo) o "quitar"
        String opcionPrecio = texto(request, "precioOpcion");
        boolean cambiarPrecio = opcionPrecio.equals("nuevo") || opcionPrecio.equals("quitar");
        BigDecimal precio = null;
        if (opcionPrecio.equals("nuevo")) {
            precio = numero(texto(request, "precio"));
        }

        // Color: "igual" (no tocar), "ninguno" (vuelve al color de la casa) o "#RRGGBB"
        String opcionColor = texto(request, "color");
        if (opcionColor.equals("otro")) {
            opcionColor = texto(request, "colorOtro");
        }
        boolean cambiarColor = !opcionColor.equals("igual");
        String color = opcionColor.equals("ninguno") ? null : opcionColor.toUpperCase(Locale.ROOT);

        String error = null;
        if (casas.isEmpty()) {
            error = "Elegí al menos una casa.";
        } else if (desde == null || hasta == null) {
            error = "Ingresá las fechas desde y hasta.";
        } else if (hasta.isBefore(desde)) {
            error = "La fecha \"hasta\" no puede ser anterior a la fecha \"desde\".";
        } else if (desde.plusDays(MAX_DIAS).isBefore(hasta)) {
            error = "Podés cambiar hasta " + MAX_DIAS + " días de una vez.";
        } else if (!cambiarPrecio && !cambiarColor) {
            error = "No elegiste ningún cambio: poné un precio o elegí un color.";
        } else if (opcionPrecio.equals("nuevo")
                && (precio == null || precio.signum() < 0 || precio.compareTo(new BigDecimal("10000000")) >= 0)) {
            error = "Ingresá un precio válido (solo números, por ejemplo 3500).";
        } else if (cambiarColor && color != null && !color.matches("#[0-9A-F]{6}")) {
            error = "El color elegido no es válido.";
        }

        if (error != null) {
            avisoError(request, error);
            return;
        }

        int casilleros = planillaDiaDAO.guardar(new ArrayList<>(casas), desde, hasta,
                cambiarPrecio, precio, cambiarColor, color);

        long dias = java.time.temporal.ChronoUnit.DAYS.between(desde, hasta) + 1;
        avisoOk(request, "Listo: se actualizaron " + casilleros + " casilleros ("
                + dias + (dias == 1 ? " día" : " días") + " en "
                + casas.size() + (casas.size() == 1 ? " casa" : " casas") + ").");
    }

    private static LocalDate fecha(String texto) {
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** Acepta "3500", "3500.50" o "3500,50". */
    private static BigDecimal numero(String texto) {
        try {
            return new BigDecimal(texto.replace(" ", "").replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /* ===================== Calendario de una casa ===================== */

    /** Semanas (lunes a domingo) del mes, con el estado de cada día. */
    private static List<List<Dia>> calendario(YearMonth mes, List<ReservaPlanilla> reservas) {
        LocalDate hoy = LocalDate.now();
        LocalDate dia = lunesAnterior(mes.atDay(1));

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

    private static String estadoDelDia(LocalDate dia, List<ReservaPlanilla> reservas) {
        String estado = "libre";
        for (ReservaPlanilla r : reservas) {
            if (!r.ocupa(dia)) {
                continue;
            }
            if (r.isConfirmada()) {
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

    private static LocalDate lunesAnterior(LocalDate dia) {
        return dia.minusDays(dia.getDayOfWeek().getValue() - 1L);
    }

    private static YearMonth mes(String texto) {
        try {
            return YearMonth.parse(texto);
        } catch (RuntimeException e) {
            return YearMonth.now();
        }
    }
}