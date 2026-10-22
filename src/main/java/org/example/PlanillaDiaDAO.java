package org.example;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Precios y colores de cada casa en cada día (tabla planilla_dia).
 * Se usan en Disponibilidad → Todas las casas.
 */
public class PlanillaDiaDAO {

    /**
     * Todo lo anotado entre "desde" (incluido) y "hasta" (excluido).
     * Resultado: id de la casa → (fecha → precio y color).
     */
    public Map<Integer, Map<LocalDate, PlanillaDia>> listar(LocalDate desde, LocalDate hasta) throws SQLException {
        String sql = """
                SELECT alojamiento_id, fecha, precio, color, nota
                FROM planilla_dia
                WHERE fecha >= ? AND fecha < ?
                """;

        Map<Integer, Map<LocalDate, PlanillaDia>> resultado = new HashMap<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setDate(1, Date.valueOf(desde));
            sentencia.setDate(2, Date.valueOf(hasta));

            try (ResultSet fila = sentencia.executeQuery()) {
                while (fila.next()) {
                    PlanillaDia dia = new PlanillaDia(
                            fila.getInt("alojamiento_id"),
                            fila.getDate("fecha").toLocalDate(),
                            fila.getBigDecimal("precio"),
                            fila.getString("color"),
                            fila.getString("nota"));
                    resultado.computeIfAbsent(dia.getAlojamientoId(), id -> new HashMap<>())
                            .put(dia.getFecha(), dia);
                }
            }
        }
        return resultado;
    }

    /**
     * Guarda la nota (observación) de una casa en un día; si el texto queda vacío, la borra.
     * Para una reserva se usa su casa y su día de ingreso.
     */
    public void guardarNota(int alojamientoId, LocalDate fecha, String texto) throws SQLException {
        String limpio = texto == null || texto.isBlank() ? null : texto.trim();
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            try (PreparedStatement guardar = conexion.prepareStatement("""
                    INSERT INTO planilla_dia (alojamiento_id, fecha, nota) VALUES (?, ?, ?)
                    ON DUPLICATE KEY UPDATE nota = VALUES(nota)""")) {
                guardar.setInt(1, alojamientoId);
                guardar.setDate(2, Date.valueOf(fecha));
                guardar.setString(3, limpio);
                guardar.executeUpdate();
            }
            // Si el casillero quedó sin precio, sin color y sin nota, se borra
            try (PreparedStatement limpiar = conexion.prepareStatement("""
                    DELETE FROM planilla_dia
                    WHERE alojamiento_id = ? AND fecha = ? AND precio IS NULL AND color IS NULL AND nota IS NULL""")) {
                limpiar.setInt(1, alojamientoId);
                limpiar.setDate(2, Date.valueOf(fecha));
                limpiar.executeUpdate();
            }
        }
    }

    /**
     * Anota precio y/o color en esas casas, del día "desde" al día "hasta" (los dos incluidos).
     *
     * @param cambiarPrecio false = dejar el precio que tenía cada día
     * @param precio        el precio nuevo (null = quitar el precio)
     * @param cambiarColor  false = dejar el color que tenía cada día
     * @param color         el color nuevo "#RRGGBB" (null = quitar el color)
     * @return cantidad de casilleros (casa + día) actualizados
     */
    public int guardar(List<Integer> casas, LocalDate desde, LocalDate hasta,
                       boolean cambiarPrecio, BigDecimal precio,
                       boolean cambiarColor, String color) throws SQLException {

        String sqlPrecio = """
                INSERT INTO planilla_dia (alojamiento_id, fecha, precio) VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE precio = VALUES(precio)
                """;
        String sqlColor = """
                INSERT INTO planilla_dia (alojamiento_id, fecha, color) VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE color = VALUES(color)
                """;
        // Los casilleros que quedaron sin precio, sin color y sin nota no hace falta guardarlos
        String sqlLimpiar = """
                DELETE FROM planilla_dia
                WHERE precio IS NULL AND color IS NULL AND nota IS NULL AND fecha BETWEEN ? AND ?
                """;

        int casilleros = 0;

        try (Connection conexion = ConexionBD.obtenerConexion()) {
            conexion.setAutoCommit(false);

            try (PreparedStatement conPrecio = conexion.prepareStatement(sqlPrecio);
                 PreparedStatement conColor = conexion.prepareStatement(sqlColor);
                 PreparedStatement limpiar = conexion.prepareStatement(sqlLimpiar)) {

                for (int casa : casas) {
                    for (LocalDate dia = desde; !dia.isAfter(hasta); dia = dia.plusDays(1)) {
                        if (cambiarPrecio) {
                            conPrecio.setInt(1, casa);
                            conPrecio.setDate(2, Date.valueOf(dia));
                            conPrecio.setBigDecimal(3, precio);
                            conPrecio.addBatch();
                        }
                        if (cambiarColor) {
                            conColor.setInt(1, casa);
                            conColor.setDate(2, Date.valueOf(dia));
                            conColor.setString(3, color);
                            conColor.addBatch();
                        }
                        casilleros++;
                    }
                }

                if (cambiarPrecio) {
                    conPrecio.executeBatch();
                }
                if (cambiarColor) {
                    conColor.executeBatch();
                }

                limpiar.setDate(1, Date.valueOf(desde));
                limpiar.setDate(2, Date.valueOf(hasta));
                limpiar.executeUpdate();

                conexion.commit();
            } catch (SQLException | RuntimeException e) {
                conexion.rollback();
                throw e;
            }
        }
        return casilleros;
    }
}