package modelo;

import modelo.ValorBD;

/** Estado de una copia física. */
public enum EstadoEjemplar implements ValorBD {

    DISPONIBLE("DISPONIBLE", "Disponible"),
    PRESTADO("PRESTADO", "Prestado"),
    DANADO("DANADO", "Dañado"),
    PERDIDO("PERDIDO", "Perdido");

    private final String valorBD;
    private final String descripcion;

    EstadoEjemplar(String valorBD, String descripcion) {
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

    public boolean esPrestable() {
        return this == DISPONIBLE;
    }

    public boolean esEditableManualmente() {
        return this != PRESTADO;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
