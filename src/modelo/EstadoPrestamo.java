package modelo;

import modelo.ValorBD;

/** Estado de un préstamo. */
public enum EstadoPrestamo implements ValorBD {

    ACTIVO("ACTIVO", "Activo"),
    DEVUELTO("DEVUELTO", "Devuelto");

    private final String valorBD;
    private final String descripcion;

    EstadoPrestamo(String valorBD, String descripcion) {
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
