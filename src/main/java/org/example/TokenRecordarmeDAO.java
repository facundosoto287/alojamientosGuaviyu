package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TokenRecordarmeDAO {

    public void guardar(int clienteId, String tokenHash, LocalDateTime expiracion)
            throws SQLException {

        String sql = """
                INSERT INTO token_recordarme
                (cliente_id, token_hash, fecha_expiracion)
                VALUES (?, ?, ?)
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, clienteId);
            sentencia.setString(2, tokenHash);
            sentencia.setTimestamp(
                    3,
                    java.sql.Timestamp.valueOf(expiracion)
            );

            sentencia.executeUpdate();
        }
    }

    public Integer buscarClienteId(String tokenHash) throws SQLException {

        String sql = """
                SELECT cliente_id
                FROM token_recordarme
                WHERE token_hash = ?
                  AND fecha_expiracion > NOW()
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, tokenHash);

            try (ResultSet fila = sentencia.executeQuery()) {

                if (fila.next()) {
                    return fila.getInt("cliente_id");
                }
            }
        }

        return null;
    }

    public void eliminar(String tokenHash) throws SQLException {

        String sql = """
                DELETE FROM token_recordarme
                WHERE token_hash = ?
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, tokenHash);
            sentencia.executeUpdate();
        }
    }

    public void eliminarExpirados() throws SQLException {

        String sql = """
                DELETE FROM token_recordarme
                WHERE fecha_expiracion <= NOW()
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.executeUpdate();
        }
    }
}