package servicio;

import dao.*;
import excepciones.ValidacionException;

/** Una cédula pertenece a UNA sola persona del sistema: o es estudiante o es usuario (administrador /. */
public class CedulaUnica {

    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void exigirQueNoSeaDeEstudiante(String cedula) {
        estudianteDAO.buscarPorCedula(cedula).ifPresent(e -> {
            throw new ValidacionException("Esa cédula ya está registrada como estudiante (" + e.getNombreCompleto() + ").");
        });
    }

    public void exigirQueNoSeaDeUsuario(String cedula) {
        usuarioDAO.buscarPorId(cedula).ifPresent(u -> {
            throw new ValidacionException("Esa cédula ya está registrada como " + u.getTipo().toLowerCase()
                    + " (" + u.getNombreCompleto() + ").");
        });
    }
}
