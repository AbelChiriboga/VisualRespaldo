package servicio;

import java.math.BigDecimal;
import java.util.List;
import modelo.*;

/** El estudiante que acaba de poner el dedo en Préstamo o Devolución. */
public final class FichaEstudiante extends SituacionEstudiante {

    private final int puntaje;

    public FichaEstudiante(Estudiante estudiante, int puntaje, List<Prestamo> prestamosActivos,
                           int maximoPrestamos, BigDecimal multasPendientes) {
        super(estudiante, prestamosActivos, maximoPrestamos, multasPendientes);
        this.puntaje = puntaje;
    }

    public int getPuntaje() {
        return puntaje;
    }
}
