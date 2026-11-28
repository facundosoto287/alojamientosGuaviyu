package org.example;

/** Una reserva junto con los datos que el panel necesita mostrar: alojamiento y cliente. */
public class ReservaDetalle {

    private final Reserva reserva;
    private final String alojamientoNombre;
    private final Integer clienteId;      // null si la reserva no tiene cliente vinculado
    private final String clienteNombre;
    private final String clienteDocumento;

    public ReservaDetalle(Reserva reserva, String alojamientoNombre, Integer clienteId,
                          String clienteNombre, String clienteDocumento) {
        this.reserva = reserva;
        this.alojamientoNombre = alojamientoNombre;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.clienteDocumento = clienteDocumento;
    }

    public Reserva getReserva() { return reserva; }
    public String getAlojamientoNombre() { return alojamientoNombre; }
    public Integer getClienteId() { return clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public String getClienteDocumento() { return clienteDocumento; }

    // Atajos para las JSP
    public int getId() { return reserva.getId(); }
    public java.sql.Date getFechaIngreso() { return reserva.getFechaIngreso(); }
    public java.sql.Date getFechaSalida() { return reserva.getFechaSalida(); }
    public int getCantidadHuespedes() { return reserva.getCantidadHuespedes(); }
    public EstadoReserva getEstado() { return reserva.getEstado(); }
    public java.sql.Date getFechaSolicitud() { return reserva.getFechaSolicitud(); }
    public int getAlojamientoId() { return reserva.getAlojamientoId(); }
    public boolean isMedioDiaExtra() { return reserva.isMedioDiaExtra(); }
    public boolean isColchonExtra() { return reserva.isColchonExtra() ;}

    public long getNoches() {
        return java.time.temporal.ChronoUnit.DAYS.between(
                getFechaIngreso().toLocalDate(), getFechaSalida().toLocalDate());
    }

    // ---- Precio ----

    /** Precio total que fijó el administrador, o null si la reserva usa el precio calculado. */
    public java.math.BigDecimal getPrecioAcordado() { return reserva.getPrecioAcordado(); }

    /**
     * Precio calculado de la estadía, con la misma cuenta que "Mis reservas" del cliente:
     * noches × precio del alojamiento, más media noche si pidió medio día extra y $ 200 por noche de colchón.
     */
    public double getPrecioCalculado() {
        double porNoche = reserva.getPrecioPorNoche();
        double total = getNoches() * porNoche + (isMedioDiaExtra() ? porNoche / 2 : 0);
        if (isColchonExtra()) {
            total += getNoches() * 200;
        }
        return total;
    }

    /** Lo que se cobra por la reserva: el precio acordado si hay uno; si no, el calculado. */
    public double getPrecioTotal() {
        return getPrecioAcordado() != null ? getPrecioAcordado().doubleValue() : getPrecioCalculado();
    }

    /** Precio total para mostrar: "$ 17.500". */
    public String getPrecioTexto() {
        return PlanillaDia.precioTexto(java.math.BigDecimal.valueOf(getPrecioTotal()));
    }
}