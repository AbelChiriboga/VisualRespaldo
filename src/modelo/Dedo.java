package modelo;

import modelo.ValorBD;

/** Dedo de la huella registrada (solo se elige al registrar). */
public enum Dedo implements ValorBD {

    PULGAR_DERECHO("PULGAR_DER", "Pulgar derecho"),
    INDICE_DERECHO("INDICE_DER", "Índice derecho"),
    MEDIO_DERECHO("MEDIO_DER", "Medio derecho"),
    ANULAR_DERECHO("ANULAR_DER", "Anular derecho"),
    MENIQUE_DERECHO("MENIQUE_DER", "Meñique derecho"),
    PULGAR_IZQUIERDO("PULGAR_IZQ", "Pulgar izquierdo"),
    INDICE_IZQUIERDO("INDICE_IZQ", "Índice izquierdo"),
    MEDIO_IZQUIERDO("MEDIO_IZQ", "Medio izquierdo"),
    ANULAR_IZQUIERDO("ANULAR_IZQ", "Anular izquierdo"),
    MENIQUE_IZQUIERDO("MENIQUE_IZQ", "Meñique izquierdo");

    private final String valorBD;
    private final String descripcion;

    Dedo(String valorBD, String descripcion) {
        this.valorBD = valorBD;
        this.descripcion = descripcion;
    }

    @Override
    public String getValorBD() {
        return valorBD;
    }

    @Override
    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
