package servicio;

import dao.*;
import modelo.*;

/** Lo que tienen en común el préstamo y la devolución: las dos se atienden SOLO con huella. */
public abstract class AtencionConHuella {

    protected final BiometriaService biometria;
    protected final PrestamoDAO prestamoDAO = new PrestamoDAO();
    protected final MultaDAO multaDAO = new MultaDAO();
    protected final ParametroDAO parametroDAO = new ParametroDAO();

    protected AtencionConHuella() {
        this(new BiometriaService());
    }

    protected AtencionConHuella(BiometriaService biometria) {
        this.biometria = biometria;
    }

    protected abstract OperacionAutenticacion operacion();

    protected abstract String accion();

    public final FichaEstudiante identificar() {
        exigirPermiso();
        BiometriaService.Identificacion id = biometria.identificarEstudiante(operacion());
        return armarFicha(id.getEstudiante(), id.getPuntaje());
    }

    public void cancelarLectura() {
        biometria.cancelarCaptura();
    }

    protected FichaEstudiante armarFicha(modelo.Estudiante e, int puntaje) {
        return new FichaEstudiante(e, puntaje, prestamoDAO.listarActivosPorEstudiante(e.getIdEstudiante()),
                parametroDAO.obtenerEntero(Parametro.MAX_PRESTAMOS), multaDAO.totalPendiente(e.getIdEstudiante()));
    }

    protected void exigirPermiso() {
        Sesion.getInstancia().exigir(Usuario::puedeGestionarPrestamos, accion());
    }

    protected static void exigirIdentificado(FichaEstudiante ficha) {
        if (ficha == null) {
            throw new excepciones.ValidacionException("Ingrese la huella del estudiante.");
        }
    }

    protected void anotarExito(FichaEstudiante ficha, String detalle) {
        biometria.anotar(ficha.getEstudiante().getIdEstudiante(), operacion(),
                modelo.ResultadoAutenticacion.EXITO, ficha.getPuntaje(), detalle);
    }
}
