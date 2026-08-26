package org.example;

import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static AdministradorDAO administradorDAO = new AdministradorDAO();

    // Guarda el administrador autenticado actualmente. Si es null, nadie inició sesión.
    static Administrador administradorActual = null;

    public static void main(String[] args) {
        int opcion;

        do {
            mostrarMenu();
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> iniciarSesion();
                case 2 -> cerrarSesion();
                case 0 -> System.out.println("Saliendo del programa...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    static void mostrarMenu() {
        System.out.println("\n===== MENÚ =====");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Cerrar sesión");
        System.out.println("0. Salir");
        System.out.print("Elija una opción: ");
    }

    static void iniciarSesion() {
        if (administradorActual != null) {
            System.out.println("Ya hay una sesión activa: " + administradorActual.getNombre());
            return;
        }

        System.out.println("Ingrese su correo:");
        String correo = sc.nextLine();

        System.out.println("Ingrese su contraseña:");
        String contrasena = sc.nextLine();

        Administrador admin = administradorDAO.iniciarSesion(correo, contrasena);

        if (admin != null) {
            administradorActual = admin; // guardamos quién está logueado
            System.out.println("Bienvenido, " + admin.getNombre() + ". Sesión iniciada correctamente.");
        } else {
            System.out.println("Correo o contraseña incorrectos.");
        }
    }

    static void cerrarSesion() {
        // Precondición: el administrador debe haber iniciado sesión
        if (administradorActual == null) {
            System.out.println("No hay ninguna sesión activa para cerrar.");
            return;
        }

        boolean exito = administradorDAO.cerrarSesion();

        if (exito) {
            System.out.println("Hasta luego, " + administradorActual.getNombre() + ".");
            administradorActual = null; // el sistema deja de reconocerlo como autenticado
        }
    }
}