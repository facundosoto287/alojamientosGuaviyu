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
import java.util.List;

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
}