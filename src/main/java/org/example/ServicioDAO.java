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
        String sql = "INSERT INTO servicio (id, nombre) VALUES(?,?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

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
    public boolean eliminarServicio(int id) throws SQLException {
        String sql = "DELETE FROM servicio WHERE id = ?";

        Connection conexion = ConexionBD.obtenerConexion();
        PreparedStatement sentencia = conexion.prepareStatement(sql);
        sentencia.setInt(1, id);

        return sentencia.executeUpdate() == 1;
    }
    public boolean editarServicio(int id, String nuevoNombre) throws SQLException {
        String sql = "UPDATE servicio SET nombre = ? WHERE id = ?";

        Connection conexion = ConexionBD.obtenerConexion();
        PreparedStatement sentencia = conexion.prepareStatement(sql);
        sentencia.setString(1, nuevoNombre);
        sentencia.setInt(2, id);

        return sentencia.executeUpdate() == 1;
    }
}

