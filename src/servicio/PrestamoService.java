package servicio;

import excepciones.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import modelo.*;
import servicio.reglas.*;

/** Préstamo de uno o varios libros a la vez, SOLO con la huella del estudiante. */
public class PrestamoService extends AtencionConHuella {

    private final List<ReglaPrestamo> reglas = List.of(
            new EstudianteActivo(), new SinMultasPendientes(), new LimitePrestamos());

    private final dao.EjemplarDAO ejemplarDAO = new dao.EjemplarDAO();

    public PrestamoService() {
        super();
    }

    public PrestamoService(BiometriaService biometria) {
        super(biometria);
    }

    @Override
    protected OperacionAutenticacion operacion() {
        return OperacionAutenticacion.PRESTAMO;
    }

    @Override
    protected String accion() {
        return "registrar préstamos";
    }

    public List<Ejemplar> listarDisponibles() {
        return ejemplarDAO.buscarDisponibles("");
    }

    public LocalDate fechaLimite() {
        return LocalDate.now().plusDays(parametroDAO.obtenerEntero(Parametro.DIAS_PRESTAMO));
    }

    /** Cuántos libros se pueden marcar: sin huella, el máximo del parámetro; con huella, los cupos que le quedan. */
    public int maximoSeleccionable(FichaEstudiante ficha) {
        return ficha == null ? parametroDAO.obtenerEntero(Parametro.MAX_PRESTAMOS) : ficha.getCupos();
    }

    public Optional<String> motivoBloqueo(FichaEstudiante ficha) {
        return primerIncumplimiento(ficha, 1);
    }

    public List<Prestamo> prestar(FichaEstudiante ficha, List<Ejemplar> ejemplares) {
        exigirPermiso();
        exigirIdentificado(ficha);
        if (ejemplares.isEmpty()) {
            throw new ValidacionException("Seleccione al menos un libro.");
        }
        primerIncumplimiento(ficha, ejemplares.size()).ifPresent(motivo -> {
            throw new NegocioException(motivo);
        });
        List<Prestamo> prestamos = new ArrayList<>();
        for (Ejemplar ej : ejemplares) {
            prestamos.add(nuevoPrestamo(ficha, ej));
        }
        prestamoDAO.insertarVarios(prestamos);
        anotarExito(ficha, "Préstamo de " + prestamos.size() + " libro(s): " + codigos(ejemplares));
        return recargar(ficha, prestamos);
    }

    private Optional<String> primerIncumplimiento(FichaEstudiante ficha, int cantidad) {
        return reglas.stream()
                .map(regla -> regla.incumplimiento(ficha, cantidad))
                .flatMap(Optional::stream)
                .findFirst();
    }

    private static Prestamo nuevoPrestamo(FichaEstudiante ficha, Ejemplar ej) {
        if (!ej.getEstado().esPrestable()) {
            throw new NegocioException("El libro " + ej.getCodigoInventario() + " ya no está disponible.");
        }
        Prestamo p = new Prestamo(ej, ficha.getEstudiante(), Sesion.getInstancia().getUsuario());
        p.setHuellaPrestamo(true);
        return p;
    }

    private static String codigos(List<Ejemplar> ejemplares) {
        return ejemplares.stream().map(Ejemplar::getCodigoInventario).collect(Collectors.joining(", "));
    }

    private List<Prestamo> recargar(FichaEstudiante ficha, List<Prestamo> guardados) {
        Set<Integer> ids = guardados.stream().map(Prestamo::getId).collect(Collectors.toSet());
        return prestamoDAO.listarActivosPorEstudiante(ficha.getEstudiante().getIdEstudiante()).stream()
                .filter(p -> ids.contains(p.getId()))
                .collect(Collectors.toList());
    }
}
