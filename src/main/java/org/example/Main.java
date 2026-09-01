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
    static ClienteDAO clienteDAO= new ClienteDAO();
    static ReservaDao reservaDao = new ReservaDao();

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
            System.out.println("\n===== MENÚ ADMINISTRADOR =====");
            System.out.println("1. Gestionar clientes");
            System.out.println("2. Gestionar servicios");
            System.out.println("3. Gestionar alojamientos");
            System.out.println("4. Gestionar reservas");
            System.out.println("5. Cerrar sesión");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elija una opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> menuClientes();
                case 2 -> menuServicios();
                case 3 -> menuAlojamientos();
                case 4 -> menuReservas();
                case 5 -> {
                    cerrarSesion();
                    opcion = 0;
                }
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0 && administradorActual != null);
    }

    // ===== Submenú de Cliente =====
    static void menuClientes() {
        int opcion;

        do {
            System.out.println("\n----- MENÚ CLIENTES -----");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Listar clientes");
            System.out.println("0. Volver");
            System.out.print("Elija una opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                //case 1 -> registrarCliente();
                case 2 -> listarClientes();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }
    static void menuServicios() {
        int opcion;

        do {
            System.out.println("\n----- MENÚ SERVICIOS -----");
            System.out.println("1. Agregar servicio");
            System.out.println("2. Listar servicios");
            System.out.println("3. Editar servicio");
            System.out.println("4. Eliminar servicio");
            System.out.println("0. Volver");
            System.out.print("Elija una opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> agregarServicio();
                case 2 -> listarServicios();
                case 3 -> editarServicio();
                case 4 -> eliminarServicio();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }
    static void menuAlojamientos() {
        int opcion;

        do {
            System.out.println("\n----- MENÚ ALOJAMIENTOS -----");
            System.out.println("1. Agregar alojamiento");
            System.out.println("2. Listar alojamientos");
            System.out.println("3. Editar alojamiento");
            System.out.println("4. Eliminar alojamiento");
            System.out.println("0. Volver");
            System.out.print("Elija una opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> agregarAlojamiento();
                case 2 -> listarAlojamientos();
                case 3 -> modificarAlojamiento();
                case 4 -> eliminarAlojamiento();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    static void menuReservas() {
        int opcion;

        do {
            System.out.println("\n----- MENÚ RESERVAS -----");
            System.out.println("1. Ver solicitudes pendientes");
            System.out.println("2. Confirmar reserva");
            System.out.println("3. Cancelar reserva");
            System.out.println("4. Ver reservas confirmadas");
            System.out.println("0. Volver");
            System.out.print("Elija una opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                //case 1 -> verSolicitudesPendientes();
                case 2 -> confirmarReserva();
                //case 3 -> cancelarReserva();
                //case 4 -> verReservasConfirmadas();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    static void agregarAlojamiento() {
        // Precondición: el administrador debe haber iniciado sesión
        if (administradorActual == null) {
            System.out.println("Debe iniciar sesión como administrador para agregar un alojamiento.");
            return;
        }

        Alojamiento nuevoAlojamiento = alojamientoDAO.pedirDatosNuevoAlojamiento(sc);
        boolean exito = alojamientoDAO.agregarAlojamiento(nuevoAlojamiento);

        if (exito) {
            System.out.println("Alojamiento registrado correctamente y disponible en el catálogo.");
        } else {
            System.out.println("No se pudo registrar el alojamiento (revise los datos ingresados).");
        }
    }

    static void listarAlojamientos() {
        List<Alojamiento> alojamientos = alojamientoDAO.listarAlojamientos();

        if (alojamientos.isEmpty()) {
            System.out.println("No hay alojamientos registrados.");
        } else {
            System.out.println("ALOJAMIENTOS:");
            for (Alojamiento a : alojamientos) {
                System.out.println(a);
            }
        }
    }

    static void modificarAlojamiento() {
        if (administradorActual == null) {
            System.out.println("Debe iniciar sesión como administrador para modificar un alojamiento.");
            return;
        }

        listarAlojamientos();

        Alojamiento alojamientoModificado = alojamientoDAO.pedirDatosModificarAlojamiento(sc);
        boolean exito = alojamientoDAO.modificarAlojamiento(alojamientoModificado);

        System.out.println(exito ? "Alojamiento modificado correctamente." : "No se pudo modificar (verifique el id).");
    }

    static void eliminarAlojamiento(){
        if (administradorActual == null){
            System.out.println("Debe iniciar sesión como administrador para eliminar un alojamiento.");
            return;
        }

        listarAlojamientos();

        System.out.println("Ingrese el id del alojamiento a eliminar:");
        int id = sc.nextInt();
        sc.nextLine();

        boolean eliminado = alojamientoDAO.eliminarAlojamiento(id);

        System.out.println(eliminado ? "El alojamiento se eliminó con éxito" : "No se encontró un alojamiento con ese id");
    }


    static void agregarServicio() {
        System.out.println("Ingrese el nombre del servicio:");
        String nombre = sc.nextLine();

        Servicio servicio = new Servicio(0, nombre); // el id se ignora, lo genera la base de datos

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
    static void editarServicio() {
        listarServicios();

        Servicio servicioExistente = null;
        int id = 0;

        while (servicioExistente == null) {
            System.out.println("Ingrese el id del servicio a editar:");
            id = sc.nextInt();
            sc.nextLine();

            try {
                servicioExistente = servicioDAO.buscarPorId(id);

                if (servicioExistente == null) {
                    System.out.println("No se encontró ningún servicio con ese id. Intente de nuevo.");
                }

            } catch (SQLException e) {
                System.out.println("Error al buscar el servicio: " + e.getMessage());
                return;
            }
        }

        System.out.println("Ingrese el nuevo nombre del servicio:");
        String nuevoNombre = sc.nextLine();

        System.out.println("¿Confirma que desea editar el servicio con id " + id + " y ponerle el nombre \"" + nuevoNombre + "\"? (s/n)");
        String confirmacion = sc.nextLine();

        if (!confirmacion.equalsIgnoreCase("s")) {
            System.out.println("Edición cancelada.");
            return;
        }

        try {
            boolean exito = servicioDAO.editarServicio(id, nuevoNombre);
            System.out.println(exito ? "Servicio editado correctamente." : "No se pudo editar el servicio.");
        } catch (SQLException e) {
            System.out.println("Error al editar el servicio: " + e.getMessage());
        }
    }

    static void eliminarServicio() {
        listarServicios();

        Servicio servicioExistente = null;
        int id = 0;

        while (servicioExistente == null) {
            System.out.println("Ingrese el id del servicio a eliminar:");
            id = sc.nextInt();
            sc.nextLine();

            try {
                servicioExistente = servicioDAO.buscarPorId(id);

                if (servicioExistente == null) {
                    System.out.println("No se encontró ningún servicio con ese id. Intente de nuevo.");
                }

            } catch (SQLException e) {
                System.out.println("Error al buscar el servicio: " + e.getMessage());
                return;
            }
        }

        System.out.println("¿Confirma que desea eliminar el servicio con id " + id + " (\"" + servicioExistente.getNombre() + "\")? (s/n)");
        String confirmacion = sc.nextLine();

        if (!confirmacion.equalsIgnoreCase("s")) {
            System.out.println("Eliminación cancelada.");
            return;
        }

        try {
            boolean exito = servicioDAO.eliminarServicio(id);
            System.out.println(exito ? "Servicio eliminado correctamente." : "No se pudo eliminar el servicio.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar el servicio: " + e.getMessage());
        }
    }

    static void listarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.listarClientes();

            if (clientes.isEmpty()) {
                System.out.println("No hay clientes registrados.");
            } else {
                System.out.println("===== CLIENTES =====");
                for (Cliente cliente : clientes) {
                    System.out.println(cliente.getId() + " - " + cliente.getNombre()
                            + " | Doc: " + cliente.getDocumennto()
                            + " | Email: " + cliente.getEmail());
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar los clientes: " + e.getMessage());
        }
    }

    //RESERVAS

    static void confirmarReserva(){

        System.out.println("Ingrese el id de la reserva a confirmar");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.println("¿Desea confirmar esta reserva? (s/n)");
        String confirmacion = sc.nextLine();

        if (!confirmacion.equalsIgnoreCase("s")) {
            System.out.println("Confirmacion cancelada.");
            return;
        }

        boolean confirmada = reservaDao.confirmarReserva(id);

        System.out.println(confirmada ? "Reserva confirmada con exito" : "No se pudo realizar la reserva");
    }
}


