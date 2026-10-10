package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Alojamiento;
import org.example.AlojamientoDAO;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/alojamiento-detalle")
public class AlojamientoDetalleServlet extends HttpServlet {

    private final AlojamientoDAO alojamientoDAO = new AlojamientoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idTexto = request.getParameter("id");

        if (idTexto == null || idTexto.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/alojamientos");
            return;
        }

        int id = Integer.parseInt(idTexto);

        try {
            Alojamiento alojamiento = alojamientoDAO.buscarPorIdALO(id);
            if (alojamiento == null) {
                response.sendRedirect(request.getContextPath() + "/alojamientos");
                return;
            }

            request.setAttribute("alojamiento", alojamiento);
            request.getRequestDispatcher("/WEB-INF/views/alojamiento-detalle.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Error al buscar el alojamiento", e);
        }
    }
}