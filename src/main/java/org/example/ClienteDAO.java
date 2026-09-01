package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClienteDAO {

    private Cliente clienteActual;

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
                    clienteActual = cliente;
                    return cliente;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public void cerrarSesion() {
        clienteActual = null;
    }

    public Cliente registrarCliente(String nombre, String documento, String telefono, String email, String procedencia, String contrasena) {

        if (nombre.isBlank() || documento.isBlank() || email.isBlank() || contrasena.isBlank()) {
            throw new IllegalArgumentException("Faltan datos obligatorios.");
        }

        String sqlVerificar = "SELECT id FROM cliente WHERE email = ? OR documento = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement verificar = conexion.prepareStatement(sqlVerificar)) {

            verificar.setString(1, email);
            verificar.setString(2, documento);

            try (ResultSet resultado = verificar.executeQuery()) {
                if (resultado.next()) {
                    throw new IllegalArgumentException("Ya existe un cliente con ese email o documento.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sqlInsertar = "INSERT INTO cliente (nombre, documento, telefono, email, procedencia, contrasena) VALUES (?,?,?,?,?,?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sqlInsertar, PreparedStatement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, nombre);
            sentencia.setString(2, documento);
            sentencia.setString(3, telefono);
            sentencia.setString(4, email);
            sentencia.setString(5, procedencia);
            sentencia.setString(6, contrasena);

            sentencia.executeUpdate();

            int id = 0;
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    id = claves.getInt(1);
                }
            }

            return new Cliente(id, nombre, documento, telefono, email, procedencia, contrasena);

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

    // ---- Métodos que agrupan la lectura de datos por consola ----

    // Pide los datos para un cliente NUEVO (sin id, porque es autoincremental)
    public Cliente pedirDatosNuevoCliente(Scanner sc) {
        System.out.println("Ingrese el nombre:");
        String nombre = sc.nextLine();

        System.out.println("Ingrese el documento:");
        String documento = sc.nextLine();

        System.out.println("Ingrese el teléfono:");
        String telefono = sc.nextLine();

        System.out.println("Ingrese el email:");
        String email = sc.nextLine();

        System.out.println("Ingrese la procedencia:");
        String procedencia = sc.nextLine();

        System.out.println("Ingrese una contraseña:");
        String contrasena = sc.nextLine();

        return new Cliente(0, nombre, documento, telefono, email, procedencia, contrasena);
    }
}
