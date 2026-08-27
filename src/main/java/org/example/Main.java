package org.example;

import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static AdministradorDAO administradorDAO = new AdministradorDAO();
    static AlojamientoDAO alojamientoDAO = new AlojamientoDAO();
    static ServicioDAO servicioDAO = new ServicioDAO();
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
            administradorActual = admin;
            System.out.println("Bienvenido, " + admin.getNombre() + ". Sesión iniciada correctamente.");
            menuAdministrador();
        } else {
            System.out.println("Correo o contraseña incorrectos.");
        }
    }

    static void cerrarSesion() {
        if (administradorActual == null) {
            System.out.println("No hay ninguna sesión activa para cerrar.");
            return;
        }

        boolean exito = administradorDAO.cerrarSesion();

        if (exito) {
            System.out.println("Hasta luego, " + administradorActual.getNombre() + ".");
            administradorActual = null;
        }
    }
    static void menuAdministrador() {
        int opcion;

        do {
            System.out.println("\nMENÚ ADMINISTRADOR:");
            System.out.println("3. Registrar cliente");
            System.out.println("4. Agregar servicio");
            System.out.println("5. Listar servicios");
            System.out.println("2. Cerrar sesión");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elija una opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
              //  case 3 -> registrarCliente();
                case 4 -> agregarServicio();
                case 5 -> listarServicios();
                case 2 -> {
                    cerrarSesion();
                    opcion = 0; // si cierra sesión, salimos del submenú
                }
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0 && administradorActual != null);
    }

    static void agregarAlojamiento() {
        // Precondición: el administrador debe haber iniciado sesión
        if (administradorActual == null) {
            System.out.println("Debe iniciar sesión como administrador para agregar un alojamiento.");
            return;
        }

        System.out.println("Ingrese el nombre del alojamiento:");
        String nombre = sc.nextLine();

        System.out.println("Ingrese la descripción:");
        String descripcion = sc.nextLine();

        System.out.println("Ingrese la capacidad (personas):");
        int capacidad = sc.nextInt();

        System.out.println("Ingrese el precio:");
        double precio = sc.nextDouble();

        System.out.println("Ingrese la ubicación:");
        sc.nextLine();
        String ubicacion = sc.nextLine();

        System.out.println("Ingrese la distancia a la piscina (metros):");
        float distanciaApiscina = sc.nextFloat();
        sc.nextLine();

        Alojamiento nuevoAlojamiento = new Alojamiento(0, nombre, descripcion, capacidad, precio, ubicacion, distanciaApiscina);
        boolean exito = alojamientoDAO.agregarAlojamiento(nuevoAlojamiento);

        if (exito) {
            System.out.println("Alojamiento registrado correctamente y disponible en el catálogo.");
        } else {
            System.out.println("No se pudo registrar el alojamiento (revise los datos ingresados).");
        }
    }
    static void agregarServicio() {
        System.out.println("Ingrese el id del servicio:");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.println("Ingrese el nombre del servicio:");
        String nombre = sc.nextLine();

        Servicio servicio = new Servicio(id, nombre);

        try {
            boolean exito = servicioDAO.agregarServicio(servicio);

            if (exito) {
                System.out.println("Servicio agregado correctamente.");
            } else {
                System.out.println("No se pudo agregar el servicio.");
            }

        } catch (SQLException e) {
            System.out.println("Error al agregar el servicio: " + e.getMessage());
        }
    }
    static void listarServicios() {
        try {
            List<Servicio> servicios = servicioDAO.listarServicios();

            if (servicios.isEmpty()) {
                System.out.println("No hay servicios cargados.");
            } else {
                System.out.println("SERVICIOS:");
                for (Servicio servicio : servicios) {
                    System.out.println(servicio.getId() + " - " + servicio.getNombre());
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar los servicios: " + e.getMessage());
        }
    }
}


