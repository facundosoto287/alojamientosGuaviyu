package org.example;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Locale;
import java.util.Scanner;

public class ClienteDAO {

    /** Costo de BCrypt: 12 tarda ~0,2 s por intento, suficiente para frenar ataques de fuerza bruta. */
    private static final int COSTO_BCRYPT = 12;

    /** Contraseña inicial de los clientes que registra el administrador; deben cambiarla en su primer ingreso. */
    public static final String CONTRASENA_POR_DEFECTO = "alojamientosguaviyu";

    /** Hash de relleno: se usa cuando el email no existe, para que la respuesta tarde lo mismo. */
    private static final String HASH_RELLENO = BCrypt.hashpw("relleno", BCrypt.gensalt(COSTO_BCRYPT));

    // ===================== INICIO DE SESIÓN =====================

    /**
     * Devuelve el cliente si el email y la contraseña son correctos, o null si no lo son.
     * La contraseña ya NO se compara en el SQL: se busca por email y se verifica el hash con BCrypt.
     * Si falla la base de datos lanza SQLException (antes se tragaba el error y parecía "contraseña incorrecta").
     */
    public Cliente iniciarSesion(String correo, String contrasena) throws SQLException {
        if (correo == null || contrasena == null || correo.isBlank() || contrasena.isEmpty()) {
            return null;
        }

        Cliente cliente = buscarPorEmail(normalizarEmail(correo));

        if (cliente == null) {
            BCrypt.checkpw(contrasena, HASH_RELLENO); // mismo tiempo de respuesta que con un email real
            return null;
        }

        String guardada = cliente.getContrasena();

        if (esHashBCrypt(guardada)) {
            return BCrypt.checkpw(contrasena, guardada) ? cliente : null;
        }

        // Transición: clientes viejos con la contraseña guardada en texto plano.
        // Si coincide, se reemplaza por su hash para que la próxima vez ya use BCrypt.
        if (guardada != null && iguales(contrasena, guardada)) {
            String hash = hashear(contrasena);
            actualizarContrasena(cliente.getId(), hash);
            cliente.setContrasena(hash);
            return cliente;
        }

        return null;
    }

