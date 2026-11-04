package org.example.web.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Cliente;
import org.example.ClienteDAO;
import org.example.ObservacionClienteDAO;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(AdminSesion.BASE + "/clientes/observaciones")
public class AdminObservacionesClientesServlet extends AdminServlet {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ObservacionClienteDAO observacionDAO = new ObservacionClienteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String q = texto(request, "q");
        Integer clienteId = entero(request, "cliente");

        try {
            // Lista de clientes
            request.setAttribute("clientes", q.isEmpty() ? clienteDAO.listarClientes() : clienteDAO.buscarPorNombreODocumento(q));

            // Cliente seleccionado
            if (clienteId != null) {
                Cliente cliente = clienteDAO.buscarPorId(clienteId);

                if (cliente != null) {
                    cliente.setContrasena(null);

                    request.setAttribute("clienteSeleccionado", cliente);

                    request.setAttribute("observaciones", observacionDAO.listarPorCliente(clienteId));
                }
            }

        } catch (SQLException e) {
            log("Error al cargar observaciones de clientes", e);

            avisoError(request, "No pudimos cargar las observaciones. Intentá de nuevo en unos minutos.");
        }

        request.setAttribute("q", q);
        mostrar(request, response, "observaciones-clientes", "clientes", "Observaciones de clientes");
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = texto(request, "accion");
        Integer clienteId = entero(request, "clienteId");

        try {
            switch (accion) {

                case "agregar" -> agregar(request, clienteId);

                case "editar" -> editar(request);

                case "eliminar" -> eliminar(request);

                default -> avisoError(request, "Acción no reconocida.");
            }

        } catch (IllegalArgumentException e) {
            avisoError(request, e.getMessage());

        } catch (SQLException | RuntimeException e) {
            log("Error en observaciones de clientes (" + accion + ")", e);
            avisoError(request, "No pudimos completar la operación. Intentá de nuevo en unos minutos.");
        }

        if (clienteId != null) {
            redirigir(request, response, "/clientes/observaciones?cliente=" + clienteId);
        } else {
            redirigir(request, response, "/clientes/observaciones");
        }
    }


    private void agregar(HttpServletRequest request, Integer clienteId) throws SQLException {
        if (clienteId == null || clienteDAO.buscarPorId(clienteId) == null) {
            avisoError(request, "El cliente ya no existe.");
            return;
        }

        String descripcion = texto(request, "descripcion");
        observacionDAO.agregarObservacion(clienteId, descripcion);
        avisoOk(request, "Observación agregada.");
    }


    private void editar(HttpServletRequest request) throws SQLException {
        Integer id = entero(request, "id");
        String descripcion = texto(request, "descripcion");

        if (id == null) {
            avisoError(request, "La observación no existe.");
            return;
        }

        if (observacionDAO.editarObservacion(id, descripcion)) {
            avisoOk(request, "Observación actualizada.");
        } else {
            avisoError(request, "No se pudo actualizar la observación.");
        }
    }


    private void eliminar(HttpServletRequest request) throws SQLException {
        Integer id = entero(request, "id");

        if (id == null) {
            avisoError(request, "La observación no existe.");
            return;
        }

        if (observacionDAO.eliminarObservacion(id)) {
            avisoOk(request, "Observación eliminada.");
        } else {
            avisoError(request, "No se pudo eliminar la observación.");
        }
    }
}