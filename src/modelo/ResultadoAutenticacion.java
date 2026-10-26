package modelo;

import modelo.ValorBD;

/** Resultado de un intento con la huella (bitácora). */
public enum ResultadoAutenticacion implements ValorBD {

    EXITO("EXITO", "Éxito"),
    FALLO("FALLO", "Fallo");

    private final String valorBD;
    private final String descripcion;

    ResultadoAutenticacion(String valorBD, String descripcion) {
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
