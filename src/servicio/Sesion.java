package servicio;

import excepciones.NegocioException;
import java.util.Optional;
import java.util.function.Predicate;
import modelo.Usuario;

/** Sesión del usuario que ingresó al sistema. */
public final class Sesion {

    private static final Sesion INSTANCIA = new Sesion();

    private Usuario usuario;

    private Sesion() {
    }

    public static Sesion getInstancia() {
        return INSTANCIA;
    }

    void iniciar(Usuario usuario) {
        this.usuario = usuario;
    }

    void cerrar() {
        this.usuario = null;
    }

    public Optional<Usuario> getUsuarioActual() {
        return Optional.ofNullable(usuario);
    }

    public Usuario getUsuario() {
        return getUsuarioActual().orElseThrow(() -> new NegocioException("Inicie sesión primero."));
    }

    public void exigir(Predicate<Usuario> permiso, String accion) {
        if (!permiso.test(getUsuario())) {
            throw new NegocioException("No tiene permiso para " + accion + ".");
        }
    }
}
