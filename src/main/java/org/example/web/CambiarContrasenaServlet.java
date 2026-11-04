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
import java.sql.SQLException;

/**
 * GET  /cambiar-contrasena → formulario. Es obligatorio (y lo único accesible) mientras el cliente use la
 *                            contraseña por defecto que le dejó el administrador.
 * POST /cambiar-contrasena → verifica la contraseña actual y guarda la nueva (con hash).
 */
@WebServlet("/cambiar-contrasena")
public class CambiarContrasenaServlet extends HttpServlet {

    private static final int MIN_CONTRASENA = 8;
    private static final int MAX_CONTRASENA_BYTES = 72; // BCrypt ignora lo que pase de 72 bytes

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Cliente cliente = LoginServlet.clienteLogueado(request);
        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/login?destino=/cambiar-contrasena");
            return;
        }

        mostrar(request, response, cliente);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Cliente cliente = LoginServlet.clienteLogueado(request);
        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/login?destino=/cambiar-contrasena");
            return;
        }

        String actual = request.getParameter("actual");
        String nueva = request.getParameter("nueva");
        String confirmacion = request.getParameter("confirmacion");

        String error = validar(actual, nueva, confirmacion);

        if (error == null) {
            try {
                clienteDAO.cambiarContrasena(cliente.getId(), actual, nueva);
                RecordarmeFilter.olvidarTodos(request, response, cliente.getId()); // los demás dispositivos vuelven a pedir la contraseña

                cliente.setDebeCambiarContrasena(false);

                String origen = request.getParameter("origen");

                if ("perfil".equals(origen)) {
                    response.sendRedirect(request.getContextPath() + "/perfil");
                } else {
                    response.sendRedirect(request.getContextPath() + LoginServlet.DESTINO_POR_DEFECTO);
                }
                return;

            } catch (IllegalArgumentException e) {
                error = e.getMessage(); // la contraseña actual no coincide
            } catch (SQLException | RuntimeException e) {
                log("Error al cambiar la contraseña", e);
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                error = "No pudimos cambiar la contraseña en este momento. Intentá de nuevo en unos minutos.";
            }
        }

        request.setAttribute("error", error);
        mostrar(request, response, cliente);
    }

    private static String validar(String actual, String nueva, String confirmacion) {
        if (actual == null || actual.isEmpty()) {
            return "Ingresá tu contraseña actual.";
        }
        if (nueva == null || nueva.length() < MIN_CONTRASENA) {
            return "La contraseña nueva debe tener al menos " + MIN_CONTRASENA + " caracteres.";
        }
        if (nueva.getBytes(StandardCharsets.UTF_8).length > MAX_CONTRASENA_BYTES) {
            return "La contraseña nueva es demasiado larga.";
        }
        if (nueva.equals(actual) || nueva.equals(ClienteDAO.CONTRASENA_POR_DEFECTO)) {
            return "Elegí una contraseña distinta de la actual y de la que te dieron al registrarte.";
        }
        if (!nueva.equals(confirmacion)) {
            return "Las contraseñas nuevas no coinciden.";
        }
        return null;
    }

    private void mostrar(HttpServletRequest request, HttpServletResponse response, Cliente cliente)
            throws ServletException, IOException {
        request.setAttribute("obligatorio", cliente.isDebeCambiarContrasena());
        request.setAttribute("paginaActiva", "");
        response.setContentType("text/html; charset=UTF-8");
        request.getRequestDispatcher("/WEB-INF/views/cambiar-contrasena.jsp").forward(request, response);
    }
}
