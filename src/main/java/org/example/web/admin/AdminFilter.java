package org.example.web.admin;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Protege TODAS las páginas del panel: sin sesión de administrador se redirige al ingreso.
 * Además exige el token anti-CSRF en cada POST y manda cabeceras de seguridad.
 * La única página pública es el formulario de ingreso.
 */
@WebFilter(AdminSesion.BASE + "/*")
public class AdminFilter implements Filter {

    private static final String RUTA_LOGIN = AdminSesion.BASE + "/login";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Que el navegador no guarde copias del panel ni lo muestre dentro de otro sitio
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Referrer-Policy", "same-origin");

        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        boolean esLogin = ruta.equals(RUTA_LOGIN) || ruta.equals(RUTA_LOGIN + "/");

        if (esLogin) {
            chain.doFilter(request, response);
            return;
        }

        if (AdminSesion.administrador(request) == null) {
            response.sendRedirect(request.getContextPath() + RUTA_LOGIN);
            return;
        }

        if ("POST".equalsIgnoreCase(request.getMethod()) && !tokenValido(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Solicitud no válida. Volvé a cargar la página e intentá de nuevo.");
            return;
        }

        chain.doFilter(request, response);
    }

    private static boolean tokenValido(HttpServletRequest request) {
        String esperado = AdminSesion.tokenCsrf(request);
        String recibido = request.getParameter("_csrf");
        return esperado != null && recibido != null
                && MessageDigest.isEqual(esperado.getBytes(StandardCharsets.UTF_8), recibido.getBytes(StandardCharsets.UTF_8));
    }
}
