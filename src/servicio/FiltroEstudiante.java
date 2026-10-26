package servicio;

import java.util.function.Predicate;
import modelo.EstadoEstudiante;

/** Filtro del combo "Estado" en la consulta de estudiantes. */
public enum FiltroEstudiante {

    TODOS("Todos", s -> true),
    ACTIVOS("Activos", s -> s.getEstudiante().getEstado() == EstadoEstudiante.ACTIVO),
    INACTIVOS("Inactivos", s -> s.getEstudiante().getEstado() != EstadoEstudiante.ACTIVO),
    CON_ATRASOS("Con atrasos", s -> s.getCantidadAtrasados() > 0),
    CON_MULTAS("Con multas", SituacionEstudiante::tieneMultasPendientes);

    private final String descripcion;
    private final Predicate<SituacionEstudiante> condicion;

    FiltroEstudiante(String descripcion, Predicate<SituacionEstudiante> condicion) {
        this.descripcion = descripcion;
        this.condicion = condicion;
    }

    public boolean cumple(SituacionEstudiante s) {
        return condicion.test(s);
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
