package org.example.web.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Cliente;
import org.example.ClienteDAO;
import org.example.ReservaDao;
import org.example.web.LoginServlet;
import org.example.web.ValidacionCliente;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Casos de uso: registrar, listar, buscar por nombre o documento, editar y eliminar cliente, y ver su historial
 * de reservas.
 * GET  → listado (?q=) y, con ?historial=id, las reservas de ese cliente.
 * POST → accion = registrar | editar | eliminar.
 */
@WebServlet(AdminSesion.BASE + "/clientes")
public class AdminClientesServlet extends AdminServlet {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ReservaDao ReservaDAO = new ReservaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q = texto(request, "q");
        Integer historialId = entero(request, "historial");

        try {
            request.setAttribute("clientes",
                    q.isEmpty() ? clienteDAO.listarClientes() : clienteDAO.buscarPorNombreODocumento(q));

            if (historialId != null) {
                Cliente cliente = clienteDAO.buscarPorId(historialId);
                if (cliente != null) {
                    cliente.setContrasena(null); // el hash nunca llega a la página
                    request.setAttribute("clienteHistorial", cliente);
                    request.setAttribute("historial", ReservaDAO.listarDetalle(null, null, historialId, null));
                }
            }
        } catch (SQLException e) {
            log("Error al listar clientes", e);
            avisoError(request, "No pudimos cargar los clientes. Intentá de nuevo en unos minutos.");
        }

        request.setAttribute("q", q);
        request.setAttribute("procedencias", LoginServlet.PROCEDENCIAS);
        request.setAttribute("localidades", LoginServlet.sugerenciasDeLocalidad());
        mostrar(request, response, "clientes", "clientes", "Clientes");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = texto(request, "accion");

        try {
            switch (accion) {
                case "registrar" -> registrar(request);
                case "editar" -> editar(request);
                case "eliminar" -> eliminar(request);
                default -> avisoError(request, "Acción no reconocida.");
            }
        } catch (SQLException | RuntimeException e) {
            log("Error en clientes (" + accion + ")", e);
            avisoError(request, "No pudimos completar la operación. Intentá de nuevo en unos minutos.");
        }

        redirigir(request, response, "/clientes");
    }

    /** Los datos personales tal como llegan de los formularios de registrar y editar. */
    private Map<String, String> datosDelFormulario(HttpServletRequest request) {
        Map<String, String> valores = new LinkedHashMap<>();
        valores.put("nombre", texto(request, "nombre"));
        valores.put("documento", texto(request, "documento"));
        valores.put("telefono", texto(request, "telefono"));
        valores.put("email", texto(request, "email"));
        valores.put("procedencia", texto(request, "procedencia"));
        valores.put("localidad", texto(request, "localidad"));
        return valores;
    }

    private void editar(HttpServletRequest request) throws SQLException {
        Integer id = entero(request, "id");
        Cliente actual = id == null ? null : clienteDAO.buscarPorId(id);
        if (actual == null) {
            avisoError(request, "El cliente ya no existe.");
            return;
        }

        Map<String, String> valores = datosDelFormulario(request);

        // Una procedencia antigua que no está en la lista no se puede mostrar en el desplegable: se conserva
        if (valores.get("procedencia").isEmpty() && actual.getProcedencia() != null
                && !LoginServlet.PROCEDENCIAS.contains(actual.getProcedencia())) {
            valores.put("procedencia", actual.getProcedencia().trim());
        }

        Map<String, String> errores = ValidacionCliente.validarCambios(valores, actual, clienteDAO);
        if (!errores.isEmpty()) {
            avisoError(request, "No se guardaron los cambios. " + String.join(" ", errores.values()));
            return;
        }

        try {
            clienteDAO.actualizarDesdeAdmin(actual,
                    valores.get("nombre"), valores.get("documento"), valores.get("telefono"),
                    valores.get("email"), valores.get("procedencia"), valores.get("localidad"));
            avisoOk(request, "Datos del cliente actualizados.");
        } catch (IllegalArgumentException e) {
            avisoError(request, e.getMessage()); // correo o documento de otro cliente
        }
    }

    private void registrar(HttpServletRequest request) {
        Map<String, String> valores = datosDelFormulario(request);

        Map<String, String> errores = ValidacionCliente.validarDatosAdmin(valores, clienteDAO);
        if (!errores.isEmpty()) {
            avisoError(request, String.join(" ", errores.values()));
            return;
        }

        try {
            clienteDAO.registrarClienteDesdeAdmin(
                    valores.get("nombre"), valores.get("documento"), valores.get("telefono"),
                    valores.get("email"), valores.get("procedencia"), valores.get("localidad"));
            avisoOk(request, valores.get("email").isEmpty()
                    ? "Cliente registrado. No tiene correo, así que todavía no puede ingresar a la web: se lo podés agregar con Editar."
                    : "Cliente registrado con la contraseña por defecto. Deberá cambiarla en su primer ingreso.");
        } catch (IllegalArgumentException e) {
            avisoError(request, e.getMessage()); // correo o documento repetido
        }
    }

    private void eliminar(HttpServletRequest request) throws SQLException {
        Integer id = entero(request, "id");
        if (id == null || clienteDAO.buscarPorId(id) == null) {
            avisoError(request, "El cliente ya no existe.");
            return;
        }

        if (clienteDAO.tieneReservas(id)) {
            avisoError(request, "No se puede eliminar: el cliente tiene reservas asociadas.");
            return;
        }

        if (clienteDAO.eliminarCliente(id)) {
            avisoOk(request, "Cliente eliminado.");
        } else {
            avisoError(request, "No se pudo eliminar el cliente.");
        }
    }
}
