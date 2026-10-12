package org.example;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResenaDAO {


    /* ==================================================
       LISTAR RESEÑAS DE UN CLIENTE
       ================================================== */

    public List<Resena> listarPorCliente(int idCliente) throws SQLException {

        List<Resena> resenas = new ArrayList<>();

        String sql = """
                SELECT r.id,
                       r.cliente_id,
                       r.alojamiento_id,
                       r.comentario,
                       r.puntuacion,
                       r.fecha,
                       a.nombre AS nombreAlojamiento
                FROM resena r
                INNER JOIN alojamiento a
                        ON a.id = r.alojamiento_id
                WHERE r.cliente_id = ?
                ORDER BY r.fecha DESC
                """;


        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idCliente);


            try (ResultSet filas = sentencia.executeQuery()) {

                while (filas.next()) {

                    Resena resena = mapearResena(filas);

                    resena.setNombreAlojamiento(filas.getString("nombreAlojamiento"));

                    resenas.add(resena);
                }
            }
        }

        return resenas;
    }


    /* ==================================================
       CREAR RESEÑA
       ================================================== */

    public boolean crearResena(Resena resena) throws SQLException {

        String sql = """
                INSERT INTO resena
                (cliente_id, alojamiento_id, comentario, puntuacion, fecha)
                VALUES (?, ?, ?, ?, ?)
                """;


        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(
                    1,
                    resena.getCliente_id()
            );

            sentencia.setInt(
                    2,
                    resena.getAlojamiento_id()
            );

            sentencia.setString(
                    3,
                    resena.getComentario()
            );

            sentencia.setInt(
                    4,
                    resena.getPuntuacion()
            );

            sentencia.setDate(
                    5,
                    Date.valueOf(resena.getFecha())
            );

            return sentencia.executeUpdate() == 1;
        }
    }


    /* ==================================================
       EDITAR RESEÑA
       ================================================== */

    public boolean editarResena(Resena resena) throws SQLException {

        String sql = """
                UPDATE resena
                SET comentario = ?,
                    puntuacion = ?
                WHERE id = ?
                AND cliente_id = ?
                """;


        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(
                    1,
                    resena.getComentario()
            );

            sentencia.setInt(
                    2,
                    resena.getPuntuacion()
            );

            sentencia.setInt(
                    3,
                    resena.getId()
            );

            sentencia.setInt(
                    4,
                    resena.getCliente_id()
            );

            return sentencia.executeUpdate() == 1;
        }
    }


    /* ==================================================
       ELIMINAR RESEÑA
       ================================================== */

    public boolean eliminarResena(int idResena,
                                  int idCliente) throws SQLException {

        String sql = """
                DELETE FROM resena
                WHERE id = ?
                AND cliente_id = ?
                """;


        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idResena);
            sentencia.setInt(2, idCliente);

            return sentencia.executeUpdate() == 1;
        }
    }


    /* ==================================================
       BUSCAR RESEÑA POR ID
       ================================================== */

    public Resena buscarPorId(int idResena,
                              int idCliente) throws SQLException {

        String sql = """
                SELECT r.id,
                       r.cliente_id,
                       r.alojamiento_id,
                       r.comentario,
                       r.puntuacion,
                       r.fecha,
                       a.nombre AS nombreAlojamiento
                FROM resena r
                INNER JOIN alojamiento a
                        ON a.id = r.alojamiento_id
                WHERE r.id = ?
                AND r.cliente_id = ?
                """;


        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idResena);
            sentencia.setInt(2, idCliente);


            try (ResultSet fila = sentencia.executeQuery()) {

                if (fila.next()) {

                    Resena resena = mapearResena(fila);

                    resena.setNombreAlojamiento(
                            fila.getString("nombreAlojamiento")
                    );

                    return resena;
                }
            }
        }

        return null;
    }


    /* ==================================================
       MAPEO
       ================================================== */

    private Resena mapearResena(ResultSet fila) throws SQLException {

        Resena resena = new Resena();

        resena.setId(
                fila.getInt("id")
        );

        resena.setCliente_id(
                fila.getInt("cliente_id")
        );

        resena.setAlojamiento_id(
                fila.getInt("alojamiento_id")
        );

        resena.setComentario(
                fila.getString("comentario")
        );

        resena.setPuntuacion(
                fila.getInt("puntuacion")
        );

        Date fecha = fila.getDate("fecha");

        if (fecha != null) {
            resena.setFecha(
                    fecha.toLocalDate()
            );
        }

        return resena;
    }
}