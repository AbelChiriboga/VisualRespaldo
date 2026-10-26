package dao;

import java.sql.*;
import java.util.*;
import modelo.*;

/** DAO de estudiantes. */
public class EstudianteDAO extends BaseDAO<Estudiante, Integer> implements CrudDAO<Estudiante, Integer> {

    static final String COLUMNAS =
            "e.id_estudiante AS est_id, e.cedula AS est_cedula, e.nombres AS est_nombres, "
            + "e.apellidos AS est_apellidos, e.correo AS est_correo, e.telefono AS est_telefono, "
            + "e.semestre AS est_semestre, e.estado AS est_estado, e.fecha_registro AS est_fecha, "
            + CarreraDAO.COLUMNAS;

    static final String TABLAS = "estudiantes e JOIN carreras ca ON ca.id_carrera = e.id_carrera";

    private final CarreraDAO carreraDAO = new CarreraDAO();

    @Override
    protected String sqlSelect() {
        return "SELECT " + COLUMNAS + " FROM " + TABLAS;
    }

    @Override
    protected String columnaId() {
        return "e.id_estudiante";
    }

    @Override
    protected String orden() {
        return "e.apellidos, e.nombres";
    }

    @Override
    protected Estudiante mapear(ResultSet rs) throws SQLException {
        Estudiante e = new Estudiante(rs.getString("est_cedula"), rs.getString("est_nombres"),
                rs.getString("est_apellidos"), carreraDAO.mapear(rs), rs.getInt("est_semestre"));
        e.setIdEstudiante(rs.getInt("est_id"));
        e.setCorreo(rs.getString("est_correo"));
        e.setTelefono(rs.getString("est_telefono"));
        e.setEstado(ValorBD.desde(EstadoEstudiante.class, rs.getString("est_estado")));
        e.setFechaRegistro(fecha(rs, "est_fecha"));
        return e;
    }

    @Override
    public Estudiante insertar(Estudiante e) {
        int id = insertarConId("INSERT INTO estudiantes (cedula, nombres, apellidos, correo, telefono, "
                + "id_carrera, semestre, estado) VALUES (?,?,?,?,?,?,?,?)",
                e.getCedula(), e.getNombres(), e.getApellidos(), e.getCorreo(), e.getTelefono(),
                e.getCarrera().getId(), e.getSemestre(), bd(e.getEstado()));
        e.setIdEstudiante(id);
        return e;
    }

    @Override
    public void actualizar(Estudiante e) {
        ejecutar("UPDATE estudiantes SET cedula = ?, nombres = ?, apellidos = ?, correo = ?, telefono = ?, "
                + "id_carrera = ?, semestre = ?, estado = ? WHERE id_estudiante = ?",
                e.getCedula(), e.getNombres(), e.getApellidos(), e.getCorreo(), e.getTelefono(),
                e.getCarrera().getId(), e.getSemestre(), bd(e.getEstado()), e.getIdEstudiante());
    }

    @Override
    public void eliminar(Integer id) {
        ejecutar("DELETE FROM estudiantes WHERE id_estudiante = ?", id);
    }

    public void guardarConHuellas(Estudiante e, java.util.Map<modelo.Dedo, byte[]> huellas) {
        boolean eraNuevo = e.esNuevo();
        try {
            enTransaccion(cn -> {
                if (e.esNuevo()) {
                    e.setIdEstudiante(insertarConId(cn, "INSERT INTO estudiantes (cedula, nombres, apellidos, correo, "
                            + "telefono, id_carrera, semestre, estado) VALUES (?,?,?,?,?,?,?,?)",
                            e.getCedula(), e.getNombres(), e.getApellidos(), e.getCorreo(), e.getTelefono(),
                            e.getCarrera().getId(), e.getSemestre(), bd(e.getEstado())));
                } else {
                    ejecutar(cn, "UPDATE estudiantes SET cedula = ?, nombres = ?, apellidos = ?, correo = ?, telefono = ?, "
                            + "id_carrera = ?, semestre = ?, estado = ? WHERE id_estudiante = ?",
                            e.getCedula(), e.getNombres(), e.getApellidos(), e.getCorreo(), e.getTelefono(),
                            e.getCarrera().getId(), e.getSemestre(), bd(e.getEstado()), e.getIdEstudiante());
                }
                for (java.util.Map.Entry<modelo.Dedo, byte[]> h : huellas.entrySet()) {
                    ejecutar(cn, "INSERT INTO huellas (id_estudiante, dedo, template) VALUES (?,?,?) "
                            + "ON DUPLICATE KEY UPDATE template = VALUES(template), fecha_registro = NOW()",
                            e.getIdEstudiante(), bd(h.getKey()), h.getValue());
                }
            });
        } catch (RuntimeException ex) {
            if (eraNuevo) {
                e.setIdEstudiante(0);
            }
            throw ex;
        }
    }

    public void cambiarEstado(int idEstudiante, EstadoEstudiante estado) {
        ejecutar("UPDATE estudiantes SET estado = ? WHERE id_estudiante = ?", bd(estado), idEstudiante);
    }

    public Optional<Estudiante> buscarPorCedula(String cedula) {
        return consultarUno(sqlSelect() + " WHERE e.cedula = ?", cedula);
    }

    public List<Estudiante> buscar(String texto, EstadoEstudiante estado) {
        String t = like(texto);
        return consultarLista(sqlSelect()
                + " WHERE (e.cedula LIKE ? OR e.nombres LIKE ? OR e.apellidos LIKE ?)"
                + " AND (? IS NULL OR e.estado = ?) ORDER BY " + orden(),
                t, t, t, bd(estado), bd(estado));
    }
}
