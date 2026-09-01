package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReservaDao {

    public boolean confirmarReserva(int id){

        String sql = """
            UPDATE reserva
            SET estado = 'CONFIRMADA'
            WHERE id = ?
            AND estado = 'PENDIENTE'
            """;

        try(Connection conexion = ConexionBD.obtenerConexion();
            PreparedStatement sentencia = conexion.prepareStatement(sql)){

            sentencia.setInt(1, id);

            int filasAfectadas = sentencia.executeUpdate();

            return filasAfectadas > 0;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
