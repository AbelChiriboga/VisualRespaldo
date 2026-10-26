package modelo;

import java.math.BigDecimal;
import util.Validaciones;

/** Parámetro del sistema (tabla parametros: clave / valor). */
public class Parametro {

    public static final String DIAS_PRESTAMO = "DIAS_PRESTAMO";
    public static final String MAX_PRESTAMOS = "MAX_PRESTAMOS";
    public static final String MULTA_POR_DIA = "MULTA_POR_DIA";
    public static final String UMBRAL_HUELLA = "UMBRAL_HUELLA";

    private String clave;
    private String valor;
    private String descripcion;

    public Parametro() {
    }

    public Parametro(String clave, String valor) {
        setClave(clave);
        setValor(valor);
    }

    public int getValorEntero() {
        return Validaciones.entero(valor, clave, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public BigDecimal getValorDecimal() {
        return Validaciones.decimal(valor, clave);
    }

    public String getClave() {
        return clave;
    }

    public final void setClave(String clave) {
        this.clave = Validaciones.obligatorio(clave, "clave").toUpperCase();
    }

    public String getValor() {
        return valor;
    }

    public final void setValor(String valor) {
        this.valor = Validaciones.obligatorio(valor, clave == null ? "valor" : clave);
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
