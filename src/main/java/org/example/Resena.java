package org.example;

import java.time.LocalDate;

public class Resena {

    private int id;
    private int cliente_id;
    private int alojamiento_id;
    private String comentario;
    private int puntuacion;
    private LocalDate fecha;

    /* Este campo viene del JOIN con alojamiento */
    private String nombreAlojamiento;

    /* Este campo viene del JOIN con cliente (quién escribió la reseña) */
    private String nombreCliente;


    public Resena() {
    }


    public Resena(String comentario,
                  int id,
                  int cliente_id,
                  int alojamiento_id,
                  int puntuacion,
                  LocalDate fecha) {

        this.comentario = comentario;
        this.id = id;
        this.cliente_id = cliente_id;
        this.alojamiento_id = alojamiento_id;
        this.puntuacion = puntuacion;
        this.fecha = fecha;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getCliente_id() {
        return cliente_id;
    }

    public void setCliente_id(int cliente_id) {
        this.cliente_id = cliente_id;
    }


    public int getAlojamiento_id() {
        return alojamiento_id;
    }

    public void setAlojamiento_id(int alojamiento_id) {
        this.alojamiento_id = alojamiento_id;
    }


    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }


    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }


    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /* La fecha lista para mostrar en la página: 12/10/2026 */
    public String getFechaFormateada() {
        return fecha == null ? "" : fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }


    public String getNombreAlojamiento() {
        return nombreAlojamiento;
    }

    public void setNombreAlojamiento(String nombreAlojamiento) {
        this.nombreAlojamiento = nombreAlojamiento;
    }


    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
}