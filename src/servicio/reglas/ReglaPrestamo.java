package servicio.reglas;

import java.util.Optional;
import servicio.FichaEstudiante;

/** Una regla que debe cumplirse para prestar. */
@FunctionalInterface
public interface ReglaPrestamo {

    Optional<String> incumplimiento(FichaEstudiante ficha, int cantidad);
}
