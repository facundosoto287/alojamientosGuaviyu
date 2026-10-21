package org.example.web.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.AlojamientoDAO;
import org.example.ClienteDAO;
import org.example.EstadoReserva;
import org.example.Reserva;
import org.example.ReservaDao;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

/**
 * Casos de uso: ver todas / pendientes / confirmadas / canceladas, buscar por cliente, ver reservas de un
 * alojamiento, crear (también reservas hechas por otros medios de contacto), confirmar y cancelar.
 * GET  → listado con filtros ?estado= &q= &alojamiento=.
 * POST → accion = crear | confirmar | cancelar.
 */
@WebServlet(AdminSesion.BASE + "/reservas")
public class AdminReservasServlet extends AdminServlet {

    private final ReservaDao reservaDao = new ReservaDao();
    private final AlojamientoDAO alojamientoDAO = new AlojamientoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EstadoReserva estado = estado(texto(request, "estado"));
        String q = texto(request, "q");
        Integer alojamientoId = entero(request, "alojamiento");

        try {
            request.setAttribute("reservas", reservaDao.listarDetalle(estado, alojamientoId, null, q));
            request.setAttribute("alojamientos", alojamientoDAO.listarAlojamientos());
            request.setAttribute("clientes", clienteDAO.listarClientes());
        } catch (SQLException e) {
            log("Error al listar reservas", e);
            avisoError(request, "No pudimos cargar las reservas. Intentá de nuevo en unos minutos.");
        }

        StringBuilder filtros = new StringBuilder();
        if (!q.isEmpty()) {
            filtros.append("&q=").append(codificar(q));
        }
        if (alojamientoId != null) {
            filtros.append("&alojamiento=").append(alojamientoId);
        }
        request.setAttribute("filtros", filtros.toString());

        request.setAttribute("estadoSel", estado == null ? "" : estado.name());
        request.setAttribute("q", q);
        request.setAttribute("alojamientoSel", alojamientoId);
        request.setAttribute("hoy", new Date(System.currentTimeMillis()).toString());
        mostrar(request, response, "reservas", "reservas", "Reservas");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = texto(request, "accion");

        try {
            switch (accion) {
                case "crear" -> crear(request);
                case "confirmar" -> confirmar(request);
                case "cancelar" -> cancelar(request);
                default -> avisoError(request, "Acción no reconocida.");
            }
        } catch (SQLException | RuntimeException e) {
            log("Error en reservas (" + accion + ")", e);
            avisoError(request, "No pudimos completar la operación. Intentá de nuevo en unos minutos.");
        }

        // Si la acción se hizo desde Disponibilidad (vista de todas las casas), volver ahí (al mismo mes)
        String volver = texto(request, "volver");
        if (volver.matches("/disponibilidad(\\?mes=\\d{4}-\\d{2})?")) {
            redirigir(request, response, volver);
            return;
        }

        redirigir(request, response, "/reservas" + filtrosActuales(request));
    }

    private void crear(HttpServletRequest request) throws SQLException {
        Integer clienteId = entero(request, "clienteId");
        Integer alojamientoId = entero(request, "alojamientoId");
        Integer huespedes = entero(request, "huespedes");
        EstadoReserva estado = estado(texto(request, "estadoInicial"));
        Date ingreso = fecha(texto(request, "fechaIngreso"));
        Date salida = fecha(texto(request, "fechaSalida"));
        Date hoy = new Date(System.currentTimeMillis());

        String error = null;
        if (clienteId == null) {
            error = "Elegí un cliente.";
        } else if (alojamientoId == null) {
            error = "Elegí un alojamiento.";
        } else if (ingreso == null || salida == null) {
            error = "Ingresá las fechas de ingreso y salida.";
        } else if (ingreso.toLocalDate().isBefore(hoy.toLocalDate())) {
            error = "La fecha de ingreso no puede ser anterior a hoy.";
        } else if (!salida.toLocalDate().isAfter(ingreso.toLocalDate())) {
            error = "La fecha de salida debe ser posterior a la de ingreso.";
        } else if (huespedes == null || huespedes < 1) {
            error = "La cantidad de huéspedes debe ser mayor que cero.";
        } else if (estado == null || estado == EstadoReserva.CANCELADA) {
            error = "Elegí si la reserva queda confirmada o pendiente.";
        }

        if (error != null) {
            avisoError(request, error);
            return;
        }

        try {
            int id = reservaDao.crearReservaAdmin(
                    new Reserva(0, ingreso, salida, huespedes, estado, hoy, alojamientoId), clienteId);
            avisoOk(request, "Reserva #" + id + " creada como " + etiqueta(estado) + "."
                    + (estado == EstadoReserva.CONFIRMADA ? " Las fechas quedaron ocupadas." : ""));
        } catch (IllegalArgumentException e) {
            avisoError(request, e.getMessage());
        }
    }

    private void confirmar(HttpServletRequest request) {
        Integer id = entero(request, "id");
        if (id == null) {
            avisoError(request, "La reserva no existe.");
            return;
        }

        switch (reservaDao.confirmarReservaConResultado(id)) {
            case CONFIRMADA -> avisoOk(request, "Reserva #" + id + " confirmada. Las fechas quedaron ocupadas.");
            case NO_PENDIENTE -> avisoError(request, "La reserva #" + id + " ya no está pendiente.");
            case FECHAS_OCUPADAS -> avisoError(request, "No se puede confirmar la reserva #" + id
                    + ": las fechas se superponen con otra reserva confirmada.");
            default -> avisoError(request, "No se pudo confirmar la reserva #" + id + ".");
        }
    }

    private void cancelar(HttpServletRequest request) {
        Integer id = entero(request, "id");
        if (id != null && reservaDao.cancelarReserva(id)) {
            avisoOk(request, "Reserva #" + id + " cancelada. Si estaba confirmada, las fechas quedaron libres.");
        } else {
            avisoError(request, "No se pudo cancelar la reserva (¿ya estaba cancelada?).");
        }
    }

    /** Conserva los filtros que tenía la página al volver del POST. */
    private static String filtrosActuales(HttpServletRequest request) {
        StringBuilder consulta = new StringBuilder();
        agregar(consulta, "estado", texto(request, "fEstado"));
        agregar(consulta, "q", texto(request, "fQ"));
        agregar(consulta, "alojamiento", texto(request, "fAlojamiento"));
        return consulta.toString();
    }

    private static void agregar(StringBuilder consulta, String nombre, String valor) {
        if (!valor.isEmpty()) {
            consulta.append(consulta.length() == 0 ? '?' : '&').append(nombre).append('=').append(codificar(valor));
        }
    }

    private static EstadoReserva estado(String texto) {
        try {
            return EstadoReserva.valueOf(texto);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Date fecha(String texto) {
        try {
            return Date.valueOf(texto);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String etiqueta(EstadoReserva estado) {
        return estado == EstadoReserva.CONFIRMADA ? "confirmada" : "pendiente";
    }
}