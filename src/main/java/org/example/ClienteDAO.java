package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClienteDAO {

    public Cliente registrarCliente(String nombre, String documento, String telefono, String email, String procedencia) {

        if (nombre.isBlank() || documento.isBlank() || email.isBlank()) {
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

        String sqlInsertar = "INSERT INTO cliente (nombre, documento, telefono, email, procedencia) VALUES (?,?,?,?,?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sqlInsertar, PreparedStatement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, nombre);
            sentencia.setString(2, documento);
            sentencia.setString(3, telefono);
            sentencia.setString(4, email);
            sentencia.setString(5, procedencia);

            sentencia.executeUpdate();

            int id = 0;
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    id = claves.getInt(1);
                }
            }

            return new Cliente(id, nombre, documento, telefono, email, procedencia);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Cliente> listarClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nombre, documento, telefono, email, procedencia FROM cliente ORDER BY nombre";

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
                        filas.getString("procedencia")
                );
                clientes.add(cliente);
            }
        }

        return clientes;
    }

    public boolean existeClientePorId(int id) {
        String sql = "SELECT id FROM cliente WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ---- Método que agrupa la lectura de datos por consola ----

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

        return new Cliente(0, nombre, documento, telefono, email, procedencia);
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, documento, telefono, email, procedencia FROM cliente WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet filas = sentencia.executeQuery()) {
                if (filas.next()) {
                    return new Cliente(
                            filas.getInt("id"),
                            filas.getString("nombre"),
                            filas.getString("documento"),
                            filas.getString("telefono"),
                            filas.getString("email"),
                            filas.getString("procedencia")
                    );
                }
            }
        }

        return null; // no existe ningún cliente con ese id
    }

    public List<Cliente> buscarPorNombreODocumento(String textoBusqueda) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nombre, documento, telefono, email, procedencia FROM cliente " +
                "WHERE nombre LIKE ? OR documento LIKE ? ORDER BY nombre";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            String comodin = "%" + textoBusqueda + "%";
            sentencia.setString(1, comodin);
            sentencia.setString(2, comodin);

            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    Cliente cliente = new Cliente(
                            filas.getInt("id"),
                            filas.getString("nombre"),
                            filas.getString("documento"),
                            filas.getString("telefono"),
                            filas.getString("email"),
                            filas.getString("procedencia")
                    );
                    clientes.add(cliente);
                }
            }
        }

        return clientes;
    }

    public boolean eliminarCliente(int id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() == 1;
        }
    }

}

