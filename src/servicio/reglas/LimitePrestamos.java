package servicio.reglas;

import java.util.Optional;
import servicio.FichaEstudiante;

/** No superar el máximo de préstamos activos (parámetro MAX_PRESTAMOS). */
public class LimitePrestamos implements ReglaPrestamo {

    @Override
    public Optional<String> incumplimiento(FichaEstudiante ficha, int cantidad) {
        if (ficha.getCupos() == 0) {
            return Optional.of("El estudiante ya tiene " + ficha.getCantidadActivos() + " libros prestados (máximo "
                    + ficha.getMaximoPrestamos() + ").");
        }
        return cantidad > ficha.getCupos()
                ? Optional.of("Solo puede llevar " + ficha.getCupos() + " libro(s) más.")
                : Optional.empty();
    }
}
