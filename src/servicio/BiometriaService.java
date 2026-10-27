package servicio;

import biometria.*;
import dao.*;
import excepciones.BibliotecaException;
import java.util.*;
import modelo.*;
import util.Validaciones;

/** Todo lo que tiene que ver con huellas: registrar, identificar 1:N y anotar cada intento en la bitácora. */
public class BiometriaService {

    private final LectorBiometrico lector;
    private final HuellaDAO huellaDAO = new HuellaDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final RegistroAutenticacionDAO bitacora = new RegistroAutenticacionDAO();
    private final ParametroDAO parametroDAO = new ParametroDAO();

    public BiometriaService() {
        this(FabricaLector.obtener());
    }

    public BiometriaService(LectorBiometrico lector) {
        this.lector = lector;
    }

    public String getNombreLector() {
        return lector.getNombre();
    }

    public byte[] capturarHuella(Estudiante e, AvisoCaptura aviso) {
        Sesion.getInstancia().exigir(Usuario::puedeRegistrarEstudiantes, "registrar huellas");
        Validaciones.requerido(e, "un estudiante");
        exigirLectorListo();
        lector.nuevaCaptura();
        byte[] plantilla = capturarRegistrando(e, aviso);
        exigirQueNoSeaDeOtro(e, plantilla);
        return plantilla;
    }

    public Optional<Identificacion> identificar(byte[] muestra) {
        Map<Integer, Integer> duenos = cargarPlantillasEnLector();
        int umbral = parametroDAO.obtenerEntero(Parametro.UMBRAL_HUELLA);
        return lector.identificar(muestra)
                .filter(c -> c.getPuntaje() >= umbral)
                .flatMap(c -> estudianteDAO.buscarPorId(duenos.get(c.getIdHuella()))
                        .map(est -> new Identificacion(est, c.getPuntaje())));
    }

    public Identificacion identificarEstudiante(OperacionAutenticacion operacion) {
        exigirLectorListo();
        lector.nuevaCaptura();
        byte[] muestra = lector.capturar();
        return identificar(muestra).orElseThrow(() -> {
            anotar(null, operacion, ResultadoAutenticacion.FALLO, null, "Huella no reconocida");
            return new excepciones.HuellaNoReconocidaException();
        });
    }

    public void cancelarCaptura() {
        lector.cancelarCaptura();
    }

    public void anotar(Integer idEstudiante, OperacionAutenticacion operacion, ResultadoAutenticacion resultado,
                       Integer puntaje, String detalle) {
        bitacora.insertar(new RegistroAutenticacion(idEstudiante, operacion, resultado, puntaje, detalle));
    }

    private void exigirLectorListo() {
        biometria.EstadoLector estado = lector.verificarEstado();
        if (!estado.permiteCapturar()) {
            throw new excepciones.LectorException("Lector " + estado.getDescripcion().toLowerCase() + ".");
        }
    }

    private byte[] capturarRegistrando(Estudiante e, AvisoCaptura aviso) {
        try {
            return lector.registrar(aviso);
        } catch (BibliotecaException ex) {
            if (ex.esIntentoConDedo()) {
                anotar(idParaBitacora(e), OperacionAutenticacion.REGISTRO_HUELLA, ResultadoAutenticacion.FALLO, null,
                        ex.getMessage());
            }
            throw ex;
        }
    }

    private void exigirQueNoSeaDeOtro(Estudiante e, byte[] plantilla) {
        identificar(plantilla)
                .filter(id -> id.getEstudiante().getIdEstudiante() != e.getIdEstudiante())
                .ifPresent(id -> {
                    anotar(idParaBitacora(e), OperacionAutenticacion.REGISTRO_HUELLA, ResultadoAutenticacion.FALLO,
                            id.getPuntaje(), "La huella ya pertenece a " + id.getEstudiante().getCedula());
                    throw new excepciones.HuellaDuplicadaException(id.getEstudiante().getNombreCompleto());
                });
    }

    private static Integer idParaBitacora(Estudiante e) {
        return e.esNuevo() ? null : e.getIdEstudiante();
    }

    private Map<Integer, Integer> cargarPlantillasEnLector() {
        List<Huella> huellas = huellaDAO.listar();
        Map<Integer, byte[]> plantillas = new HashMap<>();
        Map<Integer, Integer> duenos = new HashMap<>();
        for (Huella h : huellas) {
            plantillas.put(h.getId(), h.getPlantilla());
            duenos.put(h.getId(), h.getIdEstudiante());
        }
        lector.cargarPlantillas(plantillas);
        return duenos;
    }

    /** Resultado de identificar: el estudiante dueño de la huella y el puntaje del lector. */
    public static final class Identificacion {

        private final Estudiante estudiante;
        private final int puntaje;

        public Identificacion(Estudiante estudiante, int puntaje) {
            this.estudiante = estudiante;
            this.puntaje = puntaje;
        }

        public Estudiante getEstudiante() {
            return estudiante;
        }

        public int getPuntaje() {
            return puntaje;
        }
    }
}
