package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.example.Alojamiento;
import org.example.AlojamientoDAO;
import org.example.Cliente;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.example.Reserva;
import org.example.ReservaDao;
import org.example.EstadoReserva;
import java.time.format.DateTimeFormatter;
import org.example.CorreoPlantillas;
import org.example.CorreoServicio;

import java.sql.Date;

@WebServlet("/solicitar-reserva")
public class SolicitarReservaServlet extends HttpServlet {

    private final AlojamientoDAO alojamientoDAO = new AlojamientoDAO();
    private final ReservaDao reservaDao = new ReservaDao();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String alojamientoIdTexto = request.getParameter("alojamientoId");

        String fechaIngresoTexto = request.getParameter("fechaIngreso");

        String fechaSalidaTexto = request.getParameter("fechaSalida");

        String huespedesTexto = request.getParameter("cantidadHuespedes");


        /*Validar que lleguen los datos.*/
        if (alojamientoIdTexto == null || alojamientoIdTexto.isBlank() || fechaIngresoTexto == null || fechaIngresoTexto.isBlank() || fechaSalidaTexto == null || fechaSalidaTexto.isBlank() || huespedesTexto == null ||
                huespedesTexto.isBlank()
        ) {

            response.sendRedirect(request.getContextPath() + "/alojamientos");

            return;
        }


        int alojamientoId;
        int cantidadHuespedes;
        LocalDate fechaIngreso;
        LocalDate fechaSalida;


        try {

            alojamientoId = Integer.parseInt(alojamientoIdTexto);

            cantidadHuespedes = Integer.parseInt(huespedesTexto);

            fechaIngreso = LocalDate.parse(fechaIngresoTexto);

            fechaSalida = LocalDate.parse(fechaSalidaTexto);

        } catch (Exception e) {

            response.sendRedirect(request.getContextPath() + "/alojamientos");

            return;
        }


        /*Validaciones básicas.*/
        long cantidadNoches = ChronoUnit.DAYS.between(fechaIngreso, fechaSalida);

        if (cantidadHuespedes < 1 || cantidadNoches < 2) {
            response.sendRedirect(request.getContextPath() + "/alojamiento-detalle?id=" + alojamientoId);
            return;
        }

