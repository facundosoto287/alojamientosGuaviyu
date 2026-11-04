package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ObservacionClienteDAO {

    public void agregarObservacion(int clienteId, String descripcion) throws SQLException {

        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La observación no puede estar vacía.");
        }

        String sql = """
                INSERT INTO observacion_cliente (cliente_id, descripcion)
                VALUES (?, ?)
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, clienteId);
            sentencia.setString(2, descripcion.trim());

            sentencia.executeUpdate();
        }
    }


    public List<ObservacionCliente> listarPorCliente(int clienteId) throws SQLException {

        List<ObservacionCliente> observaciones = new ArrayList<>();

        String sql = """
                SELECT id, cliente_id, descripcion, fecha
                FROM observacion_cliente
                WHERE cliente_id = ?
                ORDER BY fecha DESC, id DESC
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, clienteId);

            try (ResultSet filas = sentencia.executeQuery()) {

                while (filas.next()) {
                    observaciones.add(mapearObservacion(filas));
                }
            }
        }

        return observaciones;
    }


    public boolean editarObservacion(int id, String descripcion) throws SQLException {

        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La observación no puede estar vacía.");
        }

        String sql = """
                UPDATE observacion_cliente
                SET descripcion = ?
                WHERE id = ?
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, descripcion.trim());
            sentencia.setInt(2, id);

            return sentencia.executeUpdate() == 1;
        }
    }


    public boolean eliminarObservacion(int id) throws SQLException {

        String sql = """
                DELETE FROM observacion_cliente
                WHERE id = ?
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() == 1;
        }
    }


    private ObservacionCliente mapearObservacion(ResultSet fila) throws SQLException {
        return new ObservacionCliente(fila.getInt("id"), fila.getInt("cliente_id"), fila.getString("descripcion"), fila.getTimestamp("fecha").toLocalDateTime());
    }
}