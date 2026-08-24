package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // Configuración de la base de datos
    private static final String URL = "jdbc:mariadb://localhost:3306/alojamientosguaviyu";
    private static final String USER = "root";     // Tu usuario de MariaDB
    private static final String PASSWORD = "";     // Tu contraseña (déjala vacía "" si usas XAMPP por defecto)

    public static Connection obtenerConexion() {
        Connection conexion = null;
        try {
            // Establece la conexión mediante el Driver de MariaDB
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("¡Conexión exitosa a la base de datos 'alojamientosguaviyu'!");
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos:");
            e.printStackTrace();
        }
        return conexion;
    }
}
