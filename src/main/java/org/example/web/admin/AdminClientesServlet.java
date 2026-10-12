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
 * Casos de uso: registrar, listar, buscar por nombre o documento, eliminar cliente y ver su historial de reservas.
 * GET  → listado (?q=) y, con ?historial=id, las reservas de ese cliente.
 * POST → accion = registrar | eliminar.
 */
@WebServlet(AdminSesion.BASE + "/clientes")
public class AdminClientesServlet extends AdminServlet {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ReservaDao reservaDao = new ReservaDao();

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
                    request.setAttribute("historial", reservaDao.listarDetalle(null, null, historialId, null));
                }
            }
        } catch (SQLException e) {
            log("Error al listar clientes", e);
            avisoError(request, "No pudimos cargar los clientes. Intentá de nuevo en unos minutos.");
        }

        request.setAttribute("q", q);
        request.setAttribute("procedencias", LoginServlet.PROCEDENCIAS);
        mostrar(request, response, "clientes", "clientes", "Clientes");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = texto(request, "accion");

        try {
            switch (accion) {
                case "registrar" -> registrar(request);
                case "eliminar" -> eliminar(request);
                default -> avisoError(request, "Acción no reconocida.");
            }
        } catch (SQLException | RuntimeException e) {
            log("Error en clientes (" + accion + ")", e);
            avisoError(request, "No pudimos completar la operación. Intentá de nuevo en unos minutos.");
        }

        redirigir(request, response, "/clientes");
    }

    private void registrar(HttpServletRequest request) {
        Map<String, String> valores = new LinkedHashMap<>();
        valores.put("nombre", texto(request, "nombre"));
        valores.put("documento", texto(request, "documento"));
        valores.put("telefono", texto(request, "telefono"));
        valores.put("email", texto(request, "email"));
        valores.put("procedencia", texto(request, "procedencia"));

        Map<String, String> errores = ValidacionCliente.validarDatos(valores, clienteDAO);
        if (!errores.isEmpty()) {
            avisoError(request, String.join(" ", errores.values()));
            return;
        }

        try {
            clienteDAO.registrarClienteDesdeAdmin(
                    valores.get("nombre"), valores.get("documento"), valores.get("telefono"),
                    valores.get("email"), valores.get("procedencia"));
            avisoOk(request, "Cliente registrado con la contraseña por defecto. Deberá cambiarla en su primer ingreso.");
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
