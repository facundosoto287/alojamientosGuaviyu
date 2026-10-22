package org.example;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Señas a favor (tabla "sena"). Los datos del cliente y la casa se traen de la reserva. */
public class SenaDAO {

    private static final String SELECT = """
            SELECT s.id, s.reserva_id, s.importe, s.usada,
                   r.fechaIngreso, a.nombre AS alojamiento,
                   c.id AS cliente_id, c.nombre AS cliente, c.documento, c.telefono
            FROM sena s
            JOIN reserva r ON r.id = s.reserva_id
            LEFT JOIN alojamiento a ON a.id = r.alojamiento_id
            LEFT JOIN cliente_reserva cr ON cr.idReserva = r.id
            LEFT JOIN cliente c ON c.id = cr.idCliente
            """;

    /**
     * Todas las señas, las que vencen antes primero.
     * texto (opcional): parte del nombre o del documento del cliente.
     */
    public List<Sena> listar(String texto) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT);
        boolean filtrar = texto != null && !texto.isBlank();
        if (filtrar) {
            sql.append(" WHERE (c.nombre LIKE ? OR c.documento LIKE ?)");
        }
        sql.append(" ORDER BY s.usada, r.fechaIngreso, s.id");

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql.toString())) {
            if (filtrar) {
                String comodin = "%" + texto.trim() + "%";
                sentencia.setString(1, comodin);
                sentencia.setString(2, comodin);
            }
            return leer(sentencia);
        }
    }

    /**
     * Señas vigentes (sin usar y sin vencer) agrupadas por cliente, la que vence antes primero.
     * Se usa en "Nueva reserva" para avisar que el cliente tiene plata a favor.
     */
    public Map<Integer, Sena> vigentePorCliente() throws SQLException {
        String sql = SELECT
                + " WHERE s.usada = 0"
                + " AND DATE_ADD(r.fechaIngreso, INTERVAL " + Sena.MESES_VALIDEZ + " MONTH) >= CURDATE()"
                + " AND c.id IS NOT NULL"
                + " ORDER BY r.fechaIngreso, s.id";
        Map<Integer, Sena> porCliente = new LinkedHashMap<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            for (Sena sena : leer(sentencia)) {
                porCliente.putIfAbsent(sena.getClienteId(), sena);
            }
        }
        return porCliente;
    }

    /**
     * Cancela una reserva CONFIRMADA y guarda su seña a favor del cliente, todo junto.
     * Lanza IllegalArgumentException con un mensaje para mostrar si no se puede.
     */
    public void cancelarGuardandoSena(int reservaId, BigDecimal importe) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try {
                try (PreparedStatement leer = conexion.prepareStatement(
                        "SELECT estado FROM reserva WHERE id = ? FOR UPDATE")) {
                    leer.setInt(1, reservaId);
                    try (ResultSet fila = leer.executeQuery()) {
                        if (!fila.next()) {
                            throw new IllegalArgumentException("La reserva #" + reservaId + " no existe.");
                        }
                        if (!"CONFIRMADA".equals(fila.getString(1))) {
                            throw new IllegalArgumentException(
                                    "Solo se guarda la seña de una reserva confirmada (la #" + reservaId + " no lo está).");
                        }
                    }
                }

                try (PreparedStatement cancelar = conexion.prepareStatement(
                        "UPDATE reserva SET estado = 'CANCELADA' WHERE id = ?")) {
                    cancelar.setInt(1, reservaId);
                    cancelar.executeUpdate();
                }

                try (PreparedStatement guardar = conexion.prepareStatement(
                        "INSERT INTO sena (reserva_id, importe) VALUES (?, ?)")) {
                    guardar.setInt(1, reservaId);
                    guardar.setBigDecimal(2, importe);
                    guardar.executeUpdate();
                }

                conexion.commit();
            } catch (SQLException | RuntimeException e) {
                conexion.rollback();
                throw e;
            }
        }
    }

    /** Marca la seña como usada (true) o la vuelve a dejar disponible (false). */
    public boolean marcarUsada(int id, boolean usada) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement("UPDATE sena SET usada = ? WHERE id = ?")) {
            sentencia.setBoolean(1, usada);
            sentencia.setInt(2, id);
            return sentencia.executeUpdate() > 0;
        }
    }

    /** Marca como usada una seña vigente de ese cliente (al crearle una reserva nueva). */
    public boolean usarEnNuevaReserva(int id, int clienteId) throws SQLException {
        String sql = """
                UPDATE sena s
                JOIN reserva r ON r.id = s.reserva_id
                JOIN cliente_reserva cr ON cr.idReserva = r.id
                SET s.usada = 1
                WHERE s.id = ? AND cr.idCliente = ? AND s.usada = 0
                """;
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            sentencia.setInt(2, clienteId);
            return sentencia.executeUpdate() > 0;
        }
    }

    /** Borra una seña cargada por error (la reserva sigue cancelada). */
    public boolean eliminar(int id) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement("DELETE FROM sena WHERE id = ?")) {
            sentencia.setInt(1, id);
            return sentencia.executeUpdate() > 0;
        }
    }

    private static List<Sena> leer(PreparedStatement sentencia) throws SQLException {
        List<Sena> lista = new ArrayList<>();
        try (ResultSet fila = sentencia.executeQuery()) {
            while (fila.next()) {
                int clienteId = fila.getInt("cliente_id");
                boolean sinCliente = fila.wasNull();
                lista.add(new Sena(
                        fila.getInt("id"),
                        fila.getInt("reserva_id"),
                        fila.getBigDecimal("importe"),
                        fila.getBoolean("usada"),
                        fila.getDate("fechaIngreso").toLocalDate(),
                        fila.getString("alojamiento"),
                        sinCliente ? null : clienteId,
                        fila.getString("cliente"),
                        fila.getString("documento"),
                        fila.getString("telefono")));
            }
        }
        return lista;
    }
}