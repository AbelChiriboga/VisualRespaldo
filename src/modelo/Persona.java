package modelo;

import util.Validaciones;

/** Clase base ABSTRACTA de todas las personas del sistema (personal y estudiantes). */
public abstract class Persona {

    private String cedula;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;

    protected Persona() {
    }

    protected Persona(String cedula, String nombres, String apellidos) {
        setCedula(cedula);
        setNombres(nombres);
        setApellidos(apellidos);
    }

    public abstract String getTipo();

    public abstract boolean puedeIniciarSesion();

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public String getCedula() {
        return cedula;
    }

    public final void setCedula(String cedula) {
        this.cedula = Validaciones.formatoCedula(cedula);
    }

    public String getNombres() {
        return nombres;
    }

    public final void setNombres(String nombres) {
        this.nombres = Validaciones.maximo(Validaciones.obligatorio(nombres, "nombres"), 60, "nombres");
    }

    public String getApellidos() {
        return apellidos;
    }

    public final void setApellidos(String apellidos) {
        this.apellidos = Validaciones.maximo(Validaciones.obligatorio(apellidos, "apellidos"), 60, "apellidos");
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        Validaciones.obligatorio(correo, "correo");
        this.correo = Validaciones.maximo(Validaciones.correo(correo), 100, "correo");
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = Validaciones.telefono(telefono);
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return cedula != null && cedula.equals(((Persona) o).cedula);
    }

    @Override
    public int hashCode() {
        return cedula == null ? 0 : cedula.hashCode();
    }
}
