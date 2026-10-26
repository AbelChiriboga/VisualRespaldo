package servicio;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import modelo.*;

/** Situación de un estudiante en la biblioteca: sus préstamos, cuántos puede tener y cuánto debe. */
public class SituacionEstudiante {

    private final Estudiante estudiante;
    private final List<Prestamo> prestamos;
    private final int maximoPrestamos;
    private final BigDecimal multasPendientes;

    public SituacionEstudiante(Estudiante estudiante, List<Prestamo> prestamos, int maximoPrestamos,
                               BigDecimal multasPendientes) {
        this.estudiante = estudiante;
        this.prestamos = Collections.unmodifiableList(new ArrayList<>(prestamos));
        this.maximoPrestamos = maximoPrestamos;
        this.multasPendientes = multasPendientes == null ? BigDecimal.ZERO : multasPendientes;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    public List<Prestamo> getPrestamosActivos() {
        return prestamos.stream().filter(Prestamo::estaActivo).collect(Collectors.toList());
    }

    public int getCantidadActivos() {
        return getPrestamosActivos().size();
    }

    public int getCantidadAtrasados() {
        return (int) prestamos.stream().filter(Prestamo::estaAtrasado).count();
    }

    public int getMaximoPrestamos() {
        return maximoPrestamos;
    }

    public int getCupos() {
        return Math.max(0, maximoPrestamos - getCantidadActivos());
    }

    public BigDecimal getMultasPendientes() {
        return multasPendientes;
    }

    public boolean tieneMultasPendientes() {
        return multasPendientes.signum() > 0;
    }

    public String getDatos() {
        return "Cédula: " + estudiante.getCedula() + "\nCarrera: " + estudiante.getCarrera();
    }

    public String getTextoActivos() {
        return "Préstamos activos: " + getCantidadActivos() + " de " + maximoPrestamos;
    }
}
