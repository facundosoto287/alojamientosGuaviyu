package org.example.web.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.AlojamientoDAO;
import org.example.ClienteDAO;
import org.example.EstadoReserva;
import org.example.PlanillaDia;
import org.example.ReservaDao;
import org.example.ReservaDetalle;
import org.example.ReservaPlanilla;
import org.example.Sena;
import org.example.SenaDAO;
import org.example.ServicioDAO;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Inicio del panel: resumen del día, indicadores del mes, gráficas de ocupación, reservas y procedencia,
 * gráfica de localidades de los clientes y últimas solicitudes. Todo se calcula con las reservas, las señas y
 * los alojamientos que ya existen: no hace falta guardar nada más en la base.
 */
@WebServlet(urlPatterns = {AdminSesion.BASE, AdminSesion.BASE + "/", AdminSesion.BASE + "/inicio"})
public class AdminInicioServlet extends AdminServlet {

    private final AlojamientoDAO alojamientoDAO = new AlojamientoDAO();
    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ReservaDao ReservaDAO = new ReservaDao();
    private final SenaDAO senaDAO = new SenaDAO();
    private static final Locale ES = Locale.forLanguageTag("es-UY");
    private static final String[] MESES_CORTOS = {"Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Set", "Oct", "Nov", "Dic"};
    private static final String[] LETRAS = {"L", "M", "M", "J", "V", "S", "D"}; // lunes a domingo

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<ReservaDetalle> todas = ReservaDAO.listarDetalle(null, null, null, null); // de la más nueva a la más vieja
            List<ReservaDetalle> pendientes = new ArrayList<>();
            for (ReservaDetalle r : todas) {
                if (r.getEstado() == EstadoReserva.PENDIENTE) {
                    pendientes.add(r);
                }
            }
            int casas = alojamientoDAO.listarAlojamientos().size();

            request.setAttribute("totalAlojamientos", casas);
            request.setAttribute("totalServicios", servicioDAO.listarServicios().size());
            request.setAttribute("totalClientes", clienteDAO.listarClientes().size());
            request.setAttribute("totalPendientes", pendientes.size());
            request.setAttribute("ultimasPendientes", pendientes.subList(0, Math.min(5, pendientes.size())));

