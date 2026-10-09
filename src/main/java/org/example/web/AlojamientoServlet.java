package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Alojamiento;
import org.example.AlojamientoDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/alojamientos")
public class AlojamientoServlet extends HttpServlet {
    private final AlojamientoDAO alojamientoDAO = new AlojamientoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        List<Alojamiento> alojamientos = alojamientoDAO.listarAlojamientos();
        request.setAttribute("alojamientos", alojamientos);

        request.getRequestDispatcher("/WEB-INF/views/alojamientos.jsp")
                .forward(request, response);
    }
}
