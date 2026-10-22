package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Alojamiento;
import org.example.AlojamientoDAO;
import org.example.ResenaDAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// Página "Mapa": muestra las Termas de Guaviyú y cada alojamiento en su ubicación.
@WebServlet("/mapa")
public class MapaServlet extends HttpServlet {

    // Centro del mapa: las Termas de Guaviyú (Ruta 3 km 432, Paysandú)
    private static final double CENTRO_LATITUD = -31.8400;
    private static final double CENTRO_LONGITUD = -57.8880;

    private final AlojamientoDAO alojamientoDAO = new AlojamientoDAO();
    private final ResenaDAO resenaDAO = new ResenaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // Si la base de datos falla, el mapa igual se muestra (sin la lista de alojamientos)
        List<Alojamiento> alojamientos = new ArrayList<>();
        try {
            alojamientos = alojamientoDAO.listarAlojamientos();
        } catch (RuntimeException e) {
            System.out.println("No se pudieron cargar los alojamientos para el mapa: " + e.getMessage());
        }

        // Promedio de reseñas de cada alojamiento (si falla, el mapa igual se muestra, sin el promedio)
        Map<Integer, double[]> promedios = new HashMap<>();
        try {
            promedios = resenaDAO.promediosPorAlojamiento();
        } catch (Exception e) {
            System.out.println("No se pudieron cargar las reseñas para el mapa: " + e.getMessage());
        }

        request.setAttribute("datosMapa", armarJson(alojamientos, promedios));
        request.setAttribute("paginaActiva", "mapa");

        request.getRequestDispatcher("/WEB-INF/views/mapa.jsp")
                .forward(request, response);
    }

    // Arma los datos que usa el JavaScript del mapa, en formato JSON.
    private String armarJson(List<Alojamiento> alojamientos, Map<Integer, double[]> promedios) {
        StringBuilder json = new StringBuilder();
        json.append("{\"centro\":{\"lat\":").append(CENTRO_LATITUD)
                .append(",\"lng\":").append(CENTRO_LONGITUD).append("},");

        json.append("\"alojamientos\":[");
        for (int i = 0; i < alojamientos.size(); i++) {
            Alojamiento a = alojamientos.get(i);
            if (i > 0) {
                json.append(",");
            }
            json.append("{")
                    .append("\"id\":").append(a.getId()).append(",")
                    .append("\"nombre\":").append(texto(a.getNombre())).append(",")
                    .append("\"descripcion\":").append(texto(a.getDescripcion())).append(",")
                    .append("\"capacidad\":").append(a.getCapacidad()).append(",")
                    .append("\"precio\":").append(a.getPrecio()).append(",")
                    .append("\"ubicacion\":").append(texto(a.getUbicacion())).append(",")
                    // Reseñas: promedio con un decimal (0 si no tiene) y cantidad
                    .append("\"promedio\":").append(String.format(Locale.ROOT, "%.1f",
                            promedios.containsKey(a.getId()) ? promedios.get(a.getId())[0] : 0)).append(",")
                    .append("\"resenas\":").append(promedios.containsKey(a.getId()) ? (int) promedios.get(a.getId())[1] : 0).append(",")
                    // Sin coordenadas cargadas va null: ese alojamiento no se dibuja en el mapa
                    .append("\"lat\":").append(a.tieneCoordenadas() ? a.getLatitud() : null).append(",")
                    .append("\"lng\":").append(a.tieneCoordenadas() ? a.getLongitud() : null)
                    .append("}");
        }
        json.append("]}");

        return json.toString();
    }

    // Convierte un texto a string JSON, escapando lo que podría romper el JavaScript.
    private String texto(String valor) {
        if (valor == null) {
            return "\"\"";
        }
        StringBuilder sb = new StringBuilder("\"");
        for (char c : valor.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                case '<' -> sb.append("\\u003c"); // evita que un texto cierre el <script>
                case '>' -> sb.append("\\u003e");
                case '&' -> sb.append("\\u0026");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.append("\"").toString();
    }
}