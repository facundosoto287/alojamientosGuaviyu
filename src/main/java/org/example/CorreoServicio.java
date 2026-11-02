package org.example;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

import java.io.InputStream;

import java.util.Properties;

public class CorreoServicio {

    private final String usuario;
    private final String contrasena;

    public CorreoServicio() {

        this.usuario = System.getenv("MAIL_USUARIO");
        this.contrasena = System.getenv("MAIL_PASSWORD");

        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isBlank()) {

            throw new IllegalStateException("Faltan MAIL_USUARIO o MAIL_PASSWORD");
        }
    }

    public void enviar(String destinatario, String asunto, String contenidoHtml) throws Exception {

        Properties propiedades = new Properties();

        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");
        propiedades.put("mail.smtp.host", "smtp.gmail.com");
        propiedades.put("mail.smtp.port", "587");

        Session sesion = Session.getInstance(
                propiedades,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(usuario, contrasena);
                    }
                }
        );

        MimeMessage mensaje = new MimeMessage(sesion);

        mensaje.setFrom(new InternetAddress(usuario));

        mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));

        mensaje.setSubject(asunto, "UTF-8");


        MimeMultipart contenidoCompleto = new MimeMultipart("related");


        // Parte HTML del correo
        MimeBodyPart parteHtml = new MimeBodyPart();

        parteHtml.setContent(contenidoHtml, "text/html; charset=UTF-8");

        contenidoCompleto.addBodyPart(parteHtml);


        // Logo incrustado
        agregarImagenInline(contenidoCompleto, "/logoAlojamientosGuaviyu.png", "logoGuaviyu");

        mensaje.setContent(contenidoCompleto);
        Transport.send(mensaje);
    }


    private void agregarImagenInline(MimeMultipart contenidoCompleto, String recurso, String cid) throws Exception {
        MimeBodyPart parteImagen = new MimeBodyPart();

        try (InputStream imagen = CorreoServicio.class.getResourceAsStream(recurso)) {

            if (imagen == null) {throw new IllegalStateException("No se encontró el recurso: " + recurso);}

            byte[] bytesImagen = imagen.readAllBytes();

            DataSource dataSource = new ByteArrayDataSource(bytesImagen, "image/png");

            parteImagen.setDataHandler(new DataHandler(dataSource));

            // Identificador usado desde el HTML: cid:iconoReserva.
            parteImagen.setContentID("<" + cid + ">");

            // Indica que forma parte visual del correo
            parteImagen.setDisposition(MimeBodyPart.INLINE);

            // Nombre de archivo para mejorar compatibilidad
            parteImagen.setFileName(cid + ".png");

            contenidoCompleto.addBodyPart(parteImagen);
        }
    }
}