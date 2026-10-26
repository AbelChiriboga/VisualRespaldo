package modelo;

import util.Validaciones;

/** Carrera de la FISEI. */
public class Carrera extends Entidad {

    private String nombre;
    private String facultad;

    public Carrera() {
    }

    public Carrera(int id, String nombre) {
        setId(id);
        setNombre(nombre);
    }

    public String getNombre() {
        return nombre;
    }

    public final void setNombre(String nombre) {
        this.nombre = Validaciones.obligatorio(nombre, "nombre de la carrera");
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
