package org.example;

public class Cliente {
    private int id;
    private String nombre,documennto,telefono,email,procedencia;

    public Cliente(int id, String nombre, String documennto, String telefono, String email, String procedencia) {
        this.id = id;
        this.nombre = nombre;
        this.documennto = documennto;
        this.telefono = telefono;
        this.email = email;
        this.procedencia = procedencia;
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

    public String getDocumennto() {
        return documennto;
    }

    public void setDocumennto(String documennto) {
        this.documennto = documennto;
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
}
