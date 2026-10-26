package modelo;

import modelo.ValorBD;

/** Operación en la que se usó la huella (bitácora). */
public enum OperacionAutenticacion implements ValorBD {

    LOGIN("LOGIN", "Inicio de sesión"),
    REGISTRO_HUELLA("REGISTRO_HUELLA", "Registro de huella"),
    PRESTAMO("PRESTAMO", "Préstamo"),
    DEVOLUCION("DEVOLUCION", "Devolución");

    private final String valorBD;
    private final String descripcion;

    OperacionAutenticacion(String valorBD, String descripcion) {
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
