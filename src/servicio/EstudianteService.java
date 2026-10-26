package servicio;

import dao.*;
import excepciones.*;
import java.util.*;
import java.util.stream.Collectors;
import modelo.*;
import util.Validaciones;

/** Estudiantes. */
public class EstudianteService {

    private final EstudianteDAO dao = new EstudianteDAO();
    private final HuellaDAO huellaDAO = new HuellaDAO();
    private final CarreraDAO carreraDAO = new CarreraDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final dao.RegistroAutenticacionDAO bitacora = new dao.RegistroAutenticacionDAO();
    private final dao.MultaDAO multaDAO = new dao.MultaDAO();
    private final CedulaUnica cedulaUnica = new CedulaUnica();
    private final dao.ParametroDAO parametroDAO = new dao.ParametroDAO();

    public List<Estudiante> listarConHuellas() {
        List<Estudiante> estudiantes = dao.listar();
        Map<Integer, List<Huella>> huellas = huellaDAO.listar().stream()
                .collect(Collectors.groupingBy(Huella::getIdEstudiante));
        for (Estudiante e : estudiantes) {
            huellas.getOrDefault(e.getIdEstudiante(), List.of()).forEach(e::agregarHuella);
        }
        return estudiantes;
    }

    public List<SituacionEstudiante> listarSituaciones() {
        Map<Integer, List<modelo.Prestamo>> activos = prestamoDAO.listarActivos().stream()
                .collect(Collectors.groupingBy(p -> p.getEstudiante().getIdEstudiante()));
        int maximo = parametroDAO.obtenerEntero(modelo.Parametro.MAX_PRESTAMOS);
        return dao.listar().stream()
                .map(e -> new SituacionEstudiante(e, activos.getOrDefault(e.getIdEstudiante(), List.of()), maximo,
                        multaDAO.totalPendiente(e.getIdEstudiante())))
                .collect(Collectors.toList());
    }

    public SituacionEstudiante situacion(Estudiante e) {
        return new SituacionEstudiante(e, prestamoDAO.listarPorEstudiante(e.getIdEstudiante()),
                parametroDAO.obtenerEntero(modelo.Parametro.MAX_PRESTAMOS), multaDAO.totalPendiente(e.getIdEstudiante()));
    }

    public List<Carrera> listarCarreras() {
        return carreraDAO.listar();
    }

    public Estudiante guardar(Estudiante e, Map<Dedo, byte[]> huellasCapturadas) {
        exigir(Usuario::puedeRegistrarEstudiantes, "registrar estudiantes");
        validarCedula(e, e.getCedula());
        if (!e.tieneHuella() && huellasCapturadas.isEmpty()) {
            throw new ValidacionException("Registre la huella del estudiante.");
        }
        dao.guardarConHuellas(e, huellasCapturadas);
        huellasCapturadas.keySet().forEach(dedo -> bitacora.insertar(new RegistroAutenticacion(e.getIdEstudiante(),
                OperacionAutenticacion.REGISTRO_HUELLA, ResultadoAutenticacion.EXITO, null,
                dedo.getDescripcion() + ", 3 muestras")));
        return e;
    }

    /** Revisa la cédula escrita: formato, que sea ecuatoriana (si es nuevo) y que no la tenga otra persona. */
    public void validarCedula(Estudiante e, String cedula) {
        e.setCedula(cedula);
        if (e.esNuevo()) {
            Validaciones.cedulaEcuatoriana(cedula);
        }
        dao.buscarPorCedula(cedula)
                .filter(otro -> otro.getIdEstudiante() != e.getIdEstudiante())
                .ifPresent(otro -> {
                    throw new ValidacionException("Esa cédula ya es de " + otro.getNombreCompleto() + ".");
                });
        cedulaUnica.exigirQueNoSeaDeUsuario(cedula);
    }

    public void eliminar(Estudiante e) {
        exigir(Usuario::puedeRegistrarEstudiantes, "eliminar estudiantes");
        List<modelo.Prestamo> prestamos = prestamoDAO.listarPorEstudiante(e.getIdEstudiante());
        long sinDevolver = prestamos.stream().filter(modelo.Prestamo::estaActivo).count();
        if (sinDevolver > 0) {
            throw new NegocioException("No se puede eliminar: tiene " + sinDevolver + " libro(s) sin devolver.");
        }
        if (!prestamos.isEmpty()) {
            throw new NegocioException("No se puede eliminar: ya tiene historial de préstamos (se conserva para "
                    + "reportes y multas). Puede desactivarlo.");
        }
        dao.eliminar(e.getIdEstudiante());
    }

    /** Sin préstamos se puede eliminar; con historial solo se desactiva (o se activa si ya está inactivo). */
    public AccionRetiro accionRetiro(Estudiante e) {
        if (prestamoDAO.listarPorEstudiante(e.getIdEstudiante()).isEmpty()) {
            return AccionRetiro.ELIMINAR;
        }
        return e.puedePrestar() ? AccionRetiro.DESACTIVAR : AccionRetiro.ACTIVAR;
    }

    public void cambiarEstado(Estudiante e, EstadoEstudiante nuevo) {
        exigir(Usuario::puedeCambiarEstadoEstudiantes, "cambiar el estado de los estudiantes");
        dao.cambiarEstado(e.getIdEstudiante(), Validaciones.requerido(nuevo, "el estado"));
        e.setEstado(nuevo);
    }

    private void exigir(java.util.function.Predicate<Usuario> permiso, String accion) {
        Sesion.getInstancia().exigir(permiso, accion);
    }
}
