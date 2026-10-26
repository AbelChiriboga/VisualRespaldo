package modelo;

/** Contrato de los enums que se guardan en la BD como texto. */
public interface ValorBD {

    String getValorBD();

    String getDescripcion();

    static <E extends Enum<E> & ValorBD> E desde(Class<E> tipo, String valor) {
        if (valor == null) {
            return null;
        }
        for (E constante : tipo.getEnumConstants()) {
            if (constante.getValorBD().equalsIgnoreCase(valor.trim())
                    || constante.name().equalsIgnoreCase(valor.trim())) {
                return constante;
            }
        }
        throw new IllegalArgumentException("Valor '" + valor + "' no válido para " + tipo.getSimpleName());
    }
}
