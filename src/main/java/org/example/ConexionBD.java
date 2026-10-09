package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // Conserva aquí la URL que ya tienes.
    private static final String URL =
            "jdbc:mysql://72.60.145.210:3308/alojamientosguaviyu";

    private static final String USER = "mysql";

    // Configura esta variable en IntelliJ; no guardes la contraseña en el código.
    private static final String PASSWORD =
            System.getenv("ALOJAMIENTOS_DB_PASSWORD");

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