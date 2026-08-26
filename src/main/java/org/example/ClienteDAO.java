package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDAO {

    private Cliente clienteActual;

    // Caso de uso 1: Iniciar sesión
    // Valida email y documento. Si coinciden, guarda la sesión y devuelve el Cliente autenticado.
    // Si no coinciden, devuelve null.
    public Cliente iniciarSesion(String email, String documento) {
        String sql = "SELECT id, nombre, documento, telefono, email, procedencia FROM cliente WHERE email = ? AND documento = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, email);
            sentencia.setString(2, documento);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    Cliente cliente = new Cliente(
                            resultado.getInt("id"),
                            resultado.getString("nombre"),
                            resultado.getString("documento"),
                            resultado.getString("telefono"),
                            resultado.getString("email"),
                            resultado.getString("procedencia")
                    );
                    clienteActual = cliente; // guardo la sesión activa
                    return cliente;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null; // no se encontró un cliente con esas credenciales
    }

    // Caso de uso 2: Cerrar sesión
    public void cerrarSesion() {
        clienteActual = null;
    }
}
