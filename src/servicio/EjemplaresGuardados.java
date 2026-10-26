package servicio;

import java.util.List;
import modelo.*;

/** Ejemplares de un libro que ya existe: cada cambio se guarda en la BD al momento. */
public class EjemplaresGuardados implements EjemplaresDeLibro {

    private final EjemplarService servicio = new EjemplarService();
    private final Libro libro;

    public EjemplaresGuardados(Libro libro) {
        this.libro = util.Validaciones.requerido(libro, "un libro");
    }

    @Override
    public Libro getLibro() {
        return libro;
    }

    @Override
    public List<Ejemplar> listar() {
        return servicio.listarPorLibro(libro);
    }

    @Override
    public String siguienteCodigo() {
        return servicio.siguienteCodigo();
    }

    @Override
    public void verificarEditable(Ejemplar e) {
        servicio.verificarEditable(e);
    }

    @Override
    public void guardar(Ejemplar e) {
        servicio.guardar(e);
    }

    @Override
    public void eliminar(Ejemplar e) {
        servicio.eliminar(e);
    }
}
