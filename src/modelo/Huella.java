package modelo;

import java.time.LocalDateTime;
import java.util.Arrays;
import util.Validaciones;

/** Plantilla (template) de una huella de un estudiante. */
public class Huella extends Entidad {

    private int idEstudiante;
    private Dedo dedo;
    private byte[] plantilla;
    private LocalDateTime fechaRegistro;

    public Huella() {
    }

    public Huella(Dedo dedo, byte[] plantilla) {
        setDedo(dedo);
        setPlantilla(plantilla);
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public Dedo getDedo() {
        return dedo;
    }

    public final void setDedo(Dedo dedo) {
        this.dedo = Validaciones.requerido(dedo, "el dedo");
    }

    public byte[] getPlantilla() {
        return plantilla == null ? null : Arrays.copyOf(plantilla, plantilla.length);
    }

    public final void setPlantilla(byte[] plantilla) {
        if (plantilla == null || plantilla.length == 0) {
            throw new excepciones.ValidacionException("La plantilla de la huella está vacía.");
        }
        this.plantilla = Arrays.copyOf(plantilla, plantilla.length);
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
