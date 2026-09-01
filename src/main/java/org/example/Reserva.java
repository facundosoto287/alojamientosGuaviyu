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
}
