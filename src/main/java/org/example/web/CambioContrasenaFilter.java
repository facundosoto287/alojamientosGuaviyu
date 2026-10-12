package org.example.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Cliente;

import java.io.IOException;

/**
 * Mientras un cliente tenga la contraseña por defecto (cuenta creada por el administrador),
 * cualquier página lo lleva a /cambiar-contrasena. Solo quedan libres esa pantalla, cerrar sesión
 * y los archivos estáticos que la pantalla necesita.
 */
@WebFilter("/*")
public class CambioContrasenaFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        Cliente cliente = LoginServlet.clienteLogueado(request);

        if (cliente != null && cliente.isDebeCambiarContrasena() && !permitida(request)) {
            response.sendRedirect(request.getContextPath() + "/cambiar-contrasena");
            return;
        }

        chain.doFilter(request, response);
    }

    private static boolean permitida(HttpServletRequest request) {
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        return ruta.equals("/cambiar-contrasena")
                || ruta.equals("/logout")
                || ruta.startsWith("/css/")
                || ruta.startsWith("/js/")
                || ruta.startsWith("/img/")
                || ruta.startsWith("/gestion-posadasguaviyu"); // el panel tiene su propia sesión
    }
}
