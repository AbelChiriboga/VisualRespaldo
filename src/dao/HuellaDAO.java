package dao;

import java.sql.*;
import java.util.List;
import modelo.*;

/** Plantillas de huella (columna template, BLOB). */
public class HuellaDAO extends BaseDAO<Huella, Integer> implements CrudDAO<Huella, Integer> {

    @Override
    protected String sqlSelect() {
        return "SELECT h.id_huella, h.id_estudiante, h.dedo, h.template, h.fecha_registro FROM huellas h";
    }

    @Override
    protected String columnaId() {
        return "h.id_huella";
    }

    @Override
    protected String orden() {
        return "h.id_estudiante, h.dedo";
    }

    @Override
    protected Huella mapear(ResultSet rs) throws SQLException {
        Huella h = new Huella(ValorBD.desde(Dedo.class, rs.getString("dedo")), rs.getBytes("template"));
        h.setId(rs.getInt("id_huella"));
        h.setIdEstudiante(rs.getInt("id_estudiante"));
        h.setFechaRegistro(fecha(rs, "fecha_registro"));
        return h;
    }

    @Override
    public Huella insertar(Huella h) {
        h.setId(insertarConId("INSERT INTO huellas (id_estudiante, dedo, template) VALUES (?,?,?)",
                h.getIdEstudiante(), bd(h.getDedo()), h.getPlantilla()));
        return h;
    }

    @Override
    public void actualizar(Huella h) {
        ejecutar("UPDATE huellas SET template = ?, fecha_registro = NOW() WHERE id_huella = ?",
                h.getPlantilla(), h.getId());
    }

    @Override
    public void eliminar(Integer id) {
        ejecutar("DELETE FROM huellas WHERE id_huella = ?", id);
    }

    public List<Huella> listarPorEstudiante(int idEstudiante) {
        return consultarLista(sqlSelect() + " WHERE h.id_estudiante = ? ORDER BY " + orden(), idEstudiante);
    }

    public int contarPorEstudiante(int idEstudiante) {
        return consultarEntero("SELECT COUNT(*) FROM huellas WHERE id_estudiante = ?", idEstudiante);
    }
}
