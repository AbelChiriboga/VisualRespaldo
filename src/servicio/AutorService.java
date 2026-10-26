package servicio;

import dao.AutorDAO;
import java.util.function.Predicate;
import modelo.*;

/** Autores. */
public class AutorService extends ServicioCrud<Autor> {

    public AutorService() {
        super(new AutorDAO());
    }

    @Override
    protected Predicate<Usuario> permiso() {
        return Usuario::puedeGestionarCatalogo;
    }

    @Override
    protected String accion() {
        return "gestionar el catálogo";
    }
}
