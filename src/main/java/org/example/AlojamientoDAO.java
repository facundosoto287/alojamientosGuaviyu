package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AlojamientoDAO {

    // Caso de uso 2: Agregar Alojamiento
    // Valida que el administrador haya iniciado sesion, si inicio, le permite agregar un nuevo alojamiento al catalogo de la plataforma.
    // Si no coinciden, le pide que inicie sesion.

    public boolean agregarAlojamiento(Alojamiento alojamiento) {

        String sql = """
               
                INSERT INTO alojamiento (nombre, descripcion, capacidad, precio, ubicacion, distanciaApiscina)
               VALUES (?, ?, ?, ?, ?, ?)""";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)){

            sentencia.setString(1, alojamiento.getNombre());
            sentencia.setString(2,alojamiento.getDescripcion());
            sentencia.setInt(3, alojamiento.getCapacidad());
            sentencia.setDouble(4, alojamiento.getPrecio());
            sentencia.setString(5, alojamiento.getUbicacion());
            sentencia.setFloat(6, alojamiento.getDistanciaApiscina());

            int filasAfectadas = sentencia.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;

        }
    }

}