    public Cliente buscarPorEmail(String email) throws SQLException {
        String sql = """
            SELECT id, nombre, documento, telefono, email, procedencia, localidad, contrasena, debe_cambiar_contrasena
            FROM cliente
            WHERE email = ?
            """;

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, email);

            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() ? mapearCliente(fila) : null;
            }
        }
    }

    // ===================== REGISTRO =====================

    /**
     * Registra un cliente guardando el hash de la contraseña.
     * Recibe la contraseña en texto (lo que escribió el usuario) y la hashea acá adentro.
     * Lanza IllegalArgumentException con un mensaje para mostrar si faltan datos o el email/documento ya existe.
     */
    public Cliente registrarCliente(String nombre, String documento, String telefono, String email, String procedencia,
                                    String localidad, String contrasena) {
        return registrar(nombre, documento, telefono, email, procedencia, localidad, contrasena, false);
    }

    /**
     * Registro hecho por el administrador: la cuenta queda con la contraseña por defecto
     * y marcada para que el cliente la cambie en su primer ingreso.
     */
    public Cliente registrarClienteDesdeAdmin(String nombre, String documento, String telefono, String email,
                                              String procedencia, String localidad) {
        return registrar(nombre, documento, telefono, email, procedencia, localidad, CONTRASENA_POR_DEFECTO, true);
    }

    private Cliente registrar(String nombre, String documento, String telefono, String email, String procedencia,
                              String localidad, String contrasena, boolean debeCambiarContrasena) {

        if (esVacio(nombre) || esVacio(documento) || esVacio(email) || contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("Faltan datos obligatorios.");
        }

        nombre = nombre.trim();
        documento = documento.trim();
        email = normalizarEmail(email);
        telefono = esVacio(telefono) ? null : telefono.trim();
        procedencia = esVacio(procedencia) ? null : procedencia.trim();

        // La procedencia (departamento, provincia o país) y la localidad (ciudad o pueblo) son obligatorias
        if (procedencia == null) {
            throw new IllegalArgumentException("Indicá la procedencia (departamento, provincia o país donde vivís).");
        }
        localidad = normalizarLocalidad(localidad);
        String errorLocalidad = errorDeLocalidad(localidad);
        if (errorLocalidad != null) {
            throw new IllegalArgumentException(errorLocalidad);
        }

        // Validar cédula y celular antes de tocar la base de datos
        if (!cedulaValida(documento)) {
            throw new IllegalArgumentException("La cédula ingresada no es válida.");
        }
        if (telefono != null && !celularValido(telefono)) {
            throw new IllegalArgumentException("El celular debe tener 9 dígitos y empezar con 09.");
        }

        // Guardar solo los números, así "1.234.567-2" y "12345672" cuentan como la misma cédula
        documento = soloNumeros(documento);
        telefono = telefono == null ? null : soloNumeros(telefono);

        String sqlVerificar = "SELECT email FROM cliente WHERE email = ? OR documento = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement verificar = conexion.prepareStatement(sqlVerificar)) {

            verificar.setString(1, email);
            verificar.setString(2, documento);

            try (ResultSet resultado = verificar.executeQuery()) {
                if (resultado.next()) {
                    throw new IllegalArgumentException(email.equalsIgnoreCase(resultado.getString("email"))
                            ? "Ya existe una cuenta con ese correo electrónico."
                            : "Ya existe una cuenta con ese documento.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String hash = hashear(contrasena);
        String sqlInsertar = "INSERT INTO cliente (nombre, documento, telefono, email, procedencia, localidad, contrasena, debe_cambiar_contrasena) VALUES (?,?,?,?,?,?,?,?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sqlInsertar, PreparedStatement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, nombre);
            sentencia.setString(2, documento);
            sentencia.setString(3, telefono);
            sentencia.setString(4, email);
            sentencia.setString(5, procedencia);
            sentencia.setString(6, localidad);
            sentencia.setString(7, hash);
            sentencia.setBoolean(8, debeCambiarContrasena);

            sentencia.executeUpdate();

            int id = 0;
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    id = claves.getInt(1);
                }
            }

            Cliente nuevo = new Cliente(id, nombre, documento, telefono, email, procedencia, hash);
            nuevo.setLocalidad(localidad);
            nuevo.setDebeCambiarContrasena(debeCambiarContrasena);
            return nuevo;

        } catch (SQLIntegrityConstraintViolationException e) {
            // Dos registros simultáneos con el mismo email/documento (lo frena el UNIQUE de la tabla)
            throw new IllegalArgumentException("Ya existe una cuenta con ese correo o documento.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cambia la contraseña de un cliente verificando la actual. Quita la marca de "debe cambiarla".
     * Lanza IllegalArgumentException con un mensaje para mostrar si la actual no coincide o la nueva no sirve.
     */
    public void cambiarContrasena(int id, String actual, String nueva) throws SQLException {
        Cliente cliente = buscarPorId(id);
        if (cliente == null) {
            throw new IllegalArgumentException("No encontramos tu cuenta.");
        }

        String guardada = cliente.getContrasena();
        boolean actualCorrecta = esHashBCrypt(guardada)
                ? BCrypt.checkpw(actual == null ? "" : actual, guardada)
                : guardada != null && actual != null && iguales(actual, guardada);
        if (!actualCorrecta) {
            throw new IllegalArgumentException("La contraseña actual no es correcta.");
        }

        String sql = "UPDATE cliente SET contrasena = ?, debe_cambiar_contrasena = 0 WHERE id = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, hashear(nueva));
            sentencia.setInt(2, id);
            sentencia.executeUpdate();
        }
    }

    public boolean actualizarContrasena(int id, String hash) throws SQLException {
        String sql = "UPDATE cliente SET contrasena = ? WHERE id = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, hash);
            sentencia.setInt(2, id);
            return sentencia.executeUpdate() == 1;
        }
    }

    // ===================== MÉTODOS QUE YA EXISTÍAN =====================

    public List<Cliente> listarClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nombre, documento, telefono, email, procedencia, localidad, contrasena, debe_cambiar_contrasena FROM cliente ORDER BY nombre";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {

            while (filas.next()) {
                clientes.add(mapearCliente(filas));
            }
        }

        return clientes;
    }

    public boolean existeClientePorId(int id) {
        String sql = "SELECT id FROM cliente WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ---- Método que agrupa la lectura de datos por consola ----

    public Cliente pedirDatosNuevoCliente(Scanner sc) {
        System.out.println("Ingrese el nombre:");
        String nombre = sc.nextLine();

        System.out.println("Ingrese el documento:");
        String documento = sc.nextLine();

        System.out.println("Ingrese el teléfono:");
        String telefono = sc.nextLine();

        System.out.println("Ingrese el email:");
        String email = sc.nextLine();

        System.out.println("Ingrese la procedencia (departamento, provincia o país):");
        String procedencia = sc.nextLine();

        System.out.println("Ingrese la localidad (ciudad o pueblo donde vive):");
        String localidad = sc.nextLine();

        System.out.println("Ingrese la contraseña:");
        String contrasena = sc.nextLine();

        // Acá va en texto: registrarCliente() la convierte en hash antes de guardarla
        Cliente cliente = new Cliente(0, nombre, documento, telefono, email, procedencia, contrasena);
        cliente.setLocalidad(localidad);
        return cliente;
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, documento, telefono, email, procedencia, localidad, contrasena, debe_cambiar_contrasena FROM cliente WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet filas = sentencia.executeQuery()) {
                if (filas.next()) {
                    return mapearCliente(filas);
                }
            }
        }

        return null; // no existe ningún cliente con ese id
    }

    public List<Cliente> buscarPorNombreODocumento(String textoBusqueda) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nombre, documento, telefono, email, procedencia, localidad, contrasena, debe_cambiar_contrasena FROM cliente " +
                "WHERE nombre LIKE ? OR documento LIKE ? ORDER BY nombre";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            String comodin = "%" + textoBusqueda + "%";
            sentencia.setString(1, comodin);
            sentencia.setString(2, comodin);

            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    clientes.add(mapearCliente(filas));
                }
            }
        }

        return clientes;
    }

    /** true si el cliente tiene al menos una reserva (en cualquier estado). */
    public boolean tieneReservas(int id) throws SQLException {
        String sql = "SELECT 1 FROM cliente_reserva WHERE idCliente = ? LIMIT 1";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet filas = sentencia.executeQuery()) {
                return filas.next();
            }
        }
    }

    /** Elimina al cliente solo si no tiene reservas; si las tiene devuelve false y no borra nada. */
    public boolean eliminarCliente(int id) throws SQLException {
        if (tieneReservas(id)) {
            return false;
        }

        String sql = "DELETE FROM cliente WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() == 1;
        }
    }

    public boolean actualizarPerfil(int id, String telefono, String email, String procedencia, String localidad)
            throws SQLException {
        String sql = "UPDATE cliente SET telefono = ?, email = ?, procedencia = ?, localidad = ? WHERE id = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, telefono);
            sentencia.setString(2, normalizarEmail(email));
            sentencia.setString(3, procedencia);
            sentencia.setString(4, normalizarLocalidad(localidad));
            sentencia.setInt(5, id);
            return sentencia.executeUpdate() == 1;
        }
    }

    // ===================== LOCALIDADES =====================

    /** Nombre con el que la gráfica agrupa a los clientes que todavía no cargaron su localidad. */
    public static final String SIN_LOCALIDAD = "Sin especificar";

    /** Palabras que van en minúscula dentro del nombre de una localidad ("Colonia del Sacramento"). */
    private static final Set<String> PALABRAS_MENORES = Set.of("de", "del", "la", "las", "los", "el", "y", "e");

    /**
     * Deja la localidad con un formato único, escriba como escriba el cliente:
     * sin espacios de más y con mayúscula inicial en cada palabra ("  paysandú " → "Paysandú",
     * "COLONIA DEL SACRAMENTO" → "Colonia del Sacramento"). Devuelve null si queda vacía.
     */
    public static String normalizarLocalidad(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = texto.trim().replaceAll("\\s+", " ");
        if (limpio.isEmpty()) {
            return null;
        }

        String[] palabras = limpio.split(" ");
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < palabras.length; i++) {
            String palabra = palabras[i].toLowerCase(Locale.forLanguageTag("es-UY"));
            resultado.append(i > 0 && PALABRAS_MENORES.contains(palabra) ? palabra : conMayusculaInicial(palabra));
            if (i < palabras.length - 1) {
                resultado.append(' ');
            }
        }
        return resultado.toString();
    }

    /** "villa-soriano" → "Villa-Soriano" */
    private static String conMayusculaInicial(String palabra) {
        StringBuilder resultado = new StringBuilder();
        boolean inicio = true;
        for (char letra : palabra.toCharArray()) {
            if (inicio && Character.isLetter(letra)) {
                resultado.append(Character.toUpperCase(letra));
                inicio = false;
            } else {
                resultado.append(letra);
            }
            if (letra == '-') {
                inicio = true;
            }
        }
        return resultado.toString();
    }

    /**
     * Mensaje de error si la localidad (ya normalizada) no sirve, o null si está bien.
     * Es obligatoria, de 2 a 100 caracteres y sin números.
     */
    public static String errorDeLocalidad(String normalizada) {
        if (normalizada == null) {
            return "Ingresá tu localidad (la ciudad o el pueblo donde vivís).";
        }
        if (normalizada.length() < 2 || normalizada.length() > 100) {
            return "La localidad debe tener entre 2 y 100 caracteres.";
        }
        if (!normalizada.matches("\\p{L}[\\p{L}\\p{M} .'\\-]*")) {
            return "La localidad solo puede tener letras, espacios, puntos o guiones (sin números).";
        }
        return null;
    }

    /** Clave para comparar localidades: sin tildes ni mayúsculas ("Paysandú" y "paysandu" son la misma). */
    private static String claveLocalidad(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }

    /** Localidades ya cargadas (para sugerirlas al escribir), las más usadas primero y sin repetir variantes. */
    public List<String> listarLocalidades() throws SQLException {
        String sql = """
                SELECT localidad, COUNT(*) AS cantidad
                FROM cliente
                WHERE localidad IS NOT NULL AND TRIM(localidad) <> ''
                GROUP BY localidad
                ORDER BY cantidad DESC, localidad
                """;

        Map<String, String> porClave = new LinkedHashMap<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {

            while (filas.next()) {
                String localidad = normalizarLocalidad(filas.getString("localidad"));
                if (localidad != null) {
                    porClave.putIfAbsent(claveLocalidad(localidad), localidad);
                }
            }
        }

        return new ArrayList<>(porClave.values());
    }

    /**
     * Cuántos clientes hay por localidad entre los que ya hicieron una estadía: tienen al menos una reserva
     * CONFIRMADA cuya fecha de salida ya llegó. Cada cliente se cuenta una sola vez, sin importar cuántas
     * estadías tuvo. El período se toma por la fecha de ingreso de la reserva: anio y mes son opcionales
     * (null = todos). Los clientes sin localidad se agrupan como "Sin especificar", que va al final.
     * Mayor a menor; las variantes de una misma localidad ("Paysandú" / "paysandu") se suman.
     */
    public Map<String, Integer> contarPorLocalidad(Integer anio, Integer mes) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT DISTINCT c.id, c.localidad
                FROM cliente c
                JOIN cliente_reserva cr ON cr.idCliente = c.id
                JOIN reserva r ON r.id = cr.idReserva
                WHERE r.estado = 'CONFIRMADA' AND r.fechaSalida <= CURDATE()""");
        List<Integer> filtros = new ArrayList<>();
        if (anio != null) {
            sql.append(" AND YEAR(r.fechaIngreso) = ?");
            filtros.add(anio);
        }
        if (mes != null) {
            sql.append(" AND MONTH(r.fechaIngreso) = ?");
            filtros.add(mes);
        }

        // clave sin tildes → (cómo está escrita → cantidad de clientes que la escribieron así)
        Map<String, Map<String, Integer>> variantes = new HashMap<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql.toString())) {

            for (int i = 0; i < filtros.size(); i++) {
                sentencia.setInt(i + 1, filtros.get(i));
            }

            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    String localidad = normalizarLocalidad(filas.getString("localidad"));
                    String texto = localidad == null ? SIN_LOCALIDAD : localidad;
                    variantes.computeIfAbsent(claveLocalidad(texto), k -> new HashMap<>()).merge(texto, 1, Integer::sum);
                }
            }
        }

        // Una fila por localidad: se muestra la variante más usada (a igual cantidad, la que lleva tildes)
        List<Map.Entry<String, Integer>> filas = new ArrayList<>();
        for (Map<String, Integer> escritas : variantes.values()) {
            String mostrar = escritas.entrySet().stream()
                    .max((a, b) -> {
                        int porCantidad = Integer.compare(a.getValue(), b.getValue());
                        if (porCantidad != 0) {
                            return porCantidad;
                        }
                        return Long.compare(tildes(a.getKey()), tildes(b.getKey()));
                    })
                    .get().getKey();
            int clientes = escritas.values().stream().mapToInt(Integer::intValue).sum();
            filas.add(Map.entry(mostrar, clientes));
        }

        filas.sort((a, b) -> {
            boolean aSin = a.getKey().equals(SIN_LOCALIDAD);
            boolean bSin = b.getKey().equals(SIN_LOCALIDAD);
            if (aSin != bSin) {
                return aSin ? 1 : -1; // "Sin especificar" siempre al final
            }
            int porCantidad = Integer.compare(b.getValue(), a.getValue());
            return porCantidad != 0 ? porCantidad : a.getKey().compareToIgnoreCase(b.getKey());
        });

        Map<String, Integer> resultado = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> fila : filas) {
            resultado.put(fila.getKey(), fila.getValue());
        }
        return resultado;
    }

    private static long tildes(String texto) {
        return texto.chars().filter(letra -> letra > 127).count();
    }

    /** Años en los que hubo estadías terminadas (confirmadas con salida ya cumplida), del más reciente al más antiguo. */
    public List<Integer> aniosConEstadias() throws SQLException {
        String sql = """
                SELECT DISTINCT YEAR(fechaIngreso) AS anio
                FROM reserva
                WHERE estado = 'CONFIRMADA' AND fechaSalida <= CURDATE()
                ORDER BY anio DESC
                """;

        List<Integer> anios = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                anios.add(filas.getInt("anio"));
            }
        }
        return anios;
    }

    // ===================== VALIDACIONES =====================

    /**
     * Valida una cédula uruguaya con su dígito verificador.
     * Acepta "1.234.567-2", "1234567-2" o "12345672".
     */
    public boolean cedulaValida(String cedula) {
        if (cedula == null) {
            return false;
        }

        // Me quedo solo con los números
        String numeros = soloNumeros(cedula);

        // Tiene que tener 7 u 8 dígitos (las cédulas viejas tienen 7)
        if (!numeros.matches("\\d{7,8}")) {
            return false;
        }

        // Si tiene 7, le agrego un 0 adelante para que siempre sean 8
        if (numeros.length() == 7) {
            numeros = "0" + numeros;
        }

        // Cada uno de los primeros 7 dígitos se multiplica por su peso
        int[] pesos = {2, 9, 8, 7, 6, 3, 4};
        int suma = 0;
        for (int i = 0; i < 7; i++) {
            suma += Character.getNumericValue(numeros.charAt(i)) * pesos[i];
        }

        // El dígito verificador es lo que le falta a la suma para llegar a la próxima decena
        int digitoCalculado = (10 - (suma % 10)) % 10;
        int digitoIngresado = Character.getNumericValue(numeros.charAt(7));

        return digitoCalculado == digitoIngresado;
    }

    /** Valida un celular uruguayo: 9 dígitos que empiezan con 09 (ej: 099 123 456). */
    public boolean celularValido(String celular) {
        if (celular == null) {
            return false;
        }
        return soloNumeros(celular).matches("09\\d{7}");
    }

    /** Saca puntos, guiones y espacios: "099 123-456" queda "099123456". */
    private static String soloNumeros(String texto) {
        return texto.replace(".", "").replace("-", "").replace(" ", "").trim();
    }

    // ===================== AUXILIARES =====================

    private Cliente mapearCliente(ResultSet fila) throws SQLException {
        Cliente cliente = new Cliente(
                fila.getInt("id"),
                fila.getString("nombre"),
                fila.getString("documento"),
                fila.getString("telefono"),
                fila.getString("email"),
                fila.getString("procedencia"),
                fila.getString("contrasena")
        );
        cliente.setLocalidad(fila.getString("localidad"));
        cliente.setDebeCambiarContrasena(fila.getBoolean("debe_cambiar_contrasena"));
        return cliente;
    }

    private static String hashear(String contrasena) {
        return BCrypt.hashpw(contrasena, BCrypt.gensalt(COSTO_BCRYPT));
    }

    private static boolean esHashBCrypt(String valor) {
        // jBCrypt genera hashes "$2a$..." de 60 caracteres
        return valor != null && valor.length() == 60 && valor.startsWith("$2a$");
    }

    /** Compara en tiempo constante (no corta en el primer carácter distinto). */
    private static boolean iguales(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }

    private static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}