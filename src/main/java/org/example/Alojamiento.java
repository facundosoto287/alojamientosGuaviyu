package org.example;

public class Alojamiento {

    private int id;
    private String nombre;
    private String descripcion;
    private int capacidad;
    private double precio;
    private String ubicacion;
    private float distanciaApiscina;
    private boolean permiteMascotas;

    // Coordenadas para el mapa (null si todavía no se cargaron)
    private Double latitud;
    private Double longitud;

    public Alojamiento(int id, String nombre, String descripcion, int capacidad, double precio, String ubicacion, float distanciaApiscina) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.capacidad = capacidad;
        this.precio = precio;
        this.ubicacion = ubicacion;
        this.distanciaApiscina = distanciaApiscina;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public float getDistanciaApiscina() {
        return distanciaApiscina;
    }

    public void setDistanciaApiscina(float distanciaApiscina) {
        this.distanciaApiscina = distanciaApiscina;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public boolean getPermiteMascotas() {return permiteMascotas;}

    public void setPermiteMascotas(boolean permiteMascotas) {this.permiteMascotas = permiteMascotas;}

    // true si el alojamiento tiene ubicación cargada para mostrarse en el mapa
    public boolean tieneCoordenadas() {
        return latitud != null && longitud != null;
    }

    @Override
    public String toString() {
        return "Alojamiento{id=" + id + ", nombre='" + nombre + "', descripcion='" + descripcion +
                "', capacidad=" + capacidad + ", precio=" + precio + ", ubicacion='" + ubicacion +
                "', distanciaApiscina=" + distanciaApiscina + "}";
    }
}