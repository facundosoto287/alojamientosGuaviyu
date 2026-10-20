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
       ¿TIENE ALGUNA ESTADÍA TERMINADA EN ESTE ALOJAMIENTO?
       (reserva CONFIRMADA, vinculada al cliente, con la fecha de salida ya pasada)
       ================================================== */

    public boolean tieneEstadiaTerminada(int idCliente, int idAlojamiento) throws SQLException {

        String sql = """
                SELECT 1
                FROM reserva r
                INNER JOIN cliente_reserva cr
                        ON cr.idReserva = r.id
                WHERE cr.idCliente = ?
                AND r.alojamiento_id = ?
                AND r.estado = 'CONFIRMADA'
                AND r.fechaSalida <= CURDATE()
                LIMIT 1
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idCliente);
            sentencia.setInt(2, idAlojamiento);

            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next();
            }
        }
    }


    /* ==================================================
       ¿PUEDE RESEÑAR AHORA?
       Una reseña POR ESTADÍA: puede opinar si tiene una estadía terminada
       que todavía no reseñó, o sea, una estadía cuya fecha de salida es
       posterior a su última reseña de este alojamiento.
       Ejemplo: se quedó en 2025 y opinó → no puede volver a opinar,
       hasta que vuelva en 2026 y termine esa nueva estadía.
       ================================================== */

    public boolean puedeResenar(int idCliente, int idAlojamiento) throws SQLException {

        String sql = """
                SELECT 1
                FROM reserva r
                INNER JOIN cliente_reserva cr
                        ON cr.idReserva = r.id
                WHERE cr.idCliente = ?
                AND r.alojamiento_id = ?
                AND r.estado = 'CONFIRMADA'
                AND r.fechaSalida <= CURDATE()
                AND r.fechaSalida > COALESCE(
                        (SELECT MAX(re.fecha)
                         FROM resena re
                         WHERE re.cliente_id = ?
                         AND re.alojamiento_id = ?),
                        '1000-01-01')
                LIMIT 1
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idCliente);
            sentencia.setInt(2, idAlojamiento);
            sentencia.setInt(3, idCliente);
            sentencia.setInt(4, idAlojamiento);

            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next();
            }
        }
    }


    /* ==================================================
       RESEÑAS DE UN ALOJAMIENTO (para la página de detalle)
       ================================================== */

    public List<Resena> listarPorAlojamiento(int idAlojamiento) throws SQLException {

        List<Resena> resenas = new ArrayList<>();

        String sql = """
                SELECT r.id,
                       r.cliente_id,
                       r.alojamiento_id,
                       r.comentario,
                       r.puntuacion,
                       r.fecha,
                       c.nombre AS nombreCliente
                FROM resena r
                INNER JOIN cliente c
                        ON c.id = r.cliente_id
                WHERE r.alojamiento_id = ?
                ORDER BY r.fecha DESC, r.id DESC
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idAlojamiento);

            try (ResultSet filas = sentencia.executeQuery()) {

                while (filas.next()) {

                    Resena resena = mapearResena(filas);

                    resena.setNombreCliente(filas.getString("nombreCliente"));

                    resenas.add(resena);
                }
            }
        }

        return resenas;
    }


    /* ==================================================
       PROMEDIO DE PUNTUACIÓN (0 si no tiene reseñas)
       ================================================== */

    public double promedioPorAlojamiento(int idAlojamiento) throws SQLException {

        String sql = """
                SELECT COALESCE(AVG(puntuacion), 0) AS promedio
                FROM resena
                WHERE alojamiento_id = ?
                """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idAlojamiento);

            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() ? fila.getDouble("promedio") : 0;
            }
        }
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