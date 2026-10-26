package servicio.regla;

import java.util.Optional;
import servicio.FichaEstudiante;

/** Solo los estudiantes ACTIVOS pueden llevar libros (el estado decide con puedePrestar()). */
public class EstudianteActivo implements ReglaPrestamo {

    @Override
    public Optional<String> incumplimiento(FichaEstudiante ficha, int cantidad) {
        return ficha.getEstudiante().puedePrestar()
                ? Optional.empty()
                : Optional.of("El estudiante está " + ficha.getEstudiante().getEstado().getDescripcion().toLowerCase() + ".");
    }
}
