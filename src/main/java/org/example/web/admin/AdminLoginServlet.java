package org.example.web.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Administrador;
import org.example.AdministradorDAO;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GET  /gestion-posadasguaviyu/login → formulario de ingreso del administrador.
 * POST /gestion-posadasguaviyu/login → valida correo y contraseña (BCrypt) y abre la sesión de administrador.
 *
 * Defensas: la cuenta se bloquea tras varios intentos fallidos (lo hace AdministradorDAO) y además se limitan
 * los intentos por dirección IP, para frenar a quien pruebe correos distintos.
 */
@WebServlet(AdminSesion.BASE + "/login")
public class AdminLoginServlet extends HttpServlet {

    private static final int MAX_FALLOS_POR_IP = 10;
    private static final long VENTANA_MS = 15 * 60 * 1000L;

    /** IP → {cantidad de fallos, inicio de la ventana}. */
    private static final ConcurrentHashMap<String, long[]> FALLOS_POR_IP = new ConcurrentHashMap<>();

    private final AdministradorDAO administradorDAO = new AdministradorDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (AdminSesion.administrador(request) != null) {
            response.sendRedirect(request.getContextPath() + AdminSesion.BASE + "/inicio");
            return;
        }
        mostrar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();

        if (demasiadosFallos(ip)) {
            response.setStatus(429);
            request.setAttribute("error", "Demasiados intentos desde esta conexión. Esperá unos minutos e intentá de nuevo.");
            mostrar(request, response);
            return;
        }

        String email = request.getParameter("email");
        String contrasena = request.getParameter("contrasena");
        request.setAttribute("emailEscrito", email);

        if (email == null || email.isBlank() || contrasena == null || contrasena.isEmpty()) {
            request.setAttribute("error", "Ingresá tu correo y tu contraseña.");
            mostrar(request, response);
            return;
        }

        Administrador admin;
        try {
            admin = administradorDAO.iniciarSesion(email, contrasena);
        } catch (AdministradorDAO.CuentaBloqueadaException e) {
            response.setStatus(429);
            request.setAttribute("error", e.getMessage());
            mostrar(request, response);
            return;
        } catch (RuntimeException e) {
            log("Error al iniciar sesión de administrador", e);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            request.setAttribute("error", "No pudimos conectarnos con el sistema. Intentá de nuevo en unos minutos.");
            mostrar(request, response);
            return;
        }

        if (admin == null) {
            registrarFallo(ip);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // Mismo mensaje si falla el correo o la contraseña
            request.setAttribute("error", "Correo o contraseña incorrectos.");
            mostrar(request, response);
            return;
        }

        FALLOS_POR_IP.remove(ip);
        AdminSesion.abrir(request, admin);
        response.sendRedirect(request.getContextPath() + AdminSesion.BASE + "/inicio");
    }

    private void mostrar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("base", request.getContextPath() + AdminSesion.BASE);
        response.setContentType("text/html; charset=UTF-8");
        request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
    }

    private static boolean demasiadosFallos(String ip) {
        long[] datos = FALLOS_POR_IP.get(ip);
        if (datos == null) {
            return false;
        }
        if (System.currentTimeMillis() - datos[1] > VENTANA_MS) {
            FALLOS_POR_IP.remove(ip);
            return false;
        }
        return datos[0] >= MAX_FALLOS_POR_IP;
    }

    private static void registrarFallo(String ip) {
        long ahora = System.currentTimeMillis();
        FALLOS_POR_IP.merge(ip, new long[]{1, ahora}, (actual, nuevo) -> {
            if (ahora - actual[1] > VENTANA_MS) {
                return new long[]{1, ahora};
            }
            return new long[]{actual[0] + 1, actual[1]};
        });
    }
}
