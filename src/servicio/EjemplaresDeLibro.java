package servicio;

import java.util.List;
import modelo.*;

/** Los ejemplares de UN libro: guardados en la BD o pendientes (libro nuevo que aún no se guarda). */
public interface EjemplaresDeLibro {

    Libro getLibro();

    List<Ejemplar> listar();

    String siguienteCodigo();

    void verificarEditable(Ejemplar e);

    void guardar(Ejemplar e);

    void eliminar(Ejemplar e);
}
