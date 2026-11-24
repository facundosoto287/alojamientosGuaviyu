package org.example.web.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.Administrador;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Base de los servlets del panel: mostrar páginas, avisos entre redirecciones y lectura de parámetros. */
abstract class AdminServlet extends HttpServlet {

    private static final String AVISO_TIPO = "adminAvisoTipo";
    private static final String AVISO_TEXTO = "adminAvisoTexto";

    /** Muestra /WEB-INF/views/admin/{vista}.jsp dentro del diseño del panel. */
    protected void mostrar(HttpServletRequest request, HttpServletResponse response,
                           String vista, String seccion, String titulo) throws ServletException, IOException {

        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            request.setAttribute("avisoTipo", sesion.getAttribute(AVISO_TIPO));
            request.setAttribute("avisoTexto", sesion.getAttribute(AVISO_TEXTO));
            sesion.removeAttribute(AVISO_TIPO);
            sesion.removeAttribute(AVISO_TEXTO);
        }

        request.setAttribute("base", request.getContextPath() + AdminSesion.BASE);
        request.setAttribute("csrf", AdminSesion.tokenCsrf(request));
        Administrador admin = AdminSesion.administrador(request);
        if (admin == null) {
            response.sendRedirect(request.getContextPath() + AdminSesion.BASE + "/login");
            return;
        }

        request.setAttribute("adminNombre", admin.getNombre());
        request.setAttribute("seccion", seccion);
        request.setAttribute("tituloPagina", titulo);

        response.setContentType("text/html; charset=UTF-8");
        request.getRequestDispatcher("/WEB-INF/views/admin/" + vista + ".jsp").forward(request, response);
    }

    /** Deja un aviso para mostrarlo en la próxima página (patrón "redirigir después de un POST"). */
    protected static void avisoOk(HttpServletRequest request, String texto) {
        guardarAviso(request, "ok", texto);
    }

    protected static void avisoError(HttpServletRequest request, String texto) {
        guardarAviso(request, "error", texto);
    }

    private static void guardarAviso(HttpServletRequest request, String tipo, String texto) {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.setAttribute(AVISO_TIPO, tipo);
            sesion.setAttribute(AVISO_TEXTO, texto);
        }
    }

    /** Redirige a una página del panel, por ejemplo redirigir(request, response, "/servicios?alojamiento=3"). */
    protected static void redirigir(HttpServletRequest request, HttpServletResponse response, String ruta)
            throws IOException {
        response.sendRedirect(request.getContextPath() + AdminSesion.BASE + ruta);
    }

    protected static String texto(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor.trim();
    }

    /** Número entero del parámetro, o null si falta o no es un número. */
    protected static Integer entero(HttpServletRequest request, String nombre) {
        try {
            return Integer.valueOf(texto(request, nombre));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected static String codificar(String valor) {
        return URLEncoder.encode(valor == null ? "" : valor, StandardCharsets.UTF_8);
    }
}
