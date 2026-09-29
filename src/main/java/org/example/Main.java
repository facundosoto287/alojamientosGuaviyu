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
    static ClienteDAO clienteDAO = new ClienteDAO();
    static ReservaDao reservaDao = new ReservaDao();
    static Cliente_reservaDAO clienteReservaDAO = new Cliente_reservaDAO();

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
            System.out.println("3. Buscar cliente por nombre o documento");
            System.out.println("4. Ver historial de reservas de un cliente");
            System.out.println("0. Volver");
            System.out.print("Elija una opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> registrarCliente();
                case 2 -> listarClientes();
                case 3 -> buscarClientePorNombreODocumento();
                case 4 -> verHistorialReservasCliente();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    static void buscarClientePorNombreODocumento() {
        // Precondición: debe existir al menos un cliente registrado
        List<Cliente> todosLosClientes;
        try {
            todosLosClientes = clienteDAO.listarClientes();
        } catch (SQLException e) {
            System.out.println("Error al verificar clientes: " + e.getMessage());
            return;
        }

        if (todosLosClientes.isEmpty()) {
            System.out.println("No hay clientes registrados en el sistema.");
            return;
        }

        System.out.println("Ingrese nombre o documento a buscar:");
        String textoBusqueda = sc.nextLine();

        try {
            List<Cliente> encontrados = clienteDAO.buscarPorNombreODocumento(textoBusqueda);

            if (encontrados.isEmpty()) {
                System.out.println("No se encontraron clientes que coincidan con \"" + textoBusqueda + "\".");
            } else {
                System.out.println("CLIENTES ENCONTRADOS:");
                for (Cliente cliente : encontrados) {
                    System.out.println(cliente.getId() + " - " + cliente.getNombre()
                            + " | Doc: " + cliente.getDocumento()
                            + " | Tel: " + cliente.getTelefono()
                            + " | Email: " + cliente.getEmail()
                            + " | Procedencia: " + cliente.getProcedencia());
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar clientes: " + e.getMessage());
        }
    }
    static void verHistorialReservasCliente() {
        listarClientes();

        System.out.println("Ingrese el id del cliente:");
        int idCliente = sc.nextInt();
        sc.nextLine();

        List<Reserva> reservas = reservaDao.verHistorialPorCliente(idCliente);

        if (reservas.isEmpty()) {
            System.out.println("Este cliente no tiene reservas registradas.");
        } else {
            System.out.println("===== HISTORIAL DE RESERVAS =====");
            for (Reserva r : reservas) {
                imprimirReserva(r);
            }
        }
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
            System.out.println("5. Asociar servicio a alojamiento");
            System.out.println("6. Desasociar servicio de un alojamiento");
            System.out.println("7. Ver servicios de un alojamiento");
            System.out.println("8. Buscar alojamiento por nombre");
            System.out.println("9. Filtrar alojamiento por servicio");
            System.out.println("0. Volver");
            System.out.print("Elija una opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> agregarAlojamiento();
                case 2 -> listarAlojamientos();
                case 3 -> modificarAlojamiento();
                case 4 -> eliminarAlojamiento();
                case 5 -> asociarServicioAalojamiento();
                case 6 -> desasociarServicioDeUnAlojamiento();
                case 7 -> verServiciosDeUnAlojamiento();
                case 8 -> buscarAlojamientoPorNombre();
                case 9 -> filtrarAlojamientoPorServicio();
                case 0 -> System.out.println("Volviendo...");
                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);
    }

    static void menuReservas() {
        int opcion;

        do {
            System.out.println("\n----- MENÚ RESERVAS -----");
            System.out.println("1. Crear reserva");
            System.out.println("2. Ver solicitudes pendientes");
            System.out.println("3. Confirmar reserva");
            System.out.println("4. Cancelar reserva");
            System.out.println("5. Ver reservas confirmadas");
            System.out.println("6. Ver reservas canceladas");
            System.out.println("7. Ver todas las reservas");
            System.out.println("8. Buscar reserva por id");
            System.out.println("9. Ver reservas de un alojamiento");
            System.out.println("0. Volver");
            System.out.print("Elija una opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> crearReserva();
                case 2 -> listarSolicitudesPendientes();
                case 3 -> confirmarReserva();
                case 4 -> cancelarReserva();
                case 5 -> listarReservasConfirmadas();
                case 6 -> listarReservasCanceladas();
                case 7 -> listarTodasLasReservas();
                case 8 -> buscarReservaPorId();
                case 9 -> verReservasDeAlojamiento();
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
                System.out.println(a.getId() + " - " + a.getNombre()
                        + " | Descripción: " + a.getDescripcion()
                        + " | Capacidad: " + a.getCapacidad()
                        + " | Precio: " + a.getPrecio()
                        + " | Ubicación: " + a.getUbicacion()
                        + " | Dist. piscina: " + a.getDistanciaApiscina() + "m");
            }
        }
    }
    static void filtrarAlojamientoPorServicio() {
        // Precondición: deben existir servicios y al menos un alojamiento con servicios asociados
        List<Servicio> servicios;
        try {
            servicios = servicioDAO.listarServicios();
        } catch (SQLException e) {
            System.out.println("Error al obtener los servicios: " + e.getMessage());
            return;
        }

        if (servicios.isEmpty()) {
            System.out.println("No hay servicios registrados en el sistema.");
            return;
        }

        System.out.println("SERVICIOS DISPONIBLES:");
        for (Servicio s : servicios) {
            System.out.println(s.getId() + " - " + s.getNombre());
        }

        System.out.println("Ingrese los id de los servicios que desea (separados por coma, ej: 1,3,5):");
        String entrada = sc.nextLine();

        List<Integer> idsSeleccionados = new ArrayList<>();
        for (String texto : entrada.split(",")) {
            try {
                idsSeleccionados.add(Integer.parseInt(texto.trim()));
            } catch (NumberFormatException e) {
                System.out.println("Se ignoró un valor inválido: " + texto);
            }
        }

        if (idsSeleccionados.isEmpty()) {
            System.out.println("No seleccionó ningún servicio válido.");
            return;
        }

        List<Alojamiento> resultados = alojamientoDAO.filtrarPorServicios(idsSeleccionados);

        if (resultados.isEmpty()) {
            System.out.println("No se encontraron alojamientos con esa combinación de servicios. Intente quitar algún filtro.");
        } else {
            System.out.println("===== ALOJAMIENTOS ENCONTRADOS =====");
            for (Alojamiento a : resultados) {
                System.out.println(a.getId() + " - " + a.getNombre()
                        + " | Capacidad: " + a.getCapacidad()
                        + " | Precio: " + a.getPrecio()
                        + " | Ubicación: " + a.getUbicacion());
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

    static void eliminarAlojamiento() {
        if (administradorActual == null) {
            System.out.println("Debe iniciar sesión como administrador para eliminar un alojamiento.");
            return;
        }

        listarAlojamientos();

        System.out.println("Ingrese el id del alojamiento a eliminar:");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.println("¿Confirma que desea eliminar el alojamiento?(s/n)");
        String confirmacion = sc.nextLine();

        if (!confirmacion.equalsIgnoreCase("s")) {
            System.out.println("Accion cancelada.");
            return;
        }

        boolean eliminado = alojamientoDAO.eliminarAlojamiento(id);

        System.out.println(eliminado ? "El alojamiento se eliminó con éxito" : "No se encontró un alojamiento con ese id");
    }

    static void asociarServicioAalojamiento(){
        listarAlojamientos();

        try {
            System.out.println("Ingrese el id del alojamiento:");
            int idAlojamiento = sc.nextInt();
            sc.nextLine();

            listarServicios();

            System.out.println("Ingrese el id del servicio:");
            int idServicio = sc.nextInt();
            sc.nextLine();

            System.out.println("¿Confirma que desea agregar el servicio al alojamiento?(s/n)");
            String confirmacion = sc.nextLine();

            if (!confirmacion.equalsIgnoreCase("s")) {
                System.out.println("Asociacion cancelada.");
                return;
            }

            boolean asociado = alojamientoDAO.asociarServicioAalojamiento(idAlojamiento, idServicio);

            System.out.println(asociado ? "El servicio fue asociado correctamente" : "No se pudo asociar el servicio");
        } catch (Exception e) {
            System.out.println("Debe ingresar ids válidos.");
            sc.nextLine();
        }
    }

    static void desasociarServicioDeUnAlojamiento(){

        listarAlojamientos();

        try {
            System.out.println("Ingrese el id del alojamiento:");
            int idAlojamiento = sc.nextInt();
            sc.nextLine();

            List<Servicio> servicios = alojamientoDAO.verServiciosDeUnAlojamiento(idAlojamiento);

            if (servicios.isEmpty()) {
                System.out.println("El alojamiento no tiene servicios asociados");
                return;
            }

            System.out.println("Servicios asociados");
            for (Servicio servicio : servicios) {
                System.out.println(
                        servicio.getId() + " - " + servicio.getNombre()
                );
            }

            System.out.println("Ingrese el id del servicio a desasociar");
            int idServicio = sc.nextInt();
            sc.nextLine();

            System.out.println("¿Confirma que desea desasociar el servicio del alojamiento?(s/n)");
            String confirmacion = sc.nextLine();

            if (!confirmacion.equalsIgnoreCase("s")) {
                System.out.println("Accion cancelada.");
                return;
            }
            boolean desasociado = alojamientoDAO.desasociarServicioDeUnAlojamiento(idAlojamiento, idServicio);

            System.out.println(desasociado ? "El servicio fue desasociado correctamente" : "No se pudo desasociar el servicio");
        } catch (Exception e) {
            System.out.println("Debe ingresar un id válido.");
            sc.nextLine();
        }
    }

    static void verServiciosDeUnAlojamiento(){
        listarAlojamientos();

        try {
            System.out.println("Ingrese el id del alojamiento:");
            int idAlojamiento = sc.nextInt();
            sc.nextLine();

            List<Servicio> servicios = alojamientoDAO.verServiciosDeUnAlojamiento(idAlojamiento);
            if(servicios.isEmpty()){
                System.out.println("El alojamiento no tiene servicios asociados");
                return;
            }

            System.out.println("Servicios del alojamiento:");
            for(Servicio servicio : servicios){
                System.out.println(servicio.getId() + "-" + servicio.getNombre());
            }
        } catch (Exception e){
            System.out.println("Debe ingresar un id valido");
            sc.nextLine();
        }
    }

    static void buscarAlojamientoPorNombre() {
        System.out.println("Ingrese el nombre a buscar:");
        String nombre = sc.nextLine();

        try {
            List<Alojamiento> resultados = alojamientoDAO.buscarPorNombre(nombre);

            if (resultados.isEmpty()) {
                System.out.println("No se encontraron alojamientos con ese nombre.");
            } else {
                System.out.println("===== RESULTADOS =====");
                for (Alojamiento a : resultados) {
                    System.out.println(a.getId() + " - " + a.getNombre()
                            + " | Capacidad: " + a.getCapacidad()
                            + " | Precio: " + a.getPrecio()
                            + " | Ubicación: " + a.getUbicacion());
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar el alojamiento: " + e.getMessage());
        }
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

        System.out.println("¿Confirma que desea editar el servicio con id " + id
                + " y ponerle el nombre \"" + nuevoNombre + "\"? (s/n)");
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

        System.out.println("¿Confirma que desea eliminar el servicio con id " + id
                + " (\"" + servicioExistente.getNombre() + "\")? (s/n)");
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

    static void registrarCliente() {
        Cliente datosCliente = clienteDAO.pedirDatosNuevoCliente(sc);

        System.out.println("¿Confirma el registro con los siguientes datos?");
        System.out.println("Nombre: " + datosCliente.getNombre());
        System.out.println("Documento: " + datosCliente.getDocumento());
        System.out.println("Email: " + datosCliente.getEmail());
        System.out.println("(s/n)");
        String confirmacion = sc.nextLine();

        if (!confirmacion.equalsIgnoreCase("s")) {
            System.out.println("Registro cancelado.");
            return;
        }

        try {
            Cliente nuevoCliente = clienteDAO.registrarCliente(
                    datosCliente.getNombre(),
                    datosCliente.getDocumento(),
                    datosCliente.getTelefono(),
                    datosCliente.getEmail(),
                    datosCliente.getProcedencia(),
                    datosCliente.getContrasena()
            );
            System.out.println("Cliente registrado correctamente: " + nuevoCliente.getNombre());
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo registrar: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Error al registrar el cliente: " + e.getMessage());
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
                            + " | Doc: " + cliente.getDocumento()
                            + " | Tel: " + cliente.getTelefono()
                            + " | Email: " + cliente.getEmail()
                            + " | Procedencia: " + cliente.getProcedencia());
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar los clientes: " + e.getMessage());
        }
    }
    static void eliminarCliente() {
        listarClientes();

        Cliente clienteExistente = null;
        int id = 0;

        while (clienteExistente == null) {
            System.out.println("Ingrese el id del cliente a eliminar:");
            id = sc.nextInt();
            sc.nextLine();

            try {
                clienteExistente = clienteDAO.buscarPorId(id);

                if (clienteExistente == null) {
                    System.out.println("No se encontró ningún cliente con ese id. Intente de nuevo.");
                }

            } catch (SQLException e) {
                System.out.println("Error al buscar el cliente: " + e.getMessage());
                return;
            }
        }

        System.out.println("¿Confirma que desea eliminar al cliente con id " + id + " (\"" + clienteExistente.getNombre() + "\")? (s/n)");
        String confirmacion = sc.nextLine();

        if (!confirmacion.equalsIgnoreCase("s")) {
            System.out.println("Eliminación cancelada.");
            return;
        }

        try {
            boolean exito = clienteDAO.eliminarCliente(id);
            System.out.println(exito ? "Cliente eliminado correctamente." : "No se pudo eliminar el cliente.");
        } catch (SQLException e) {
            System.out.println("Error al eliminar el cliente: " + e.getMessage());
        }
    }
    // ===== RESERVAS =====

    static void crearReserva() {
        // Precondición: el administrador debe haber iniciado sesión
        if (administradorActual == null) {
            System.out.println("Debe iniciar sesión como administrador para crear una reserva.");
            return;
        }

        List<Cliente> clientes;
        try {
            clientes = clienteDAO.listarClientes();
        } catch (SQLException e) {
            System.out.println("Error al verificar clientes: " + e.getMessage());
            return;
        }

        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados. Debe registrar al menos un cliente antes de crear una reserva.");
            return;
        }

        listarClientes();

        int idCliente = -1;
        boolean clienteValido = false;

        while (!clienteValido) {
            System.out.println("Ingrese el id del cliente para el cual se registra la reserva:");
            idCliente = sc.nextInt();
            sc.nextLine();

            clienteValido = clienteDAO.existeClientePorId(idCliente);

            if (!clienteValido) {
                System.out.println("No existe ningún cliente con ese id. Intente de nuevo.");
            }
        }

        listarAlojamientos();

        Reserva nuevaReserva = reservaDao.pedirDatosNuevaReserva(sc);
        int idReservaGenerado = reservaDao.crear(nuevaReserva);

        if (idReservaGenerado == -1) {
            System.out.println("No se pudo crear la reserva.");
            return;
        }

        Cliente_reserva relacion = new Cliente_reserva(String.valueOf(idCliente), String.valueOf(idReservaGenerado));
        boolean exitoRelacion = clienteReservaDAO.crear(relacion);

        if (exitoRelacion) {
            System.out.println("Reserva #" + idReservaGenerado + " creada y vinculada al cliente correctamente.");
        } else {
            System.out.println("La reserva se creó, pero no se pudo vincular al cliente (revise el id del cliente).");
        }
    }

    static void listarSolicitudesPendientes() {
        List<Reserva> pendientes = reservaDao.listarPendientes();

        if (pendientes.isEmpty()) {
            System.out.println("No hay solicitudes de reserva pendientes.");
        } else {
            System.out.println("SOLICITUDES PENDIENTES:");
            for (Reserva r : pendientes) {
                imprimirReserva(r);
            }
        }
    }

    static void listarReservasConfirmadas() {
        List<Reserva> confirmadas = reservaDao.listarConfirmadas();

        if (confirmadas.isEmpty()) {
            System.out.println("No hay reservas confirmadas.");
        } else {
            System.out.println("RESERVAS CONFIRMADAS:");
            for (Reserva r : confirmadas) {
                imprimirReserva(r);
            }
        }
    }

    static void listarReservasCanceladas() {
        List<Reserva> canceladas = reservaDao.listarPorEstado(EstadoReserva.CANCELADA);

        if (canceladas.isEmpty()) {
            System.out.println("No hay reservas canceladas.");
        } else {
            System.out.println("RESERVAS CANCELADAS:");
            for (Reserva r : canceladas) {
                imprimirReserva(r);
            }
        }
    }

    static void listarTodasLasReservas() {
        List<Reserva> pendientes = reservaDao.listarPorEstado(EstadoReserva.PENDIENTE);
        List<Reserva> confirmadas = reservaDao.listarPorEstado(EstadoReserva.CONFIRMADA);
        List<Reserva> canceladas = reservaDao.listarPorEstado(EstadoReserva.CANCELADA);

        if (pendientes.isEmpty() && confirmadas.isEmpty() && canceladas.isEmpty()) {
            System.out.println("No hay reservas registradas.");
            return;
        }

        System.out.println("\n----- PENDIENTES -----");
        if (pendientes.isEmpty()) {
            System.out.println("No hay reservas pendientes.");
        } else {
            for (Reserva r : pendientes) {
                imprimirReserva(r);
            }
        }

        System.out.println("\n----- CONFIRMADAS -----");
        if (confirmadas.isEmpty()) {
            System.out.println("No hay reservas confirmadas.");
        } else {
            for (Reserva r : confirmadas) {
                imprimirReserva(r);
            }
        }

        System.out.println("\n----- CANCELADAS -----");
        if (canceladas.isEmpty()) {
            System.out.println("No hay reservas canceladas.");
        } else {
            for (Reserva r : canceladas) {
                imprimirReserva(r);
            }
        }
    }

    static void confirmarReserva() {
        List<Reserva> pendientes = reservaDao.listarPendientes();

        if (pendientes.isEmpty()) {
            System.out.println("No hay solicitudes de reserva pendientes para confirmar.");
            return;
        }

        System.out.println("SOLICITUDES PENDIENTES:");
        for (Reserva r : pendientes) {
            imprimirReserva(r);
        }

        System.out.println("Ingrese el id de la reserva a confirmar:");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.println("¿Desea confirmar esta reserva? (s/n)");
        String confirmacion = sc.nextLine();

        if (!confirmacion.equalsIgnoreCase("s")) {
            System.out.println("Confirmación cancelada.");
            return;
        }

        boolean confirmada = reservaDao.confirmarReserva(id);

        System.out.println(confirmada ? "Reserva confirmada con éxito" : "No se pudo confirmar la reserva (verifique el id o que esté pendiente).");
    }

    static void cancelarReserva() {
        List<Reserva> pendientes = reservaDao.listarPendientes();

        if (pendientes.isEmpty()) {
            System.out.println("No hay solicitudes de reserva pendientes para cancelar.");
            return;
        }

        System.out.println("SOLICITUDES PENDIENTES:");
        for (Reserva r : pendientes) {
            imprimirReserva(r);
        }

        System.out.println("Ingrese el id de la reserva a cancelar:");
        int id = sc.nextInt();
        sc.nextLine();

        boolean exito = reservaDao.cancelarReserva(id);
        System.out.println(exito ? "Reserva cancelada correctamente." : "No se pudo cancelar (verifique el id o que esté pendiente).");
    }
    static void imprimirReserva(Reserva r) {
        System.out.println(r.getId() + " - Ingreso: " + r.getFechaIngreso()
                + " | Salida: " + r.getFechaSalida()
                + " | Huéspedes: " + r.getCantidadHuespedes()
                + " | Estado: " + r.getEstado()
                + " | Solicitado: " + r.getFechaSolicitud()
                + " | Alojamiento: " + r.getAlojamientoId());
    }

    static void buscarReservaPorId() {
        try {
            System.out.println("Ingrese el id de la reserva:");
            int id = sc.nextInt();
            sc.nextLine();

            Reserva reserva = reservaDao.buscarReservaPorId(id);

            if (reserva == null) {
                System.out.println("No se encontró una reserva con ese id.");
                return;
            }
            System.out.println("Id: " + reserva.getId());
            System.out.println("Fecha de ingreso: " + reserva.getFechaIngreso());
            System.out.println("Fecha de salida: " + reserva.getFechaSalida());
            System.out.println("Cantidad de huéspedes: " + reserva.getCantidadHuespedes());
            System.out.println("Estado: " + reserva.getEstado());
            System.out.println("Fecha de solicitud: " + reserva.getFechaSolicitud());
            System.out.println("Id alojamiento: " + reserva.getAlojamientoId());
        } catch (Exception e) {
            System.out.println("Debe ingresar un id valido");
            sc.nextLine();
        }
    }
    static void verReservasDeAlojamiento() {
        List<Alojamiento> alojamientos = alojamientoDAO.listarAlojamientos();

        if (alojamientos.isEmpty()) {
            System.out.println("No hay alojamientos registrados.");
            return;
        }

        listarAlojamientos();

        int idAlojamiento = -1;
        boolean alojamientoValido = false;

        while (!alojamientoValido) {
            System.out.println("Ingrese el id del alojamiento para ver sus reservas:");
            idAlojamiento = sc.nextInt();
            sc.nextLine();

            alojamientoValido = alojamientoDAO.existeAlojamientoPorId(idAlojamiento);

            if (!alojamientoValido) {
                System.out.println("No existe ningún alojamiento con ese id. Intente de nuevo.");
            }
        }

        List<Reserva> reservas = reservaDao.listarPorAlojamiento(idAlojamiento);

        if (reservas.isEmpty()) {
            System.out.println("Este alojamiento no tiene reservas registradas.");
        } else {
            System.out.println("RESERVAS DEL ALOJAMIENTO #" + idAlojamiento + ":");
            for (Reserva r : reservas) {
                imprimirReserva(r);
            }
        }
    }


}