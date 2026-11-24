package org.example.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.TokenRecordarmeDAO;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final String COOKIE_RECORDAR = "recordarme";

    private final TokenRecordarmeDAO tokenRecordarmeDAO = new TokenRecordarmeDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        /*Primero eliminamos el token Recordarme,si existe*/
        String token = obtenerToken(request);

        if (token != null) {
            try {
                String tokenHash = LoginServlet.hashToken(token);

                tokenRecordarmeDAO.eliminar(tokenHash);

            } catch (Exception e) {
                log("No se pudo eliminar el token Recordarme", e);
            }
        }

        /*Borrar cookie del navegador*/
        borrarCookie(request, response);

        /*Cerrar sesion normal*/
        HttpSession sesion = request.getSession(false);

        if (sesion != null) {
            sesion.invalidate();
        }
        response.sendRedirect(request.getContextPath() + LoginServlet.DESTINO_POR_DEFECTO);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + LoginServlet.DESTINO_POR_DEFECTO);
    }

    private String obtenerToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (COOKIE_RECORDAR.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void borrarCookie(HttpServletRequest request, HttpServletResponse response) {

        Cookie cookie = new Cookie(COOKIE_RECORDAR, "");

        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());

        cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }
}