package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Alojamiento;
import org.example.AlojamientoDAO;
import org.example.Servicio;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.io.IOException;

@WebServlet("/inicio")
public class InicioServlet extends HttpServlet {

    private final AlojamientoDAO alojamientoDAO = new AlojamientoDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String huespedesTexto = request.getParameter("huespedes");
        String precioMaxTexto = request.getParameter("precioMax");
        String mascotas = request.getParameter("mascotas");

        List<Alojamiento> alojamientos = alojamientoDAO.listarAlojamientos();

        if (huespedesTexto != null && !huespedesTexto.isBlank()) {
            int huespedes = Integer.parseInt(huespedesTexto);

            alojamientos = alojamientos.stream()
                    .filter(a -> a.getCapacidad() >= huespedes)
                    .toList();
        }

        if (precioMaxTexto != null && !precioMaxTexto.isBlank()) {
            double precioMax = Double.parseDouble(precioMaxTexto);

            alojamientos = alojamientos.stream()
                    .filter(a -> a.getPrecio() <= precioMax)
                    .toList();
        }

        if ("si".equals(mascotas)) {

            alojamientos = alojamientos.stream()
                    .filter(Alojamiento::getPermiteMascotas)
                    .toList();
        } else if ("no".equals(mascotas)) {

            alojamientos = alojamientos.stream()
                    .filter(a -> !a.getPermiteMascotas())
                    .toList();
        }
        request.setAttribute("alojamientos", alojamientos);

        Map<Integer, List<Servicio>> serviciosPorAlojamiento = new HashMap<>();
        for (Alojamiento alojamiento : alojamientos) {
            List<Servicio> servicios = alojamientoDAO.verServiciosDeUnAlojamiento(alojamiento.getId());
            serviciosPorAlojamiento.put(alojamiento.getId(), servicios);
        }
        request.setAttribute("serviciosPorAlojamiento", serviciosPorAlojamiento);

        request.getRequestDispatcher("/WEB-INF/views/inicio.jsp")
                .forward(request, response);
    }
}