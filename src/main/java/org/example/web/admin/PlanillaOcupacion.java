package org.example.web.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.example.Alojamiento;
import org.example.PlanillaDia;
import org.example.ReservaPlanilla;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Vista "Todas las casas" de Disponibilidad (la planilla de ocupación):
 * las casas en columnas y los días del mes en filas, igual que la planilla de Excel.
 * Cada reserva es un bloque que ocupa todos sus días.
 * Un día cuenta como ocupado desde el ingreso hasta el día anterior a la salida
 * (con medio día extra, también el día de salida): la misma regla del calendario por casa
 * y de la validación de reservas.
 * La usa AdminDisponibilidadServlet; no es un servlet.
 */
public final class PlanillaOcupacion {

    private static final Locale ES = Locale.forLanguageTag("es-UY");
    private static final String[] LETRAS = {"L", "M", "M", "J", "V", "S", "D"}; // lunes a domingo, como en el Excel
    private static final String[] MESES_CORTOS = {"Ene", "Feb", "Mar", "Abr", "May", "Jun",
            "Jul", "Ago", "Set", "Oct", "Nov", "Dic"};

    private PlanillaOcupacion() {
    }

    /* ===================== Clases para el JSP ===================== */

    /** Una columna: una casa con su color (los mismos colores pastel del Excel). */
    public static class Casa {
        private final int id;
        private final String nombre;
        private final String color;

        Casa(int id, String nombre, String color) {
            this.id = id;
            this.nombre = nombre;
            this.color = color;
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getColor() { return color; }
    }

    /**
     * Una celda de la planilla.
     * tipo "libre": nadie reservó esa noche. tipo "reserva": empieza un bloque que ocupa "filas" días.
     * (Los días que ya cubre un bloque de arriba no tienen celda: los tapa el rowspan.)
     */
    public static class Celda {
        private final String tipo;
        private final Casa casa;
        private final String fecha;          // 2026-10-20 (para el formulario de nueva reserva)
        private final String fechaSiguiente; // 2026-10-21 (salida sugerida: una noche)
        private final String descripcion;    // texto para lectores de pantalla
        private final boolean pasado;
        private final int filas;
        private final ReservaPlanilla reserva;
        private final boolean superpuesta;   // hay otra solicitud para alguna de esas noches
        private PlanillaDia anotado;         // precio y color que cargó la administradora (puede ser null)

        Celda(String tipo, Casa casa, String fecha, String descripcion, boolean pasado,
              int filas, ReservaPlanilla reserva, boolean superpuesta) {
            this.tipo = tipo;
            this.casa = casa;
            this.fecha = fecha;
            this.fechaSiguiente = LocalDate.parse(fecha).plusDays(1).toString();
            this.descripcion = descripcion;
            this.pasado = pasado;
            this.filas = filas;
            this.reserva = reserva;
            this.superpuesta = superpuesta;
        }

        public String getTipo() { return tipo; }
        public Casa getCasa() { return casa; }
        public String getFecha() { return fecha; }
        public String getFechaSiguiente() { return fechaSiguiente; }
        public String getDescripcion() { return descripcion; }
        public boolean isPasado() { return pasado; }
        public int getFilas() { return filas; }
        public ReservaPlanilla getReserva() { return reserva; }
        public boolean isSuperpuesta() { return superpuesta; }

        /** Precio por noche anotado para ese día, por ejemplo "$ 3.500" ("" si no hay). */
        public String getPrecio() {
            return anotado == null ? "" : anotado.getPrecioTexto();
        }

        /** Precio sin formato, para llenar el formulario (por ejemplo 3500.00). */
        public String getPrecioValor() {
            return anotado == null || anotado.getPrecio() == null ? "" : anotado.getPrecio().toPlainString();
        }

        /** Color elegido para ese día ("" si usa el color de la casa). */
        public String getColorPropio() {
            return anotado == null || anotado.getColor() == null ? "" : anotado.getColor();
        }

        /** Fondo del casillero: el color elegido o, si no hay, el color suave de la casa. */
        public String getFondo() {
            String propio = getColorPropio();
            return propio.isEmpty() ? casa.getColor() + "80" : propio;
        }