        /*Comprobar sesión.*/

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(LoginServlet.CLIENTE_SESION) == null) {

            String destino = "/solicitar-reserva"
                            + "?alojamientoId="
                            + alojamientoId
                            + "&fechaIngreso="
                            + fechaIngreso
                            + "&fechaSalida="
                            + fechaSalida
                            + "&cantidadHuespedes="
                            + cantidadHuespedes;

            String destinoCodificado = URLEncoder.encode(destino, StandardCharsets.UTF_8);

            response.sendRedirect(request.getContextPath()
                            + "/login?destino="
                            + destinoCodificado
                            + "&motivo=reserva"
            );

            return;
        }


        Cliente cliente = (Cliente) session.getAttribute(LoginServlet.CLIENTE_SESION);


        try {

            /*Traer alojamiento real. */

            Alojamiento alojamiento = alojamientoDAO.buscarPorIdALO(alojamientoId);


            if (alojamiento == null) {

                response.sendRedirect(request.getContextPath() + "/alojamientos");

                return;
            }


            /*Validar capacidad.*/

            if (cantidadHuespedes > alojamiento.getCapacidad()) {

                response.sendRedirect(request.getContextPath() + "/alojamiento-detalle?id=" + alojamientoId);

                return;
            }


            /*Calcular noches.*/

            cantidadNoches = ChronoUnit.DAYS.between(fechaIngreso, fechaSalida);


            /*Calcular precio.*/

            double precioPorNoche = alojamiento.getPrecioParaHuespedes(cantidadHuespedes);

            double precioTotal = precioPorNoche * cantidadNoches;


            /*Enviar datos al JSP.*/

            request.setAttribute("cliente", cliente);

            request.setAttribute("alojamiento", alojamiento);

            request.setAttribute("fechaIngreso", fechaIngreso);

            request.setAttribute("fechaSalida", fechaSalida);

            request.setAttribute("cantidadHuespedes", cantidadHuespedes);

            request.setAttribute("cantidadNoches", cantidadNoches);

            request.setAttribute("precioPorNoche", precioPorNoche);

            request.setAttribute("precioTotal", precioTotal);

            request.getRequestDispatcher("/WEB-INF/views/solicitar-reserva.jsp").forward(request, response);


        } catch (SQLException e) {

            throw new ServletException("Error al preparar la solicitud de reserva", e);
        }
    }
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        /*COMPROBAR SESION*/

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(LoginServlet.CLIENTE_SESION) == null) {

            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }


        Cliente cliente = (Cliente) session.getAttribute(LoginServlet.CLIENTE_SESION);


        /*LEER DATOS*/

        String alojamientoIdTexto = request.getParameter("alojamientoId");

        String fechaIngresoTexto = request.getParameter("fechaIngreso");

        String fechaSalidaTexto = request.getParameter("fechaSalida");

        String huespedesTexto = request.getParameter("cantidadHuespedes");

        String observaciones = request.getParameter("observaciones");

        String[] edades = request.getParameterValues("edad");

        boolean medioDiaExtra = request.getParameter("medioDiaExtra") != null;

        boolean colchonExtra = request.getParameter("colchonExtra") != null;


        try {

            int alojamientoId = Integer.parseInt(alojamientoIdTexto);

            int cantidadHuespedes = Integer.parseInt(huespedesTexto);


            LocalDate fechaIngreso = LocalDate.parse(fechaIngresoTexto);

            LocalDate fechaSalida = LocalDate.parse(fechaSalidaTexto);


            /*VALIDACIONES*/

            if (cantidadHuespedes < 1 || !fechaSalida.isAfter(fechaIngreso)) {

                response.sendRedirect(request.getContextPath() + "/alojamiento-detalle?id=" + alojamientoId);
                return;
            }

            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            String ingresoFormateado = fechaIngreso.format(formatoFecha);

            String salidaFormateada = fechaSalida.format(formatoFecha);


            Alojamiento alojamiento = alojamientoDAO.buscarPorIdALO(alojamientoId);


            if (alojamiento == null) {

                response.sendRedirect(request.getContextPath() + "/alojamientos");
                return;
            }

            if (colchonExtra && !alojamiento.isPermiteColchonExtra()) {
                response.sendRedirect(request.getContextPath() + "/alojamiento-detalle?id=" + alojamientoId);
                return;
            }

            if (cantidadHuespedes > alojamiento.getCapacidad()) {

                response.sendRedirect(request.getContextPath() + "/alojamiento-detalle?id=" + alojamientoId);
                return;
            }


            /*Validar edades*/

            if (edades == null || edades.length != cantidadHuespedes) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/solicitar-reserva"
                                + "?alojamientoId="
                                + alojamientoId
                                + "&fechaIngreso="
                                + fechaIngreso
                                + "&fechaSalida="
                                + fechaSalida
                                + "&cantidadHuespedes="
                                + cantidadHuespedes
                );

                return;
            }


            for (String edadTexto : edades) {

                int edad = Integer.parseInt(edadTexto);

                if (edad < 0 || edad > 120) {

                    response.sendRedirect(
                            request.getContextPath()
                                    + "/solicitar-reserva"
                                    + "?alojamientoId="
                                    + alojamientoId
                                    + "&fechaIngreso="
                                    + fechaIngreso
                                    + "&fechaSalida="
                                    + fechaSalida
                                    + "&cantidadHuespedes="
                                    + cantidadHuespedes
                    );
                    return;
                }
            }


            /*CREAR RESERVA*/

            Reserva reserva = new Reserva(
                            0,
                            Date.valueOf(fechaIngreso),
                            Date.valueOf(fechaSalida),
                            cantidadHuespedes,
                            EstadoReserva.PENDIENTE,
                            new Date(System.currentTimeMillis()), alojamientoId);


            reserva.setMedioDiaExtra(medioDiaExtra);
            reserva.setColchonExtra(colchonExtra);


            int idReserva = reservaDao.crearSolicitudReserva(reserva, cliente.getId());


            /*-1 significa que las fechas ya estaban ocupadas*/

            if (idReserva == -1) {
                response.sendRedirect(request.getContextPath() + "/alojamiento-detalle?id=" + alojamientoId);
                return;
            }

            try {
                String contenido = CorreoPlantillas.solicitudRecibida(cliente, alojamiento, idReserva, fechaIngreso, fechaSalida, cantidadHuespedes);

                CorreoServicio correoServicio = new CorreoServicio();

                correoServicio.enviar(cliente.getEmail(), "Solicitud de reserva recibida - Alojamientos Guaviyú", contenido);
            } catch (Exception e) {
                getServletContext().log("La solicitud #" + idReserva + " se creó, pero no se pudo enviar el correo.", e);
            }


            /*CALCULAR PRECIO*/

            long cantidadNoches = ChronoUnit.DAYS.between(fechaIngreso, fechaSalida);

            double precioPorNoche = alojamiento.getPrecioParaHuespedes(cantidadHuespedes);

            double precioTotal = precioPorNoche * cantidadNoches;

            if (medioDiaExtra) {
                precioTotal += precioPorNoche / 2;
            }
            if (colchonExtra) {
                precioTotal += cantidadNoches * 200;
            }


            /*ARMAR WHATSAPP*/

            StringBuilder mensaje = new StringBuilder();


            mensaje.append("Hola, quiero solicitar una reserva.\n\n");


            mensaje.append("Solicitud N.º ").append(idReserva).append("\n\n");


            mensaje.append("Alojamiento: ").append(alojamiento.getNombre()).append("\n");


            mensaje.append("Ingreso: ").append(ingresoFormateado).append(" - 12:00 pm\n");

            mensaje.append("Salida: ").append(salidaFormateada).append(medioDiaExtra ? " - hasta las 21:00 pm\n" : " - 10:00 am\n");


            mensaje.append("Huéspedes: ").append(cantidadHuespedes).append("\n");


            mensaje.append("Noches: ").append(cantidadNoches).append("\n");


            mensaje.append("Medio día adicional: ").append(medioDiaExtra ? "Sí" : "No").append("\n\n");
            mensaje.append("Colchón extra: ").append(colchonExtra ? "Sí" : "No").append("\n\n");

            /*Edades */

            mensaje.append("Edades de los huéspedes:\n");


            for (int i = 0; i < edades.length; i++) {

                mensaje.append("- Huésped ").append(i + 1).append(": ").append(edades[i]).append(" años\n");
            }


            /*Datos del cliente*/

            mensaje.append("\nCliente: ").append(cliente.getNombre());


            mensaje.append("\nCédula: ").append(java.util.Objects.toString(cliente.getDocumento(), "—"));


            mensaje.append("\nTeléfono: ").append(java.util.Objects.toString(cliente.getTelefono(), "—"));


            mensaje.append("\nCorreo: ").append(java.util.Objects.toString(cliente.getEmail(), "—"));


            mensaje.append("\nProcedencia: ").append(java.util.Objects.toString(cliente.getProcedencia(), "—"));


            /*Observaciones*/

            if (observaciones != null && !observaciones.isBlank()) {

                mensaje.append("\n\nObservaciones: ").append(observaciones.trim());
            }


            /*Precio*/

            mensaje.append("\n\nTotal estimado: $ ").append(String.format(java.util.Locale.US, "%.0f", precioTotal));


            /*REDIRECT A WHATSAPP*/
            /*
             * Uruguay:
             * 598 + número sin 0 inicial
             * Ejemplo:
             * 099 123 456
             * sería:
             * 59899123456
             */

            String telefonoAdministrador =
                    "59899933446";


            String mensajeCodificado =
                    URLEncoder.encode(
                            mensaje.toString(),
                            StandardCharsets.UTF_8
                    );


            String urlWhatsApp =
                    "https://wa.me/"
                            + telefonoAdministrador
                            + "?text="
                            + mensajeCodificado;


            response.sendRedirect(
                    urlWhatsApp
            );


        } catch (
                NumberFormatException |
                SQLException e
        ) {

            throw new ServletException(
                    "Error al enviar la solicitud de reserva",
                    e
            );
        }
    }
}