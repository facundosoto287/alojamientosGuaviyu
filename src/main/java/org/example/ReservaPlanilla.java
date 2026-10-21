package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Una reserva tal como se muestra en la Planilla de ocupación del panel:
 * los datos de ReservaDetalle (alojamiento y cliente) más el teléfono del cliente, el medio día extra,
 * y textos ya formateados para la página.
 */
public class ReservaPlanilla extends ReservaDetalle {

    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String clienteTelefono;

    public ReservaPlanilla(Reserva reserva, String alojamientoNombre, Integer clienteId,
                           String clienteNombre, String clienteDocumento, String clienteTelefono) {
        super(reserva, alojamientoNombre, clienteId, clienteNombre, clienteDocumento);
        this.clienteTelefono = clienteTelefono;
    }

    public String getClienteTelefono() {
        return clienteTelefono;
    }

    /** Nombre para mostrar en la planilla (las reservas viejas pueden no tener cliente vinculado). */
    public String getNombreMostrar() {
        String nombre = getClienteNombre();
        return (nombre == null || nombre.isBlank()) ? "Sin cliente" : nombre;
    }

    public LocalDate getIngreso() {
        return getFechaIngreso().toLocalDate();
    }

    public LocalDate getSalida() {
        return getFechaSalida().toLocalDate();
    }

    public String getIngresoTexto() {
        return getIngreso().format(DIA);
    }

    public String getSalidaTexto() {
        return getSalida().format(DIA);
    }

    /** Fecha de salida corta para el bloque de la planilla: 22/10 */
    public String getSalidaCorta() {
        return getSalida().format(DateTimeFormatter.ofPattern("dd/MM"));
    }

    /** true si pidió medio día extra: se va más tarde y ese día no se puede volver a alquilar. */
    public boolean isMedioDiaExtra() {
        return getReserva().isMedioDiaExtra();
    }

    /**
     * Primer día libre después de esta reserva (no incluido en la ocupación).
     * Sin medio día extra es el día de salida; con medio día extra, el día siguiente.
     */
    public LocalDate getFinOcupacion() {
        return isMedioDiaExtra() ? getSalida().plusDays(1) : getSalida();
    }

    /** ¿Esta reserva ocupa ese día? */
    public boolean ocupa(LocalDate dia) {
        return !dia.isBefore(getIngreso()) && dia.isBefore(getFinOcupacion());
    }

    public long getNoches() {
        return ChronoUnit.DAYS.between(getIngreso(), getSalida());
    }

    public boolean isConfirmada() {
        return getEstado() == EstadoReserva.CONFIRMADA;
    }

    /**
     * Teléfono listo para un link de WhatsApp (https://wa.me/...): 099 123 456 → 59899123456.
     * Devuelve "" si no hay teléfono o no tiene el formato de un celular uruguayo.
     */
    public String getTelefonoWhatsapp() {
        if (clienteTelefono == null) {
            return "";
        }
        String numeros = clienteTelefono.replaceAll("\\D", "");
        if (numeros.matches("09\\d{7}")) {
            return "598" + numeros.substring(1);
        }
        if (numeros.matches("5989\\d{7}")) {
            return numeros;
        }
        return "";
    }
}