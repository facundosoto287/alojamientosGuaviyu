package org.example;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class AdministradorDAO {

    /** Intentos fallidos seguidos que bloquean la cuenta. */
    static final int MAX_INTENTOS = 5;

    /** Minutos que dura el bloqueo. */
    static final int MINUTOS_BLOQUEO = 15;

    /** Mismo costo que usa ClienteDAO. */
    private static final int COSTO_BCRYPT = 12;

    /** Hash de relleno: si el correo no existe se verifica igual, para que la respuesta tarde lo mismo. */
    private static final String HASH_RELLENO = BCrypt.hashpw("relleno", BCrypt.gensalt(COSTO_BCRYPT));

    /** La cuenta está bloqueada temporalmente por demasiados intentos fallidos. */
    public static class CuentaBloqueadaException extends RuntimeException {
        private final long minutos;

        public CuentaBloqueadaException(long minutos) {
            super("Demasiados intentos fallidos. Probá de nuevo en " + minutos + " minuto(s).");
            this.minutos = minutos;
        }

        public long getMinutos() {
            return minutos;
        }
    }

    // Caso de uso 1: Iniciar sesión
    // Busca por correo y verifica la contraseña contra su hash BCrypt (ya no se compara en el SQL).
    // Devuelve el administrador autenticado (sin el hash) o null si las credenciales no coinciden.
    // Lanza CuentaBloqueadaException si la cuenta está bloqueada por intentos fallidos.
    public Administrador iniciarSesion(String email, String contrasena) {
        if (email == null || contrasena == null || email.isBlank() || contrasena.isEmpty()) {
            return null;
        }

        String sql = """
                SELECT id, nombre, email, contrasena,
                       (bloqueado_hasta IS NOT NULL AND bloqueado_hasta > NOW()) AS bloqueado,
                       GREATEST(1, CEIL(TIMESTAMPDIFF(SECOND, NOW(), bloqueado_hasta) / 60)) AS minutos
                FROM administrador
                WHERE email = ?""";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, email.trim().toLowerCase(Locale.ROOT));

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    BCrypt.checkpw(contrasena, HASH_RELLENO); // mismo tiempo de respuesta que con un correo real
                    return null;
                }

                int id = resultado.getInt("id");

                if (resultado.getBoolean("bloqueado")) {
                    throw new CuentaBloqueadaException(resultado.getLong("minutos"));
                }

                String hash = resultado.getString("contrasena");
                boolean correcta = esHashBCrypt(hash) && BCrypt.checkpw(contrasena, hash);

                if (!correcta) {
                    registrarIntentoFallido(conexion, id);
                    return null;
                }

                limpiarIntentos(conexion, id);
                return new Administrador(id, resultado.getString("nombre"), resultado.getString("email"), null);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Suma un intento fallido; al llegar al máximo bloquea la cuenta y reinicia el contador.
    // (En el UPDATE, MySQL evalúa las asignaciones de izquierda a derecha: bloqueado_hasta usa el valor anterior.)
    private void registrarIntentoFallido(Connection conexion, int id) throws SQLException {
        String sql = """
                UPDATE administrador
                SET bloqueado_hasta = IF(intentos_fallidos + 1 >= ?, DATE_ADD(NOW(), INTERVAL ? MINUTE), bloqueado_hasta),
                    intentos_fallidos = IF(intentos_fallidos + 1 >= ?, 0, intentos_fallidos + 1)
                WHERE id = ?""";

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, MAX_INTENTOS);
            sentencia.setInt(2, MINUTOS_BLOQUEO);
            sentencia.setInt(3, MAX_INTENTOS);
            sentencia.setInt(4, id);
            sentencia.executeUpdate();
        }
    }

    private void limpiarIntentos(Connection conexion, int id) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(
                "UPDATE administrador SET intentos_fallidos = 0, bloqueado_hasta = NULL WHERE id = ?")) {
            sentencia.setInt(1, id);
            sentencia.executeUpdate();
        }
    }

    private static boolean esHashBCrypt(String valor) {
        // jBCrypt genera hashes "$2a$..." de 60 caracteres
        return valor != null && valor.length() == 60 && valor.startsWith("$2a$");
    }

    // Caso de uso: Cerrar sesión
    // No requiere acceso a la base de datos: solo indica que la operación fue exitosa.
    public boolean cerrarSesion() {
        System.out.println("Sesión finalizada correctamente.");
        return true;
    }

}
