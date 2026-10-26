package dao;

/** Contrato CRUD genérico: insertar, actualizar, eliminar (o desactivar), buscarPorId y listar. */
public interface CrudDAO<T, ID> extends ConsultaDAO<T, ID> {

    T insertar(T objeto);

    void actualizar(T objeto);

    void eliminar(ID id);
}
