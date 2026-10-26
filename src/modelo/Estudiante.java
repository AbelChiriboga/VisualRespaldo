package modelo;

import java.util.*;
import util.Validaciones;

/** Estudiante: hereda los datos personales de Persona y agrega carrera, semestre, estado y huellas. */
public class Estudiante extends Persona {

    public static final int SEMESTRE_MIN = 1;
    public static final int SEMESTRE_MAX = 10;

    private int idEstudiante;
    private Carrera carrera;
    private int semestre = SEMESTRE_MIN;
    private EstadoEstudiante estado = EstadoEstudiante.ACTIVO;
    private java.time.LocalDateTime fechaRegistro;
    private final List<Huella> huellas = new ArrayList<>();

    public Estudiante() {
    }

    public Estudiante(String cedula, String nombres, String apellidos, Carrera carrera, int semestre) {
        super(cedula, nombres, apellidos);
        setCarrera(carrera);
        setSemestre(semestre);
    }

    @Override
    public String getTipo() {
        return "Estudiante";
    }

    @Override
    public boolean puedeIniciarSesion() {
        return false;
    }

    public boolean puedePrestar() {
        return estado.puedePrestar();
    }

    public boolean tieneHuella() {
        return !huellas.isEmpty();
    }

    public void agregarHuella(Huella huella) {
        Validaciones.requerido(huella, "la huella");
        for (Huella h : huellas) {
            if (h.getDedo() == huella.getDedo()) {
                throw new excepciones.ValidacionException("El dedo " + huella.getDedo() + " ya está registrado.");
            }
        }
        huella.setIdEstudiante(idEstudiante);
        huellas.add(huella);
    }

    public java.util.Optional<Huella> buscarHuella(Dedo dedo) {
        return huellas.stream().filter(h -> h.getDedo() == dedo).findFirst();
    }

    public List<Huella> getHuellas() {
        return Collections.unmodifiableList(huellas);
    }

    public void limpiarHuellas() {
        huellas.clear();
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public boolean esNuevo() {
        return idEstudiante == 0;
    }

    public java.time.LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(java.time.LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public final void setCarrera(Carrera carrera) {
        this.carrera = Validaciones.requerido(carrera, "la carrera");
    }

    public int getSemestre() {
        return semestre;
    }

    public final void setSemestre(int semestre) {
        this.semestre = Validaciones.rango(semestre, "semestre", SEMESTRE_MIN, SEMESTRE_MAX);
    }

    public EstadoEstudiante getEstado() {
        return estado;
    }

    public void setEstado(EstadoEstudiante estado) {
        this.estado = Validaciones.requerido(estado, "el estado");
    }
}
