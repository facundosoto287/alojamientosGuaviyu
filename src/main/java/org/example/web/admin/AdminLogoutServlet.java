package org.example.web.admin;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** POST /gestion-posadasguaviyu/logout → cierra la sesión del administrador y vuelve al ingreso. */
@WebServlet(AdminSesion.BASE + "/logout")
public class AdminLogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
        response.sendRedirect(request.getContextPath() + AdminSesion.BASE + "/login");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Cerrar sesión solo por POST (con token): un GET no hace nada
        response.sendRedirect(request.getContextPath() + AdminSesion.BASE + "/inicio");
    }
}
