package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.Cliente;
import org.example.Resena;
import org.example.ResenaDAO;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;

/* Recibe el formulario "Dejar una reseña" de la página de detalle */
@WebServlet("/resena")
public class ResenaServlet extends HttpServlet {

    private final ResenaDAO resenaDAO = new ResenaDAO();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        /* 1. Leer el alojamiento */
        int idAlojamiento;
        try {
            idAlojamiento = Integer.parseInt(request.getParameter("alojamientoId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/alojamientos");
            return;
        }

        String volver = request.getContextPath() + "/alojamiento-detalle?id=" + idAlojamiento;

        /* 2. Tiene que haber iniciado sesión */
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(LoginServlet.CLIENTE_SESION) == null) {

            String destino = URLEncoder.encode("/alojamiento-detalle?id=" + idAlojamiento,
                    StandardCharsets.UTF_8);

            response.sendRedirect(request.getContextPath() + "/login?destino=" + destino);
            return;
        }

        Cliente cliente = (Cliente) session.getAttribute(LoginServlet.CLIENTE_SESION);

        /* 3. Validar los datos del formulario */
        int puntuacion;
        try {
            puntuacion = Integer.parseInt(request.getParameter("puntuacion"));
        } catch (NumberFormatException e) {
            response.sendRedirect(volver + "&resena=datos#resenas");
            return;
        }

        String comentario = request.getParameter("comentario");
        comentario = (comentario == null) ? "" : comentario.trim();

        if (puntuacion < 1 || puntuacion > 5 || comentario.length() > 500) {
            response.sendRedirect(volver + "&resena=datos#resenas");
            return;
        }

        try {
            /* 4. Volver a comprobar la regla ACÁ, aunque el JSP oculte el formulario:
                  alguien podría mandar el formulario a mano sin pasar por la página.
                  Regla: una reseña por estadía terminada (ver ResenaDAO.puedeResenar) */
            if (!resenaDAO.puedeResenar(cliente.getId(), idAlojamiento)) {

                String motivo = resenaDAO.tieneEstadiaTerminada(cliente.getId(), idAlojamiento)
                        ? "repetida"       // ya opinó sobre su última estadía
                        : "sin-estadia";   // nunca terminó una estadía acá

                response.sendRedirect(volver + "&resena=" + motivo + "#resenas");
                return;
            }

            /* 5. Guardar la reseña */
            Resena resena = new Resena();
            resena.setCliente_id(cliente.getId());
            resena.setAlojamiento_id(idAlojamiento);
            resena.setPuntuacion(puntuacion);
            resena.setComentario(comentario);
            resena.setFecha(LocalDate.now());

            resenaDAO.crearResena(resena);

            response.sendRedirect(volver + "&resena=ok#resenas");

        } catch (SQLException e) {
            throw new ServletException("Error al guardar la reseña", e);
        }
    }
}