        /** Fecha para leer: "jueves 1 de octubre". */
        public String getFechaTexto() {
            LocalDate dia = LocalDate.parse(fecha);
            return dia.getDayOfWeek().getDisplayName(TextStyle.FULL, ES) + " " + dia.getDayOfMonth()
                    + " de " + dia.getMonth().getDisplayName(TextStyle.FULL, ES);
        }
    }

    /** Una fila: un día del mes. */
    public static class Fila {
        private final int numero;
        private final String letra;
        private final boolean hoy;
        private final boolean finDeSemana;
        private final String feriado;
        private final List<Celda> celdas = new ArrayList<>();

        Fila(int numero, String letra, boolean hoy, boolean finDeSemana, String feriado) {
            this.numero = numero;
            this.letra = letra;
            this.hoy = hoy;
            this.finDeSemana = finDeSemana;
            this.feriado = feriado;
        }

        public int getNumero() { return numero; }
        public String getLetra() { return letra; }
        public boolean isHoy() { return hoy; }
        public boolean isFinDeSemana() { return finDeSemana; }
        public String getFeriado() { return feriado; }
        public List<Celda> getCeldas() { return celdas; }
    }

    /** Una pestaña de mes (Ene … Dic). */
    public static class PestanaMes {
        private final String valor;
        private final String nombre;
        private final boolean activo;

        PestanaMes(String valor, String nombre, boolean activo) {
            this.valor = valor;
            this.nombre = nombre;
            this.activo = activo;
        }

        public String getValor() { return valor; }
        public String getNombre() { return nombre; }
        public boolean isActivo() { return activo; }
    }

    /* ===================== Carga ===================== */

    /**
     * Arma la planilla del mes y deja en el request lo que usa disponibilidad.jsp:
     * casas, filas (con precio y color de cada casillero), meses (pestañas), resumen de hoy.
     */
    static void cargar(HttpServletRequest request, YearMonth mes,
                       List<Alojamiento> alojamientos, List<ReservaPlanilla> reservas,
                       Map<Integer, Map<LocalDate, PlanillaDia>> anotados) {
        LocalDate hoy = LocalDate.now();

        List<Casa> casas = new ArrayList<>();
        for (int i = 0; i < alojamientos.size(); i++) {
            Alojamiento a = alojamientos.get(i);
            casas.add(new Casa(a.getId(), a.getNombre(), color(a.getNombre(), i)));
        }
        armarPlanilla(request, mes, hoy, casas, reservas, anotados);

        // Pestañas de los 12 meses del año que se está mirando
        List<PestanaMes> meses = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            YearMonth ym = YearMonth.of(mes.getYear(), m);
            meses.add(new PestanaMes(ym.toString(), MESES_CORTOS[m - 1], ym.equals(mes)));
        }
        request.setAttribute("meses", meses);
        request.setAttribute("anio", mes.getYear());
        request.setAttribute("anioAnterior", mes.minusYears(1).toString());
        request.setAttribute("anioSiguiente", mes.plusYears(1).toString());
        request.setAttribute("mesActual", YearMonth.from(hoy).toString());
    }

    /* ===================== Armado de la grilla ===================== */

