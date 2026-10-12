package org.example.web.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.Administrador;

import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Sesión del administrador. Es independiente de la de los clientes: un cliente logueado
 * nunca queda con permisos de administrador (usan atributos de sesión distintos).
 */
public final class AdminSesion {

    /** Dirección reservada del panel. No aparece en ningún enlace del sitio público. */
    public static final String BASE = "/gestion-posadasguaviyu";

    public static final String ADMIN_SESION = "adminSesion";
    public static final String CSRF = "adminCsrf";

    private static final int MINUTOS_INACTIVIDAD = 30;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private AdminSesion() {
    }

    /** Crea una sesión NUEVA (evita la fijación de sesión) con el administrador y su token anti-CSRF. */
    public static void abrir(HttpServletRequest request, Administrador admin) {
        HttpSession anterior = request.getSession(false);
        if (anterior != null) {
            anterior.invalidate();
        }

        admin.setContrasenia(null);

        byte[] bytes = new byte[32];
        ALEATORIO.nextBytes(bytes);

        HttpSession sesion = request.getSession(true);
        sesion.setAttribute(ADMIN_SESION, admin);
        sesion.setAttribute(CSRF, HexFormat.of().formatHex(bytes));
        sesion.setMaxInactiveInterval(MINUTOS_INACTIVIDAD * 60);
    }

    public static Administrador administrador(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        return sesion == null ? null : (Administrador) sesion.getAttribute(ADMIN_SESION);
    }

    public static String tokenCsrf(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        return sesion == null ? null : (String) sesion.getAttribute(CSRF);
    }
}
