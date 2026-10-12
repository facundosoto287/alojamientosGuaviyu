package org.example.web.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.AlojamientoDAO;
import org.example.ClienteDAO;
import org.example.EstadoReserva;
import org.example.ReservaDao;
import org.example.ReservaDetalle;
import org.example.ServicioDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/** Inicio del panel: contadores y las últimas solicitudes pendientes. */
@WebServlet(urlPatterns = {AdminSesion.BASE, AdminSesion.BASE + "/", AdminSesion.BASE + "/inicio"})
public class AdminInicioServlet extends AdminServlet {

    private final AlojamientoDAO alojamientoDAO = new AlojamientoDAO();
    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ReservaDao reservaDao = new ReservaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<ReservaDetalle> pendientes = reservaDao.listarDetalle(EstadoReserva.PENDIENTE, null, null, null);

            request.setAttribute("totalAlojamientos", alojamientoDAO.listarAlojamientos().size());
            request.setAttribute("totalServicios", servicioDAO.listarServicios().size());
            request.setAttribute("totalClientes", clienteDAO.listarClientes().size());
            request.setAttribute("totalPendientes", pendientes.size());
            request.setAttribute("totalConfirmadas",
                    reservaDao.listarDetalle(EstadoReserva.CONFIRMADA, null, null, null).size());
            request.setAttribute("ultimasPendientes", pendientes.subList(0, Math.min(5, pendientes.size())));
        } catch (SQLException e) {
            log("Error al cargar el inicio del panel", e);
            avisoError(request, "No pudimos cargar todos los datos. Intentá de nuevo en unos minutos.");
        }

        mostrar(request, response, "inicio", "inicio", "Inicio");
    }
}
