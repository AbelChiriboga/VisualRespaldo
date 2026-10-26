package modelo;

/** Administrador: registra estudiantes (con huella) y personal, configura parámetros, ve la bitácora y genera. */
public class Administrador extends Usuario {

    public Administrador() {
    }

    public Administrador(String cedula, String nombres, String apellidos, String usuario) {
        super(cedula, nombres, apellidos, usuario);
    }

    @Override
    public Rol getRol() {
        return Rol.ADMINISTRADOR;
    }

    @Override
    public boolean puedeRegistrarEstudiantes() {
        return true;
    }

    @Override
    public boolean puedeCambiarEstadoEstudiantes() {
        return true;
    }

    @Override
    public boolean puedeGestionarPersonal() {
        return true;
    }

    @Override
    public boolean puedeGestionarCatalogo() {
        return false;
    }

    @Override
    public boolean puedeGestionarPrestamos() {
        return false;
    }

    @Override
    public boolean puedeConfigurarParametros() {
        return true;
    }

    @Override
    public boolean puedeVerBitacora() {
        return true;
    }
}
