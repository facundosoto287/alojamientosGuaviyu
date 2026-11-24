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

@WebServlet("/olvide-contrasena")
public class OlvideContrasenaServlet extends HttpServlet {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/olvide-contrasena.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");

        if (email == null || email.isBlank()) {
            request.setAttribute("error", "Ingresá tu correo electrónico.");
            doGet(request, response);
            return;
        }

        try {
            Cliente cliente = clienteDAO.buscarPorEmail(email.trim());

            if (cliente != null) {
                String token = clienteDAO.generarTokenRecuperacion(email.trim());

                if (token != null) {
                    String enlace = request.getScheme()
                                    + "://"
                                    + request.getServerName()
                                    + ":"
                                    + request.getServerPort()
                                    + request.getContextPath()
                                    + "/restablecer-contrasena?token="
                                    + token;

                    String contenido = CorreoPlantillas.recuperarContrasena(cliente, enlace);

                    CorreoServicio correoServicio = new CorreoServicio();

                    correoServicio.enviar(cliente.getEmail(), "Restablecer contraseña - Alojamientos Guaviyú", contenido);
                }
            }

            request.setAttribute("mensaje", "Si existe una cuenta asociada a ese correo, te enviamos un enlace para restablecer tu contraseña.");

            doGet(request, response);

        } catch (Exception e) {

            log("Error al solicitar recuperación de contraseña", e);
            e.printStackTrace();

            request.setAttribute("error", "No pudimos procesar la solicitud en este momento.");

            doGet(request, response);
        }
    }
}