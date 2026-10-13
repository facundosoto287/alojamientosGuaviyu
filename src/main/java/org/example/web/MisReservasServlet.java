package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.Cliente;
import org.example.Reserva;
import org.example.ReservaDao;

import java.io.IOException;
import java.util.List;

@WebServlet("/mis-reservas")
public class MisReservasServlet extends HttpServlet {

    private final ReservaDao reservaDao = new ReservaDao();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("clienteSesion") == null) {
            response.sendRedirect(request.getContextPath() + "/login?destino=/mis-reservas");
            return;
        }

        Cliente cliente = (Cliente) session.getAttribute("clienteSesion");

        List<Reserva> reservas = reservaDao.listarReservasCliente(cliente.getId());

        request.setAttribute("reservas", reservas);

        request.setAttribute("paginaActiva", "mis-reservas");

        request.getRequestDispatcher("/WEB-INF/views/mis-reservas.jsp").forward(request, response);
    }
}