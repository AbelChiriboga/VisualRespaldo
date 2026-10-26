package servicio;

import dao.UsuarioDAO;
import excepciones.*;
import java.util.List;
import modelo.*;
import util.*;

/** Personal del sistema (administradores y bibliotecarios). */
public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();
    private final CedulaUnica cedulaUnica = new CedulaUnica();

    public List<Usuario> listar() {
        exigirPermiso();
        return dao.listar();
    }

    public Usuario crear(Rol rol) {
        return Validaciones.requerido(rol, "el rol").crearUsuario();
    }

    public void registrar(Usuario u, String clave, String repetida) {
        exigirPermiso();
        validarCedulaNueva(u.getCedula());
        u.setPasswordHash(Seguridad.sha256(Seguridad.validarNueva(clave, repetida)));
        u.setActivo(true);
        dao.insertar(u);
    }

    /** Revisa la cédula de un usuario nuevo: ecuatoriana y que no la tenga otra persona. */
    public void validarCedulaNueva(String cedula) {
        Validaciones.cedulaEcuatoriana(cedula);
        if (dao.buscarPorId(cedula).isPresent()) {
            throw new ValidacionException("Ya existe un usuario con esa cédula.");
        }
        cedulaUnica.exigirQueNoSeaDeEstudiante(cedula);
    }

    public void actualizar(Usuario u, String clave, String repetida) {
        exigirPermiso();
        exigirQueNoCambieSuPropioRol(u);
        boolean cambiaClave = clave != null && !clave.isEmpty();
        String hash = cambiaClave ? Seguridad.sha256(Seguridad.validarNueva(clave, repetida)) : null;
        dao.actualizar(u);
        if (cambiaClave) {
            dao.actualizarPassword(u.getCedula(), hash);
        }
    }

    public void cambiarEstado(Usuario u) {
        exigirPermiso();
        if (esUsuarioActual(u)) {
            throw new NegocioException("No puede desactivar su propia cuenta.");
        }
        dao.cambiarActivo(u.getCedula(), !u.isActivo());
        u.setActivo(!u.isActivo());
    }

    private void exigirQueNoCambieSuPropioRol(Usuario u) {
        if (esUsuarioActual(u) && Sesion.getInstancia().getUsuario().getRol() != u.getRol()) {
            throw new NegocioException("No puede cambiar su propio rol.");
        }
    }

    private boolean esUsuarioActual(Usuario u) {
        return Sesion.getInstancia().getUsuario().getCedula().equals(u.getCedula());
    }

    private void exigirPermiso() {
        Sesion.getInstancia().exigir(Usuario::puedeGestionarPersonal, "gestionar al personal");
    }
}
