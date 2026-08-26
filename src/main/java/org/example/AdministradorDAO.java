package org.example;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdministradorDAO {

    // Caso de uso 1: Iniciar sesión
    // Valida email y contraseña. Si son correctos, devuelve el Administrador autenticado.
    // Si no coinciden, devuelve null.
    public Administrador iniciarSesion(String email, String contrasena) {
        String sql = "SELECT id, nombre, email, contrasena FROM administrador WHERE email = ? AND contrasena = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, email);
            sentencia.setString(2, contrasena);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return new Administrador(
                            resultado.getInt("id"),
                            resultado.getString("nombre"),
                            resultado.getString("email"),
                            resultado.getString("contrasena")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null; // no se encontró un administrador con esas credenciales
    }
    // Caso de uso: Cerrar sesión
// No requiere acceso a la base de datos: solo indica que la operación fue exitosa.
    public boolean cerrarSesion() {
        System.out.println("Sesión finalizada correctamente.");
        return true;
    }


}
