package modelo;

import modelo.ValorBD;

/** Estado del estudiante. */
public enum EstadoEstudiante implements ValorBD {

    ACTIVO("ACTIVO", "Activo"),
    INACTIVO("INACTIVO", "Inactivo"),
    SANCIONADO("SANCIONADO", "Sancionado");

    private final String valorBD;
    private final String descripcion;

    EstadoEstudiante(String valorBD, String descripcion) {
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

    public boolean puedePrestar() {
        return this == ACTIVO;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
