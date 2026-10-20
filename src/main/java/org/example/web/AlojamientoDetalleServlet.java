package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.example.Alojamiento;
import org.example.AlojamientoDAO;
import org.example.Servicio;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.example.Reserva;
import org.example.ReservaDao;

import jakarta.servlet.http.HttpSession;
import org.example.Cliente;
import org.example.Resena;
import org.example.ResenaDAO;


@WebServlet("/alojamiento-detalle")
public class AlojamientoDetalleServlet extends HttpServlet {

    private final AlojamientoDAO alojamientoDAO = new AlojamientoDAO();

    private final ReservaDao reservaDao = new ReservaDao();

    private final ResenaDAO resenaDAO = new ResenaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idTexto = request.getParameter("id");

        // Si no llega un id, vuelve al listado de alojamientos
        if (idTexto == null || idTexto.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/alojamientos");
            return;
        }

        int id = Integer.parseInt(idTexto);

        try {

            // Buscar el alojamiento seleccionado
            Alojamiento alojamiento = alojamientoDAO.buscarPorIdALO(id);

            if (alojamiento == null) {
                response.sendRedirect(request.getContextPath() + "/alojamientos");
                return;
            }

            List<Reserva> reservasConfirmadas = reservaDao.reservasConfirmadasAlojamiento(id);
            request.setAttribute("reservasConfirmadas", reservasConfirmadas);

            // Buscar los servicios asociados a ese alojamiento
            List<Servicio> servicios =
                    alojamientoDAO.verServiciosDeUnAlojamiento(id);

            // ===== Reseñas =====
            List<Resena> resenas = resenaDAO.listarPorAlojamiento(id);
            request.setAttribute("resenas", resenas);
            request.setAttribute("promedioResenas", resenaDAO.promedioPorAlojamiento(id));

            // ¿Quién está mirando la página? Eso decide qué ve en la parte de reseñas
            HttpSession session = request.getSession(false);
            Cliente cliente = (session == null) ? null
                    : (Cliente) session.getAttribute(LoginServlet.CLIENTE_SESION);

            boolean puedeResenar = false;
            boolean tieneEstadia = false;

            if (cliente != null) {
                puedeResenar = resenaDAO.puedeResenar(cliente.getId(), id);
                tieneEstadia = puedeResenar || resenaDAO.tieneEstadiaTerminada(cliente.getId(), id);
            }

            request.setAttribute("logueado", cliente != null);
            request.setAttribute("puedeResenar", puedeResenar);
            request.setAttribute("tieneEstadia", tieneEstadia);

            // Enviar los datos al JSP
            request.setAttribute("alojamiento", alojamiento);
            request.setAttribute("servicios", servicios);

            request.getRequestDispatcher("/WEB-INF/views/alojamiento-detalle.jsp")
                    .forward(request, response);

        } catch (SQLException e) {

            throw new ServletException(
                    "Error al buscar el alojamiento y sus servicios",
                    e
            );
        }
    }
}