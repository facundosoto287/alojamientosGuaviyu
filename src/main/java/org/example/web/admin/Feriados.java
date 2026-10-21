package org.example.web.admin;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Feriados y fechas especiales que la Planilla de ocupación pinta de naranja,
 * como en la planilla de Excel. La lista es fácil de editar: agregá o sacá líneas.
 */
final class Feriados {

    private Feriados() {
    }

    /** Feriados que caen siempre el mismo día. */
    private static final Map<MonthDay, String> FIJOS = new LinkedHashMap<>();

    /**
     * Feriados "trasladables": si caen martes o miércoles se pasan al lunes anterior,
     * y si caen jueves o viernes, al lunes siguiente.
     */
    private static final Map<MonthDay, String> TRASLADABLES = new LinkedHashMap<>();

    static {
        FIJOS.put(MonthDay.of(1, 1), "Año Nuevo");
        FIJOS.put(MonthDay.of(5, 1), "Día de los Trabajadores");
        FIJOS.put(MonthDay.of(6, 19), "Natalicio de Artigas");
        FIJOS.put(MonthDay.of(7, 18), "Jura de la Constitución");
        FIJOS.put(MonthDay.of(8, 25), "Independencia");
        FIJOS.put(MonthDay.of(11, 2), "Día de los Difuntos");
        FIJOS.put(MonthDay.of(12, 25), "Navidad");

        TRASLADABLES.put(MonthDay.of(1, 6), "Reyes");
        TRASLADABLES.put(MonthDay.of(4, 19), "Desembarco de los 33");
        TRASLADABLES.put(MonthDay.of(5, 18), "Batalla de Las Piedras");
        TRASLADABLES.put(MonthDay.of(10, 12), "Diversidad Cultural");
    }

    /** Nombre del feriado de ese día, o null si es un día común. */
    static String nombre(LocalDate dia) {
        String fijo = FIJOS.get(MonthDay.from(dia));
        if (fijo != null) {
            return fijo;
        }

        for (Map.Entry<MonthDay, String> feriado : TRASLADABLES.entrySet()) {
            if (dia.equals(trasladar(feriado.getKey().atYear(dia.getYear())))) {
                return feriado.getValue();
            }
        }

        // Carnaval y Semana de Turismo dependen de la fecha de Pascua
        LocalDate pascua = pascua(dia.getYear());
        if (dia.equals(pascua.minusDays(48)) || dia.equals(pascua.minusDays(47))) {
            return "Carnaval";
        }
        if (!dia.isBefore(pascua.minusDays(6)) && !dia.isAfter(pascua)) {
            return "Semana de Turismo";
        }
        return null;
    }

    private static LocalDate trasladar(LocalDate fecha) {
        DayOfWeek dia = fecha.getDayOfWeek();
        return switch (dia) {
            case TUESDAY, WEDNESDAY -> fecha.minusDays(dia.getValue() - 1L);   // lunes anterior
            case THURSDAY, FRIDAY -> fecha.plusDays(8L - dia.getValue());      // lunes siguiente
            default -> fecha;
        };
    }

    /** Domingo de Pascua (algoritmo de Gauss/Meeus para el calendario gregoriano). */
    static LocalDate pascua(int anio) {
        int a = anio % 19;
        int b = anio / 100;
        int c = anio % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int mes = (h + l - 7 * m + 114) / 31;
        int diaMes = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(anio, mes, diaMes);
    }
}