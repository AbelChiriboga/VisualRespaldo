package modelo;

import java.math.*;
import java.time.LocalDateTime;
import modelo.Prestamo;
import util.Validaciones;

/** Multa por devolver tarde. */
public class Multa extends Entidad {

    private Prestamo prestamo;
    private int diasAtraso;
    private BigDecimal monto = BigDecimal.ZERO;
    private boolean pagada;
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaPago;

    public Multa() {
    }

    public static BigDecimal calcularMonto(int diasAtraso, BigDecimal multaPorDia) {
        if (diasAtraso <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        return multaPorDia.multiply(BigDecimal.valueOf(diasAtraso)).setScale(2, RoundingMode.HALF_UP);
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public int getDiasAtraso() {
        return diasAtraso;
    }

    public void setDiasAtraso(int diasAtraso) {
        this.diasAtraso = Validaciones.rango(diasAtraso, "días de atraso", 0, Integer.MAX_VALUE);
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = Validaciones.noNegativo(monto, "monto");
    }

    public boolean isPagada() {
        return pagada;
    }

    public void setPagada(boolean pagada) {
        this.pagada = pagada;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMontoTexto() {
        return "$" + monto.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
