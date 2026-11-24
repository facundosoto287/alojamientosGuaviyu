package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServicioDAO {

    public boolean agregarServicio(Servicio servicio) throws SQLException {
        String sql = "INSERT INTO servicio (nombre) VALUES (?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, servicio.getNombre());

            return sentencia.executeUpdate() == 1;
        }
    }

    public List<Servicio> listarServicios() throws SQLException {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT id, nombre FROM servicio ORDER BY nombre";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {

            while (filas.next()) {
                Servicio servicio = new Servicio(
                        filas.getInt("id"),
                        filas.getString("nombre")
                );
                servicios.add(servicio);
            }
        }

        return servicios;
    }
    // Pide los datos para un servicio nuevo por consola
    public Servicio pedirDatosNuevoServicio(Scanner sc) {
        System.out.println("Ingrese el id del servicio:");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.println("Ingrese el nombre del servicio:");
        String nombre = sc.nextLine();

        return new Servicio(id, nombre);
    }
    /** Cantidad de alojamientos que tienen asociado este servicio. */
    public int contarAlojamientos(int idServicio) throws SQLException {
        String sql = "SELECT COUNT(*) FROM alojamiento_servicio WHERE servicio_id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idServicio);

            try (ResultSet filas = sentencia.executeQuery()) {
                return filas.next() ? filas.getInt(1) : 0;
            }
        }
    }

    /** true si ya hay un servicio con ese nombre (sin importar mayúsculas), ignorando el id indicado. */
    public boolean existeNombre(String nombre, int excluirId) throws SQLException {
        String sql = "SELECT 1 FROM servicio WHERE LOWER(nombre) = LOWER(?) AND id <> ? LIMIT 1";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, nombre);
            sentencia.setInt(2, excluirId);

            try (ResultSet filas = sentencia.executeQuery()) {
                return filas.next();
            }
        }
    }

    /**
     * Elimina el servicio junto con sus asociaciones a alojamientos, todo en una sola transacción
     * (antes fallaba por la clave foránea si el servicio estaba asociado).
     */
    public boolean eliminarServicio(int id) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try {
                try (PreparedStatement asociaciones = conexion.prepareStatement(
                        "DELETE FROM alojamiento_servicio WHERE servicio_id = ?")) {
                    asociaciones.setInt(1, id);
                    asociaciones.executeUpdate();
                }

                boolean eliminado;
                try (PreparedStatement sentencia = conexion.prepareStatement("DELETE FROM servicio WHERE id = ?")) {
                    sentencia.setInt(1, id);
                    eliminado = sentencia.executeUpdate() == 1;
                }

                conexion.commit();
                return eliminado;
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        }
    }
    public boolean editarServicio(int id, String nuevoNombre) throws SQLException {
        String sql = "UPDATE servicio SET nombre = ? WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, nuevoNombre);
            sentencia.setInt(2, id);

            return sentencia.executeUpdate() == 1;
        }
    }

    public Servicio buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre FROM servicio WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet filas = sentencia.executeQuery()) {
                if (filas.next()) {
                    return new Servicio(filas.getInt("id"), filas.getString("nombre"));
                }
            }
        }

        return null; // no existe ningún servicio con ese id
    }
}

