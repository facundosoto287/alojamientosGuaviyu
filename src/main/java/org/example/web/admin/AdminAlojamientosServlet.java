package org.example.web.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Alojamiento;
import org.example.AlojamientoDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Casos de uso: agregar, editar, eliminar, listar y buscar por nombre alojamientos.
 * GET  → listado (con búsqueda ?q=).
 * POST → accion = agregar | editar | eliminar.
 */
@WebServlet(AdminSesion.BASE + "/alojamientos")
public class AdminAlojamientosServlet extends AdminServlet {

    private final AlojamientoDAO alojamientoDAO = new AlojamientoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q = texto(request, "q");

        try {
            List<Alojamiento> alojamientos = q.isEmpty()
                    ? alojamientoDAO.listarAlojamientos()
                    : alojamientoDAO.buscarPorNombre(q);
            request.setAttribute("alojamientos", alojamientos);
        } catch (SQLException e) {
            log("Error al listar alojamientos", e);
            avisoError(request, "No pudimos cargar los alojamientos. Intentá de nuevo en unos minutos.");
        }

        request.setAttribute("q", q);
        mostrar(request, response, "alojamientos", "alojamientos", "Alojamientos");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = texto(request, "accion");

        try {
            switch (accion) {
                case "agregar" -> guardar(request, null);
                case "editar" -> guardar(request, entero(request, "id"));
                case "eliminar" -> eliminar(request);
                default -> avisoError(request, "Acción no reconocida.");
            }
        } catch (SQLException | RuntimeException e) {
            log("Error en alojamientos (" + accion + ")", e);
            avisoError(request, "No pudimos completar la operación. Intentá de nuevo en unos minutos.");
        }

        redirigir(request, response, "/alojamientos");
    }

    /** Agrega (id == null) o edita un alojamiento después de validar los datos. */
    private void guardar(HttpServletRequest request, Integer id) throws SQLException {
        boolean editando = id != null;

        if (editando && alojamientoDAO.buscarPorIdALO(id) == null) {
            avisoError(request, "El alojamiento ya no existe.");
            return;
        }

        String nombre = texto(request, "nombre");
        String descripcion = texto(request, "descripcion");
        String ubicacion = texto(request, "ubicacion");
        Integer capacidad = entero(request, "capacidad");
        Double precio = decimal(texto(request, "precio"));
        Double distancia = texto(request, "distanciaApiscina").isEmpty() ? Double.valueOf(0) : decimal(texto(request, "distanciaApiscina"));

        String error = null;
        if (nombre.isEmpty() || nombre.length() > 100) {
            error = "El nombre es obligatorio (hasta 100 caracteres).";
        } else if (descripcion.length() > 1000) {
            error = "La descripción puede tener hasta 1000 caracteres.";
        } else if (capacidad == null || capacidad < 1 || capacidad > 100) {
            error = "La capacidad debe ser un número entre 1 y 100.";
        } else if (precio == null || precio < 0 || precio > 10_000_000) {
            error = "El precio debe ser un número válido, mayor o igual a cero.";
        } else if (ubicacion.isEmpty() || ubicacion.length() > 150) {
            error = "La ubicación es obligatoria (hasta 150 caracteres).";
        } else if (distancia == null || distancia < 0 || distancia > 100_000) {
            error = "La distancia a la piscina debe ser un número válido, mayor o igual a cero.";
        }

        if (error != null) {
            avisoError(request, error);
            return;
        }

        Alojamiento alojamiento = new Alojamiento(editando ? id : 0, nombre, descripcion, capacidad, precio,
                ubicacion, distancia.floatValue());

        boolean exito = editando
                ? alojamientoDAO.modificarAlojamiento(alojamiento)
                : alojamientoDAO.agregarAlojamiento(alojamiento);

        if (exito) {
            avisoOk(request, editando ? "Alojamiento actualizado." : "Alojamiento agregado al catálogo.");
        } else {
            avisoError(request, "No se pudo guardar el alojamiento.");
        }
    }

    private void eliminar(HttpServletRequest request) throws SQLException {
        Integer id = entero(request, "id");
        if (id == null || alojamientoDAO.buscarPorIdALO(id) == null) {
            avisoError(request, "El alojamiento ya no existe.");
            return;
        }

        int reservas = alojamientoDAO.contarReservas(id);
        if (reservas > 0) {
            avisoError(request, "No se puede eliminar: el alojamiento tiene " + reservas
                    + " reserva(s) asociada(s). Cancelalas o conservalo en el catálogo.");
            return;
        }

        if (alojamientoDAO.eliminarAlojamiento(id)) {
            avisoOk(request, "Alojamiento eliminado.");
        } else {
            avisoError(request, "No se pudo eliminar el alojamiento.");
        }
    }

    /** Acepta "12500", "12500.50" y "12500,50". Devuelve null si no es un número. */
    private static Double decimal(String texto) {
        try {
            double valor = Double.parseDouble(texto.replace(',', '.'));
            return Double.isNaN(valor) || Double.isInfinite(valor) ? null : valor;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
