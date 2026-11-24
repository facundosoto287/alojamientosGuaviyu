package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ObservacionCliente {

    private int id;
    private int clienteId;
    private String descripcion;
    private LocalDateTime fecha;

    public ObservacionCliente(int id, int clienteId, String descripcion, LocalDateTime fecha) {
        this.id = id;
        this.clienteId = clienteId;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "ObservacionCliente{id=" + id +
                ", clienteId=" + clienteId +
                ", descripcion='" + descripcion + '\'' +
                ", fecha=" + fecha +
                '}';
    }

    public String getFechaFormateada() {
        if (fecha == null) {
            return "";
        }
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formato);
    }
}