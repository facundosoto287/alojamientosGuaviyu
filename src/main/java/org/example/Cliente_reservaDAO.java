package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Cliente_reservaDAO {

    public boolean crear(Cliente_reserva relacion) {
        String sql = "INSERT INTO cliente_reserva (idCliente, idReserva) VALUES (?, ?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, relacion.getIdCliente());
            sentencia.setString(2, relacion.getIdReserva());

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
