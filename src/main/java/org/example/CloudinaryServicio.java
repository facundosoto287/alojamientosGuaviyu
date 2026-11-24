package org.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sube y borra las fotos de los alojamientos en Cloudinary (API REST firmada, sin librerías externas).
 *
 * La cuenta se configura con la variable de entorno CLOUDINARY_URL, con el formato
 * cloudinary://API_KEY:API_SECRET@CLOUD_NAME (igual que ALOJAMIENTOS_DB_PASSWORD: nunca va en el código).
 *
 * Las fotos quedan en la carpeta alojamientos/{id del alojamiento}/ y en la base solo se guarda su
 * identificador (public_id); el sitio arma la dirección de cada foto con el nombre de la cuenta.
 */
public class CloudinaryServicio {

    /** Máximo de fotos por alojamiento. */
    public static final int MAX_FOTOS = 8;

    /** Tamaño máximo de cada foto. */
    public static final long MAX_BYTES = 5L * 1024 * 1024;

    private static final String API = "https://api.cloudinary.com/v1_1/";

    /** Los public_id que crea este servicio: alojamientos/3/xxxxxxxx-xxxx-... */
    private static final Pattern ID_VALIDO = Pattern.compile("alojamientos/\\d+/[A-Za-z0-9-]+");

    private static final Pattern PUBLIC_ID = Pattern.compile("\"public_id\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern MENSAJE = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern RESULTADO = Pattern.compile("\"result\"\\s*:\\s*\"([^\"]*)\"");

    private static final SecureRandom ALEATORIO = new SecureRandom();

    /** Marcas ("brands") de los archivos HEIC/HEIF, el formato de las fotos del iPhone. */
    private static final Set<String> MARCAS_HEIC = Set.of(
            "heic", "heix", "hevc", "hevx", "heim", "heis", "hevm", "hevs", "mif1", "msf1", "heif");

    private final String nube;
    private final String apiKey;
    private final String apiSecret;
    private final String urlApi;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();

    public CloudinaryServicio(String nube, String apiKey, String apiSecret) {
        this(nube, apiKey, apiSecret, API + nube);
    }

    /** Con dirección de la API propia: solo para pruebas con un servidor falso. */
    public CloudinaryServicio(String nube, String apiKey, String apiSecret, String urlApi) {
        this.nube = nube;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.urlApi = urlApi;
    }

    // ===================== Configuración =====================

    /** Crea el servicio con CLOUDINARY_URL. Lanza IllegalStateException si falta o está mal escrita. */
    public static CloudinaryServicio desdeEntorno() {
        String[] datos = leerEntorno();
        if (datos == null) {
            throw new IllegalStateException("Falta configurar la variable CLOUDINARY_URL");
        }
        // La propiedad cloudinary.api.base (-D al arrancar Java) solo se usa en las pruebas, con un servidor falso
        String apiPropia = System.getProperty("cloudinary.api.base");
        return apiPropia == null || apiPropia.isBlank()
                ? new CloudinaryServicio(datos[2], datos[0], datos[1])
                : new CloudinaryServicio(datos[2], datos[0], datos[1], apiPropia);
    }

    /** Nombre de la cuenta (cloud name), o null si CLOUDINARY_URL no está configurada. */
    public static String nubeDelEntorno() {
        String[] datos = leerEntorno();
        return datos == null ? null : datos[2];
    }

    /** {API_KEY, API_SECRET, CLOUD_NAME} o null. */
    private static String[] leerEntorno() {
        String url = System.getenv("CLOUDINARY_URL");
        if (url != null) {
            // Ni las claves ni el nombre de la cuenta llevan espacios, saltos de línea ni comillas: si se
            // colaron al pegar el valor en IntelliJ o en el servidor, se descartan (un salto de línea en el
            // API Key hace que Cloudinary responda "Invalid api_key").
            url = url.replaceAll("[\\s\\p{Cntrl}\"']", "");
        }
        if (url == null || !url.startsWith("cloudinary://")) {
            return null;
        }
        String resto = url.substring("cloudinary://".length());
        int arroba = resto.lastIndexOf('@');
        int dosPuntos = resto.indexOf(':');
        if (arroba < 0 || dosPuntos < 1 || dosPuntos > arroba - 1) {
            return null;
        }
        String nube = resto.substring(arroba + 1).trim();
        if (nube.isEmpty()) {
            return null;
        }
        return new String[]{resto.substring(0, dosPuntos), resto.substring(dosPuntos + 1, arroba), nube};
    }

    // ===================== Subir y borrar =====================

