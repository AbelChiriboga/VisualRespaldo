package servicio;

import dao.EjemplarDAO;
import excepciones.NegocioException;
import java.util.List;
import java.util.function.Predicate;
import modelo.*;

/** Ejemplares (copias físicas) de un libro. */
public class EjemplarService extends ServicioCrud<Ejemplar> {

    private final EjemplarDAO ejemplarDAO;

    public EjemplarService() {
        this(new EjemplarDAO());
    }

    public EjemplarService(EjemplarDAO dao) {
        super(dao);
        this.ejemplarDAO = dao;
    }

    @Override
    protected Predicate<Usuario> permiso() {
        return Usuario::puedeGestionarCatalogo;
    }

    @Override
    protected String accion() {
        return "gestionar el catálogo";
    }

    public String siguienteCodigo() {
        return Ejemplar.codigoNumero(ejemplarDAO.ultimoNumeroCodigo() + 1);
    }

    public List<Ejemplar> listarPorLibro(Libro libro) {
        return ejemplarDAO.listarPorLibro(libro.getId());
    }

    @Override
    protected void validar(Ejemplar e) {
        exigirEstadoElegible(e);
        exigirQueNoEstePrestado(e);
    }

    public void exigirEstadoElegible(Ejemplar e) {
        if (!e.getEstado().esEditableManualmente()) {
            throw new NegocioException("El estado " + e.getEstado().getDescripcion() + " no se puede elegir.");
        }
    }

    @Override
    protected void antesDeEliminar(Ejemplar e) {
        exigirQueNoEstePrestado(e);
    }

    public void verificarEditable(Ejemplar e) {
        exigirQueNoEstePrestado(e);
    }

    private void exigirQueNoEstePrestado(Ejemplar e) {
        if (e.esNuevo()) {
            return;
        }
        ejemplarDAO.buscarPorId(e.getId())
                .filter(guardado -> guardado.getEstado() == EstadoEjemplar.PRESTADO)
                .ifPresent(guardado -> {
                    throw new NegocioException("El ejemplar está prestado. Espere a que lo devuelvan.");
                });
    }
}
