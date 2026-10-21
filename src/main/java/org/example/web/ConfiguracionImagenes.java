package org.example.web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.CloudinaryServicio;

/**
 * Al arrancar la aplicación deja en el contexto la dirección base de las fotos (atributo "imagenBase"),
 * para que las JSP armen cada foto así:
 *
 *   ${imagenBase},w_600,h_400,c_fill/${alojamiento.portada}
 *
 * La parte "f_auto,q_auto" hace que Cloudinary elija el mejor formato y la mejor calidad para cada visitante.
 * Si CLOUDINARY_URL no está configurada, "imagenBase" no existe y las páginas muestran la imagen por defecto.
 */
@WebListener
public class ConfiguracionImagenes implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        ServletContext contexto = evento.getServletContext();
        String nube = CloudinaryServicio.nubeDelEntorno();

        if (nube == null) {
            contexto.log("CLOUDINARY_URL no está configurada: no se mostrarán ni se podrán subir fotos de alojamientos.");
            return;
        }

        contexto.setAttribute("imagenBase", "https://res.cloudinary.com/" + nube + "/image/upload/f_auto,q_auto");
        contexto.log("Fotos de alojamientos en Cloudinary (cuenta " + nube + ").");
    }
}
