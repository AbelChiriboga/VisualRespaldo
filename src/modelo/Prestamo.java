package modelo;

import java.time.*;
import java.time.temporal.ChronoUnit;
import util.Validaciones;

/** Préstamo de UN ejemplar a un estudiante, registrado por un usuario del personal. */
public class Prestamo extends Entidad {

    private Ejemplar ejemplar;
    private Estudiante estudiante;
    private Usuario usuario;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaLimite;
    private LocalDateTime fechaDevolucion;
    private EstadoPrestamo estado = EstadoPrestamo.ACTIVO;
    private boolean huellaPrestamo;
    private boolean huellaDevolucion;
    private String observaciones;
    private Multa multa;

    public Prestamo() {
    }

    public Prestamo(Ejemplar ejemplar, Estudiante estudiante, Usuario usuario) {
        setEjemplar(ejemplar);
        setEstudiante(estudiante);
        setUsuario(usuario);
    }

    public boolean estaActivo() {
        return estado == EstadoPrestamo.ACTIVO;
    }

    public int diasAtraso(LocalDate aLaFecha) {
        if (fechaLimite == null || !aLaFecha.isAfter(fechaLimite.toLocalDate())) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(fechaLimite.toLocalDate(), aLaFecha);
    }

    public int diasAtraso() {
        return diasAtraso(fechaDevolucion != null ? fechaDevolucion.toLocalDate() : LocalDate.now());
    }

    /** Días que faltan para la fecha límite (negativo si ya pasó). */
    public int diasRestantes(LocalDate hoy) {
        return (int) ChronoUnit.DAYS.between(hoy, fechaLimite.toLocalDate());
    }

    /** "Faltan 3 días", "Falta 1 día", "Vence hoy" o "Atrasado 2 días". */
    public String getTextoPlazo() {
        int dias = diasRestantes(LocalDate.now());
        if (dias > 0) {
            return dias == 1 ? "Falta 1 día" : "Faltan " + dias + " días";
        }
        if (dias == 0) {
            return "Vence hoy";
        }
        return "Atrasado " + (-dias) + (dias == -1 ? " día" : " días");
    }

    public boolean estaAtrasado() {
        return estaActivo() && diasAtraso() > 0;
    }

    public boolean tieneMulta() {
        return multa != null;
    }

    public Ejemplar getEjemplar() {
        return ejemplar;
    }

    public final void setEjemplar(Ejemplar ejemplar) {
        this.ejemplar = Validaciones.requerido(ejemplar, "el ejemplar");
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public final void setEstudiante(Estudiante estudiante) {
        this.estudiante = Validaciones.requerido(estudiante, "el estudiante");
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public final void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDateTime fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDateTime fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = Validaciones.requerido(estado, "el estado");
    }

    public boolean isHuellaPrestamo() {
        return huellaPrestamo;
    }

    public void setHuellaPrestamo(boolean huellaPrestamo) {
        this.huellaPrestamo = huellaPrestamo;
    }

    public boolean isHuellaDevolucion() {
        return huellaDevolucion;
    }

    public void setHuellaDevolucion(boolean huellaDevolucion) {
        this.huellaDevolucion = huellaDevolucion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = Validaciones.maximo(Validaciones.opcional(observaciones), 255, "observaciones");
    }

    public Multa getMulta() {
        return multa;
    }

    public void setMulta(Multa multa) {
        this.multa = multa;
    }
}
