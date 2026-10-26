package servicio;

import dao.*;
import java.util.List;
import java.util.function.Predicate;
import modelo.*;

/** Libros del catálogo. */
public class LibroService extends ServicioCrud<Libro> {

    private final AutorDAO autorDAO = new AutorDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final LibroDAO libroDAO;

    public LibroService() {
        this(new LibroDAO());
    }

    private LibroService(LibroDAO dao) {
        super(dao);
        this.libroDAO = dao;
    }

    /** Guarda un libro nuevo junto con los ejemplares que se agregaron antes de guardarlo (pueden ser cero). */
    public void crear(Libro l, List<modelo.Ejemplar> ejemplares) {
        exigirPermiso();
        util.Validaciones.requerido(l, "un libro");
        validar(l);
        libroDAO.insertarConEjemplares(l, ejemplares);
    }

    @Override
    protected Predicate<Usuario> permiso() {
        return Usuario::puedeGestionarCatalogo;
    }

    @Override
    protected String accion() {
        return "gestionar el catálogo";
    }

    public List<Autor> listarAutores() {
        return autorDAO.listar();
    }

    public List<Categoria> listarCategorias() {
        return categoriaDAO.listar();
    }

    @Override
    protected void antesDeEliminar(Libro l) {
        if (l.getEjemplaresTotales() > 0) {
            throw new excepciones.NegocioException("No se puede eliminar: el libro tiene ejemplares.");
        }
    }
}
