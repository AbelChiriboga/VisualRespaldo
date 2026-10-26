package servicio;

import dao.CrudDAO;
import java.util.List;
import java.util.function.Predicate;
import modelo.*;
import util.Validaciones;

/** Servicio CRUD GENÉRICO y ABSTRACTO para las entidades con id numérico. */
public abstract class ServicioCrud<T extends Entidad> {

    protected final CrudDAO<T, Integer> dao;

    protected ServicioCrud(CrudDAO<T, Integer> dao) {
        this.dao = dao;
    }

    protected abstract Predicate<Usuario> permiso();

    protected abstract String accion();

    protected void validar(T objeto) {
    }

    protected void antesDeEliminar(T objeto) {
    }

    public List<T> listar() {
        return dao.listar();
    }

    public T guardar(T objeto) {
        exigirPermiso();
        Validaciones.requerido(objeto, "un registro");
        validar(objeto);
        if (objeto.esNuevo()) {
            return dao.insertar(objeto);
        }
        dao.actualizar(objeto);
        return objeto;
    }

    public void eliminar(T objeto) {
        exigirPermiso();
        Validaciones.requerido(objeto, "un registro");
        antesDeEliminar(objeto);
        dao.eliminar(objeto.getId());
    }

    protected void exigirPermiso() {
        Sesion.getInstancia().exigir(permiso(), accion());
    }
}
