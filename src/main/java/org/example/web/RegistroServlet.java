package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Cliente;
import org.example.ClienteDAO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GET  /registro → misma página que /login, con la pestaña "Registrarse" activa.
 * POST /registro → valida los datos, crea el cliente (con la contraseña hasheada) y lo deja logueado.
 */
@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {

    private static final int MIN_CONTRASENA = 8;
    private static final int MAX_CONTRASENA_BYTES = 72; // BCrypt ignora lo que pase de 72 bytes

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (LoginServlet.clienteLogueado(request) != null) {
            response.sendRedirect(request.getContextPath() + LoginServlet.DESTINO_POR_DEFECTO);
            return;
        }

        LoginServlet.mostrarFormulario(request, response, "registro");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Valores escritos (sin contraseñas) para volver a mostrarlos si hay errores
        Map<String, String> valores = new LinkedHashMap<>();
        valores.put("nombre", limpiar(request.getParameter("nombre")));
        valores.put("documento", limpiar(request.getParameter("documento")));
        valores.put("telefono", limpiar(request.getParameter("telefono")));
        valores.put("email", limpiar(request.getParameter("email")));
        valores.put("procedencia", limpiar(request.getParameter("procedencia")));

        String contrasena = request.getParameter("contrasena");
        String confirmacion = request.getParameter("confirmacion");

        Map<String, String> errores = validar(valores, contrasena, confirmacion);

        if (errores.isEmpty()) {
            try {
                Cliente nuevo = clienteDAO.registrarCliente(
                        valores.get("nombre"),
                        valores.get("documento"),
                        valores.get("telefono"),
                        valores.get("email"),
                        valores.get("procedencia"),
                        contrasena
                );

                LoginServlet.abrirSesion(request, nuevo); // queda logueado al registrarse
                response.sendRedirect(request.getContextPath()
                        + LoginServlet.destinoSeguro(request.getParameter("destino")));
                return;

            } catch (IllegalArgumentException e) {
                errores.put("general", e.getMessage()); // email o documento repetido
            } catch (RuntimeException e) {
                log("Error al registrar cliente", e);
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                errores.put("general", "No pudimos crear la cuenta en este momento. Intentá de nuevo en unos minutos.");
            }
        }

        request.setAttribute("registro", valores);
        request.setAttribute("registroErrores", errores);
        LoginServlet.mostrarFormulario(request, response, "registro");
    }

    private Map<String, String> validar(Map<String, String> v, String contrasena, String confirmacion) {
        Map<String, String> errores = new HashMap<>();

        String nombre = v.get("nombre");
        if (nombre.isEmpty()) {
            errores.put("nombre", "Ingresá tu nombre y apellido.");
        } else if (nombre.length() > 100) {
            errores.put("nombre", "El nombre puede tener hasta 100 caracteres.");
        }

        String documento = v.get("documento");
        if (documento.isEmpty()) {
            errores.put("documento", "Ingresá tu documento.");
        } else if (documento.length() > 20 || !documento.matches("[0-9A-Za-z.\\-]+")) {
            errores.put("documento", "Usá solo números, letras, puntos o guiones (máximo 20).");
        }

        String telefono = v.get("telefono");
        if (!telefono.isEmpty() && (telefono.length() > 30 || !telefono.matches("[0-9+()\\s\\-]+"))) {
            errores.put("telefono", "Ingresá un teléfono válido, por ejemplo 099 123 456.");
        }

        String email = v.get("email");
        if (email.isEmpty()) {
            errores.put("email", "Ingresá tu correo electrónico.");
        } else if (email.length() > 100 || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            errores.put("email", "El correo no tiene un formato válido.");
        }

        String procedencia = v.get("procedencia");
        if (!procedencia.isEmpty() && !LoginServlet.PROCEDENCIAS.contains(procedencia)) {
            errores.put("procedencia", "Elegí una opción de la lista.");
        }

        if (contrasena == null || contrasena.length() < MIN_CONTRASENA) {
            errores.put("contrasena", "La contraseña debe tener al menos " + MIN_CONTRASENA + " caracteres.");
        } else if (contrasena.getBytes(StandardCharsets.UTF_8).length > MAX_CONTRASENA_BYTES) {
            errores.put("contrasena", "La contraseña es demasiado larga.");
        } else if (!contrasena.equals(confirmacion)) {
            errores.put("confirmacion", "Las contraseñas no coinciden.");
        }

        return errores;
    }

    private static String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
