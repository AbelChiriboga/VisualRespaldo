package modelo;

import java.time.LocalDateTime;
import util.Validaciones;

/** Personal del sistema (tabla usuarios). */
public abstract class Usuario extends Persona {

    private String usuario;
    private String passwordHash;
    private String direccion;
    private boolean activo = true;
    private LocalDateTime fechaCreacion;

    protected Usuario() {
    }

    protected Usuario(String cedula, String nombres, String apellidos, String usuario) {
        super(cedula, nombres, apellidos);
        setUsuario(usuario);
    }

    public abstract Rol getRol();

    public abstract boolean puedeRegistrarEstudiantes();

    public abstract boolean puedeCambiarEstadoEstudiantes();

    public abstract boolean puedeGestionarPersonal();

    public abstract boolean puedeGestionarCatalogo();

    public abstract boolean puedeGestionarPrestamos();

    public abstract boolean puedeConfigurarParametros();

    public abstract boolean puedeVerBitacora();

    public boolean puedeGenerarReportes() {
        return true;
    }

    @Override
    public String getTipo() {
        return getRol().getDescripcion();
    }

    @Override
    public boolean puedeIniciarSesion() {
        return activo;
    }

    public String getUsuario() {
        return usuario;
    }

    public final void setUsuario(String usuario) {
        String u = Validaciones.obligatorio(usuario, "usuario");
        if (!u.matches("[A-Za-z0-9._-]{3,50}")) {
            throw new excepciones.ValidacionException(
                    "El usuario debe tener de 3 a 50 caracteres, sin espacios (letras, números, punto, guion).");
        }
        this.usuario = u;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = Validaciones.obligatorio(passwordHash, "contraseña");
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = Validaciones.maximo(Validaciones.opcional(direccion), 150, "dirección");
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
