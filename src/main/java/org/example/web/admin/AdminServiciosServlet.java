package org.example.web.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.AlojamientoDAO;
import org.example.Servicio;
import org.example.ServicioDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Casos de uso: agregar, editar, eliminar y listar servicios; ver, asociar y desasociar servicios de un alojamiento.
 * GET  → listado de servicios y panel de asociaciones (?alojamiento=id).
 * POST → accion = agregar | editar | eliminar | asociar | desasociar.
 */
@WebServlet(AdminSesion.BASE + "/servicios")
public class AdminServiciosServlet extends AdminServlet {

    private final ServicioDAO servicioDAO = new ServicioDAO();
    private final AlojamientoDAO alojamientoDAO = new AlojamientoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Servicio> servicios = servicioDAO.listarServicios();

            // Cuántos alojamientos usan cada servicio (para avisar antes de eliminarlo)
            Map<Integer, Integer> usos = new LinkedHashMap<>();
            for (Servicio servicio : servicios) {
                usos.put(servicio.getId(), servicioDAO.contarAlojamientos(servicio.getId()));
            }

            Integer alojamientoId = entero(request, "alojamiento");
            Set<Integer> asociados = new HashSet<>();
            if (alojamientoId != null && alojamientoDAO.buscarPorIdALO(alojamientoId) != null) {
                List<Servicio> delAlojamiento = alojamientoDAO.verServiciosDeUnAlojamiento(alojamientoId);
                request.setAttribute("serviciosDelAlojamiento", delAlojamiento);
                for (Servicio s : delAlojamiento) {
                    asociados.add(s.getId());
                }
            } else {
                alojamientoId = null;
            }

            request.setAttribute("servicios", servicios);
            request.setAttribute("usos", usos);
            request.setAttribute("alojamientos", alojamientoDAO.listarAlojamientos());
            request.setAttribute("alojamientoSel", alojamientoId);
            request.setAttribute("asociados", asociados);
        } catch (SQLException e) {
            log("Error al cargar servicios", e);
            avisoError(request, "No pudimos cargar los servicios. Intentá de nuevo en unos minutos.");
        }

        mostrar(request, response, "servicios", "servicios", "Servicios y asociaciones");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = texto(request, "accion");
        Integer alojamientoId = entero(request, "alojamiento");

        try {
            switch (accion) {
                case "agregar" -> agregar(request);
                case "editar" -> editar(request);
                case "eliminar" -> eliminar(request);
                case "asociar" -> asociar(request, alojamientoId);
                case "desasociar" -> desasociar(request, alojamientoId);
                default -> avisoError(request, "Acción no reconocida.");
            }
        } catch (SQLException | RuntimeException e) {
            log("Error en servicios (" + accion + ")", e);
            avisoError(request, "No pudimos completar la operación. Intentá de nuevo en unos minutos.");
        }

        redirigir(request, response, alojamientoId == null ? "/servicios" : "/servicios?alojamiento=" + alojamientoId);
    }

    private void agregar(HttpServletRequest request) throws SQLException {
        String nombre = texto(request, "nombre");
        String error = validarNombre(nombre, 0);
        if (error != null) {
            avisoError(request, error);
            return;
        }

        if (servicioDAO.agregarServicio(new Servicio(0, nombre))) {
            avisoOk(request, "Servicio agregado.");
        } else {
            avisoError(request, "No se pudo agregar el servicio.");
        }
    }

    private void editar(HttpServletRequest request) throws SQLException {
        Integer id = entero(request, "id");
        if (id == null || servicioDAO.buscarPorId(id) == null) {
            avisoError(request, "El servicio ya no existe.");
            return;
        }

        String nombre = texto(request, "nombre");
        String error = validarNombre(nombre, id);
        if (error != null) {
            avisoError(request, error);
            return;
        }

        if (servicioDAO.editarServicio(id, nombre)) {
            avisoOk(request, "Servicio actualizado.");
        } else {
            avisoError(request, "No se modificó el servicio (¿escribiste el mismo nombre?).");
        }
    }

    private void eliminar(HttpServletRequest request) throws SQLException {
        Integer id = entero(request, "id");
        if (id == null || servicioDAO.buscarPorId(id) == null) {
            avisoError(request, "El servicio ya no existe.");
            return;
        }

        int usos = servicioDAO.contarAlojamientos(id);
        if (servicioDAO.eliminarServicio(id)) {
            avisoOk(request, usos == 0
                    ? "Servicio eliminado."
                    : "Servicio eliminado y quitado de " + usos + " alojamiento(s).");
        } else {
            avisoError(request, "No se pudo eliminar el servicio.");
        }
    }

    private void asociar(HttpServletRequest request, Integer alojamientoId) throws SQLException {
        if (alojamientoId == null || alojamientoDAO.buscarPorIdALO(alojamientoId) == null) {
            avisoError(request, "Elegí un alojamiento.");
            return;
        }

        List<Integer> elegidos = serviciosElegidos(request);
        if (elegidos.isEmpty()) {
            avisoError(request, "Marcá al menos un servicio para asociar.");
            return;
        }

        Set<Integer> yaAsociados = new HashSet<>();
        for (Servicio s : alojamientoDAO.verServiciosDeUnAlojamiento(alojamientoId)) {
            yaAsociados.add(s.getId());
        }

        int nuevos = 0;
        int repetidos = 0;
        for (Integer servicioId : elegidos) {
            if (yaAsociados.contains(servicioId)) {
                repetidos++;
            } else if (servicioDAO.buscarPorId(servicioId) != null
                    && alojamientoDAO.asociarServicioAalojamiento(alojamientoId, servicioId)) {
                nuevos++;
            }
        }

        if (nuevos == 0) {
            avisoError(request, "Esos servicios ya estaban asociados al alojamiento.");
        } else {
            avisoOk(request, nuevos + " servicio(s) asociado(s) correctamente."
                    + (repetidos > 0 ? " " + repetidos + " ya estaban asociados." : ""));
        }
    }

    private void desasociar(HttpServletRequest request, Integer alojamientoId) throws SQLException {
        if (alojamientoId == null || alojamientoDAO.buscarPorIdALO(alojamientoId) == null) {
            avisoError(request, "Elegí un alojamiento.");
            return;
        }

        List<Integer> elegidos = serviciosElegidos(request);
        if (elegidos.isEmpty()) {
            avisoError(request, "Marcá al menos un servicio para desasociar.");
            return;
        }

        int quitados = 0;
        for (Integer servicioId : elegidos) {
            if (alojamientoDAO.desasociarServicioDeUnAlojamiento(alojamientoId, servicioId)) {
                quitados++;
            }
        }

        if (quitados == 0) {
            avisoError(request, "Esos servicios no estaban asociados al alojamiento.");
        } else {
            avisoOk(request, quitados + " servicio(s) desasociado(s).");
        }
    }

    private static List<Integer> serviciosElegidos(HttpServletRequest request) {
        String[] valores = request.getParameterValues("servicioIds");
        List<Integer> ids = new java.util.ArrayList<>();
        if (valores != null) {
            for (String valor : valores) {
                try {
                    ids.add(Integer.valueOf(valor.trim()));
                } catch (NumberFormatException ignorado) {
                    // se ignora un id mal formado
                }
            }
        }
        return ids;
    }

    private String validarNombre(String nombre, int excluirId) throws SQLException {
        if (nombre.isEmpty() || nombre.length() > 60) {
            return "El nombre del servicio es obligatorio (hasta 60 caracteres).";
        }
        if (servicioDAO.existeNombre(nombre, excluirId)) {
            return "Ya existe un servicio con ese nombre.";
        }
        return null;
    }
}
