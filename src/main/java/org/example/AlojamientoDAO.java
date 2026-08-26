package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AlojamientoDAO {

    // Caso de uso 2: Agregar alojamiento
    // Valida los datos básicos antes de insertar (capacidad y precio deben ser válidos).
    public boolean crear(Alojamiento alojamiento) {
        if (!esValido(alojamiento)) {
            System.out.println("Datos inválidos: la capacidad y el precio deben ser mayores a 0.");
            return false;
        }

        String sql = "INSERT INTO alojamiento (nombre, descripcion, capacidad, precio, ubicacion) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, alojamiento.getNombre());
            sentencia.setString(2, alojamiento.getDescripcion());
            sentencia.setInt(3, alojamiento.getCapacidad());
            sentencia.setDouble(4, alojamiento.getPrecio());
            sentencia.setString(5, alojamiento.getUbicacion());

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean esValido(Alojamiento alojamiento) {
        return alojamiento.getCapacidad() > 0 && alojamiento.getPrecio() > 0;
    }
}
