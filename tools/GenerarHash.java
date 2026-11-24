import org.mindrot.jbcrypt.BCrypt;

import java.io.Console;
import java.util.Scanner;

/**
 * Genera el hash BCrypt de una contraseña para guardarlo en la base de datos.
 * La contraseña se escribe por consola (sin mostrarse) y no se guarda en ningún archivo.
 *
 * Uso (ver docs/panel-administrador-cambios.md):
 *   java -cp <ruta a jbcrypt-0.4.jar> tools/GenerarHash.java
 */
public class GenerarHash {

    /** Mismo costo que usa ClienteDAO. */
    private static final int COSTO_BCRYPT = 12;
    private static final int MINIMO = 12;

    public static void main(String[] args) {
        String clave = pedir("Contraseña: ");
        String repetida = pedir("Repetí la contraseña: ");

        if (clave == null || clave.length() < MINIMO) {
            System.out.println("La contraseña debe tener al menos " + MINIMO + " caracteres.");
            return;
        }
        if (!clave.equals(repetida)) {
            System.out.println("Las contraseñas no coinciden.");
            return;
        }

        System.out.println();
        System.out.println("Hash BCrypt (copialo completo, son 60 caracteres):");
        System.out.println(BCrypt.hashpw(clave, BCrypt.gensalt(COSTO_BCRYPT)));
    }

    private static String pedir(String mensaje) {
        Console consola = System.console();
        if (consola != null) {
            char[] texto = consola.readPassword(mensaje);
            return texto == null ? null : new String(texto);
        }
        // Sin consola real (algunos IDE): se lee igual, pero la clave se ve al escribirla
        System.out.print(mensaje + "(se verá al escribir) ");
        return ENTRADA.nextLine();
    }

    private static final Scanner ENTRADA = new Scanner(System.in);
}
