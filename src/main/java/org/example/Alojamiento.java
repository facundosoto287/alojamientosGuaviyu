package org.example;

public class Alojamiento {

    private int id;
    private String nombre;
    private String descripcion;
    private int capacidad;
    private double precio;
    private String ubicacion;
    private float DistanciaApiscina;

    public Alojamiento(int id, String nombre, String descripcion, int capacidad, double precio, String ubicacion, float distanciaApiscina) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.capacidad = capacidad;
        this.precio = precio;
        this.ubicacion = ubicacion;
        DistanciaApiscina = distanciaApiscina;
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

    public float getDistanciaApiscina() {
        return DistanciaApiscina;
    }

    public void setDistanciaApiscina(float distanciaApiscina) {
        DistanciaApiscina = distanciaApiscina;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
