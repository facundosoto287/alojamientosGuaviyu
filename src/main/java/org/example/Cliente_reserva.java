package org.example;

public class Cliente_reserva {

    private String idCliente, idReserva;

    public Cliente_reserva(String idCliente, String idReserva) {
        this.idCliente = idCliente;
        this.idReserva = idReserva;
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
}
