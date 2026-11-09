package servicio.reglas;

import java.util.Optional;
import servicio.FichaEstudiante;

/** Un estudiante con libros atrasados (sin devolver) no puede llevar más hasta que los devuelva. */
public class SinLibrosAtrasados implements ReglaPrestamo {

    @Override
    public Optional<String> incumplimiento(FichaEstudiante ficha, int cantidad) {
        int atrasados = ficha.getCantidadAtrasados();
        return atrasados > 0
                ? Optional.of("El estudiante tiene " + atrasados + " libro(s) atrasado(s). Debe devolverlos antes de un nuevo préstamo.")
                : Optional.empty();
    }
}
