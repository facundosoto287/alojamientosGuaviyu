package org.example;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;

/**
 * Lo que la administradora anota en un casillero de la planilla de Disponibilidad
 * (una casa en un día): el precio por noche de ese día y un color para marcarlo
 * (feriado, temporada alta, etc.). Las dos cosas son opcionales.
 * Tabla: planilla_dia.
 */
public class PlanillaDia {

    private final int alojamientoId;
    private final LocalDate fecha;
    private final BigDecimal precio;   // null = sin precio anotado
    private final String color;        // null = el color de la casa; si no, "#RRGGBB"

    public PlanillaDia(int alojamientoId, LocalDate fecha, BigDecimal precio, String color) {
        this.alojamientoId = alojamientoId;
        this.fecha = fecha;
        this.precio = precio;
        this.color = color;
    }

    public int getAlojamientoId() { return alojamientoId; }
    public LocalDate getFecha() { return fecha; }
    public BigDecimal getPrecio() { return precio; }
    public String getColor() { return color; }

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