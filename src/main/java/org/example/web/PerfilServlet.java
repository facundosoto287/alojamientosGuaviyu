package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.Cliente;
import org.example.ClienteDAO;
import org.example.Resena;
import org.example.ResenaDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Locale;

@WebServlet("/perfil")
public class PerfilServlet extends HttpServlet {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ResenaDAO resenaDAO = new ResenaDAO();


    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        /*Si no hay sesión o no hay cliente logueado,lo mandamos al login*/
        if (session == null || session.getAttribute("clienteSesion") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setAttribute(
                "procedencias",
                LoginServlet.PROCEDENCIAS
        );

        request.setAttribute("localidades", LoginServlet.sugerenciasDeLocalidad());

        Cliente clienteSesion = (Cliente) session.getAttribute("clienteSesion");

        try {
            /*Buscamos los datos actualizados del clientedirectamente desde la base de datos*/
            Cliente cliente = clienteDAO.buscarPorId(clienteSesion.getId());

            if (cliente == null) {

                session.invalidate();

                response.sendRedirect(request.getContextPath() + "/login");

                return;
            }

            /*Traemos las reseñas de ese cliente*/
            List<Resena> resenas = resenaDAO.listarPorCliente(cliente.getId());


            /*Enviamos los datos al JSP*/
            request.setAttribute("cliente", cliente);

            request.setAttribute("resenas", resenas);


            /*Mostramos la pantalla de perfil*/
            request
                    .getRequestDispatcher("/WEB-INF/views/perfil.jsp")
                    .forward(
                            request,
                            response
                    );


        } catch (SQLException e) {

            throw new ServletException("Error al cargar el perfil.", e);
        }
    }

    /**
     * "Guardar cambios" del perfil: teléfono, correo, procedencia (departamento, provincia o país)
     * y localidad (ciudad o pueblo). Nombre y documento no se pueden cambiar desde acá.
     */
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clienteSesion") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Cliente clienteSesion = (Cliente) session.getAttribute("clienteSesion");

        String telefono = texto(request, "telefono");
        String email = texto(request, "email").toLowerCase(Locale.ROOT);
        String procedencia = texto(request, "procedencia");
        String localidad = ClienteDAO.normalizarLocalidad(texto(request, "localidad"));

        String error = null;
        if (!telefono.isEmpty() && (!telefono.matches("[0-9\\s\\-]+") || !clienteDAO.celularValido(telefono))) {
            error = "El celular debe tener 9 dígitos y empezar con 09 (ej. 099 123 456).";
        } else if (email.isEmpty() || email.length() > 100 || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            error = "El correo no tiene un formato válido.";
        } else if (procedencia.isEmpty() || !LoginServlet.PROCEDENCIAS.contains(procedencia)) {
            error = "Elegí tu procedencia (departamento, provincia o país donde vivís).";
        } else {
            error = ClienteDAO.errorDeLocalidad(localidad);
        }

        if (error == null) {
            String soloNumeros = telefono.replaceAll("[^0-9]", "");
            try {
                if (clienteDAO.actualizarPerfil(clienteSesion.getId(), soloNumeros.isEmpty() ? null : soloNumeros,
                        email, procedencia, localidad)) {
                    clienteSesion.setTelefono(soloNumeros.isEmpty() ? null : soloNumeros);
                    clienteSesion.setEmail(email);
                    clienteSesion.setProcedencia(procedencia);
                    clienteSesion.setLocalidad(localidad);
                    request.setAttribute("mensajePerfil", "Tus datos se actualizaron correctamente.");
                } else {
                    error = "No encontramos tu cuenta para actualizarla.";
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                error = "Ese correo ya está en uso por otra cuenta.";
            } catch (SQLException e) {
                log("Error al actualizar el perfil", e);
                error = "No pudimos guardar los cambios en este momento. Intentá de nuevo en unos minutos.";
            }
        }

        if (error != null) {
            request.setAttribute("errorPerfil", error);
        }

        doGet(request, response); // vuelve a mostrar el perfil con los datos guardados y el mensaje
    }

    private static String texto(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor.trim();
    }
}