    /**
     * Sube una foto a alojamientos/{id}/ y devuelve su public_id.
     *
     * @param datos        contenido del archivo (ya validado)
     * @param tipoMime     image/jpeg, image/png o image/webp
     * @param alojamientoId id del alojamiento al que pertenece
     */
    public String subir(byte[] datos, String tipoMime, int alojamientoId) throws IOException {
        String carpeta = "alojamientos/" + alojamientoId;
        String publicId = carpeta + "/" + UUID.randomUUID();

        Map<String, String> firmados = new TreeMap<>();
        firmados.put("asset_folder", carpeta);
        firmados.put("public_id", publicId);
        firmados.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));

        String cuerpoRespuesta = enviar("/image/upload", firmados, datos, tipoMime);

        Matcher encontrado = PUBLIC_ID.matcher(cuerpoRespuesta);
        return encontrado.find() ? encontrado.group(1) : publicId;
    }

    /** Borra una foto. Si ya no existe en Cloudinary no es un error. */
    public void borrar(String publicId) throws IOException {
        if (!idValido(publicId)) {
            throw new IOException("Identificador de foto no válido");
        }

        Map<String, String> firmados = new TreeMap<>();
        firmados.put("invalidate", "true");
        firmados.put("public_id", publicId);
        firmados.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));

        String cuerpoRespuesta = enviar("/image/destroy", firmados, null, null);

        Matcher resultado = RESULTADO.matcher(cuerpoRespuesta);
        String texto = resultado.find() ? resultado.group(1) : "";
        if (!texto.equals("ok") && !texto.equals("not found")) {
            throw new IOException("Cloudinary no borró la foto: " + cuerpoRespuesta);
        }
    }

    /** true si es un identificador como los que crea este servicio (evita borrar otra cosa). */
    public static boolean idValido(String publicId) {
        return publicId != null && ID_VALIDO.matcher(publicId).matches();
    }

    // ===================== Petición firmada =====================

    private String enviar(String ruta, Map<String, String> firmados, byte[] archivo, String tipoMime) throws IOException {
        String boundary = "----guaviyu" + Long.toHexString(ALEATORIO.nextLong());
        ByteArrayOutputStream cuerpo = new ByteArrayOutputStream();

        for (Map.Entry<String, String> campo : firmados.entrySet()) {
            escribirCampo(cuerpo, boundary, campo.getKey(), campo.getValue());
        }
        escribirCampo(cuerpo, boundary, "api_key", apiKey);
        escribirCampo(cuerpo, boundary, "signature", firmar(firmados, apiSecret));

        if (archivo != null) {
            escribir(cuerpo, "--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"file\"; filename=\"foto\"\r\n"
                    + "Content-Type: " + tipoMime + "\r\n\r\n");
            cuerpo.write(archivo);
            escribir(cuerpo, "\r\n");
        }
        escribir(cuerpo, "--" + boundary + "--\r\n");

        HttpRequest peticion = HttpRequest.newBuilder(URI.create(urlApi + ruta))
                .timeout(Duration.ofSeconds(90))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(cuerpo.toByteArray()))
                .build();

        HttpResponse<String> respuesta;
        try {
            respuesta = http.send(peticion, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Se interrumpió la comunicación con Cloudinary", e);
        }

        if (respuesta.statusCode() != 200) {
            Matcher mensaje = MENSAJE.matcher(respuesta.body());
            throw new IOException("Cloudinary respondió " + respuesta.statusCode()
                    + (mensaje.find() ? ": " + mensaje.group(1) : ""));
        }
        return respuesta.body();
    }

    /**
     * Firma de Cloudinary: los parámetros ordenados por nombre ("a=1&b=2"), seguidos del API Secret,
     * pasados por SHA-1. No entran en la firma el archivo ni el api_key.
     */
    public static String firmar(Map<String, String> parametros, String secreto) {
        StringBuilder texto = new StringBuilder();
        for (Map.Entry<String, String> parametro : new TreeMap<>(parametros).entrySet()) {
            if (texto.length() > 0) {
                texto.append('&');
            }
            texto.append(parametro.getKey()).append('=').append(parametro.getValue());
        }
        texto.append(secreto);

        try {
            byte[] resumen = MessageDigest.getInstance("SHA-1").digest(texto.toString().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(resumen);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void escribirCampo(ByteArrayOutputStream cuerpo, String boundary, String nombre, String valor)
            throws IOException {
        escribir(cuerpo, "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"" + nombre + "\"\r\n\r\n"
                + valor + "\r\n");
    }

    private static void escribir(ByteArrayOutputStream cuerpo, String texto) throws IOException {
        cuerpo.write(texto.getBytes(StandardCharsets.UTF_8));
    }

    // ===================== Validación de archivos =====================

    /**
     * Tipo real de la imagen según los primeros bytes del archivo (no se confía en la extensión ni en lo que
     * diga el navegador). Devuelve image/jpeg, image/png, image/webp o image/heic (fotos de iPhone, que
     * Cloudinary convierte solo al mostrarlas), o null si no es ninguna de esas.
     */
    public static String tipoDeImagen(byte[] d) {
        if (d == null || d.length < 12) {
            return null;
        }
        if ((d[0] & 0xFF) == 0xFF && (d[1] & 0xFF) == 0xD8 && (d[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if ((d[0] & 0xFF) == 0x89 && d[1] == 'P' && d[2] == 'N' && d[3] == 'G'
                && d[4] == 0x0D && d[5] == 0x0A && d[6] == 0x1A && d[7] == 0x0A) {
            return "image/png";
        }
        if (d[0] == 'R' && d[1] == 'I' && d[2] == 'F' && d[3] == 'F'
                && d[8] == 'W' && d[9] == 'E' && d[10] == 'B' && d[11] == 'P') {
            return "image/webp";
        }
        if (d[4] == 'f' && d[5] == 't' && d[6] == 'y' && d[7] == 'p'
                && MARCAS_HEIC.contains(new String(d, 8, 4, StandardCharsets.ISO_8859_1))) {
            return "image/heic";
        }
        return null;
    }

    /** Nombre de la cuenta con la que se arman las direcciones de las fotos. */
    public String getNube() {
        return nube;
    }
}
