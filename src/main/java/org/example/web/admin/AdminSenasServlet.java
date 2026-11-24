package org.example.web.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Sena;
import org.example.SenaDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Señas a favor: la seña de las reservas confirmadas que el cliente tuvo que cancelar.
 * Se guardan 6 meses desde la fecha de ingreso y sirven para cualquier casa.
 * GET  → listado con pestañas ?estado=VIGENTE (por defecto) | USADA | VENCIDA | TODAS y búsqueda ?q=.
 * POST → accion = usada | disponible | eliminar.
 * (Las señas se crean al cancelar una reserva confirmada, desde Reservas o Disponibilidad.)
 */
@WebServlet(AdminSesion.BASE + "/senas")
public class AdminSenasServlet extends AdminServlet {

    private final SenaDAO senaDAO = new SenaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String estado = estado(texto(request, "estado"));
        String q = texto(request, "q");

        try {
            List<Sena> todas = senaDAO.listar(q);
            List<Sena> mostrar = new ArrayList<>();
            int vigentes = 0;
            int porVencer = 0;
            for (Sena sena : todas) {
                if (sena.isVigente()) {
                    vigentes++;
                    if (sena.isPorVencer()) {
                        porVencer++;
                    }
                }
                if (estado.equals("TODAS") || estado.equals(sena.getEstado())) {
                    mostrar.add(sena);
                }
            }
            request.setAttribute("senas", mostrar);
            request.setAttribute("totalVigentes", vigentes);
            request.setAttribute("totalPorVencer", porVencer);
        } catch (SQLException e) {
            log("Error al listar señas", e);
            avisoError(request, "No pudimos cargar las señas. Intentá de nuevo en unos minutos.");
        }

        request.setAttribute("estadoSel", estado);
        request.setAttribute("q", q);
        request.setAttribute("filtroQ", q.isEmpty() ? "" : "&q=" + codificar(q));
        mostrar(request, response, "senas", "senas", "Señas a favor");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = texto(request, "accion");
        Integer id = entero(request, "id");

        try {
            if (id == null) {
                avisoError(request, "La seña no existe.");
            } else {
                switch (accion) {
                    case "usada" -> aviso(request, senaDAO.marcarUsada(id, true), "Seña marcada como usada.");
                    case "disponible" -> aviso(request, senaDAO.marcarUsada(id, false), "La seña vuelve a estar a favor del cliente.");
                    case "eliminar" -> aviso(request, senaDAO.eliminar(id), "Seña eliminada.");
                    default -> avisoError(request, "Acción no reconocida.");
                }
            }
        } catch (SQLException | RuntimeException e) {
            log("Error en señas (" + accion + ")", e);
            avisoError(request, "No pudimos completar la operación. Intentá de nuevo en unos minutos.");
        }

        String volver = "/senas?estado=" + estado(texto(request, "fEstado"));
        String q = texto(request, "fQ");
        redirigir(request, response, q.isEmpty() ? volver : volver + "&q=" + codificar(q));
    }

    private static void aviso(HttpServletRequest request, boolean hecho, String texto) {
        if (hecho) {
            avisoOk(request, texto);
        } else {
            avisoError(request, "La seña ya no existe.");
        }
    }

    private static String estado(String texto) {
        return switch (texto) {
            case "USADA", "VENCIDA", "TODAS" -> texto;
            default -> "VIGENTE";
        };
    }
}