    private static void armarPlanilla(HttpServletRequest request, YearMonth mes, LocalDate hoy,
                                      List<Casa> casas, List<ReservaPlanilla> reservas,
                                      Map<Integer, Map<LocalDate, PlanillaDia>> anotados) {

        int dias = mes.lengthOfMonth();

        // Para cada casa: qué reserva ocupa cada noche del mes (índice 1..dias)
        Map<Integer, ReservaPlanilla[]> ocupacion = new HashMap<>();
        Map<Integer, boolean[]> superpuestas = new HashMap<>();
        for (Casa casa : casas) {
            ocupacion.put(casa.getId(), new ReservaPlanilla[dias + 1]);
            superpuestas.put(casa.getId(), new boolean[dias + 1]);
        }

        // Las confirmadas vienen primero: si una pendiente pisa una confirmada, se ve la confirmada
        for (ReservaPlanilla r : reservas) {
            ReservaPlanilla[] noches = ocupacion.get(r.getAlojamientoId());
            if (noches == null) {
                continue; // reserva de un alojamiento que ya no existe
            }
            for (int d = 1; d <= dias; d++) {
                if (!r.ocupa(mes.atDay(d))) {
                    continue;
                }
                if (noches[d] == null) {
                    noches[d] = r;
                } else {
                    superpuestas.get(r.getAlojamientoId())[d] = true;
                }
            }
        }

        // Filas (días) con sus celdas
        List<Fila> filas = new ArrayList<>();
        for (int d = 1; d <= dias; d++) {
            LocalDate fecha = mes.atDay(d);
            DayOfWeek diaSemana = fecha.getDayOfWeek();
            Fila fila = new Fila(d, LETRAS[diaSemana.getValue() - 1], fecha.equals(hoy),
                    diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY,
                    Feriados.nombre(fecha));

            String fechaTexto = diaSemana.getDisplayName(TextStyle.FULL, ES) + " " + d + " de "
                    + mes.getMonth().getDisplayName(TextStyle.FULL, ES);

            for (Casa casa : casas) {
                ReservaPlanilla[] noches = ocupacion.get(casa.getId());
                ReservaPlanilla r = noches[d];

                if (r == null) {
                    fila.getCeldas().add(new Celda("libre", casa, fecha.toString(),
                            casa.getNombre() + ", " + fechaTexto + ": libre",
                            fecha.isBefore(hoy), 1, null, false));
                    continue;
                }

                if (d > 1 && noches[d - 1] == r) {
                    continue; // este día ya lo cubre el bloque que empezó más arriba
                }

                // ¿Cuántos días seguidos ocupa este bloque dentro del mes?
                int largo = 1;
                boolean hayOtra = superpuestas.get(casa.getId())[d];
                while (d + largo <= dias && noches[d + largo] == r) {
                    hayOtra |= superpuestas.get(casa.getId())[d + largo];
                    largo++;
                }

                fila.getCeldas().add(new Celda("reserva", casa, fecha.toString(),
                        casa.getNombre() + ": " + r.getNombreMostrar() + ", del " + r.getIngresoTexto()
                                + " al " + r.getSalidaTexto() + ", "
                                + (r.isConfirmada() ? "confirmada" : "pendiente de confirmar"),
                        false, largo, r, hayOtra));
            }
            filas.add(fila);
        }

        // Precio y color anotados en cada casillero
        for (Fila fila : filas) {
            for (Celda celda : fila.getCeldas()) {
                Map<LocalDate, PlanillaDia> deLaCasa = anotados.get(celda.getCasa().getId());
                if (deLaCasa != null) {
                    celda.anotado = deLaCasa.get(LocalDate.parse(celda.getFecha()));
                }
            }
        }

        request.setAttribute("casas", casas);
        request.setAttribute("filas", filas);

        // Resumen de hoy (solo si se está mirando el mes actual)
        if (YearMonth.from(hoy).equals(mes)) {
            int ocupadas = 0;
            int porConfirmar = 0;
            for (Casa casa : casas) {
                ReservaPlanilla r = ocupacion.get(casa.getId())[hoy.getDayOfMonth()];
                if (r == null) {
                    continue;
                }
                if (r.isConfirmada()) {
                    ocupadas++;
                } else {
                    porConfirmar++;
                }
            }
            request.setAttribute("hoyOcupadas", ocupadas);
            request.setAttribute("hoyPorConfirmar", porConfirmar);
            request.setAttribute("hoyLibres", casas.size() - ocupadas - porConfirmar);
        }
    }

    /* ===================== Ayudas ===================== */

    /* ===================== Ayudas ===================== */

    /** Color de cada casa: el mismo del Excel según el nombre; si no coincide, uno de la lista. */
    private static String color(String nombre, int posicion) {
        String n = nombre == null ? "" : nombre.toLowerCase(ES);
        if (n.contains("pampita")) return "#C2FEF7";
        if (n.contains("grandiosa")) return "#D6DCE4";
        if (n.contains("morada")) return "#FFF2CB";
        if (n.contains("remanso")) return "#E2EFD9";
        if (n.contains("quebracho")) return "#FBE4D5";
        if (n.contains("guaviy")) return "#D9D9FF";
        if (n.contains("anacahuita")) return "#FFE1FF";

        String[] otros = {"#C2FEF7", "#D6DCE4", "#FFF2CB", "#E2EFD9", "#FBE4D5", "#D9D9FF", "#FFE1FF"};
        return otros[posicion % otros.length];
    }
}