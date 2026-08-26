package org.example;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdministradorDAO {

    // Caso de uso 1: Iniciar sesión
    // Valida correo y contraseña. Si son correctos, devuelve el Administrador autenticado.
    // Si no coinciden, devuelve null.
    public Administrador iniciarSesion(String correo, String contrasena) {
        String sql = "SELECT id, nombre, correo, contrasena FROM administrador WHERE correo = ? AND contrasena = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, correo);
            sentencia.setString(2, contrasena);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return new Administrador(
                            resultado.getInt("id"),
                            resultado.getString("nombre"),
                            resultado.getString("correo"),
                            resultado.getString("contrasena")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null; // no se encontró un administrador con esas credenciales
    }
}
