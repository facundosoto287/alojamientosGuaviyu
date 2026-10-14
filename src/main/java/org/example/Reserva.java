package org.example;

import java.sql.Date;

public class Reserva {

    private int id;
    private Date fechaIngreso;
    private Date fechaSalida;
    private int cantidadHuespedes;
    private EstadoReserva estado;
    private Date fechaSolicitud;
    private int alojamientoId;
    private String nombreAlojamiento;
    private String ubicacionAlojamiento;
    private double precioPorNoche;
    private int cantidadNoches;
    private double precioTotal;
    private boolean medioDiaExtra;

    public Reserva(int id, Date fechaIngreso, Date fechaSalida, int cantidadHuespedes,
                   EstadoReserva estado, Date fechaSolicitud, int alojamientoId) {
        this.id = id;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.cantidadHuespedes = cantidadHuespedes;
        this.estado = estado;
        this.fechaSolicitud = fechaSolicitud;
        this.alojamientoId = alojamientoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public int getCantidadHuespedes() {
        return cantidadHuespedes;
    }

    public void setCantidadHuespedes(int cantidadHuespedes) {
        this.cantidadHuespedes = cantidadHuespedes;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public int getAlojamientoId() {
        return alojamientoId;
    }

    public void setAlojamientoId(int alojamientoId) {
        this.alojamientoId = alojamientoId;
    }

    @Override
    public String toString() {
        return "Reserva{id=" + id + ", ingreso=" + fechaIngreso + ", salida=" + fechaSalida +
                ", huespedes=" + cantidadHuespedes + ", estado=" + estado +
                ", solicitado=" + fechaSolicitud + ", alojamientoId=" + alojamientoId + "}";
    }

    public String getNombreAlojamiento() {
        return nombreAlojamiento;
    }

    public void setNombreAlojamiento(String nombreAlojamiento) {
        this.nombreAlojamiento = nombreAlojamiento;
    }

    public String getUbicacionAlojamiento() {
        return ubicacionAlojamiento;
    }

    public void setUbicacionAlojamiento(String ubicacionAlojamiento) {
        this.ubicacionAlojamiento = ubicacionAlojamiento;
    }

    public double getPrecioPorNoche() {
        return precioPorNoche;
    }

    public void setPrecioPorNoche(double precioPorNoche) {
        this.precioPorNoche = precioPorNoche;
    }

    public int getCantidadNoches() {
        return cantidadNoches;
    }

    public void setCantidadNoches(int cantidadNoches) {
        this.cantidadNoches = cantidadNoches;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public boolean isMedioDiaExtra() {
        return medioDiaExtra;
    }

    public void setMedioDiaExtra(boolean medioDiaExtra) {
        this.medioDiaExtra = medioDiaExtra;
    }
}

