package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Cliente;
import org.example.ClienteDAO;
import org.example.CorreoPlantillas;
import org.example.CorreoServicio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/restablecer-contrasena")
public class RestablecerContrasenaServlet extends HttpServlet {

    private static final int MIN_CONTRASENA = 8;
    private static final int MAX_CONTRASENA_BYTES = 72;

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String token = request.getParameter("token");

        try {
            Cliente cliente = clienteDAO.buscarPorTokenRecuperacion(token);

            if (cliente == null) {
                request.setAttribute("error", "El enlace no es válido o ya venció.");

                request.getRequestDispatcher("/WEB-INF/views/restablecer-contrasena.jsp").forward(request, response);

                return;
            }

            request.setAttribute("token", token);

            request.getRequestDispatcher("/WEB-INF/views/restablecer-contrasena.jsp").forward(request, response);

        } catch (Exception e) {

            log("Error al validar token de recuperación", e);

            request.setAttribute("error", "No pudimos validar el enlace.");

            request.getRequestDispatcher("/WEB-INF/views/restablecer-contrasena.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String token = request.getParameter("token");
        String nueva = request.getParameter("contrasena");
        String confirmacion = request.getParameter("confirmacion");

        String error = null;

        if (nueva == null || nueva.length() < MIN_CONTRASENA) {
            error = "La contraseña debe tener al menos " + MIN_CONTRASENA + " caracteres.";

        } else if (nueva.getBytes(StandardCharsets.UTF_8).length > MAX_CONTRASENA_BYTES) {
            error = "La contraseña es demasiado larga.";

        } else if (!nueva.equals(confirmacion)) {
            error = "Las contraseñas no coinciden.";
        }

        if (error != null) {

            request.setAttribute("error", error);
            request.setAttribute("token", token);
            request.getRequestDispatcher("/WEB-INF/views/restablecer-contrasena.jsp").forward(request, response);
            return;
        }

        try {
            Cliente cliente = clienteDAO.buscarPorTokenRecuperacion(token);

            if (cliente == null) {
                request.setAttribute("error", "El enlace no es válido o ya venció.");

                request.getRequestDispatcher("/WEB-INF/views/restablecer-contrasena.jsp").forward(request, response);

                return;
            }

            boolean actualizado = clienteDAO.restablecerContrasena(token, nueva);

            if (!actualizado) {
                request.setAttribute("error", "No pudimos actualizar la contraseña.");

                request.getRequestDispatcher("/WEB-INF/views/restablecer-contrasena.jsp").forward(request, response);

                return;
            }

            try {
                String contenido = CorreoPlantillas.contrasenaActualizada(cliente);

                CorreoServicio correoServicio = new CorreoServicio();

                correoServicio.enviar(cliente.getEmail(), "Tu contraseña fue actualizada - Alojamientos Guaviyú", contenido);

            } catch (Exception e) {
                log("La contraseña se actualizó, pero no se pudo enviar el correo de aviso.", e);
            }

            response.sendRedirect(request.getContextPath() + "/login?contrasenaRestablecida=1");

        } catch (Exception e) {
            log("Error al restablecer contraseña", e);

            request.setAttribute("error", "No pudimos actualizar la contrasena.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/WEB-INF/views/restablecer-contrasena.jsp").forward(request, response);
        }
    }
}