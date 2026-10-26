package servicio;

import dao.*;
import excepciones.NegocioException;
import modelo.*;
import util.*;

/** Reglas del ingreso al sistema: - Personal: usuario + contraseña (SHA-256). */
public class AutenticacionService {

    private static final String CREDENCIALES_INVALIDAS = "Usuario o contraseña incorrectos.";

    private final UsuarioDAO usuarioDAO;
    private final EstudianteDAO estudianteDAO;

    public AutenticacionService() {
        this(new UsuarioDAO(), new EstudianteDAO());
    }

    public AutenticacionService(UsuarioDAO usuarioDAO, EstudianteDAO estudianteDAO) {
        this.usuarioDAO = usuarioDAO;
        this.estudianteDAO = estudianteDAO;
    }

    public Usuario iniciarSesion(String nombreUsuario, String clave) {
        String usuarioEscrito = Validaciones.obligatorio(nombreUsuario, "usuario");
        Validaciones.obligatorio(clave, "contraseña");

        Usuario u = usuarioDAO.buscarPorUsuario(usuarioEscrito)
                .filter(x -> Seguridad.coincide(clave, x.getPasswordHash()))
                .orElseThrow(() -> new NegocioException(CREDENCIALES_INVALIDAS));

        if (!u.puedeIniciarSesion()) {
            throw new NegocioException("Su cuenta está desactivada.");
        }
        Sesion.getInstancia().iniciar(u);
        return u;
    }

    public void cerrarSesion() {
        Sesion.getInstancia().cerrar();
    }

    public Estudiante consultarEstudiante(String cedula) {
        String c = Validaciones.formatoCedula(cedula);
        return estudianteDAO.buscarPorCedula(c)
                .orElseThrow(() -> new NegocioException("No existe un estudiante con esa cédula."));
    }
}
