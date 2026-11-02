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
import org.example.CorreoPlantillas;
import org.example.CorreoServicio;

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
        valores.put("localidad", limpiar(request.getParameter("localidad")));

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
                        valores.get("localidad"),
                        contrasena
                );

                try {
                    String contenido = CorreoPlantillas.bienvenidaCliente(nuevo);

                    CorreoServicio correoServicio = new CorreoServicio();

                    correoServicio.enviar(nuevo.getEmail(), "Bienvenido a Alojamientos Guaviyú", contenido);

                } catch (Exception e) {
                    log("El cliente se registró correctamente, pero no se pudo enviar el correo de bienvenida.", e);
                    e.printStackTrace();
                }

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
        Map<String, String> errores = ValidacionCliente.validarDatos(v, clienteDAO);

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
