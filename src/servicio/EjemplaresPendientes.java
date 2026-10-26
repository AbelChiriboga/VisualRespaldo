package servicio;

import java.util.*;
import modelo.*;

/** Ejemplares de un libro NUEVO: quedan en memoria y se guardan junto con el libro. */
public class EjemplaresPendientes implements EjemplaresDeLibro {

    private final EjemplarService servicio = new EjemplarService();
    private final Libro libro;
    private final List<Ejemplar> ejemplares = new ArrayList<>();

    public EjemplaresPendientes(Libro libro) {
        this.libro = util.Validaciones.requerido(libro, "un libro");
    }

    @Override
    public Libro getLibro() {
        return libro;
    }

    @Override
    public List<Ejemplar> listar() {
        return new ArrayList<>(ejemplares);
    }

    /** El siguiente código libre, contando también los que están pendientes. */
    @Override
    public String siguienteCodigo() {
        int siguiente = Integer.parseInt(numero(servicio.siguienteCodigo()));
        for (Ejemplar e : ejemplares) {
            siguiente = Math.max(siguiente, Integer.parseInt(numero(e.getCodigoInventario())) + 1);
        }
        return Ejemplar.codigoNumero(siguiente);
    }

    @Override
    public void verificarEditable(Ejemplar e) {
    }

    @Override
    public void guardar(Ejemplar e) {
        servicio.exigirEstadoElegible(e);
        if (ejemplares.stream().noneMatch(x -> x == e)) {
            ejemplares.add(e);
        }
    }

    @Override
    public void eliminar(Ejemplar e) {
        ejemplares.removeIf(x -> x == e);
    }

    private static String numero(String codigo) {
        return codigo.substring(codigo.lastIndexOf('-') + 1);
    }
}
