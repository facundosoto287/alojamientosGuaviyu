package org.example.web;

import org.example.Cliente;
import org.example.ClienteDAO;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** Validación de los datos personales de un cliente, compartida por el registro público y el panel de administrador. */
public final class ValidacionCliente {

    private ValidacionCliente() {
    }

    /**
     * Valida nombre, documento (cédula), teléfono, correo, procedencia y localidad.
     * Devuelve un mapa campo → mensaje; vacío si todo está bien.
     */
    public static Map<String, String> validarDatos(Map<String, String> v, ClienteDAO clienteDAO) {
        Map<String, String> errores = new HashMap<>();

        String nombre = v.get("nombre");
        if (nombre.isEmpty()) {
            errores.put("nombre", "Ingresá el nombre y apellido.");
        } else if (nombre.length() > 100) {
            errores.put("nombre", "El nombre puede tener hasta 100 caracteres.");
        }

        String documento = v.get("documento");
        if (documento.isEmpty()) {
            errores.put("documento", "Ingresá el documento.");
        } else if (documento.length() > 20 || !documento.matches("[0-9.\\-\\s]+")) {
            errores.put("documento", "Usá solo números, puntos o guiones (ej. 1.234.567-2).");
        } else if (!clienteDAO.cedulaValida(documento)) {
            errores.put("documento", "La cédula no es válida. Revisá el número y el dígito verificador.");
        }

        String telefono = v.get("telefono");
        if (!telefono.isEmpty() && (telefono.length() > 30 || !telefono.matches("[0-9\\s\\-]+"))) {
            errores.put("telefono", "Usá solo números, espacios o guiones (ej. 099 123 456).");
        } else if (!telefono.isEmpty() && !clienteDAO.celularValido(telefono)) {
            errores.put("telefono", "El celular debe tener 9 dígitos y empezar con 09 (ej. 099 123 456).");
        }

        String email = v.get("email");
        if (email.isEmpty()) {
            errores.put("email", "Ingresá el correo electrónico.");
        } else if (email.length() > 100 || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            errores.put("email", "El correo no tiene un formato válido.");
        }

        String procedencia = v.get("procedencia");
        if (procedencia.isEmpty()) {
            errores.put("procedencia", "Elegí la procedencia (departamento, provincia o país donde vivís).");
        } else if (!LoginServlet.PROCEDENCIAS.contains(procedencia)) {
            errores.put("procedencia", "Elegí una opción de la lista.");
        }

        // La localidad es la ciudad o el pueblo; se compara ya normalizada (sin espacios de más)
        String errorLocalidad = ClienteDAO.errorDeLocalidad(ClienteDAO.normalizarLocalidad(v.getOrDefault("localidad", "")));
        if (errorLocalidad != null) {
            errores.put("localidad", errorLocalidad);
        }

        return errores;
    }

    /**
     * Validación del panel de administrador: solo el nombre es obligatorio. Los demás datos se pueden dejar
     * vacíos, pero los que se cargan tienen que ser válidos. El registro público usa validarDatos.
     */
    public static Map<String, String> validarDatosAdmin(Map<String, String> v, ClienteDAO clienteDAO) {
        Map<String, String> errores = validarDatos(v, clienteDAO);
        errores.keySet().removeIf(campo -> !campo.equals("nombre") && v.getOrDefault(campo, "").isBlank());
        return errores;
    }

    /**
     * Validación al editar un cliente desde el panel: como validarDatosAdmin, y además solo se revisan
     * los datos que se modificaron. Así un dato antiguo mal cargado no impide corregir otro.
     */
    public static Map<String, String> validarCambios(Map<String, String> v, Cliente actual, ClienteDAO clienteDAO) {
        Map<String, String> errores = validarDatosAdmin(v, clienteDAO);

        quitarSiNoCambio(errores, v, "documento", actual.getDocumento());
        quitarSiNoCambio(errores, v, "telefono", actual.getTelefono());
        quitarSiNoCambio(errores, v, "email", actual.getEmail());
        quitarSiNoCambio(errores, v, "procedencia", actual.getProcedencia());
        quitarSiNoCambio(errores, v, "localidad", actual.getLocalidad());

        return errores;
    }

    private static void quitarSiNoCambio(Map<String, String> errores, Map<String, String> v, String campo,
                                         String guardado) {
        if (v.getOrDefault(campo, "").equals(Objects.toString(guardado, "").trim())) {
            errores.remove(campo);
        }
    }
}
