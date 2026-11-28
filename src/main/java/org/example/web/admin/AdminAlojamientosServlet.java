package org.example.web.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.example.Alojamiento;
import org.example.AlojamientoDAO;
import org.example.CloudinaryServicio;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Casos de uso: agregar, editar, eliminar, listar y buscar por nombre alojamientos.
 * GET  → listado (con búsqueda ?q=).
 * POST → accion = agregar | editar | eliminar | subirFotos | quitarFoto | portada.
 * Las fotos viven en Cloudinary (ver CloudinaryServicio); en la base solo se guardan sus identificadores.
 * El servlet acepta formularios con archivos (multipart) para subir fotos: hasta 5 MB cada una.
 */
@WebServlet(AdminSesion.BASE + "/alojamientos")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = CloudinaryServicio.MAX_BYTES,
        maxRequestSize = CloudinaryServicio.MAX_BYTES * (CloudinaryServicio.MAX_FOTOS + 1))
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
        request.setAttribute("maxFotos", CloudinaryServicio.MAX_FOTOS);
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
                case "subirFotos" -> subirFotosAExistente(request);
                case "quitarFoto" -> quitarFoto(request);
                case "portada" -> elegirPortada(request);
                default -> avisoError(request, "Acción no reconocida.");
            }
        } catch (SQLException | RuntimeException e) {
            log("Error en alojamientos (" + accion + ")", e);
            avisoError(request, "No pudimos completar la operación. Intentá de nuevo en unos minutos.");
        }

        // Después de tocar las fotos de un alojamiento se vuelve a abrir su ventana de fotos
        boolean deFotos = accion.equals("subirFotos") || accion.equals("quitarFoto") || accion.equals("portada");
        Integer idFotos = entero(request, "id");
        redirigir(request, response, deFotos && idFotos != null ? "/alojamientos?fotos=" + idFotos : "/alojamientos");
    }

    /** Agrega (id == null) o edita un alojamiento después de validar los datos. */
    private void guardar(HttpServletRequest request, Integer id) throws SQLException, IOException, ServletException {
        boolean editando = id != null;

        if (editando && alojamientoDAO.buscarPorIdALO(id) == null) {
            avisoError(request, "El alojamiento ya no existe.");
            return;
        }

        String nombre = texto(request, "nombre");
        String descripcion = texto(request, "descripcion");
        String latitudTexto = texto(request, "latitud");
        String longitudTexto = texto(request, "longitud");
        Double latitud = latitudTexto.isEmpty() ? null : decimal(latitudTexto);
        Double longitud = longitudTexto.isEmpty() ? null : decimal(longitudTexto);
        Integer capacidad = entero(request, "capacidad");
        Double precio = decimal(texto(request, "precio"));
        Double precioHasta2 = texto(request, "precioHasta2").isEmpty() ? null : decimal(texto(request, "precioHasta2"));
        Double precioHasta4 = texto(request, "precioHasta4").isEmpty() ? null : decimal(texto(request, "precioHasta4"));
        Double precioHasta6 = texto(request, "precioHasta6").isEmpty() ? null : decimal(texto(request, "precioHasta6"));
        Double distancia = texto(request, "distanciaApiscina").isEmpty() ? Double.valueOf(0) : decimal(texto(request, "distanciaApiscina"));

        boolean permiteMascotas = request.getParameter("permiteMascotas") != null;

        String error = null;
        if (nombre.isEmpty() || nombre.length() > 100) {
            error = "El nombre es obligatorio (hasta 100 caracteres).";
        } else if (descripcion.length() > 1000) {
            error = "La descripción puede tener hasta 1000 caracteres.";
        } else if (capacidad == null || capacidad < 1 || capacidad > 100) {
            error = "La capacidad debe ser un número entre 1 y 100.";
        } else if (precio == null || precio < 0 || precio > 10_000_000) {
            error = "El precio debe ser un número válido, mayor o igual a cero.";
        } else if (latitudTexto.isEmpty() != longitudTexto.isEmpty()) {
            error = "Completá la latitud y la longitud, o dejá las dos vacías.";
        } else if (!latitudTexto.isEmpty() && (latitud == null || longitud == null
                || !Alojamiento.coordenadasEnUruguay(latitud, longitud))) {
            error = "Las coordenadas no son válidas: la latitud va de -35 a -30 y la longitud de -59 a -53.";
        } else if (distancia == null || distancia < 0 || distancia > 100_000) {
            error = "La distancia a la piscina debe ser un número válido, mayor o igual a cero.";
        }

        if (error != null) {
            avisoError(request, error);
            return;
        }

        Alojamiento alojamiento = new Alojamiento(editando ? id : 0, nombre, descripcion, capacidad, precio, distancia.floatValue());
        alojamiento.setPermiteMascotas(permiteMascotas);
        alojamiento.setLatitud(latitud);
        alojamiento.setLongitud(longitud);

        if (nombre.equalsIgnoreCase("Pampita")) {
            alojamiento.setPrecioHasta2(precioHasta2);
            alojamiento.setPrecioHasta4(precioHasta4);
            alojamiento.setPrecioHasta6(precioHasta6);
        } else {
            alojamiento.setPrecioHasta2(null);
            alojamiento.setPrecioHasta4(null);
            alojamiento.setPrecioHasta6(null);
        }

        if (editando) {
            if (alojamientoDAO.modificarAlojamiento(alojamiento)) {
                avisoOk(request, "Alojamiento actualizado.");
            } else {
                avisoError(request, "No se pudo guardar el alojamiento.");
            }
            return;
        }

        int nuevoId = alojamientoDAO.agregarAlojamientoConId(alojamiento);
        if (nuevoId < 0) {
            avisoError(request, "No se pudo guardar el alojamiento.");
            return;
        }

        // Las fotos son opcionales: si no se pudieron subir, el alojamiento igual queda creado
        ResultadoFotos fotos = subirFotos(request, nuevoId);
        avisoOk(request, "Alojamiento agregado al catálogo." + (fotos.texto.isEmpty() ? "" : " " + fotos.texto));
    }

    private void eliminar(HttpServletRequest request) throws SQLException {
        Integer id = entero(request, "id");
        Alojamiento existente = id == null ? null : alojamientoDAO.buscarPorIdALO(id);
        if (existente == null) {
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
            borrarEnLaNube(existente.getListaImagenes());
            avisoOk(request, "Alojamiento eliminado.");
        } else {
            avisoError(request, "No se pudo eliminar el alojamiento.");
        }
    }

    // ===================== Fotos =====================

    /** Resultado de subir fotos: cuántas se subieron y un texto para mostrar ("" si no se eligió ninguna). */
    private record ResultadoFotos(int subidas, String texto) {
    }

    /** Acción "subirFotos": agrega fotos a un alojamiento que ya existe. */
    private void subirFotosAExistente(HttpServletRequest request) throws SQLException, IOException, ServletException {
        Integer id = entero(request, "id");
        if (id == null || alojamientoDAO.buscarPorIdALO(id) == null) {
            avisoError(request, "El alojamiento ya no existe.");
            return;
        }

        ResultadoFotos fotos = subirFotos(request, id);
        if (fotos.texto.isEmpty()) {
            avisoError(request, "Elegí al menos una foto.");
        } else if (fotos.subidas > 0) {
            avisoOk(request, fotos.texto);
        } else {
            avisoError(request, fotos.texto);
        }
    }

    /**
     * Sube las fotos del campo "fotos" del formulario y las agrega a las que el alojamiento ya tiene.
     * Cada archivo se valida por su contenido real (JPG, PNG, WebP o HEIC), tamaño (hasta 5 MB) y cantidad total
     * (hasta 8 por alojamiento). Un archivo que no sirve no impide que se suban los demás.
     */
    private ResultadoFotos subirFotos(HttpServletRequest request, int alojamientoId)
            throws SQLException, IOException, ServletException {

        List<Part> elegidas = new ArrayList<>();
        try {
            for (Part parte : request.getParts()) {
                if ("fotos".equals(parte.getName()) && parte.getSize() > 0) {
                    elegidas.add(parte);
                }
            }
        } catch (IllegalStateException e) {
            return new ResultadoFotos(0, "No se subieron las fotos: cada una puede pesar hasta "
                    + CloudinaryServicio.MAX_BYTES / (1024 * 1024) + " MB.");
        }
        if (elegidas.isEmpty()) {
            return new ResultadoFotos(0, "");
        }

        CloudinaryServicio nube;
        try {
            nube = CloudinaryServicio.desdeEntorno();
        } catch (IllegalStateException e) {
            return new ResultadoFotos(0, "No se subieron las fotos: falta configurar CLOUDINARY_URL en el servidor.");
        }

        Alojamiento alojamiento = alojamientoDAO.buscarPorIdALO(alojamientoId);
        List<String> lista = new ArrayList<>(alojamiento.getListaImagenes());
        List<String> problemas = new ArrayList<>();
        int subidas = 0;

        for (Part parte : elegidas) {
            String nombre = nombreParaMostrar(parte);

            if (lista.size() >= CloudinaryServicio.MAX_FOTOS) {
                problemas.add("cada alojamiento admite hasta " + CloudinaryServicio.MAX_FOTOS + " fotos");
                break;
            }

            byte[] datos;
            try (InputStream entrada = parte.getInputStream()) {
                datos = entrada.readNBytes((int) CloudinaryServicio.MAX_BYTES + 1);
            }
            if (datos.length > CloudinaryServicio.MAX_BYTES) {
                problemas.add(nombre + " pesa más de " + CloudinaryServicio.MAX_BYTES / (1024 * 1024) + " MB");
                continue;
            }

            String tipo = CloudinaryServicio.tipoDeImagen(datos);
            if (tipo == null) {
                problemas.add(nombre + " no es una imagen JPG, PNG, WebP o HEIC");
                continue;
            }

            try {
                lista.add(nube.subir(datos, tipo, alojamientoId));
                subidas++;
            } catch (IOException e) {
                log("No se pudo subir una foto a Cloudinary", e);
                // El motivo viene de Cloudinary (por ejemplo "Invalid image file"); solo lo ve la administradora
                String motivo = e.getMessage() == null ? "error desconocido" : e.getMessage();
                problemas.add(nombre + " no se pudo subir (" + (motivo.length() > 160 ? motivo.substring(0, 160) + "…" : motivo) + ")");
            }
        }

        if (subidas > 0) {
            alojamientoDAO.actualizarImagenes(alojamientoId, Alojamiento.unirImagenes(lista));
        }

        StringBuilder texto = new StringBuilder();
        if (subidas > 0) {
            texto.append(subidas).append(subidas == 1 ? " foto subida." : " fotos subidas.");
        }
        if (!problemas.isEmpty()) {
            texto.append(texto.length() > 0 ? " " : "").append("No se subió: ").append(String.join("; ", problemas)).append(".");
        }
        return new ResultadoFotos(subidas, texto.toString());
    }

    /** Acción "quitarFoto": la saca del alojamiento y la borra de Cloudinary. */
    private void quitarFoto(HttpServletRequest request) throws SQLException {
        Integer id = entero(request, "id");
        Alojamiento alojamiento = id == null ? null : alojamientoDAO.buscarPorIdALO(id);
        if (alojamiento == null) {
            avisoError(request, "El alojamiento ya no existe.");
            return;
        }

        String foto = texto(request, "foto");
        List<String> lista = new ArrayList<>(alojamiento.getListaImagenes());
        if (!lista.remove(foto)) {
            avisoError(request, "Esa foto ya no está en el alojamiento.");
            return;
        }

        alojamientoDAO.actualizarImagenes(id, Alojamiento.unirImagenes(lista));
        borrarEnLaNube(List.of(foto)); // si Cloudinary falla, la foto igual deja de mostrarse en el sitio
        avisoOk(request, "Foto eliminada.");
    }

    /** Acción "portada": pasa la foto elegida al primer lugar. */
    private void elegirPortada(HttpServletRequest request) throws SQLException {
        Integer id = entero(request, "id");
        Alojamiento alojamiento = id == null ? null : alojamientoDAO.buscarPorIdALO(id);
        if (alojamiento == null) {
            avisoError(request, "El alojamiento ya no existe.");
            return;
        }

        String foto = texto(request, "foto");
        List<String> lista = new ArrayList<>(alojamiento.getListaImagenes());
        if (!lista.remove(foto)) {
            avisoError(request, "Esa foto ya no está en el alojamiento.");
            return;
        }

        lista.add(0, foto);
        alojamientoDAO.actualizarImagenes(id, Alojamiento.unirImagenes(lista));
        avisoOk(request, "La foto ahora es la portada del alojamiento.");
    }

    /** Borra fotos de Cloudinary. Es un intento: si falla se registra y se sigue. */
    private void borrarEnLaNube(List<String> fotos) {
        if (fotos.isEmpty()) {
            return;
        }
        try {
            CloudinaryServicio nube = CloudinaryServicio.desdeEntorno();
            for (String foto : fotos) {
                try {
                    nube.borrar(foto);
                } catch (IOException e) {
                    log("No se pudo borrar la foto " + foto + " de Cloudinary", e);
                }
            }
        } catch (IllegalStateException e) {
            log("Cloudinary no está configurado: no se borraron " + fotos.size() + " foto(s) de la nube");
        }
    }

    /** Nombre del archivo para mostrarlo en un mensaje (sin rutas y acotado). */
    private static String nombreParaMostrar(Part parte) {
        String nombre = parte.getSubmittedFileName();
        if (nombre == null || nombre.isBlank()) {
            return "una foto";
        }
        nombre = nombre.replace('\\', '/');
        nombre = nombre.substring(nombre.lastIndexOf('/') + 1);
        return nombre.length() > 40 ? nombre.substring(0, 40) + "…" : nombre;
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
