package org.example;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;

/**
 * Lo que la administradora anota en un casillero de la planilla de Disponibilidad
 * (una casa en un día): el precio por noche de ese día y un color para marcarlo
 * (feriado, temporada alta, etc.), y una nota. Todo es opcional.
 * La observación de una reserva ("llega 22 hs", "trae mascota") se guarda como nota
 * en la casa y el día de ingreso de esa reserva.
 * Tabla: planilla_dia.
 */
public class PlanillaDia {

    private final int alojamientoId;
    private final LocalDate fecha;
    private final BigDecimal precio;   // null = sin precio anotado
    private final String color;        // null = el color de la casa; si no, "#RRGGBB"
    private final String nota;         // null = sin nota

    public PlanillaDia(int alojamientoId, LocalDate fecha, BigDecimal precio, String color, String nota) {
        this.alojamientoId = alojamientoId;
        this.fecha = fecha;
        this.precio = precio;
        this.color = color;
        this.nota = nota;
    }

    public int getAlojamientoId() { return alojamientoId; }
    public LocalDate getFecha() { return fecha; }
    public BigDecimal getPrecio() { return precio; }
    public String getColor() { return color; }
    public String getNota() { return nota; }

    /** Precio para mostrar: "$ 3.500" o "$ 3.500,50". Vacío si no hay precio. */
    public String getPrecioTexto() {
        return precioTexto(precio);
    }

    public static String precioTexto(BigDecimal precio) {
        if (precio == null) {
            return "";
        }
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');
        boolean entero = precio.stripTrailingZeros().scale() <= 0;
        DecimalFormat formato = new DecimalFormat(entero ? "#,##0" : "#,##0.00", simbolos);
        return "$ " + formato.format(precio);
    }
}