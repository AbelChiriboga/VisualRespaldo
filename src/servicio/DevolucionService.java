package servicio;

import excepciones.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import modelo.*;

/** Devolución de uno o varios libros, SOLO con la huella del mismo estudiante que los llevó. */
public class DevolucionService extends AtencionConHuella {

    public DevolucionService() {
        super();
    }

    public DevolucionService(BiometriaService biometria) {
        super(biometria);
    }

    @Override
    protected OperacionAutenticacion operacion() {
        return OperacionAutenticacion.DEVOLUCION;
    }

    @Override
    protected String accion() {
        return "registrar devoluciones";
    }

    public BigDecimal multaPorDia() {
        return parametroDAO.obtenerDecimal(Parametro.MULTA_POR_DIA);
    }

    public ResumenDevolucion devolver(FichaEstudiante ficha, List<Prestamo> marcados) {
        exigirPermiso();
        exigirIdentificado(ficha);
        if (marcados.isEmpty()) {
            throw new ValidacionException("Seleccione al menos un libro.");
        }
        exigirQueSeanSuyos(ficha, marcados);
        exigirFechaPosteriorAlPrestamo(marcados);
        List<Integer> ids = marcados.stream().map(Prestamo::getId).collect(Collectors.toList());
        prestamoDAO.registrarDevoluciones(ids);
        anotarExito(ficha, "Devolución de " + ids.size() + " libro(s): " + marcados.stream()
                .map(p -> p.getEjemplar().getCodigoInventario()).collect(Collectors.joining(", ")));
        return new ResumenDevolucion(devueltos(ficha, ids), multaPorDia());
    }

    private static void exigirQueSeanSuyos(FichaEstudiante ficha, List<Prestamo> marcados) {
        Set<Integer> suyos = ficha.getPrestamosActivos().stream().map(Prestamo::getId).collect(Collectors.toSet());
        marcados.stream().filter(p -> !suyos.contains(p.getId())).findFirst().ifPresent(p -> {
            throw new NegocioException("El libro " + p.getEjemplar().getCodigoInventario()
                    + " no está prestado a este estudiante.");
        });
    }

    /** La fecha del equipo no puede ser anterior al día en que se prestó el libro (no tendría lógica). */
    private static void exigirFechaPosteriorAlPrestamo(List<Prestamo> marcados) {
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        marcados.stream().filter(p -> ahora.isBefore(p.getFechaPrestamo())).findFirst().ifPresent(p -> {
            throw new NegocioException("No se puede devolver el libro " + p.getEjemplar().getCodigoInventario()
                    + " antes de la fecha en que se prestó.\n"
                    + "Fecha del préstamo: " + util.Formatos.fechaHora(p.getFechaPrestamo()) + "\n"
                    + "Fecha del equipo: " + util.Formatos.fechaHora(ahora) + "\n"
                    + "Revise la fecha y hora del equipo.");
        });
    }

    private List<Prestamo> devueltos(FichaEstudiante ficha, List<Integer> ids) {
        return prestamoDAO.listarPorEstudiante(ficha.getEstudiante().getIdEstudiante()).stream()
                .filter(p -> ids.contains(p.getId()))
                .collect(Collectors.toList());
    }
}
