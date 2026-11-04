package org.example.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.example.Cliente;
import org.example.ClienteDAO;
import org.example.TokenRecordarmeDAO;

import java.io.IOException;

@WebFilter("/*")
public class RecordarmeFilter implements Filter {

    private static final String COOKIE_RECORDAR = "recordarme";

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TokenRecordarmeDAO tokenRecordarmeDAO = new TokenRecordarmeDAO();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        HttpServletResponse response = (HttpServletResponse) servletResponse;

        /*Si ya hay un cliente en sesiin, no necesitamos hacer nada*/
        if (LoginServlet.clienteLogueado(request) == null) {
            String token = obtenerCookieRecordarme(request);

            if (token != null && !token.isBlank()) {
                try {
                    String tokenHash = LoginServlet.hashToken(token);

                    Integer clienteId = tokenRecordarmeDAO.buscarClienteId(tokenHash);

                    if (clienteId != null) {
                        Cliente cliente = clienteDAO.buscarPorId(clienteId);

                        if (cliente != null) {
                            LoginServlet.abrirSesion(request, cliente);
                        } else {
                            borrarCookie(request, response);
                            tokenRecordarmeDAO.eliminar(tokenHash);
                        }

                    } else {
                        /*El token no existe o vencio se elimina la cookie del navegador. */
                        borrarCookie(request, response);
                    }

                } catch (Exception e) {
                    request.getServletContext().log("No se pudo restaurar la sesión con Recordarme", e);
                }
            }
        }
        chain.doFilter(request, response);
    }

    private String obtenerCookieRecordarme(HttpServletRequest request) {

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