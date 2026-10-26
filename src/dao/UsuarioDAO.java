package dao;

import java.sql.*;
import java.util.*;
import modelo.*;

/** DAO del personal (tabla usuarios). */
public class UsuarioDAO extends BaseDAO<Usuario, String> implements CrudDAO<Usuario, String> {

    static final String COLUMNAS =
            "u.cedula AS usu_cedula, u.nombres AS usu_nombres, u.apellidos AS usu_apellidos, "
            + "u.correo AS usu_correo, u.direccion AS usu_direccion, u.telefono AS usu_telefono, "
            + "u.usuario AS usu_usuario, u.password_hash AS usu_password, u.rol AS usu_rol, "
            + "u.activo AS usu_activo, u.fecha_creacion AS usu_fecha";

    @Override
    protected String sqlSelect() {
        return "SELECT " + COLUMNAS + " FROM usuarios u";
    }

    @Override
    protected String columnaId() {
        return "u.cedula";
    }

    @Override
    protected String orden() {
        return "u.apellidos, u.nombres";
    }

    @Override
    protected Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = ValorBD.desde(Rol.class, rs.getString("usu_rol")).crearUsuario();
        u.setCedula(rs.getString("usu_cedula"));
        u.setNombres(rs.getString("usu_nombres"));
        u.setApellidos(rs.getString("usu_apellidos"));
        u.setCorreo(rs.getString("usu_correo"));
        u.setDireccion(rs.getString("usu_direccion"));
        u.setTelefono(rs.getString("usu_telefono"));
        u.setUsuario(rs.getString("usu_usuario"));
        u.setPasswordHash(rs.getString("usu_password"));
        u.setActivo(rs.getBoolean("usu_activo"));
        u.setFechaCreacion(fecha(rs, "usu_fecha"));
        return u;
    }

    @Override
    public Usuario insertar(Usuario u) {
        ejecutar("INSERT INTO usuarios (cedula, nombres, apellidos, correo, direccion, telefono, "
                + "usuario, password_hash, rol, activo) VALUES (?,?,?,?,?,?,?,?,?,?)",
                u.getCedula(), u.getNombres(), u.getApellidos(), u.getCorreo(), u.getDireccion(),
                u.getTelefono(), u.getUsuario(), u.getPasswordHash(), bd(u.getRol()), u.isActivo());
        return u;
    }

    @Override
    public void actualizar(Usuario u) {
        ejecutar("UPDATE usuarios SET nombres = ?, apellidos = ?, correo = ?, direccion = ?, telefono = ?, "
                + "usuario = ?, rol = ?, activo = ? WHERE cedula = ?",
                u.getNombres(), u.getApellidos(), u.getCorreo(), u.getDireccion(), u.getTelefono(),
                u.getUsuario(), bd(u.getRol()), u.isActivo(), u.getCedula());
    }

    @Override
    public void eliminar(String cedula) {
        cambiarActivo(cedula, false);
    }

    public void cambiarActivo(String cedula, boolean activo) {
        ejecutar("UPDATE usuarios SET activo = ? WHERE cedula = ?", activo, cedula);
    }

    public void actualizarPassword(String cedula, String passwordHash) {
        ejecutar("UPDATE usuarios SET password_hash = ? WHERE cedula = ?", passwordHash, cedula);
    }

    public Optional<Usuario> buscarPorUsuario(String usuario) {
        return consultarUno(sqlSelect() + " WHERE u.usuario = ?", usuario);
    }

    public List<Usuario> buscar(String texto) {
        String t = like(texto);
        return consultarLista(sqlSelect()
                + " WHERE u.cedula LIKE ? OR u.nombres LIKE ? OR u.apellidos LIKE ? OR u.usuario LIKE ?"
                + " ORDER BY " + orden(), t, t, t, t);
    }
}
