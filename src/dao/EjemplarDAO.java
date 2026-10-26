package dao;

import java.sql.*;
import java.util.List;
import modelo.*;

/** CRUD de ejemplares (copias físicas). */
public class EjemplarDAO extends BaseDAO<Ejemplar, Integer> implements CrudDAO<Ejemplar, Integer> {

    static final String COLUMNAS = "ej.id_ejemplar AS eje_id, ej.codigo_inventario AS eje_codigo, "
            + "ej.estado AS eje_estado, ej.ubicacion AS eje_ubicacion, " + LibroDAO.COLUMNAS;

    static final String TABLAS = "ejemplares ej JOIN libros l ON l.id_libro = ej.id_libro " + LibroDAO.JOINS;

    private final LibroDAO libroDAO = new LibroDAO();

    @Override
    protected String sqlSelect() {
        return "SELECT " + COLUMNAS + " FROM " + TABLAS;
    }

    @Override
    protected String columnaId() {
        return "ej.id_ejemplar";
    }

    @Override
    protected String orden() {
        return "ej.codigo_inventario";
    }

    @Override
    protected Ejemplar mapear(ResultSet rs) throws SQLException {
        Ejemplar e = new Ejemplar(libroDAO.mapear(rs), rs.getString("eje_codigo"), rs.getString("eje_ubicacion"));
        e.setId(rs.getInt("eje_id"));
        e.setEstado(ValorBD.desde(EstadoEjemplar.class, rs.getString("eje_estado")));
        return e;
    }

    private static final String SQL_INSERTAR =
            "INSERT INTO ejemplares (id_libro, codigo_inventario, estado, ubicacion) VALUES (?,?,?,?)";
    private static final String SQL_ULTIMO_NUMERO =
            "SELECT COALESCE(MAX(CAST(SUBSTRING_INDEX(codigo_inventario, '-', -1) AS UNSIGNED)), 0) FROM ejemplares";

    @Override
    public Ejemplar insertar(Ejemplar e) {
        e.setId(insertarConId(SQL_INSERTAR,
                e.getLibro().getId(), e.getCodigoInventario(), bd(e.getEstado()), e.getUbicacion()));
        return e;
    }

    /** Inserta dentro de una transacción ya abierta (lo usa LibroDAO al crear un libro). */
    void insertar(Connection cn, Ejemplar e) throws SQLException {
        e.setId(insertarConId(cn, SQL_INSERTAR,
                e.getLibro().getId(), e.getCodigoInventario(), bd(e.getEstado()), e.getUbicacion()));
    }

    int ultimoNumeroCodigo(Connection cn) throws SQLException {
        return consultarEntero(cn, SQL_ULTIMO_NUMERO);
    }

    @Override
    public void actualizar(Ejemplar e) {
        ejecutar("UPDATE ejemplares SET codigo_inventario = ?, estado = ?, ubicacion = ? WHERE id_ejemplar = ?",
                e.getCodigoInventario(), bd(e.getEstado()), e.getUbicacion(), e.getId());
    }

    @Override
    public void eliminar(Integer id) {
        ejecutar("DELETE FROM ejemplares WHERE id_ejemplar = ?", id);
    }

    public List<Ejemplar> listarPorLibro(int idLibro) {
        return consultarLista(sqlSelect() + " WHERE ej.id_libro = ? ORDER BY " + orden(), idLibro);
    }

    public int ultimoNumeroCodigo() {
        return consultarEntero(SQL_ULTIMO_NUMERO);
    }

    public List<Ejemplar> buscarDisponibles(String texto) {
        String t = like(texto);
        return consultarLista(sqlSelect() + " WHERE ej.estado = ? AND (ej.codigo_inventario LIKE ? "
                + "OR l.titulo LIKE ? OR CONCAT(a.nombres, ' ', a.apellidos) LIKE ?) ORDER BY l.titulo, " + orden(),
                bd(EstadoEjemplar.DISPONIBLE), t, t, t);
    }
}
