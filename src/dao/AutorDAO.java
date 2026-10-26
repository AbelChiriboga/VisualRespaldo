package dao;

import java.sql.*;
import java.util.List;
import modelo.Autor;

/** CRUD de autores. */
public class AutorDAO extends BaseDAO<Autor, Integer> implements CrudDAO<Autor, Integer> {

    static final String COLUMNAS = "a.id_autor AS aut_id, a.nombres AS aut_nombres, "
            + "a.apellidos AS aut_apellidos, a.nacionalidad AS aut_nacionalidad";

    @Override
    protected String sqlSelect() {
        return "SELECT " + COLUMNAS + " FROM autores a";
    }

    @Override
    protected String columnaId() {
        return "a.id_autor";
    }

    @Override
    protected String orden() {
        return "a.apellidos, a.nombres";
    }

    @Override
    protected Autor mapear(ResultSet rs) throws SQLException {
        Autor a = new Autor(rs.getString("aut_nombres"), rs.getString("aut_apellidos"));
        a.setId(rs.getInt("aut_id"));
        a.setNacionalidad(rs.getString("aut_nacionalidad"));
        return a;
    }

    @Override
    public Autor insertar(Autor a) {
        a.setId(insertarConId("INSERT INTO autores (nombres, apellidos, nacionalidad) VALUES (?,?,?)",
                a.getNombres(), a.getApellidos(), a.getNacionalidad()));
        return a;
    }

    @Override
    public void actualizar(Autor a) {
        ejecutar("UPDATE autores SET nombres = ?, apellidos = ?, nacionalidad = ? WHERE id_autor = ?",
                a.getNombres(), a.getApellidos(), a.getNacionalidad(), a.getId());
    }

    @Override
    public void eliminar(Integer id) {
        ejecutar("DELETE FROM autores WHERE id_autor = ?", id);
    }

    public List<Autor> buscar(String texto) {
        String t = like(texto);
        return consultarLista(sqlSelect() + " WHERE a.nombres LIKE ? OR a.apellidos LIKE ? OR a.nacionalidad LIKE ?"
                + " ORDER BY " + orden(), t, t, t);
    }
}
