package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.Cliente;
import org.example.ClienteDAO;

import java.io.IOException;
import java.util.List;

/**
 * GET  /login  → muestra la página con "Iniciar sesión" y "Registrarse".
 * POST /login  → valida correo y contraseña; si están bien guarda el cliente en la sesión.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    /** Nombre del atributo de sesión donde queda el cliente logueado. Lo usan el header y los demás servlets. */
    public static final String CLIENTE_SESION = "clienteSesion";

    /** A dónde va el cliente después de entrar, si no pidió otra página. */
    static final String DESTINO_POR_DEFECTO = "/inicio";

    /** Opciones del desplegable "Procedencia" (la columna es VARCHAR, así que se pueden cambiar libremente). */
    public static final List<String> PROCEDENCIAS = List.of(
            "Artigas", "Canelones", "Cerro Largo", "Colonia", "Durazno", "Flores", "Florida",
            "Lavalleja", "Maldonado", "Montevideo", "Paysandú", "Río Negro", "Rivera", "Rocha",
            "Salto", "San José", "Soriano", "Tacuarembó", "Treinta y Tres",
            "Argentina", "Brasil", "Otro país"
    );

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (clienteLogueado(request) != null) {
            response.sendRedirect(request.getContextPath() + DESTINO_POR_DEFECTO);
            return;
        }

        mostrarFormulario(request, response, "login");

        if ("reserva".equals(request.getParameter("motivo"))) {

            request.setAttribute("loginInfo", "Debés iniciar sesión para solicitar una reserva.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String contrasena = request.getParameter("contrasena");
        String destino = destinoSeguro(request.getParameter("destino"));

        request.setAttribute("loginEmail", email);

        if (email == null || email.isBlank() || contrasena == null || contrasena.isEmpty()) {
            request.setAttribute("loginError", "Ingresá tu correo y tu contraseña.");
            mostrarFormulario(request, response, "login");
            return;
        }

        Cliente cliente;
        try {
            cliente = clienteDAO.iniciarSesion(email, contrasena);
        } catch (Exception e) { // SQLException o IllegalStateException de ConexionBD
            log("Error al iniciar sesión", e);
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            request.setAttribute("loginError", "No pudimos conectarnos con el sistema. Intentá de nuevo en unos minutos.");
            mostrarFormulario(request, response, "login");
            return;
        }

        if (cliente == null) {
            // Mismo mensaje si falla el correo o la contraseña: no se revela qué correos están registrados
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("loginError", "Correo o contraseña incorrectos.");
            mostrarFormulario(request, response, "login");
            return;
        }

        abrirSesion(request, cliente);
        response.sendRedirect(request.getContextPath() + destino);
    }

    // ===================== AUXILIARES (también los usa RegistroServlet) =====================

    /** Crea una sesión NUEVA (evita la fijación de sesión) y guarda el cliente sin su hash. */
    static void abrirSesion(HttpServletRequest request, Cliente cliente) {
        HttpSession anterior = request.getSession(false);
        if (anterior != null) {
            anterior.invalidate();
        }

        cliente.setContrasena(null);

        HttpSession sesion = request.getSession(true);
        sesion.setAttribute(CLIENTE_SESION, cliente);
        sesion.setMaxInactiveInterval(30 * 60); // 30 minutos sin actividad
    }

    static Cliente clienteLogueado(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        return sesion == null ? null : (Cliente) sesion.getAttribute(CLIENTE_SESION);
    }

    static void mostrarFormulario(HttpServletRequest request, HttpServletResponse response, String pestana)
            throws ServletException, IOException {
        request.setAttribute("pestana", pestana);
        request.setAttribute("procedencias", PROCEDENCIAS);
        request.setAttribute("localidades", sugerenciasDeLocalidad());
        request.setAttribute("paginaActiva", "login");
        request.setAttribute("destino", destinoSeguro(request.getParameter("destino")));
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    /** Localidades ya cargadas, para sugerirlas en el campo "Localidad". Si falla la base, simplemente no hay sugerencias. */
    public static List<String> sugerenciasDeLocalidad() {
        try {
            return new ClienteDAO().listarLocalidades();
        } catch (Exception e) {
            return List.of();
        }
    }

    /** Solo acepta rutas internas ("/mis-reservas"), nunca otro sitio ("https://..." o "//sitio.com"). */
    static String destinoSeguro(String destino) {
        if (destino == null || !destino.startsWith("/") || destino.startsWith("//")
                || destino.contains("\\") || destino.contains("\r") || destino.contains("\n")
                || destino.startsWith("/login") || destino.startsWith("/registro")) {
            return DESTINO_POR_DEFECTO;
        }
        return destino;
    }
}
