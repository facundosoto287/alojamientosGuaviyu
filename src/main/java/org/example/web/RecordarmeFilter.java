package org.example.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.Cliente;
import org.example.ClienteDAO;
import org.example.web.admin.AdminSesion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.regex.Pattern;

/**
 * "Recordarme en este dispositivo" del login de clientes.
 *
 * Al marcar la casilla se guarda una cookie de 30 días con "selector.validador": el selector identifica
 * la fila de la tabla sesion_recordada y el validador es un valor al azar del que en la base solo queda el hash.
 * Cuando la sesión normal ya no existe (se cerró el navegador, pasaron 30 min o se reinició Tomcat) este filtro
 * lee la cookie y vuelve a abrir la sesión del cliente sin pedir la contraseña.
 *
 * Debe correr ANTES que CambioContrasenaFilter (el orden está en web.xml) para que ese filtro ya vea al cliente.
 * Si la tabla no existe o la base falla, el sitio sigue funcionando: simplemente nadie queda recordado.
 */
public class RecordarmeFilter implements Filter {

    private static final String COOKIE = "recordarme";
    private static final int DIAS = 30;
    private static final Pattern FORMATO = Pattern.compile("[A-Za-z0-9_-]{16}\\.[A-Za-z0-9_-]{43}");

    private static final SecureRandom ALEATORIO = new SecureRandom();
    private static final Base64.Encoder BASE64 = Base64.getUrlEncoder().withoutPadding();
    private static final ClienteDAO CLIENTE_DAO = new ClienteDAO();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (LoginServlet.clienteLogueado(request) == null && corresponde(request)) {
            restaurar(request, response);
        }

        chain.doFilter(request, response);
    }

    /** Ni el panel de administración (tiene su propia sesión), ni los archivos estáticos, ni cerrar sesión. */
    private static boolean corresponde(HttpServletRequest request) {
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        return !ruta.equals("/logout")
                && !ruta.startsWith("/css/")
                && !ruta.startsWith("/js/")
                && !ruta.startsWith("/img/")
                && !ruta.startsWith(AdminSesion.BASE);
    }

    private static void restaurar(HttpServletRequest request, HttpServletResponse response) {
        String valor = valorDeLaCookie(request);
        if (valor == null) {
            return;
        }
        if (!FORMATO.matcher(valor).matches()) {
            borrarCookie(request, response);
            return;
        }

        String selector = valor.substring(0, 16);
        String validador = valor.substring(17);

        Cliente cliente;
        try {
            cliente = CLIENTE_DAO.buscarPorSesionRecordada(selector, hash(validador));
        } catch (Exception e) { // SQLException, o la tabla todavía no existe
            avisar(request, "No se pudo leer la sesión recordada: " + e.getMessage());
            return;
        }

        if (cliente == null) { // vencida, revocada o cookie inventada
            borrarCookie(request, response);
            return;
        }

        // Se conserva la sesión que ya hubiera (por ejemplo la del administrador), pero con otro identificador.
        HttpSession sesion = request.getSession(true);
        if (!sesion.isNew()) {
            request.changeSessionId();
        }
        cliente.setContrasena(null);
        sesion.setAttribute(LoginServlet.CLIENTE_SESION, cliente);
        sesion.setMaxInactiveInterval(30 * 60);
    }

    // ===================== LO QUE USAN LOS SERVLETS =====================

    /** Después de un login correcto con la casilla marcada: guarda el token y entrega la cookie. */
    static void activar(HttpServletRequest request, HttpServletResponse response, int clienteId) {
        byte[] selector = new byte[12];
        byte[] validador = new byte[32];
        ALEATORIO.nextBytes(selector);
        ALEATORIO.nextBytes(validador);

        String selectorTexto = BASE64.encodeToString(selector);
        String validadorTexto = BASE64.encodeToString(validador);

        try {
            CLIENTE_DAO.guardarSesionRecordada(clienteId, selectorTexto, hash(validadorTexto), DIAS);
        } catch (Exception e) {
            avisar(request, "No se pudo guardar la sesión recordada: " + e.getMessage());
            return;
        }

        response.addCookie(crearCookie(request, selectorTexto + "." + validadorTexto, DIAS * 24 * 60 * 60));
    }

    /** Olvida este dispositivo: borra su token de la base y su cookie. Lo usan el login (sin casilla) y el logout. */
    static void olvidar(HttpServletRequest request, HttpServletResponse response) {
        String valor = valorDeLaCookie(request);
        if (valor == null) {
            return;
        }
        if (FORMATO.matcher(valor).matches()) {
            try {
                CLIENTE_DAO.borrarSesionRecordada(valor.substring(0, 16));
            } catch (Exception e) {
                avisar(request, "No se pudo borrar la sesión recordada: " + e.getMessage());
            }
        }
        borrarCookie(request, response);
    }

    /** Olvida todos los dispositivos del cliente (por ejemplo, cuando cambia su contraseña). */
    static void olvidarTodos(HttpServletRequest request, HttpServletResponse response, int clienteId) {
        try {
            CLIENTE_DAO.borrarSesionesRecordadas(clienteId);
        } catch (Exception e) {
            avisar(request, "No se pudieron borrar las sesiones recordadas: " + e.getMessage());
        }
        borrarCookie(request, response);
    }

    // ===================== AUXILIARES =====================

    private static String valorDeLaCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private static Cookie crearCookie(HttpServletRequest request, String valor, int segundos) {
        Cookie cookie = new Cookie(COOKIE, valor);
        String contexto = request.getContextPath();
        cookie.setPath(contexto.isEmpty() ? "/" : contexto);
        cookie.setMaxAge(segundos);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setAttribute("SameSite", "Lax");
        return cookie;
    }

    private static void borrarCookie(HttpServletRequest request, HttpServletResponse response) {
        response.addCookie(crearCookie(request, "", 0));
    }

    private static String hash(String validador) {
        try {
            byte[] resumen = MessageDigest.getInstance("SHA-256").digest(validador.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(resumen);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e); // SHA-256 existe en toda JVM
        }
    }

    private static void avisar(HttpServletRequest request, String mensaje) {
        request.getServletContext().log(mensaje);
    }
}
