package org.example;

public class Cliente {

    private int id;
    private String nombre;
    private String documento;
    private String telefono;
    private String email;
    private String procedencia;

    // Guarda el HASH BCrypt de la contraseña (ej: "$2a$12$..."), nunca la contraseña en texto.
    // Al guardar el cliente en la sesión web se pone en null para no tenerlo en memoria sin necesidad.
    private String contrasena;

    // true si el cliente todavía usa la contraseña por defecto y debe cambiarla antes de seguir navegando
    private boolean debeCambiarContrasena;

    public Cliente(int id, String nombre, String documento, String telefono, String email, String procedencia, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.telefono = telefono;
        this.email = email;
        this.procedencia = procedencia;
        this.contrasena = contrasena;
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

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProcedencia() {
        return procedencia;
    }

    public void setProcedencia(String procedencia) {
        this.procedencia = procedencia;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public boolean isDebeCambiarContrasena() {
        return debeCambiarContrasena;
    }

    public void setDebeCambiarContrasena(boolean debeCambiarContrasena) {
        this.debeCambiarContrasena = debeCambiarContrasena;
    }

    /** Primer nombre, para el saludo del encabezado ("Hola, Brandon"). */
    public String getPrimerNombre() {
        if (nombre == null || nombre.isBlank()) {
            return "";
        }
        return nombre.trim().split("\\s+")[0];
    }

    @Override
    public String toString() {
        return "Cliente{id=" + id + ", nombre='" + nombre + "', documento='" + documento +
                "', telefono='" + telefono + "', email='" + email + "', procedencia='" + procedencia + "'}";
    }
}
