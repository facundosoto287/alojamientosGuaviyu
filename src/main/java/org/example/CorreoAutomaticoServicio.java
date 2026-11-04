package org.example;

import java.util.List;

public class CorreoAutomaticoServicio {

    private final ReservaDao reservaDao;
    private final ClienteDAO clienteDAO;
    private final CorreoServicio correoServicio;

    public CorreoAutomaticoServicio() {
        this.reservaDao = new ReservaDao();
        this.clienteDAO = new ClienteDAO();
        this.correoServicio = new CorreoServicio();
    }

    public void procesarCorreos() {
        procesarRecordatorios();
        procesarSolicitudesResena();
    }

    private void procesarRecordatorios() {

        List<Reserva> reservas = reservaDao.listarParaRecordatorio();

        for (Reserva reserva : reservas) {
            try {

                Integer idCliente = reservaDao.buscarClienteIdPorReserva(reserva.getId());

                if (idCliente == null) {
                    continue;
                }

                Cliente cliente = clienteDAO.buscarPorId(idCliente);

                if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isBlank()) {continue;
                }

                ReservaDetalle detalle = reservaDao.obtenerDetallePorId(reserva.getId());

                if (detalle == null) {
                    continue;
                }

                correoServicio.enviar(cliente.getEmail(), "Tu estadía en Guaviyú se acerca", CorreoPlantillas.recordatorioEstadia(cliente, detalle));

                reservaDao.marcarRecordatorioEnviado(reserva.getId());

                System.out.println("Recordatorio enviado para reserva #" + reserva.getId());

            } catch (Exception e) {

                System.err.println("No se pudo enviar el recordatorio de la reserva #" + reserva.getId());

                e.printStackTrace();
            }
        }
    }

    private void procesarSolicitudesResena() {

        List<Reserva> reservas = reservaDao.listarParaSolicitarResena();

        for (Reserva reserva : reservas) {
            try {

                Integer idCliente = reservaDao.buscarClienteIdPorReserva(reserva.getId());

                if (idCliente == null) {
                    continue;
                }

                Cliente cliente = clienteDAO.buscarPorId(idCliente);

                if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isBlank()) {
                    continue;
                }

                ReservaDetalle detalle = reservaDao.obtenerDetallePorId(reserva.getId());

                if (detalle == null) {
                    continue;
                }

                String urlBase = System.getenv("APP_BASE_URL");

                if (urlBase == null || urlBase.isBlank()) {
                    urlBase = "http://localhost:8080/alojamientosGuaviyu_war_exploded";
                }

                String enlaceResena = urlBase + "/alojamiento-detalle?id=" + reserva.getAlojamientoId() + "#resenas";

                System.out.println(
                        "Reserva #" + reserva.getId()
                                + " - alojamientoId: " + reserva.getAlojamientoId()
                                + " - enlace: " + enlaceResena
                );

                correoServicio.enviar(cliente.getEmail(), "¿Cómo fue tu estadía en Guaviyú?", CorreoPlantillas.solicitarResena(cliente, detalle, enlaceResena));

                reservaDao.marcarCorreoResenaEnviado(reserva.getId());

                System.out.println("Correo de reseña enviado para reserva #" + reserva.getId());

            } catch (Exception e) {

                System.err.println("No se pudo enviar el correo de reseña de la reserva #" + reserva.getId());

                e.printStackTrace();
            }
        }
    }
}