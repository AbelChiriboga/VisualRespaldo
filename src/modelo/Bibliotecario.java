package modelo;

/** Bibliotecario: libros, ejemplares, autores y categorías; préstamos, devoluciones y atrasos; reportes. */
public class Bibliotecario extends Usuario {

    public Bibliotecario() {
    }

    public Bibliotecario(String cedula, String nombres, String apellidos, String usuario) {
        super(cedula, nombres, apellidos, usuario);
    }

    @Override
    public Rol getRol() {
        return Rol.BIBLIOTECARIO;
    }

    @Override
    public boolean puedeRegistrarEstudiantes() {
        return false;
    }

    @Override
    public boolean puedeCambiarEstadoEstudiantes() {
        return true;
    }

    @Override
    public boolean puedeGestionarPersonal() {
        return false;
    }

    @Override
    public boolean puedeGestionarCatalogo() {
        return true;
    }

    @Override
    public boolean puedeGestionarPrestamos() {
        return true;
    }

    @Override
    public boolean puedeConfigurarParametros() {
        return false;
    }

    @Override
    public boolean puedeVerBitacora() {
        return false;
    }
}
