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

    public boolean existeSuperposicion(
            int alojamientoId,
            java.sql.Date fechaIngreso,
            java.sql.Date fechaSalida
        ) throws SQLException {
        String sql = """
        SELECT 1
        FROM reserva
        WHERE alojamiento_id = ?
          AND estado IN ('PENDIENTE', 'CONFIRMADA')
          AND fechaIngreso < ?
          AND fechaSalida > ?
        LIMIT 1
        """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, alojamientoId);
            sentencia.setDate(2, fechaSalida);
            sentencia.setDate(3, fechaIngreso);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
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



    /** Resultado de intentar confirmar una reserva. */
    public enum ResultadoConfirmacion { CONFIRMADA, NO_PENDIENTE, FECHAS_OCUPADAS, ERROR }

    public boolean confirmarReserva(int id) {
        return confirmarReservaConResultado(id) == ResultadoConfirmacion.CONFIRMADA;
    }

    /**
     * Confirma una reserva PENDIENTE solo si sus fechas no se superponen con otra reserva CONFIRMADA
     * del mismo alojamiento. Todo ocurre en una transacción, bloqueando la fila del alojamiento para que
     * dos confirmaciones simultáneas no ocupen las mismas fechas.
     */
    public ResultadoConfirmacion confirmarReservaConResultado(int id) {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try {
                int alojamientoId;
                Date ingreso;
                Date salida;

                try (PreparedStatement leer = conexion.prepareStatement(
                        "SELECT alojamiento_id, fechaIngreso, fechaSalida FROM reserva WHERE id = ? AND estado = 'PENDIENTE'")) {
                    leer.setInt(1, id);
                    try (ResultSet fila = leer.executeQuery()) {
                        if (!fila.next()) {
                            conexion.rollback();
                            return ResultadoConfirmacion.NO_PENDIENTE;
                        }
                        alojamientoId = fila.getInt("alojamiento_id");
                        ingreso = fila.getDate("fechaIngreso");
                        salida = fila.getDate("fechaSalida");
                    }
                }

                try (PreparedStatement bloquear = conexion.prepareStatement(
                        "SELECT id FROM alojamiento WHERE id = ? FOR UPDATE")) {
                    bloquear.setInt(1, alojamientoId);
                    bloquear.executeQuery().close();
                }

                try (PreparedStatement conflicto = conexion.prepareStatement("""
                        SELECT 1 FROM reserva
                        WHERE alojamiento_id = ? AND id <> ? AND estado = 'CONFIRMADA'
                          AND fechaIngreso < ? AND fechaSalida > ?
                        LIMIT 1""")) {
                    conflicto.setInt(1, alojamientoId);
                    conflicto.setInt(2, id);
                    conflicto.setDate(3, salida);
                    conflicto.setDate(4, ingreso);
                    try (ResultSet fila = conflicto.executeQuery()) {
                        if (fila.next()) {
                            conexion.rollback();
                            return ResultadoConfirmacion.FECHAS_OCUPADAS;
                        }
                    }
                }

                try (PreparedStatement confirmar = conexion.prepareStatement(
                        "UPDATE reserva SET estado = 'CONFIRMADA' WHERE id = ? AND estado = 'PENDIENTE'")) {
                    confirmar.setInt(1, id);
                    if (confirmar.executeUpdate() == 0) {
                        conexion.rollback();
                        return ResultadoConfirmacion.NO_PENDIENTE;
                    }
                }

                conexion.commit();
                return ResultadoConfirmacion.CONFIRMADA;
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultadoConfirmacion.ERROR;
        }
    }

    /** Cancela una reserva PENDIENTE o CONFIRMADA. Si estaba confirmada, sus fechas vuelven a quedar libres. */
    public boolean cancelarReserva(int id) {
        String sql = """
                UPDATE reserva
                SET estado = 'CANCELADA'
                WHERE id = ?
                AND estado IN ('PENDIENTE', 'CONFIRMADA')""";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===================== PANEL DE ADMINISTRADOR =====================

    /**
     * Crea una reserva desde el panel (por ejemplo, la de un cliente que reservó por teléfono o WhatsApp)
     * y la vincula al cliente, todo en una transacción. El estado inicial lo elige el administrador:
     * CONFIRMADA bloquea las fechas de inmediato; PENDIENTE queda como solicitud.
     * Lanza IllegalArgumentException con un mensaje para mostrar si los datos no son válidos.
     */
    public int crearReservaAdmin(Reserva reserva, int idCliente) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            conexion.setAutoCommit(false);
            try {
                int capacidad;
                try (PreparedStatement bloquear = conexion.prepareStatement(
                        "SELECT capacidad FROM alojamiento WHERE id = ? FOR UPDATE")) {
                    bloquear.setInt(1, reserva.getAlojamientoId());
                    try (ResultSet fila = bloquear.executeQuery()) {
                        if (!fila.next()) {
                            throw new IllegalArgumentException("El alojamiento seleccionado no existe.");
                        }
                        capacidad = fila.getInt(1);
                    }
                }

                if (reserva.getCantidadHuespedes() > capacidad) {
                    throw new IllegalArgumentException("El alojamiento admite hasta " + capacidad + " huéspedes.");
                }

                try (PreparedStatement existeCliente = conexion.prepareStatement("SELECT 1 FROM cliente WHERE id = ?")) {
                    existeCliente.setInt(1, idCliente);
                    try (ResultSet fila = existeCliente.executeQuery()) {
                        if (!fila.next()) {
                            throw new IllegalArgumentException("El cliente seleccionado no existe.");
                        }
                    }
                }

                try (PreparedStatement conflicto = conexion.prepareStatement("""
                        SELECT estado FROM reserva
                        WHERE alojamiento_id = ? AND estado IN ('PENDIENTE', 'CONFIRMADA')
                          AND fechaIngreso < ? AND fechaSalida > ?
                        LIMIT 1""")) {
                    conflicto.setInt(1, reserva.getAlojamientoId());
                    conflicto.setDate(2, reserva.getFechaSalida());
                    conflicto.setDate(3, reserva.getFechaIngreso());
                    try (ResultSet fila = conflicto.executeQuery()) {
                        if (fila.next()) {
                            throw new IllegalArgumentException("PENDIENTE".equals(fila.getString(1))
                                    ? "Hay una solicitud pendiente para esas fechas. Confirmala o cancelala primero."
                                    : "El alojamiento ya está reservado en esas fechas.");
                        }
                    }
                }

                int idReserva;
                try (PreparedStatement insertar = conexion.prepareStatement("""
                        INSERT INTO reserva (fechaIngreso, fechaSalida, cantidadHuespedes, estado, fechaSolicitud, alojamiento_id)
                        VALUES (?, ?, ?, ?, ?, ?)""", PreparedStatement.RETURN_GENERATED_KEYS)) {
                    insertar.setDate(1, reserva.getFechaIngreso());
                    insertar.setDate(2, reserva.getFechaSalida());
                    insertar.setInt(3, reserva.getCantidadHuespedes());
                    insertar.setString(4, reserva.getEstado().name());
                    insertar.setDate(5, reserva.getFechaSolicitud());
                    insertar.setInt(6, reserva.getAlojamientoId());
                    insertar.executeUpdate();
                    try (ResultSet claves = insertar.getGeneratedKeys()) {
                        if (!claves.next()) {
                            throw new SQLException("No se obtuvo el id de la reserva creada.");
                        }
                        idReserva = claves.getInt(1);
                    }
                }

                try (PreparedStatement relacion = conexion.prepareStatement(
                        "INSERT INTO cliente_reserva (idCliente, idReserva) VALUES (?, ?)")) {
                    relacion.setInt(1, idCliente);
                    relacion.setInt(2, idReserva);
                    relacion.executeUpdate();
                }

                conexion.commit();
                return idReserva;
            } catch (SQLException | RuntimeException e) {
                conexion.rollback();
                throw e;
            }
        }
    }

    /**
     * Lista reservas con el nombre del alojamiento y los datos del cliente. Todos los filtros son opcionales:
     * estado (null = todos), alojamiento (null = todos), cliente (null = todos) y texto
     * (parte del nombre o del documento del cliente).
     */
    public List<ReservaDetalle> listarDetalle(EstadoReserva estado, Integer alojamientoId, Integer clienteId, String textoCliente) {
        StringBuilder sql = new StringBuilder("""
                SELECT r.id, r.fechaIngreso, r.fechaSalida, r.cantidadHuespedes, r.estado, r.fechaSolicitud,
                       r.alojamiento_id, a.nombre AS alojamiento, c.id AS cliente_id, c.nombre AS cliente, c.documento
                FROM reserva r
                LEFT JOIN alojamiento a ON a.id = r.alojamiento_id
                LEFT JOIN cliente_reserva cr ON cr.idReserva = r.id
                LEFT JOIN cliente c ON c.id = cr.idCliente
                WHERE 1 = 1""");
        List<Object> parametros = new ArrayList<>();

        if (estado != null) {
            sql.append(" AND r.estado = ?");
            parametros.add(estado.name());
        }
        if (alojamientoId != null) {
            sql.append(" AND r.alojamiento_id = ?");
            parametros.add(alojamientoId);
        }
        if (clienteId != null) {
            sql.append(" AND c.id = ?");
            parametros.add(clienteId);
        }
        if (textoCliente != null && !textoCliente.isBlank()) {
            sql.append(" AND (c.nombre LIKE ? OR c.documento LIKE ?)");
            String comodin = "%" + textoCliente.trim() + "%";
            parametros.add(comodin);
            parametros.add(comodin);
        }
        sql.append(" ORDER BY r.fechaSolicitud DESC, r.id DESC");

        List<ReservaDetalle> lista = new ArrayList<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                sentencia.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet fila = sentencia.executeQuery()) {
                while (fila.next()) {
                    int idCliente = fila.getInt("cliente_id");
                    boolean sinCliente = fila.wasNull();
                    lista.add(new ReservaDetalle(
                            mapearReserva(fila),
                            fila.getString("alojamiento"),
                            sinCliente ? null : idCliente,
                            fila.getString("cliente"),
                            fila.getString("documento")));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Reserva> buscarReservasPorCliente(String nombreCliente) {

        List<Reserva> reservas = new ArrayList<>();

        String sql = """
            SELECT r.id, r.fechaIngreso, r.fechaSalida,
                   r.cantidadHuespedes, r.estado,
                   r.fechaSolicitud, r.alojamiento_id
            FROM reserva r
            JOIN cliente_reserva cr
                ON r.id = cr.idReserva
            JOIN cliente c
                ON cr.idCliente = c.id
            WHERE c.nombre LIKE ?
            ORDER BY r.fechaSolicitud DESC
            """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, "%" + nombreCliente + "%");

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
        SELECT id,
               fechaIngreso,
               fechaSalida,
               cantidadHuespedes,
               estado,
               fechaSolicitud,
               alojamiento_id,
               medioDiaExtra
        FROM reserva
        WHERE alojamiento_id = ?
        AND estado IN ('PENDIENTE', 'CONFIRMADA')
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

                    reserva.setMedioDiaExtra(resultado.getBoolean("medioDiaExtra"));

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

    public int crearSolicitudReserva(Reserva reserva, int idCliente) throws SQLException {

        /*
         * Si pidió medio día extra, para comprobar disponibilidad
         * consideramos que el alojamiento queda ocupado hasta el día siguiente.
         *
         * Ejemplo:
         * salida: 17/12
         * medio día extra: sí
         * próximo ingreso permitido: 18/12
         */
        Date fechaSalidaParaDisponibilidad =
                reserva.isMedioDiaExtra()
                        ? Date.valueOf(
                        reserva.getFechaSalida()
                                .toLocalDate()
                                .plusDays(1)
                )
                        : reserva.getFechaSalida();


        String sqlConflicto = """
            SELECT COUNT(*)
            FROM reserva
            WHERE alojamiento_id = ?
              AND estado IN ('PENDIENTE', 'CONFIRMADA')
              AND fechaIngreso < ?
              AND (
                    CASE
                        WHEN medioDiaExtra = 1
                        THEN DATE_ADD(fechaSalida, INTERVAL 1 DAY)
                        ELSE fechaSalida
                    END
                  ) > ?
            """;


        String sqlReserva = """
            INSERT INTO reserva
            (
                fechaIngreso,
                fechaSalida,
                cantidadHuespedes,
                estado,
                fechaSolicitud,
                alojamiento_id,
                medioDiaExtra
            )
            VALUES (?, ?, ?, 'PENDIENTE', ?, ?, ?)
            """;


        String sqlRelacion =
                "INSERT INTO cliente_reserva (idCliente, idReserva) VALUES (?, ?)";


        try (Connection conexion = ConexionBD.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {

                /*
                 * Comprobar que las fechas estén disponibles.
                 */
                try (PreparedStatement verificar =
                             conexion.prepareStatement(sqlConflicto)) {

                    verificar.setInt(
                            1,
                            reserva.getAlojamientoId()
                    );

                    verificar.setDate(
                            2,
                            fechaSalidaParaDisponibilidad
                    );

                    verificar.setDate(
                            3,
                            reserva.getFechaIngreso()
                    );


                    try (ResultSet filas =
                                 verificar.executeQuery()) {

                        if (
                                filas.next() &&
                                        filas.getInt(1) > 0
                        ) {

                            conexion.rollback();

                            return -1;
                        }
                    }
                }


                /*
                 * Crear reserva pendiente.
                 */
                int idReserva;

                try (PreparedStatement insertar =
                             conexion.prepareStatement(
                                     sqlReserva,
                                     PreparedStatement.RETURN_GENERATED_KEYS
                             )) {

                    insertar.setDate(
                            1,
                            reserva.getFechaIngreso()
                    );

                    insertar.setDate(
                            2,
                            reserva.getFechaSalida()
                    );

                    insertar.setInt(
                            3,
                            reserva.getCantidadHuespedes()
                    );

                    insertar.setDate(
                            4,
                            reserva.getFechaSolicitud()
                    );

                    insertar.setInt(
                            5,
                            reserva.getAlojamientoId()
                    );

                    insertar.setBoolean(
                            6,
                            reserva.isMedioDiaExtra()
                    );


                    if (insertar.executeUpdate() != 1) {

                        conexion.rollback();

                        return -1;
                    }


                    try (ResultSet claves =
                                 insertar.getGeneratedKeys()) {

                        if (!claves.next()) {

                            conexion.rollback();

                            return -1;
                        }

                        idReserva =
                                claves.getInt(1);
                    }
                }


                /*
                 * Relacionar reserva con cliente.
                 */
                try (PreparedStatement relacion =
                             conexion.prepareStatement(sqlRelacion)) {

                    relacion.setInt(
                            1,
                            idCliente
                    );

                    relacion.setInt(
                            2,
                            idReserva
                    );

                    relacion.executeUpdate();
                }


                conexion.commit();

                return idReserva;


            } catch (SQLException e) {

                conexion.rollback();

                throw e;
            }
        }
    }

    public boolean cancelarSolicitudCliente(int idReserva, int idCliente) {

        String sql = """
            UPDATE reserva r
            JOIN cliente_reserva cr
                ON r.id = cr.idReserva
            SET r.estado = 'CANCELADA'
            WHERE r.id = ?
            AND cr.idCliente = ?
            AND r.estado = 'PENDIENTE'
            """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idReserva);
            sentencia.setInt(2, idCliente);

            int filasAfectadas = sentencia.executeUpdate();

            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //cu para ver mis reservas del cliente
    public List<Reserva> listarReservasCliente(int idCliente) {

        List<Reserva> reservas = new ArrayList<>();

        String sql = """
        SELECT
            r.id,
            r.fechaIngreso,
            r.fechaSalida,
            r.cantidadHuespedes,
            r.estado,
            r.fechaSolicitud,
            r.alojamiento_id,
            r.medioDiaExtra,

            a.nombre AS nombreAlojamiento,
            a.ubicacion AS ubicacionAlojamiento,
            a.precio AS precioPorNoche,

            DATEDIFF(r.fechaSalida, r.fechaIngreso) AS cantidadNoches,

            (
                DATEDIFF(r.fechaSalida, r.fechaIngreso) * a.precio
                +
                CASE
                    WHEN r.medioDiaExtra = 1
                    THEN a.precio / 2
                    ELSE 0
                END
            ) AS precioTotal

        FROM reserva r

        JOIN cliente_reserva cr
            ON r.id = cr.idReserva

        JOIN alojamiento a
            ON r.alojamiento_id = a.id

        WHERE cr.idCliente = ?

        ORDER BY r.fechaSolicitud DESC
        """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idCliente);

            try (ResultSet resultado = sentencia.executeQuery()) {

                while (resultado.next()) {

                    Reserva reserva = new Reserva(
                            resultado.getInt("id"),
                            resultado.getDate("fechaIngreso"),
                            resultado.getDate("fechaSalida"),
                            resultado.getInt("cantidadHuespedes"),
                            EstadoReserva.valueOf(
                                    resultado.getString("estado")
                            ),
                            resultado.getDate("fechaSolicitud"),
                            resultado.getInt("alojamiento_id")
                    );


                    reserva.setMedioDiaExtra(
                            resultado.getBoolean("medioDiaExtra")
                    );


                    reserva.setNombreAlojamiento(
                            resultado.getString("nombreAlojamiento")
                    );


                    reserva.setUbicacionAlojamiento(
                            resultado.getString("ubicacionAlojamiento")
                    );


                    reserva.setPrecioPorNoche(
                            resultado.getDouble("precioPorNoche")
                    );


                    reserva.setCantidadNoches(
                            resultado.getInt("cantidadNoches")
                    );


                    reserva.setPrecioTotal(resultado.getDouble("precioTotal"));
                    reservas.add(reserva);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservas;
    }
} //fin dao

