package org.example;

import java.util.Date;


public class Reserva {

    private int id,cantidadHuesedes;
    private Date fechaIngreso,fechaSalida,fechaSolucitud;
    private EstadoReserva estado;

    public Reserva(int id, int cantidadHuesedes, Date fechaIngreso, Date fechaSalida, Date fechaSolucitud, EstadoReserva estado) {
        this.id = id;
        this.cantidadHuesedes = cantidadHuesedes;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.fechaSolucitud = fechaSolucitud;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidadHuesedes() {
        return cantidadHuesedes;
    }

    public void setCantidadHuesedes(int cantidadHuesedes) {
        this.cantidadHuesedes = cantidadHuesedes;
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

    public Date getFechaSolucitud() {
        return fechaSolucitud;
    }

    public void setFechaSolucitud(Date fechaSolucitud) {
        this.fechaSolucitud = fechaSolucitud;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }
}
