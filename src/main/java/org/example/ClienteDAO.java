package org.example;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ClienteDAO {

    /** Costo de BCrypt: 12 tarda ~0,2 s por intento, suficiente para frenar ataques de fuerza bruta. */
    private static final int COSTO_BCRYPT = 12;

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
            SELECT id, nombre, documento, telefono, email, procedencia, contrasena
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
    public Cliente registrarCliente(String nombre, String documento, String telefono, String email, String procedencia, String contrasena) {

        if (esVacio(nombre) || esVacio(documento) || esVacio(email) || contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("Faltan datos obligatorios.");
        }

        nombre = nombre.trim();
        documento = documento.trim();
        email = normalizarEmail(email);
        telefono = esVacio(telefono) ? null : telefono.trim();
        procedencia = esVacio(procedencia) ? null : procedencia.trim();

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
        String sqlInsertar = "INSERT INTO cliente (nombre, documento, telefono, email, procedencia, contrasena) VALUES (?,?,?,?,?,?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sqlInsertar, PreparedStatement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, nombre);
            sentencia.setString(2, documento);
            sentencia.setString(3, telefono);
            sentencia.setString(4, email);
            sentencia.setString(5, procedencia);
            sentencia.setString(6, hash);

            sentencia.executeUpdate();

            int id = 0;
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (claves.next()) {
                    id = claves.getInt(1);
                }
            }

            return new Cliente(id, nombre, documento, telefono, email, procedencia, hash);

        } catch (SQLIntegrityConstraintViolationException e) {
            // Dos registros simultáneos con el mismo email/documento (lo frena el UNIQUE de la tabla)
            throw new IllegalArgumentException("Ya existe una cuenta con ese correo o documento.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        String sql = "SELECT id, nombre, documento, telefono, email, procedencia, contrasena FROM cliente ORDER BY nombre";

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

        System.out.println("Ingrese la procedencia:");
        String procedencia = sc.nextLine();

        System.out.println("Ingrese la contraseña:");
        String contrasena = sc.nextLine();

        // Acá va en texto: registrarCliente() la convierte en hash antes de guardarla
        return new Cliente(0, nombre, documento, telefono, email, procedencia, contrasena);
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, documento, telefono, email, procedencia, contrasena FROM cliente WHERE id = ?";

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
        String sql = "SELECT id, nombre, documento, telefono, email, procedencia, contrasena FROM cliente " +
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

    public boolean eliminarCliente(int id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() == 1;
        }
    }

    public boolean actualizarPerfil(int id, String telefono, String email, String procedencia)
            throws SQLException {
        String sql = "UPDATE cliente SET telefono = ?, email = ?, procedencia = ? WHERE id = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, telefono);
            sentencia.setString(2, normalizarEmail(email));
            sentencia.setString(3, procedencia);
            sentencia.setInt(4, id);
            return sentencia.executeUpdate() == 1;
        }
    }

    // ===================== AUXILIARES =====================

    private Cliente mapearCliente(ResultSet fila) throws SQLException {
        return new Cliente(
                fila.getInt("id"),
                fila.getString("nombre"),
                fila.getString("documento"),
                fila.getString("telefono"),
                fila.getString("email"),
                fila.getString("procedencia"),
                fila.getString("contrasena")
        );
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
