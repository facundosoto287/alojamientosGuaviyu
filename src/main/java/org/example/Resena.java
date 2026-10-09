package org.example;

public class Resena {

    private String comentario;
    private int id,cliente_id,alojamiento_id,puntuacion;

    public Resena(String comentario, int id, int cliente_id, int alojamiento_id, int puntuacion) {
        this.comentario = comentario;
        this.id = id;
        this.cliente_id = cliente_id;
        this.alojamiento_id = alojamiento_id;
        this.puntuacion = puntuacion;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
