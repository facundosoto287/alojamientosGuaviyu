package org.example;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReservaDao {

    // Crea una nueva reserva con estado PENDIENTE. Devuelve el id generado, o -1 si falló.
    public int crear(Reserva reserva) {
        String sql = """
            INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id)
            VALUES (?, ?, ?, ?, ?, ?)""";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            sentencia.setDate(1, reserva.getFechaIngreso());
            sentencia.setDate(2, reserva.getFechaSalida());
            sentencia.setInt(3, reserva.getCantidadHuespedes());
            sentencia.setString(4, reserva.getEstado().name());
            sentencia.setDate(5, reserva.getFechaSolicitud());
            sentencia.setInt(6, reserva.getAlojamientoId());

            int filasAfectadas = sentencia.executeUpdate();
            if (filasAfectadas == 0) {
                return -1;
            }

            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                }
            }

            return -1;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Ver solicitudes pendientes (caso de uso del administrador)
    public List<Reserva> listarPendientes() {
        List<Reserva> pendientes = new ArrayList<>();
        String sql = """
                SELECT id, fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id
                FROM reserva
                WHERE estado = 'PENDIENTE'""";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                pendientes.add(mapearReserva(resultado));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pendientes;
    }

    public List<Reserva> listarTodas() {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT id, fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id FROM reserva";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                reservas.add(mapearReserva(resultado));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservas;
    }
    public List<Reserva> listarConfirmadas() {
        List<Reserva> confirmadas = new ArrayList<>();
        String sql = """
            SELECT id, fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id
            FROM reserva
            WHERE estado = 'CONFIRMADA'""";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                confirmadas.add(mapearReserva(resultado));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return confirmadas;
    }



    public boolean confirmarReserva(int id) {
        String sql = """
                UPDATE reserva
                SET estado = 'CONFIRMADA'
                WHERE id = ?
                AND estado = 'PENDIENTE'""";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelarReserva(int id) {
        String sql = """
                UPDATE reserva
                SET estado = 'CANCELADA'
                WHERE id = ?
                AND estado = 'PENDIENTE'""";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Reserva buscarReservaPorId(int id){
        String sql = """
            SELECT id, fechaIngreso, fechaSalida, cantidadHuespedes,
                   estado, fechaSolicitud, alojamiento_id
            FROM reserva
            WHERE id = ?
            """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {
                    Reserva reserva = new Reserva(
                            resultado.getInt("id"),
                            resultado.getDate("fechaIngreso"),
                            resultado.getDate("fechaSalida"),
                            resultado.getInt("cantidadHuespedes"),
                            EstadoReserva.valueOf(resultado.getString("estado")),
                            resultado.getDate("fechaSolicitud"),
                            resultado.getInt("alojamiento_id")
                    );
                    return reserva;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Reserva> listarPorEstado(EstadoReserva estado) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = """
            SELECT id, fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id
            FROM reserva
            WHERE estado = ?""";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, estado.name());

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    reservas.add(mapearReserva(resultado));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservas;
    }

    private Reserva mapearReserva(ResultSet resultado) throws SQLException {
        return new Reserva(
                resultado.getInt("id"),
                resultado.getDate("fechaIngreso"),
                resultado.getDate("fechaSalida"),
                resultado.getInt("cantidadHuespedes"),
                EstadoReserva.valueOf(resultado.getString("estado")),
                resultado.getDate("fechaSolicitud"),
                resultado.getInt("alojamiento_id")
        );
    }

    // ---- Metodo que agrupa la lectura de datos por consola ----

    public Reserva pedirDatosNuevaReserva(Scanner sc) {
        System.out.println("Ingrese el id del alojamiento:");
        int alojamientoId = sc.nextInt();
        sc.nextLine();

        Date hoy = new Date(System.currentTimeMillis());

        Date fechaIngreso = null;
        while (fechaIngreso == null) {
            System.out.println("Ingrese la fecha de ingreso (YYYY-MM-DD):");
            String textoFecha = sc.nextLine();

            try {
                Date fechaIngresada = Date.valueOf(textoFecha);

                if (fechaIngresada.before(hoy)) {
                    System.out.println("La fecha de ingreso no puede ser anterior a hoy (" + hoy + "). Intente de nuevo.");
                } else {
                    fechaIngreso = fechaIngresada;
                }

            } catch (IllegalArgumentException e) {
                System.out.println("Formato de fecha inválido. Use YYYY-MM-DD. Intente de nuevo.");
            }
        }

        Date fechaSalida = null;
        while (fechaSalida == null) {
            System.out.println("Ingrese la fecha de salida (YYYY-MM-DD):");
            String textoFecha = sc.nextLine();

            try {
                Date fechaIngresada = Date.valueOf(textoFecha);

                if (fechaIngresada.before(fechaIngreso)) {
                    System.out.println("La fecha de salida no puede ser anterior a la fecha de ingreso (" + fechaIngreso + "). Intente de nuevo.");
                } else {
                    fechaSalida = fechaIngresada;
                }

            } catch (IllegalArgumentException e) {
                System.out.println("Formato de fecha inválido. Use YYYY-MM-DD. Intente de nuevo.");
            }
        }

        System.out.println("Ingrese la cantidad de huéspedes:");
        int cantidadHuespedes = sc.nextInt();
        sc.nextLine();

        Date fechaSolicitud = new Date(System.currentTimeMillis());

        return new Reserva(0, fechaIngreso, fechaSalida, cantidadHuespedes,
                EstadoReserva.PENDIENTE, fechaSolicitud, alojamientoId);
    }

    // Caso de uso: Ver historial de reservas de un cliente (Administrador)
    public List<Reserva> verHistorialPorCliente(int idCliente) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = """
            SELECT r.id, r.fechaIngreso, r.fechaSalida, r.cantidadHuespedes, r.estado, r.fechaSolicitud, r.alojamiento_id
            FROM reserva r
            JOIN cliente_reserva cr ON r.id = cr.idReserva
            WHERE cr.idCliente = ?
            ORDER BY r.fechaSolicitud DESC""";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idCliente);

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    reservas.add(mapearReserva(resultado));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservas;
    }

    // Caso de uso: Ver reservas de un alojamiento (Administrador)
    public List<Reserva> listarPorAlojamiento(int idAlojamiento) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = """
            SELECT id, fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id
            FROM reserva
            WHERE alojamiento_id = ?
            ORDER BY fechaIngreso""";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idAlojamiento);

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    reservas.add(mapearReserva(resultado));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservas;
    }

    public List<Reserva> reservasConfirmadasAlojamiento(int idAlojamiento) {

        List<Reserva> reservas = new ArrayList<>();

        String sql = """
            SELECT id, fechaIngreso, fechaSalida, cantidadHuespedes,
                   estado, fechaSolicitud, alojamiento_id
            FROM reserva
            WHERE alojamiento_id = ?
            AND estado = 'CONFIRMADA'
            ORDER BY fechaIngreso
            """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idAlojamiento);

            try (ResultSet resultado = sentencia.executeQuery()) {

                while (resultado.next()) {

                    Reserva reserva = new Reserva(
                            resultado.getInt("id"),
                            resultado.getDate("fechaIngreso"),
                            resultado.getDate("fechaSalida"),
                            resultado.getInt("cantidadHuespedes"),
                            EstadoReserva.valueOf(resultado.getString("estado")),
                            resultado.getDate("fechaSolicitud"),
                            resultado.getInt("alojamiento_id")
                    );
                    reservas.add(reserva);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservas;
    }

    public Reserva verDetalleReservaCliente(int idReserva, int idCliente) {

        String sql = """
            SELECT r.id, r.fechaIngreso, r.fechaSalida,
                   r.cantidadHuespedes, r.estado,
                   r.fechaSolicitud, r.alojamiento_id
            FROM reserva r
            JOIN cliente_reserva cr
                ON r.id = cr.idReserva
            WHERE r.id = ?
            AND cr.idCliente = ?
            """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idReserva);
            sentencia.setInt(2, idCliente);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {
                    return mapearReserva(resultado);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

} //fin dao

