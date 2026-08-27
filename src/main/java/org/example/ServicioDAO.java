package org.example;

import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServicioDAO {

    public boolean agregarServicio(Servicio servicio) throws SQLException {
        try {
            Connection conexion = ConexionBD.obtenerConexion();
            String sql = "INSERT INTO servicio (id, nombre) VALUES(?,?)";
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setInt(1, servicio.getId());
            sentencia.setString(2, servicio.getNombre());

            return sentencia.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Servicio> listarServicios() throws SQLException {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT id, nombre FROM servicio ORDER BY nombre";

        Connection conexion = ConexionBD.obtenerConexion();
        PreparedStatement sentencia = conexion.prepareStatement(sql);
        ResultSet filas = sentencia.executeQuery();

        while (filas.next()) {
            Servicio servicio = new Servicio(
                    filas.getInt("id"),
                    filas.getString("nombre")
            );
            servicios.add(servicio);
        }

        return servicios;
    }

}
