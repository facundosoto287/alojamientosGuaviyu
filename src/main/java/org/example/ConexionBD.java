package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL =
            "jdbc:mysql://72.60.145.210:3308/alojamientosguaviyu";

    private static final String USER = "mysql";
    private static final String PASSWORD = "6bmlxfglapxdu5hl";

    public static Connection obtenerConexion() {
        try {
            Connection conexion = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            return conexion;

        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos:");
            e.printStackTrace();
            return null;
        }
    }
}