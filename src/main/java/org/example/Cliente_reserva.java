package org.example;

public class Cliente_reserva {

    private String idCliente,idReserva;
    private boolean hizoReserva;

    public Cliente_reserva(String idCliente, String idReserva, boolean hizoReserva) {
        this.idCliente = idCliente;
        this.idReserva = idReserva;
        this.hizoReserva = hizoReserva;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public boolean isHizoReserva() {
        return hizoReserva;
    }

    public void setHizoReserva(boolean hizoReserva) {
        this.hizoReserva = hizoReserva;
    }
}
