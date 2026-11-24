package org.example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Seña a favor: lo que dejó un cliente en una reserva CONFIRMADA que después tuvo que cancelar.
 * En la tabla "sena" solo se guarda la reserva, el importe y si ya la usó.
 * El resto (cliente, celular, casa, fecha de ingreso) sale de la reserva,
 * y la validez se calcula: 6 meses desde la fecha de ingreso de la reserva cancelada.
 * Sirve para cualquier casa.
 */
public class Sena {

    /** Meses que se guarda la seña. */
    public static final int MESES_VALIDEZ = 6;

    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final int id;
    private final int reservaId;
    private final BigDecimal importe;
    private final boolean usada;

    // Datos que vienen de la reserva cancelada
    private final LocalDate fechaIngreso;
    private final String alojamientoNombre;
    private final Integer clienteId;
    private final String clienteNombre;
    private final String clienteDocumento;
    private final String clienteTelefono;

    public Sena(int id, int reservaId, BigDecimal importe, boolean usada, LocalDate fechaIngreso,
                String alojamientoNombre, Integer clienteId, String clienteNombre,
                String clienteDocumento, String clienteTelefono) {
        this.id = id;
        this.reservaId = reservaId;
        this.importe = importe;
        this.usada = usada;
        this.fechaIngreso = fechaIngreso;
        this.alojamientoNombre = alojamientoNombre;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.clienteDocumento = clienteDocumento;
        this.clienteTelefono = clienteTelefono;
    }

    public int getId() { return id; }
    public int getReservaId() { return reservaId; }
    public BigDecimal getImporte() { return importe; }
    public boolean isUsada() { return usada; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public String getAlojamientoNombre() { return alojamientoNombre; }
    public Integer getClienteId() { return clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public String getClienteDocumento() { return clienteDocumento; }
    public String getClienteTelefono() { return clienteTelefono; }

    /** Último día en que se puede usar. */
    public LocalDate getValidaHasta() {
        return fechaIngreso.plusMonths(MESES_VALIDEZ);
    }

    public boolean isVencida() {
        return !usada && LocalDate.now().isAfter(getValidaHasta());
    }

    public boolean isVigente() {
        return !usada && !isVencida();
    }

    /** VIGENTE, USADA o VENCIDA. */
    public String getEstado() {
        if (usada) return "USADA";
        return isVencida() ? "VENCIDA" : "VIGENTE";
    }

    /** Días que faltan para que venza (0 si vence hoy; negativo si ya venció). */
    public long getDiasRestantes() {
        return ChronoUnit.DAYS.between(LocalDate.now(), getValidaHasta());
    }

    /** Vigente y vence en 30 días o menos: para resaltarla. */
    public boolean isPorVencer() {
        return isVigente() && getDiasRestantes() <= 30;
    }

    public String getFechaIngresoTexto() { return fechaIngreso.format(DIA); }
    public String getValidaHastaTexto() { return getValidaHasta().format(DIA); }
    public String getImporteTexto() { return PlanillaDia.precioTexto(importe); }

    /** Nombre para mostrar (una reserva vieja puede no tener cliente vinculado). */
    public String getNombreMostrar() {
        return clienteNombre == null || clienteNombre.isBlank() ? "Sin cliente" : clienteNombre;
    }

    /** Celular listo para WhatsApp (099 123 456 → 59899123456), o "" si no es un celular uruguayo. */
    public String getTelefonoWhatsapp() {
        if (clienteTelefono == null) return "";
        String numeros = clienteTelefono.replaceAll("\\D", "");
        if (numeros.matches("09\\d{7}")) return "598" + numeros.substring(1);
        if (numeros.matches("5989\\d{7}")) return numeros;
        return "";
    }

    /** Texto corto para avisos: "$ 2.000 a favor hasta el 14/07/2027". */
    public String getResumen() {
        return getImporteTexto() + " a favor hasta el " + getValidaHastaTexto();
    }
}