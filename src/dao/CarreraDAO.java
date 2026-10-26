package dao;

import java.sql.*;
import modelo.Carrera;

/** Carreras de la FISEI: 5 fijas, precargadas y SIN CRUD. */
public class CarreraDAO extends BaseDAO<Carrera, Integer> {

    static final String COLUMNAS = "ca.id_carrera AS car_id, ca.nombre AS car_nombre, ca.facultad AS car_facultad";

    @Override
    protected String sqlSelect() {
        return "SELECT " + COLUMNAS + " FROM carreras ca";
    }

    @Override
    protected String columnaId() {
        return "ca.id_carrera";
    }

    @Override
    protected String orden() {
        return "ca.id_carrera";
    }

    @Override
    protected Carrera mapear(ResultSet rs) throws SQLException {
        Carrera c = new Carrera(rs.getInt("car_id"), rs.getString("car_nombre"));
        c.setFacultad(rs.getString("car_facultad"));
        return c;
    }
}
