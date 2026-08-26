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
// Valida datos obligatorios, verifica que el correo y documento no estén repetidos,
// crea el cliente en la base de datos y devuelve el Cliente creado.
// Si falla alguna validación, lanza una excepción con el motivo.
    public Cliente registrarCliente(String nombre, String documennto, String telefono,
                                    String email, String procedencia, String contrasena) {

        // 1. Validar que los datos requeridos estén completos
        if (nombre == null || nombre.isBlank()
                || documennto == null || documennto.isBlank()
                || email == null || email.isBlank()
                || contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("Faltan datos obligatorios para el registro.");
        }

        // 2. Verificar que el correo y el documento no estén registrados previamente
        if (existeClientePorEmailODocumento(email, documennto)) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con ese correo o documento.");
        }

        // 3. Crear el nuevo cliente en la base de datos
        String sql = "INSERT INTO cliente (nombre, documennto, telefono, email, procedencia, contrasena) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, nombre);
            sentencia.setString(2, documennto);
            sentencia.setString(3, telefono);
            sentencia.setString(4, email);
            sentencia.setString(5, procedencia);
            sentencia.setString(6, contrasena);

            int filasAfectadas = sentencia.executeUpdate();
            if (filasAfectadas == 0) {
                throw new RuntimeException("No se pudo registrar el cliente.");
            }

            int idGenerado = 0;
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    idGenerado = claves.getInt(1);
                }
            }

            return new Cliente(idGenerado, nombre, documennto, telefono, email, procedencia, contrasena);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
