package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AlojamientoDAO {

    public boolean agregarAlojamiento(Alojamiento alojamiento) {

        String sql = """
                INSERT INTO alojamiento (nombre, descripcion, capacidad, precio, ubicacion, distanciaApiscina)
                VALUES (?, ?, ?, ?, ?, ?)""";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, alojamiento.getNombre());
            sentencia.setString(2, alojamiento.getDescripcion());
            sentencia.setInt(3, alojamiento.getCapacidad());
            sentencia.setDouble(4, alojamiento.getPrecio());
            sentencia.setString(5, alojamiento.getUbicacion());
            sentencia.setFloat(6, alojamiento.getDistanciaApiscina());

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Alojamiento> listarAlojamientos() {
        List<Alojamiento> alojamientos = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, capacidad, precio, ubicacion, distanciaApiscina FROM alojamiento";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                alojamientos.add(mapearAlojamiento(resultado));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alojamientos;
    }

    public boolean modificarAlojamiento(Alojamiento alojamiento) {

        String sql = """
                UPDATE alojamiento
                SET nombre = ?, descripcion = ?, capacidad = ?, precio = ?, ubicacion = ?, distanciaApiscina = ?
                WHERE id = ?""";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, alojamiento.getNombre());
            sentencia.setString(2, alojamiento.getDescripcion());
            sentencia.setInt(3, alojamiento.getCapacidad());
            sentencia.setDouble(4, alojamiento.getPrecio());
            sentencia.setString(5, alojamiento.getUbicacion());
            sentencia.setFloat(6, alojamiento.getDistanciaApiscina());
            sentencia.setInt(7, alojamiento.getId());

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarAlojamiento(int id) {
        String sql = "DELETE FROM alojamiento WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)){

            sentencia.setInt(1, id);

            int filasAeliminar = sentencia.executeUpdate();

            return filasAeliminar > 0;
        }catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private Alojamiento mapearAlojamiento(ResultSet resultado) throws SQLException {
        return new Alojamiento(
                resultado.getInt("id"),
                resultado.getString("nombre"),
                resultado.getString("descripcion"),
                resultado.getInt("capacidad"),
                resultado.getDouble("precio"),
                resultado.getString("ubicacion"),
                resultado.getFloat("distanciaApiscina")
        );
    }

    // ---- Métodos que agrupan la lectura de datos por consola ----

    // Pide los datos para un alojamiento NUEVO (sin id, porque es autoincremental)
    public Alojamiento pedirDatosNuevoAlojamiento(Scanner sc) {
        System.out.println("Ingrese el nombre del alojamiento:");
        String nombre = sc.nextLine();

        System.out.println("Ingrese la descripción:");
        String descripcion = sc.nextLine();

        System.out.println("Ingrese la capacidad (personas):");
        int capacidad = sc.nextInt();

        System.out.println("Ingrese el precio:");
        double precio = sc.nextDouble();
        sc.nextLine();

        System.out.println("Ingrese la ubicación:");
        String ubicacion = sc.nextLine();

        System.out.println("Ingrese la distancia a la piscina (metros):");
        float distanciaApiscina = sc.nextFloat();
        sc.nextLine();

        return new Alojamiento(0, nombre, descripcion, capacidad, precio, ubicacion, distanciaApiscina);
    }

    // Pide los datos para MODIFICAR un alojamiento existente (incluye id)
    public Alojamiento pedirDatosModificarAlojamiento(Scanner sc) {
        System.out.println("Ingrese el id del alojamiento a modificar:");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.println("Ingrese el nuevo nombre:");
        String nombre = sc.nextLine();

        System.out.println("Ingrese la nueva descripción:");
        String descripcion = sc.nextLine();

        System.out.println("Ingrese la nueva capacidad:");
        int capacidad = sc.nextInt();

        System.out.println("Ingrese el nuevo precio:");
        double precio = sc.nextDouble();
        sc.nextLine();

        System.out.println("Ingrese la nueva ubicación:");
        String ubicacion = sc.nextLine();

        System.out.println("Ingrese la nueva distancia a la piscina:");
        float distanciaApiscina = sc.nextFloat();
        sc.nextLine();

        return new Alojamiento(id, nombre, descripcion, capacidad, precio, ubicacion, distanciaApiscina);
    }
}
