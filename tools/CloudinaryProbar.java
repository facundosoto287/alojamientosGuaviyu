import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Comprueba la conexión con Cloudinary y crea las carpetas de las fotos.
 * Lee CLOUDINARY_URL (cloudinary://API_KEY:API_SECRET@CLOUD_NAME) del entorno: la clave nunca se escribe
 * en un archivo ni se muestra en pantalla.
 *
 * Uso (ver docs/panel-administrador-cambios.md):
 *   java tools/CloudinaryProbar.java            -> prueba la conexión y crea la carpeta "alojamientos"
 *   java tools/CloudinaryProbar.java 1 2 3      -> además crea "alojamientos/1", "alojamientos/2", "alojamientos/3"
 */
public class CloudinaryProbar {

    public static void main(String[] args) throws Exception {
        String url = System.getenv("CLOUDINARY_URL");
        if (url == null || !url.startsWith("cloudinary://") || !url.contains("@") || !url.substring(13).contains(":")) {
            System.out.println("Falta la variable de entorno CLOUDINARY_URL, o no tiene el formato");
            System.out.println("cloudinary://API_KEY:API_SECRET@CLOUD_NAME");
            System.exit(1);
        }

        String resto = url.substring("cloudinary://".length());
        int arroba = resto.lastIndexOf('@');
        String credenciales = resto.substring(0, arroba);
        String nube = resto.substring(arroba + 1).trim();
        String apiKey = credenciales.substring(0, credenciales.indexOf(':'));

        System.out.println("Cuenta (cloud name): " + nube);
        System.out.println("API Key termina en: ..." + apiKey.substring(Math.max(0, apiKey.length() - 4)));

        String basico = "Basic " + Base64.getEncoder().encodeToString(credenciales.getBytes(StandardCharsets.UTF_8));
        HttpClient cliente = HttpClient.newHttpClient();
        String base = "https://api.cloudinary.com/v1_1/" + nube;

        // 1) Conexión y credenciales
        HttpResponse<String> ping = enviar(cliente, HttpRequest.newBuilder(URI.create(base + "/ping"))
                .header("Authorization", basico).GET().build());
        System.out.println();
        System.out.println("Conexión: HTTP " + ping.statusCode() + " " + ping.body());
        if (ping.statusCode() != 200) {
            System.out.println("No se pudo autenticar. Revisá el API Key, el API Secret y el cloud name.");
            System.exit(2);
        }

        // 2) Carpetas
        crearCarpeta(cliente, base, basico, "alojamientos");
        for (String id : args) {
            if (id.matches("\\d+")) {
                crearCarpeta(cliente, base, basico, "alojamientos/" + id);
            } else {
                System.out.println("Se ignora \"" + id + "\": el id de un alojamiento es un número.");
            }
        }
    }

    private static void crearCarpeta(HttpClient cliente, String base, String basico, String ruta) throws Exception {
        // Cada tramo de la ruta se codifica por separado; las barras se conservan
        StringBuilder codificada = new StringBuilder();
        for (String tramo : ruta.split("/")) {
            if (codificada.length() > 0) {
                codificada.append('/');
            }
            codificada.append(URLEncoder.encode(tramo, StandardCharsets.UTF_8));
        }

        HttpResponse<String> respuesta = enviar(cliente, HttpRequest.newBuilder(URI.create(base + "/folders/" + codificada))
                .header("Authorization", basico)
                .POST(HttpRequest.BodyPublishers.noBody()).build());

        System.out.println("Carpeta " + ruta + ": HTTP " + respuesta.statusCode() + " " + respuesta.body());
    }

    private static HttpResponse<String> enviar(HttpClient cliente, HttpRequest peticion) throws Exception {
        return cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
    }
}
