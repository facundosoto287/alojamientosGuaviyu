package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                            resultado.getString("procedencia"),
                            resultado.getString("contrasenia")
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

    // Caso de uso 9: Cerrar sesión
    public void cerrarSesion() {
        clienteActual = null;
    }

    // Caso de uso 8: Registrar cliente
    public Cliente registrarCliente(String nombre, String documennto, String telefono, String email, String procedencia, String contrasena) {

        // Validar que no falten datos
        if (nombre.isBlank() || documennto.isBlank() || email.isBlank() || contrasena.isBlank()) {
            throw new IllegalArgumentException("Faltan datos obligatorios.");
        }

        // Validar que no exista ya un cliente con ese email o documento
        String sqlVerificar = "SELECT id FROM cliente WHERE email = ? OR documennto = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement verificar = conexion.prepareStatement(sqlVerificar)) {

            verificar.setString(1, email);
            verificar.setString(2, documennto);

            ResultSet resultado = verificar.executeQuery();
            if (resultado.next()) {
                throw new IllegalArgumentException("Ya existe un cliente con ese email o documento.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Insertar el nuevo cliente
        String sqlInsertar = "INSERT INTO cliente (nombre, documennto, telefono, email, procedencia, contrasena) VALUES (?,?,?,?,?,?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sqlInsertar, PreparedStatement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, nombre);
            sentencia.setString(2, documennto);
            sentencia.setString(3, telefono);
            sentencia.setString(4, email);
            sentencia.setString(5, procedencia);
            sentencia.setString(6, contrasena);

            sentencia.executeUpdate();

            ResultSet claves = sentencia.getGeneratedKeys();
            int id = 0;
            if (claves.next()) {
                id = claves.getInt(1);
            }

            return new Cliente(id, nombre, documennto, telefono, email, procedencia, contrasena);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Cliente> listarClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nombre, documento, telefono, email, procedencia, contrasena FROM cliente ORDER BY nombre";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {

            while (filas.next()) {
                Cliente cliente = new Cliente(
                        filas.getInt("id"),
                        filas.getString("nombre"),
                        filas.getString("documento"),
                        filas.getString("telefono"),
                        filas.getString("email"),
                        filas.getString("procedencia"),
                        filas.getString("contrasena")
                );
                clientes.add(cliente);
            }
        }

        return clientes;
    }
    // Método auxiliar: verifica si ya existe un cliente con ese correo o documento
    private boolean existeClientePorEmailODocumento(String email, String documennto) {
        String sql = "SELECT id FROM cliente WHERE email = ? OR documennto = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, email);
            sentencia.setString(2, documennto);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next(); // true si encontró al menos una fila
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
