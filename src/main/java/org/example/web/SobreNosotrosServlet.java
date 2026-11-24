package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

// Página "Sobre nosotros": arma los datos del equipo y se los pasa al JSP.
@WebServlet("/sobre-nosotros")
public class SobreNosotrosServlet extends HttpServlet {

    // Datos de cada integrante del equipo (lo que se muestra en cada tarjeta)
    public static class MiembroEquipo {
        private final String nombre;
        private final String cargo;
        private final String lugar;
        private final String foto;

        public MiembroEquipo(String nombre, String cargo, String lugar, String foto) {
            this.nombre = nombre;
            this.cargo = cargo;
            this.lugar = lugar;
            this.foto = foto;
        }

        public String getNombre() { return nombre; }
        public String getCargo() { return cargo; }
        public String getLugar() { return lugar; }
        public String getFoto() { return foto; }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        List<MiembroEquipo> equipo = List.of(
                new MiembroEquipo(
                        "Alejandra Depratti",
                        "Gerenta",
                        "Alojamiento Guaviyú & Galería Guaviyú",
                        "img/equipo/alejandra.jpg"
                ),
                new MiembroEquipo(
                        "Antonio Bacci",
                        "Encargado de mantenimiento",
                        "Alojamiento Guaviyú",
                        "img/equipo/antonio.jpg"
                ),
                new MiembroEquipo(
                        "Joaquín Bacci",
                        "Recepcionista y Encargado tienda",
                        "Alojamiento Guaviyú & Galería Guaviyú",
                        "img/equipo/joaquin.jpg"
                )
        );

        request.setAttribute("equipo", equipo);
        request.setAttribute("paginaActiva", "sobre-nosotros");

        request.getRequestDispatcher("/WEB-INF/views/sobreNosotros.jsp")
                .forward(request, response);
    }
}