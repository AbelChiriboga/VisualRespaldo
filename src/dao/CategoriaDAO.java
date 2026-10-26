package dao;

import java.sql.*;
import java.util.List;
import modelo.Categoria;

/** CRUD de categorías. */
public class CategoriaDAO extends BaseDAO<Categoria, Integer> implements CrudDAO<Categoria, Integer> {

    static final String COLUMNAS = "c.id_categoria AS cat_id, c.nombre AS cat_nombre";

    @Override
    protected String sqlSelect() {
        return "SELECT " + COLUMNAS + " FROM categorias c";
    }

    @Override
    protected String columnaId() {
        return "c.id_categoria";
    }

    @Override
    protected String orden() {
        return "c.nombre";
    }

    @Override
    protected Categoria mapear(ResultSet rs) throws SQLException {
        Categoria c = new Categoria(rs.getString("cat_nombre"));
        c.setId(rs.getInt("cat_id"));
        return c;
    }

    @Override
    public Categoria insertar(Categoria c) {
        c.setId(insertarConId("INSERT INTO categorias (nombre) VALUES (?)", c.getNombre()));
        return c;
    }

    @Override
    public void actualizar(Categoria c) {
        ejecutar("UPDATE categorias SET nombre = ? WHERE id_categoria = ?", c.getNombre(), c.getId());
    }

    @Override
    public void eliminar(Integer id) {
        ejecutar("DELETE FROM categorias WHERE id_categoria = ?", id);
    }

    public List<Categoria> listarConTotales(String texto) {
        String sql = "SELECT " + COLUMNAS + ", COUNT(DISTINCT l.id_libro) AS libros, COUNT(ej.id_ejemplar) AS ejemplares "
                + "FROM categorias c LEFT JOIN libros l ON l.id_categoria = c.id_categoria "
                + "LEFT JOIN ejemplares ej ON ej.id_libro = l.id_libro "
                + "WHERE c.nombre LIKE ? GROUP BY c.id_categoria, c.nombre ORDER BY " + orden();
        return consultar(sql, rs -> {
            Categoria c = mapear(rs);
            c.setCantidadLibros(rs.getInt("libros"));
            c.setCantidadEjemplares(rs.getInt("ejemplares"));
            return c;
        }, like(texto));
    }
}
