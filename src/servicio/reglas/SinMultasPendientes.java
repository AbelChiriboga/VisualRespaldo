package servicio.reglas;

import java.util.Optional;
import servicio.FichaEstudiante;

/** Decisión del equipo: un estudiante con multas SIN PAGAR no puede llevar libros hasta que las pague (aunque. */
public class SinMultasPendientes implements ReglaPrestamo {

    @Override
    public Optional<String> incumplimiento(FichaEstudiante ficha, int cantidad) {
        return ficha.tieneMultasPendientes()
                ? Optional.of("El estudiante tiene multas sin pagar (" + util.Formatos.dinero(ficha.getMultasPendientes()) + ").")
                : Optional.empty();
    }
}
