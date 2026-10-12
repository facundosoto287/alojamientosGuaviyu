package org.example.web;

import org.example.ClienteDAO;

import java.util.HashMap;
import java.util.Map;

/** Validación de los datos personales de un cliente, compartida por el registro público y el panel de administrador. */
public final class ValidacionCliente {

    private ValidacionCliente() {
    }

    /**
     * Valida nombre, documento (cédula), teléfono, correo y procedencia.
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
        if (!procedencia.isEmpty() && !LoginServlet.PROCEDENCIAS.contains(procedencia)) {
            errores.put("procedencia", "Elegí una opción de la lista.");
        }

        return errores;
    }
}
