package servicio;

import dao.CategoriaDAO;
import java.util.List;
import java.util.function.Predicate;
import modelo.*;

/** Categorías de libros. */
public class CategoriaService extends ServicioCrud<Categoria> {

    private final CategoriaDAO categoriaDAO;

    public CategoriaService() {
        this(new CategoriaDAO());
    }

    public CategoriaService(CategoriaDAO dao) {
        super(dao);
        this.categoriaDAO = dao;
    }

    @Override
    protected Predicate<Usuario> permiso() {
        return Usuario::puedeGestionarCatalogo;
    }

    @Override
    protected String accion() {
        return "gestionar el catálogo";
    }

    public List<Categoria> listarConTotales() {
        return categoriaDAO.listarConTotales("");
    }

    @Override
    protected void antesDeEliminar(Categoria c) {
        if (c.getCantidadLibros() > 0) {
            throw new excepciones.NegocioException("No se puede eliminar: la categoría tiene libros.");
        }
    }
}
