package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // Todo se toma de variables de entorno: nada de claves ni servidores escritos en el código.
    //   ALOJAMIENTOS_DB_URL       ej: jdbc:mysql://HOST:3306/alojamientosguaviyu
    //   ALOJAMIENTOS_DB_USER      ej: guaviyu
    //   ALOJAMIENTOS_DB_PASSWORD  la contraseña
    // Si URL o USER no están definidas, se usan los valores de desarrollo de siempre,
    // así IntelliJ sigue funcionando igual que antes.
    private static final String URL = entorno(
            "ALOJAMIENTOS_DB_URL",
            "jdbc:mysql://72.60.145.210:3308/alojamientosguaviyu");

    private static final String USER = entorno("ALOJAMIENTOS_DB_USER", "mysql");

    private static final String PASSWORD = System.getenv("ALOJAMIENTOS_DB_PASSWORD");

    private static String entorno(String nombre, String porDefecto) {
        String valor = System.getenv(nombre);
        return (valor == null || valor.isBlank()) ? porDefecto : valor;
    }

    public static Connection obtenerConexion() {
        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new IllegalStateException(
                    "Falta configurar la variable ALOJAMIENTOS_DB_PASSWORD"
            );
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new IllegalStateException(
                    "No se pudo cargar el driver o conectar con MySQL",
                    e
            );
        }
    }
}
