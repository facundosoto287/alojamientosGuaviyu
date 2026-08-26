package org.example;

public class Alojamiento {

    private int id,capacidad;
    private String nombre,descripcion,ubicacion;
    private Double precio;
    private Float distanciaApiscina;


    public Alojamiento(int id, int capacidad, String nombre, String descripcion, String ubicacion, Double precio, Float distanciaApiscina) {
        this.id = id;
        this.capacidad = capacidad;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.precio = precio;
        this.distanciaApiscina = distanciaApiscina;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
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

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Float getDistanciaApiscina() {
        return distanciaApiscina;
    }

    public void setDistanciaApiscina(Float distanciaApiscina) {
        this.distanciaApiscina = distanciaApiscina;
    }
}
