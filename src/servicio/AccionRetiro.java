package servicio;

import modelo.*;

/** Qué hace el botón Eliminar de Estudiantes: sin historial se elimina; con historial solo se desactiva o activa. */
public enum AccionRetiro {

    ELIMINAR("Eliminar", "¿Eliminar a %s?") {
        @Override
        public void ejecutar(EstudianteService servicio, Estudiante e) {
            servicio.eliminar(e);
        }
    },
    DESACTIVAR("Desactivar", "%s tiene historial de préstamos y no se puede eliminar.\n¿Desea desactivarlo?") {
        @Override
        public void ejecutar(EstudianteService servicio, Estudiante e) {
            servicio.cambiarEstado(e, EstadoEstudiante.INACTIVO);
        }
    },
    ACTIVAR("Activar", "¿Desea activar a %s?") {
        @Override
        public void ejecutar(EstudianteService servicio, Estudiante e) {
            servicio.cambiarEstado(e, EstadoEstudiante.ACTIVO);
        }
    };

    private final String texto;
    private final String pregunta;

    AccionRetiro(String texto, String pregunta) {
        this.texto = texto;
        this.pregunta = pregunta;
    }

    public String getTexto() {
        return texto;
    }

    public String pregunta(Estudiante e) {
        return String.format(pregunta, e.getNombreCompleto());
    }

    public abstract void ejecutar(EstudianteService servicio, Estudiante e);
}
