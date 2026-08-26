package org.example;

public class AlojamientoServicio {

    private int alojamientoId;
    private int servicioId;

    public AlojamientoServicio(int alojamientoId, int servicioId) {
        this.alojamientoId = alojamientoId;
        this.servicioId = servicioId;
    }

    public int getAlojamientoId() {
        return alojamientoId;
    }

    public void setAlojamientoId(int alojamientoId) {
        this.alojamientoId = alojamientoId;
    }

    public int getServicioId() {
        return servicioId;
    }

    public void setServicioId(int servicioId) {
        this.servicioId = servicioId;
    }

}