            cargarResumen(request, todas, pendientes.size(), casas);
            cargarGraficaLocalidades(request);
        } catch (SQLException e) {
            log("Error al cargar el inicio del panel", e);
            avisoError(request, "No pudimos cargar todos los datos. Intentá de nuevo en unos minutos.");
        }

        mostrar(request, response, "inicio", "inicio", "Inicio");
    }

    /* ===================== Resumen e indicadores ===================== */

    /**
     * Prepara todo lo que muestra el Inicio salvo la gráfica de localidades. Una "noche ocupada" es una noche de
     * una reserva CONFIRMADA; el porcentaje de ocupación es noches ocupadas sobre noches posibles (casas × días).
     */
    private void cargarResumen(HttpServletRequest request, List<ReservaDetalle> todas, int cantidadPendientes, int casas)
            throws SQLException {

        LocalDate hoy = LocalDate.now();
        int anio = hoy.getYear();
        YearMonth mesActual = YearMonth.from(hoy);
        LocalDate hasta = LocalDate.of(anio + 1, 1, 1).isAfter(hoy.plusDays(16)) ? LocalDate.of(anio + 1, 1, 1) : hoy.plusDays(16);

        // Reservas pendientes y confirmadas que tocan desde el 1 de enero hasta dentro de dos semanas
        List<ReservaPlanilla> activas = ReservaDAO.listarParaPlanilla(LocalDate.of(anio, 1, 1), hasta);
        List<ReservaPlanilla> confirmadas = new ArrayList<>();
        for (ReservaPlanilla r : activas) {
            if (r.isConfirmada()) {
                confirmadas.add(r);
            }
        }

        // ---- Fecha del encabezado
        String fecha = hoy.getDayOfWeek().getDisplayName(TextStyle.FULL, ES) + " " + hoy.getDayOfMonth() + " de "
                + hoy.getMonth().getDisplayName(TextStyle.FULL, ES);
        request.setAttribute("fechaTexto", Character.toUpperCase(fecha.charAt(0)) + fecha.substring(1));

        // ---- Hoy: casas ocupadas, llegadas y salidas
        Set<Integer> casasOcupadas = new HashSet<>();
        int llegadas = 0;
        int salidas = 0;
        for (ReservaPlanilla r : confirmadas) {
            if (r.ocupa(hoy)) {
                casasOcupadas.add(r.getAlojamientoId());
            }
            if (r.getIngreso().equals(hoy)) {
                llegadas++;
            }
            if (r.getSalida().equals(hoy)) {
                salidas++;
            }
        }
        int ocupadasHoy = Math.min(casasOcupadas.size(), casas);
        request.setAttribute("hoyOcupadas", ocupadasHoy);
        request.setAttribute("hoyLibres", Math.max(0, casas - ocupadasHoy));
        request.setAttribute("hoyPorcentaje", casas == 0 ? 0 : ocupadasHoy * 100 / casas);
        request.setAttribute("hoyLlegadas", llegadas);
        request.setAttribute("hoySalidas", salidas);

        // ---- Indicadores del mes
        long nochesMes = 0;
        double ingresos = 0;
        long nochesVendidas = 0;
        for (ReservaPlanilla r : confirmadas) {
            nochesMes += nochesEntre(r, mesActual.atDay(1), mesActual.plusMonths(1).atDay(1));
            if (YearMonth.from(r.getIngreso()).equals(mesActual)) {
                ingresos += r.getPrecioTotal();
                nochesVendidas += r.getNoches();
            }
        }
        request.setAttribute("kpiMesNombre", mesActual.getMonth().getDisplayName(TextStyle.FULL, ES));
        request.setAttribute("kpiOcupacion", porcentaje(nochesMes, (long) casas * mesActual.lengthOfMonth()));
        request.setAttribute("kpiIngresos", PlanillaDia.precioTexto(BigDecimal.valueOf(ingresos)));
        request.setAttribute("kpiNochesVendidas", nochesVendidas);

        int senasVigentes = 0;
        BigDecimal totalSenas = BigDecimal.ZERO;
        for (Sena sena : senaDAO.listar(null)) {
            if (sena.isVigente()) {
                senasVigentes++;
                totalSenas = totalSenas.add(sena.getImporte());
            }
        }
        request.setAttribute("kpiSenas", senasVigentes);
        request.setAttribute("kpiSenasTotal", PlanillaDia.precioTexto(totalSenas));

        // ---- Noches ocupadas por mes (año en curso)
        long[] nochesPorMes = new long[12];
        long maximoMes = 1;
        for (int m = 1; m <= 12; m++) {
            YearMonth ym = YearMonth.of(anio, m);
            for (ReservaPlanilla r : confirmadas) {
                nochesPorMes[m - 1] += nochesEntre(r, ym.atDay(1), ym.plusMonths(1).atDay(1));
            }
            maximoMes = Math.max(maximoMes, nochesPorMes[m - 1]);
        }
        List<Map<String, Object>> meses = new ArrayList<>();
        long totalNochesAnio = 0;
        for (int m = 1; m <= 12; m++) {
            long noches = nochesPorMes[m - 1];
            totalNochesAnio += noches;
            Map<String, Object> mes = new LinkedHashMap<>();
            mes.put("nombre", MESES_CORTOS[m - 1]);
            mes.put("noches", noches);
            mes.put("porcentaje", porcentaje(noches, (long) casas * YearMonth.of(anio, m).lengthOfMonth()));
            mes.put("alto", noches * 100 / maximoMes);
            mes.put("tiempo", m == mesActual.getMonthValue() ? "actual" : (m < mesActual.getMonthValue() ? "pasado" : "futuro"));
            meses.add(mes);
        }
        request.setAttribute("anioResumen", anio);
        request.setAttribute("mesesBarras", meses);
        request.setAttribute("nochesAnio", totalNochesAnio);

        // ---- Estado de las reservas (dona)
        int[] porEstado = new int[3];
        for (ReservaDetalle r : todas) {
            porEstado[r.getEstado() == EstadoReserva.CONFIRMADA ? 0 : r.getEstado() == EstadoReserva.PENDIENTE ? 1 : 2]++;
        }
        int totalReservas = todas.size();
        int pConf = (int) porcentaje(porEstado[0], totalReservas);
        int pPend = (int) porcentaje(porEstado[1], totalReservas);
        int pCanc = (int) porcentaje(porEstado[2], totalReservas);
        request.setAttribute("estadoTotal", totalReservas);
        request.setAttribute("estadoConfirmadas", porEstado[0]);
        request.setAttribute("estadoPendientes", porEstado[1]);
        request.setAttribute("estadoCanceladas", porEstado[2]);
        request.setAttribute("estadoPConfirmadas", pConf);
        request.setAttribute("estadoPPendientes", pPend);
        request.setAttribute("estadoPCanceladas", pCanc);
        double finConf = totalReservas == 0 ? 0 : porEstado[0] * 100.0 / totalReservas;
        double finPend = totalReservas == 0 ? 0 : (porEstado[0] + porEstado[1]) * 100.0 / totalReservas;
        request.setAttribute("estadoDona", totalReservas == 0
                ? "conic-gradient(#dde3ee 0 100%)"
                : String.format(Locale.ROOT, "conic-gradient(var(--verde) 0 %.2f%%, var(--naranja) %.2f%% %.2f%%, var(--rojo) %.2f%% 100%%)",
                finConf, finConf, finPend, finPend));

        // ---- Ocupación de los próximos 14 días: por noche, casas confirmadas, pendientes y libres
        List<Map<String, Object>> dias = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            LocalDate dia = hoy.plusDays(i);
            Set<Integer> conf = new HashSet<>();
            Set<Integer> pend = new HashSet<>();
            for (ReservaPlanilla r : activas) {
                if (r.ocupa(dia)) {
                    (r.isConfirmada() ? conf : pend).add(r.getAlojamientoId());
                }
            }
            pend.removeAll(conf);
            Map<String, Object> d = new LinkedHashMap<>();
            d.put("letra", LETRAS[dia.getDayOfWeek().getValue() - 1]);
            d.put("numero", i == 0 ? "Hoy" : String.valueOf(dia.getDayOfMonth()));
            d.put("finDeSemana", dia.getDayOfWeek() == DayOfWeek.SATURDAY || dia.getDayOfWeek() == DayOfWeek.SUNDAY);
            d.put("hoy", i == 0);
            d.put("confirmadas", conf.size());
            d.put("pendientes", pend.size());
            d.put("altoConfirmadas", casas == 0 ? 0 : conf.size() * 100 / casas);
            d.put("altoPendientes", casas == 0 ? 0 : pend.size() * 100 / casas);
            d.put("titulo", dia.getDayOfMonth() + "/" + dia.getMonthValue() + ": " + conf.size() + " confirmadas, "
                    + pend.size() + " pendientes, " + Math.max(0, casas - conf.size() - pend.size()) + " libres");
            dias.add(d);
        }
        request.setAttribute("proximosDias", dias);

        // ---- Casas más reservadas (noches confirmadas del año)
        Map<String, Long> nochesPorCasa = new LinkedHashMap<>();
        for (ReservaPlanilla r : confirmadas) {
            long noches = nochesEntre(r, LocalDate.of(anio, 1, 1), LocalDate.of(anio + 1, 1, 1));
            if (noches > 0) {
                nochesPorCasa.merge(r.getAlojamientoNombre() == null ? "Sin nombre" : r.getAlojamientoNombre(), noches, Long::sum);
            }
        }
        request.setAttribute("casasMasReservadas", barras(nochesPorCasa, 8, false));

        // ---- Próximas llegadas (reservas confirmadas desde hoy)
        List<Map<String, Object>> llegando = new ArrayList<>();
        confirmadas.stream()
                .filter(r -> !r.getIngreso().isBefore(hoy))
                .sorted(Comparator.comparing(ReservaPlanilla::getIngreso).thenComparing(ReservaPlanilla::getId))
                .limit(4)
                .forEach(r -> {
                    long faltan = ChronoUnit.DAYS.between(hoy, r.getIngreso());
                    Map<String, Object> l = new LinkedHashMap<>();
                    l.put("dia", r.getIngreso().getDayOfMonth());
                    l.put("mes", MESES_CORTOS[r.getIngreso().getMonthValue() - 1].toUpperCase(ES));
                    l.put("cliente", r.getNombreMostrar());
                    l.put("detalle", r.getAlojamientoNombre() + " · " + r.getNochesTexto() + " · " + r.getHuespedesTexto());
                    l.put("cuando", faltan == 0 ? "Hoy" : faltan == 1 ? "Mañana" : "En " + faltan + " días");
                    l.put("pronto", faltan <= 1);
                    llegando.add(l);
                });
        request.setAttribute("proximasLlegadas", llegando);

        // ---- Procedencia en las reservas confirmadas del año
        Map<String, Long> porProcedencia = new LinkedHashMap<>();
        for (ReservaPlanilla r : confirmadas) {
            if (r.getIngreso().getYear() != anio) {
                continue;
            }
            String procedencia = r.getClienteProcedencia();
            porProcedencia.merge(procedencia == null || procedencia.isBlank() ? ClienteDAO.SIN_LOCALIDAD : procedencia.trim(), 1L, Long::sum);
        }
        request.setAttribute("procedenciaBarras", barras(porProcedencia, 5, true));
    }

    /** Noches de la reserva que caen entre desde (incluido) y hasta (excluido). */
    private static long nochesEntre(ReservaPlanilla r, LocalDate desde, LocalDate hasta) {
        LocalDate ini = r.getIngreso().isAfter(desde) ? r.getIngreso() : desde;
        LocalDate fin = r.getSalida().isBefore(hasta) ? r.getSalida() : hasta;
        return Math.max(0, ChronoUnit.DAYS.between(ini, fin));
    }

    private static long porcentaje(long parte, long total) {
        return total <= 0 ? 0 : Math.round(parte * 100.0 / total);
    }

    /**
     * Convierte conteos en barras para el JSP: las "limite" mayores, de mayor a menor (los vacíos al final si
     * vacioAlFinal), con el ancho relativo a la más larga.
     */
    private static List<Map<String, Object>> barras(Map<String, Long> conteos, int limite, boolean vacioAlFinal) {
        List<Map.Entry<String, Long>> orden = new ArrayList<>(conteos.entrySet());
        orden.sort((a, b) -> {
            boolean aSin = vacioAlFinal && a.getKey().equals(ClienteDAO.SIN_LOCALIDAD);
            boolean bSin = vacioAlFinal && b.getKey().equals(ClienteDAO.SIN_LOCALIDAD);
            if (aSin != bSin) {
                return aSin ? 1 : -1;
            }
            return Long.compare(b.getValue(), a.getValue());
        });
        long maximo = orden.stream().mapToLong(Map.Entry::getValue).max().orElse(1);
        List<Map<String, Object>> barras = new ArrayList<>();
        for (Map.Entry<String, Long> e : orden.subList(0, Math.min(limite, orden.size()))) {
            Map<String, Object> b = new LinkedHashMap<>();
            b.put("nombre", e.getKey());
            b.put("cantidad", e.getValue());
            b.put("ancho", e.getValue() * 100 / maximo);
            b.put("sinDato", vacioAlFinal && e.getKey().equals(ClienteDAO.SIN_LOCALIDAD));
            barras.add(b);
        }
        return barras;
    }

    /* ===================== Gráfica de localidades ===================== */

    /**
     * Prepara la gráfica "De dónde son nuestros clientes": cuántos clientes que ya hicieron una estadía
     * hay por localidad. Filtros opcionales por año (locAnio) y por mes (locMes); sin filtro, todo el historial.
     * Cada cliente cuenta una sola vez (ver ClienteDAO.contarPorLocalidad).
     */
    private void cargarGraficaLocalidades(HttpServletRequest request) throws SQLException {
        Integer anio = entero(request, "locAnio");
        Integer mesFiltro = entero(request, "locMes");
        if (anio != null && (anio < 2000 || anio > 2100)) {
            anio = null;
        }
        if (mesFiltro != null && (mesFiltro < 1 || mesFiltro > 12)) {
            mesFiltro = null;
        }

        Map<String, Integer> conteo = clienteDAO.contarPorLocalidad(anio, mesFiltro);
        int total = conteo.values().stream().mapToInt(Integer::intValue).sum();
        int maximo = conteo.values().stream().mapToInt(Integer::intValue).max().orElse(0);

        List<Map<String, Object>> barras = new ArrayList<>();
        for (Map.Entry<String, Integer> fila : conteo.entrySet()) {
            Map<String, Object> barra = new LinkedHashMap<>();
            barra.put("nombre", fila.getKey());
            barra.put("cantidad", fila.getValue());
            barra.put("porcentajeTexto", String.format(ES, "%.1f", fila.getValue() * 100.0 / total));
            barra.put("ancho", String.format(Locale.ROOT, "%.1f", fila.getValue() * 100.0 / maximo)); // la más larga ocupa todo el ancho
            barra.put("sinDato", fila.getKey().equals(ClienteDAO.SIN_LOCALIDAD));
            barras.add(barra);
        }

        List<String> meses = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            String nombre = Month.of(m).getDisplayName(TextStyle.FULL, ES);
            meses.add(Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1));
        }

        String periodo;
        if (anio == null && mesFiltro == null) {
            periodo = "Todo el historial";
        } else if (mesFiltro == null) {
            periodo = "Año " + anio;
        } else if (anio == null) {
            periodo = meses.get(mesFiltro - 1) + " (todos los años)";
        } else {
            periodo = meses.get(mesFiltro - 1) + " " + anio;
        }

        request.setAttribute("locBarras", barras);
        request.setAttribute("locTotal", total);
        request.setAttribute("locAnioSel", anio);
        request.setAttribute("locMesSel", mesFiltro);
        request.setAttribute("locAnios", clienteDAO.aniosConEstadias());
        request.setAttribute("locMeses", meses);
        request.setAttribute("locPeriodo", periodo);
    }
}
