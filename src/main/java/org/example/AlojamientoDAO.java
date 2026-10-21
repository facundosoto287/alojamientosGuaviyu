package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class AlojamientoDAO {

    public boolean agregarAlojamiento(Alojamiento alojamiento) {
        return agregarAlojamientoConId(alojamiento) > 0;
    }

    /** Agrega el alojamiento y devuelve el id que le asignó la base, o -1 si falló. */
    public int agregarAlojamientoConId(Alojamiento alojamiento) {

        String sql = """
                INSERT INTO alojamiento (nombre, descripcion, capacidad, precio, ubicacion, distanciaApiscina, latitud, longitud, permiteMascotas)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, alojamiento.getNombre());
            sentencia.setString(2, alojamiento.getDescripcion());
            sentencia.setInt(3, alojamiento.getCapacidad());
            sentencia.setDouble(4, alojamiento.getPrecio());
            sentencia.setString(5, alojamiento.getUbicacion());
            sentencia.setFloat(6, alojamiento.getDistanciaApiscina());
            guardarCoordenadas(sentencia, 7, alojamiento);
            sentencia.setBoolean(9, alojamiento.getPermiteMascotas());

            if (sentencia.executeUpdate() == 0) {
                return -1;
            }
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                return claves.next() ? claves.getInt(1) : -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** Guarda las fotos del alojamiento ("a|b|c", o null para dejarlo sin fotos). */
    public boolean actualizarImagenes(int id, String imagenes) throws SQLException {
        String sql = "UPDATE alojamiento SET imagenes = ? WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, imagenes);
            sentencia.setInt(2, id);

            return sentencia.executeUpdate() > 0;
        }
    }

    public List<Alojamiento> listarAlojamientos() {
        List<Alojamiento> alojamientos = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, capacidad, precio, ubicacion, distanciaApiscina, latitud, longitud, permiteMascotas, imagenes FROM alojamiento";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                Alojamiento alojamiento = mapearAlojamiento(resultado);
                leerCoordenadas(resultado, alojamiento);
                alojamientos.add(alojamiento);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alojamientos;
    }

    public boolean modificarAlojamiento(Alojamiento alojamiento) {

        String sql = """
                UPDATE alojamiento
                SET nombre = ?, descripcion = ?, capacidad = ?, precio = ?, ubicacion = ?, distanciaApiscina = ?,
                    latitud = ?, longitud = ?, permiteMascotas = ?
                WHERE id = ?""";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, alojamiento.getNombre());
            sentencia.setString(2, alojamiento.getDescripcion());
            sentencia.setInt(3, alojamiento.getCapacidad());
            sentencia.setDouble(4, alojamiento.getPrecio());
            sentencia.setString(5, alojamiento.getUbicacion());
            sentencia.setFloat(6, alojamiento.getDistanciaApiscina());
            guardarCoordenadas(sentencia, 7, alojamiento);
            sentencia.setBoolean(9, alojamiento.getPermiteMascotas());
            sentencia.setInt(10, alojamiento.getId());

            return sentencia.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Cantidad de reservas (en cualquier estado) que tiene el alojamiento. */
    public int contarReservas(int idAlojamiento) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva WHERE alojamiento_id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idAlojamiento);

            try (ResultSet filas = sentencia.executeQuery()) {
                return filas.next() ? filas.getInt(1) : 0;
            }
        }
    }

    /**
     * Elimina el alojamiento y sus servicios asociados en una sola transacción.
     * Si el alojamiento tiene reservas no borra nada y devuelve false.
     */
    public boolean eliminarAlojamiento(int id) {
        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (contarReservas(id) > 0) {
                return false;
            }

            conexion.setAutoCommit(false);
            try {
                try (PreparedStatement sentenciaServicios = conexion.prepareStatement(
                        "DELETE FROM alojamiento_servicio WHERE alojamiento_id = ?")) {
                    sentenciaServicios.setInt(1, id);
                    sentenciaServicios.executeUpdate();
                }

                boolean eliminado;
                try (PreparedStatement sentencia = conexion.prepareStatement("DELETE FROM alojamiento WHERE id = ?")) {
                    sentencia.setInt(1, id);
                    eliminado = sentencia.executeUpdate() > 0;
                }

                conexion.commit();
                return eliminado;
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // ---- Coordenadas para el mapa ----

    // Pide la ubicación en el mapa. Se pega tal cual la copia Google Maps: "-31.83812, -57.89034".
    // Si se deja vacío, el alojamiento se guarda sin ubicación y no aparece en el mapa.
    private void pedirCoordenadas(Scanner sc, Alojamiento alojamiento) {
        while (true) {
            System.out.println("Ingrese las coordenadas del mapa (ej: -31.83812, -57.89034), o Enter para dejarlas vacías:");
            System.out.println("  Tip: en Google Maps, clic derecho sobre el lugar y clic en los números para copiarlos.");
            String linea = sc.nextLine().trim();

            if (linea.isEmpty()) {
                alojamiento.setLatitud(null);
                alojamiento.setLongitud(null);
                return;
            }

            String[] partes = linea.split(",");
            if (partes.length == 2) {
                try {
                    double latitud = Double.parseDouble(partes[0].trim());
                    double longitud = Double.parseDouble(partes[1].trim());

                    // Uruguay está entre las latitudes -35 y -30 y las longitudes -59 y -53
                    if (latitud >= -35 && latitud <= -30 && longitud >= -59 && longitud <= -53) {
                        alojamiento.setLatitud(latitud);
                        alojamiento.setLongitud(longitud);
                        return;
                    }
                    System.out.println("Esas coordenadas no son de Uruguay. Revise que no estén al revés.");
                    continue;
                } catch (NumberFormatException e) {
                    // cae al mensaje de abajo
                }
            }
            System.out.println("Formato inválido. Escriba dos números separados por coma, con punto decimal.");
        }
    }

    // Guarda latitud y longitud en las posiciones indicada y siguiente (NULL si no hay)
    private void guardarCoordenadas(PreparedStatement sentencia, int posicion, Alojamiento alojamiento)
            throws SQLException {
        if (alojamiento.tieneCoordenadas()) {
            sentencia.setDouble(posicion, alojamiento.getLatitud());
            sentencia.setDouble(posicion + 1, alojamiento.getLongitud());
        } else {
            sentencia.setNull(posicion, java.sql.Types.DOUBLE);
            sentencia.setNull(posicion + 1, java.sql.Types.DOUBLE);
        }
    }

    // Lee latitud y longitud del resultado (quedan en null si en la base están vacías)
    private void leerCoordenadas(ResultSet resultado, Alojamiento alojamiento) throws SQLException {
        double latitud = resultado.getDouble("latitud");
        boolean latitudVacia = resultado.wasNull();
        double longitud = resultado.getDouble("longitud");
        boolean longitudVacia = resultado.wasNull();

        if (!latitudVacia && !longitudVacia) {
            alojamiento.setLatitud(latitud);
            alojamiento.setLongitud(longitud);
        }
    }

    private Alojamiento mapearAlojamiento(ResultSet resultado) throws SQLException {
        Alojamiento alojamiento = new Alojamiento(
                resultado.getInt("id"),
                resultado.getString("nombre"),
                resultado.getString("descripcion"),
                resultado.getInt("capacidad"),
                resultado.getDouble("precio"),
                resultado.getString("ubicacion"),
                resultado.getFloat("distanciaApiscina")
        );
        alojamiento.setPermiteMascotas(resultado.getBoolean("permiteMascotas"));
        alojamiento.setImagenes(resultado.getString("imagenes"));
        return alojamiento;
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

        Alojamiento alojamiento = new Alojamiento(0, nombre, descripcion, capacidad, precio, ubicacion, distanciaApiscina);
        pedirCoordenadas(sc, alojamiento);
        return alojamiento;
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

        Alojamiento alojamiento = new Alojamiento(id, nombre, descripcion, capacidad, precio, ubicacion, distanciaApiscina);
        pedirCoordenadas(sc, alojamiento);
        return alojamiento;
    }

    public int obtenerCapacidad(int alojamientoId) throws SQLException {
        String sql = "SELECT capacidad FROM alojamiento WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, alojamientoId);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt("capacidad");
                }
                return -1; // No existe ese alojamiento
            }
        }
    }

    public boolean asociarServicioAalojamiento(int idAlojamiento, int idServicio) {
        String sql = """
            INSERT INTO alojamiento_servicio
            (alojamiento_id, servicio_id)
            VALUES (?, ?)
            """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)){

            sentencia.setInt(1, idAlojamiento);
            sentencia.setInt(2, idServicio);

            int filasAfectadas = sentencia.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean desasociarServicioDeUnAlojamiento(int idAlojamiento, int idServicio) {
        String sql = """
            DELETE FROM alojamiento_servicio
            WHERE alojamiento_id = ?
            AND servicio_id = ?
            """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idAlojamiento);
            sentencia.setInt(2, idServicio);

            int filasAfectadas = sentencia.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public List<Servicio> verServiciosDeUnAlojamiento(int idAlojamiento){
        List<Servicio> servicios = new ArrayList<>();

        String sql = """
            SELECT s.id, s.nombre
            FROM servicio s
            JOIN alojamiento_servicio als
                ON s.id = als.servicio_id
            WHERE als.alojamiento_id = ?
            """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)){

            sentencia.setInt(1, idAlojamiento);
            try (ResultSet resultado = sentencia.executeQuery()) {

                while (resultado.next()){
                    Servicio servicio = new Servicio(
                            resultado.getInt("id"),
                            resultado.getString("nombre")
                    );
                    servicios.add(servicio); //hacer un print del resultado para ver que trae
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servicios;
    }

    public List<Alojamiento> buscarPorNombre(String nombre) throws SQLException {
        List<Alojamiento> alojamientos = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, capacidad, precio, ubicacion, distanciaApiscina, permiteMascotas, imagenes " +
                "FROM alojamiento WHERE nombre LIKE ? ORDER BY nombre";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, "%" + nombre + "%");

            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    Alojamiento alojamiento = new Alojamiento(
                            filas.getInt("id"),
                            filas.getString("nombre"),
                            filas.getString("descripcion"),
                            filas.getInt("capacidad"),
                            filas.getDouble("precio"),
                            filas.getString("ubicacion"),
                            filas.getFloat("distanciaApiscina")
                    );
                    alojamiento.setPermiteMascotas(filas.getBoolean("permiteMascotas"));
                    alojamiento.setImagenes(filas.getString("imagenes"));
                    alojamientos.add(alojamiento);
                }
            }
        }

        return alojamientos;
    }
    // Filtrar alojamientos que tengan TODOS los servicios seleccionados
    public List<Alojamiento> filtrarPorServicios(List<Integer> idsServicios) {
        List<Alojamiento> alojamientos = new ArrayList<>();

        // Armamos los "?" según la cantidad de servicios seleccionados
        String placeholders = idsServicios.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));// unir varios elementos de una lista en un solo string

        String sql = """
            SELECT a.id, a.nombre, a.descripcion, a.capacidad, a.precio, a.ubicacion, a.distanciaApiscina, a.permiteMascotas, a.imagenes
            FROM alojamiento a
            JOIN alojamiento_servicio als ON a.id = als.alojamiento_id
            WHERE als.servicio_id IN (%s)
            GROUP BY a.id, a.nombre, a.descripcion, a.capacidad, a.precio, a.ubicacion, a.distanciaApiscina, a.permiteMascotas, a.imagenes
            HAVING COUNT(DISTINCT als.servicio_id) = ?
            """.formatted(placeholders);

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            int posicion = 1;
            for (int idServicio : idsServicios) {
                sentencia.setInt(posicion, idServicio);
                posicion++;
            }
            sentencia.setInt(posicion, idsServicios.size()); // cantidad de servicios que debe cumplir

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    Alojamiento a = new Alojamiento(
                            resultado.getInt("id"),
                            resultado.getString("nombre"),
                            resultado.getString("descripcion"),
                            resultado.getInt("capacidad"),
                            resultado.getDouble("precio"),
                            resultado.getString("ubicacion"),
                            resultado.getFloat("distanciaApiscina")
                    );
                    a.setPermiteMascotas(resultado.getBoolean("permiteMascotas"));
                    a.setImagenes(resultado.getString("imagenes"));
                    alojamientos.add(a);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alojamientos;
    }

    public List<Alojamiento> filtrarAlojamientosporPrecio(
            int capacidadMinima,
            double precioMin,
            double precioMax
    ) {

        List<Alojamiento> alojamientos = new ArrayList<>();

        String sql = """
            SELECT id, nombre, descripcion, capacidad, precio, ubicacion, distanciaApiscina, permiteMascotas, imagenes
            FROM alojamiento
            WHERE capacidad >= ?
            AND precio BETWEEN ? AND ?
            ORDER BY precio
            """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, capacidadMinima);
            sentencia.setDouble(2, precioMin);
            sentencia.setDouble(3, precioMax);

            try (ResultSet resultado = sentencia.executeQuery()) {

                while (resultado.next()) {

                    Alojamiento alojamiento = new Alojamiento(
                            resultado.getInt("id"),
                            resultado.getString("nombre"),
                            resultado.getString("descripcion"),
                            resultado.getInt("capacidad"),
                            resultado.getDouble("precio"),
                            resultado.getString("ubicacion"),
                            resultado.getFloat("distanciaApiscina")
                    );
                    alojamiento.setPermiteMascotas(resultado.getBoolean("permiteMascotas"));
                    alojamiento.setImagenes(resultado.getString("imagenes"));
                    alojamientos.add(alojamiento);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alojamientos;
    }

    public boolean existeAlojamientoPorId(int id) {
        String sql = "SELECT id FROM alojamiento WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Alojamiento buscarPorIdALO(int id) throws SQLException {
        String sql = "SELECT id, nombre, descripcion, capacidad, precio, ubicacion, distanciaApiscina, latitud, longitud, permiteMascotas, imagenes " +
                "FROM alojamiento WHERE id = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    return null;
                }
                Alojamiento alojamiento = mapearAlojamiento(resultado);
                leerCoordenadas(resultado, alojamiento); // para el mapa de la página de detalle
                return alojamiento;
            }
        }
    }

}