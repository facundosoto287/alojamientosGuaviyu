import org.example.CloudinaryServicio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

/**
 * Prueba real de la subida de fotos: sube una imagen de 1x1 píxel con el mismo código que usa el panel,
 * comprueba que Cloudinary la entrega y la borra. Si todo sale bien, la firma y las claves son correctas.
 * Lee CLOUDINARY_URL del entorno (cloudinary://API_KEY:API_SECRET@CLOUD_NAME).
 *
 * Uso (ver docs/panel-administrador-cambios.md):
 *   javac -encoding UTF-8 -d "$env:TEMP\cld" src\main\java\org\example\CloudinaryServicio.java
 *   java -cp "$env:TEMP\cld" tools\CloudinarySubidaPrueba.java
 */
public class CloudinarySubidaPrueba {

    // PNG de 1x1 píxel
    private static final String PNG_1X1 =
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==";

    public static void main(String[] args) throws Exception {
        CloudinaryServicio nube;
        try {
            nube = CloudinaryServicio.desdeEntorno();
        } catch (IllegalStateException e) {
            System.out.println("Falta la variable CLOUDINARY_URL (cloudinary://API_KEY:API_SECRET@CLOUD_NAME).");
            System.exit(1);
            return;
        }

        byte[] imagen = Base64.getDecoder().decode(PNG_1X1);
        System.out.println("Tipo detectado: " + CloudinaryServicio.tipoDeImagen(imagen));

        String id = nube.subir(imagen, "image/png", 0);
        System.out.println("Subida correcta: " + id);

        String url = "https://res.cloudinary.com/" + nube.getNube() + "/image/upload/f_auto,q_auto/" + id;
        HttpResponse<Void> respuesta = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create(url)).method("HEAD", HttpRequest.BodyPublishers.noBody()).build(),
                HttpResponse.BodyHandlers.discarding());
        System.out.println("Cloudinary entrega la imagen: HTTP " + respuesta.statusCode() + " (" + url + ")");

        nube.borrar(id);
        System.out.println("Borrada correctamente. La subida y el borrado funcionan.");
    }
}
