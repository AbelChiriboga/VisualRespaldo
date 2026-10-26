package servicio;

import java.math.*;
import modelo.Parametro;
import util.Validaciones;

/** Los 4 parámetros que el administrador puede cambiar. */
public enum ParametroEditable {

    DIAS_PRESTAMO(Parametro.DIAS_PRESTAMO, "días de préstamo") {
        @Override
        public String validar(String valor) {
            return String.valueOf(Validaciones.entero(valor, getNombre(), 1, 60));
        }
    },
    MAX_PRESTAMOS(Parametro.MAX_PRESTAMOS, "máximo de préstamos") {
        @Override
        public String validar(String valor) {
            return String.valueOf(Validaciones.entero(valor, getNombre(), 1, 10));
        }
    },
    MULTA_POR_DIA(Parametro.MULTA_POR_DIA, "multa por día") {
        @Override
        public String validar(String valor) {
            BigDecimal multa = Validaciones.noNegativo(Validaciones.decimal(valor, getNombre()), getNombre());
            if (multa.compareTo(new BigDecimal("50")) > 0) {
                throw new excepciones.ValidacionException("La multa por día no puede pasar de $50.");
            }
            return multa.setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
    },
    UMBRAL_HUELLA(Parametro.UMBRAL_HUELLA, "umbral de huella") {
        @Override
        public String validar(String valor) {
            return String.valueOf(Validaciones.entero(valor, getNombre(), 1, 1000));
        }
    };

    private final String clave;
    private final String nombre;

    ParametroEditable(String clave, String nombre) {
        this.clave = clave;
        this.nombre = nombre;
    }

    public String getClave() {
        return clave;
    }

    public String getNombre() {
        return nombre;
    }

    public abstract String validar(String valor);
}
