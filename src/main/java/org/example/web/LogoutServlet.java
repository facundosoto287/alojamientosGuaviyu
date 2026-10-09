package org.example.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** POST /logout → cierra la sesión del cliente y vuelve al listado. */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
        response.sendRedirect(request.getContextPath() + LoginServlet.DESTINO_POR_DEFECTO);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Cerrar sesión solo por POST (el botón del encabezado); un GET simplemente vuelve al inicio
        response.sendRedirect(request.getContextPath() + LoginServlet.DESTINO_POR_DEFECTO);
    }
}
