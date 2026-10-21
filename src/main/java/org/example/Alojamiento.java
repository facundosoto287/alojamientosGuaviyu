package org.example;

import java.util.ArrayList;
import java.util.List;

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

    // Fotos en Cloudinary: identificadores (public_id) separados por "|". La primera es la portada.
    private String imagenes;

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

    /** Texto tal como se guarda en la columna "imagenes" (null si no hay fotos). */
    public String getImagenes() {
        return imagenes;
    }

    public void setImagenes(String imagenes) {
        this.imagenes = imagenes;
    }

    /** Identificadores de las fotos, en orden (la primera es la portada). Nunca es null. */
    public List<String> getListaImagenes() {
        return separarImagenes(imagenes);
    }

    /** Identificador de la foto de portada, o null si no hay fotos. */
    public String getPortada() {
        List<String> lista = getListaImagenes();
        return lista.isEmpty() ? null : lista.get(0);
    }

    /** "a|b|c" a [a, b, c]. Ignora vacíos y espacios. */
    public static List<String> separarImagenes(String texto) {
        List<String> lista = new ArrayList<>();
        if (texto != null) {
            for (String parte : texto.split("\\|")) {
                if (!parte.isBlank()) {
                    lista.add(parte.trim());
                }
            }
        }
        return lista;
    }

    /** [a, b, c] a "a|b|c", o null si la lista está vacía (así la columna queda NULL). */
    public static String unirImagenes(List<String> lista) {
        return lista == null || lista.isEmpty() ? null : String.join("|", lista);
    }

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