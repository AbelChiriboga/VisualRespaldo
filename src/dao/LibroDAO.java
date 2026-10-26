package dao;

import java.sql.*;
import java.util.List;
import modelo.Libro;

/** CRUD de libros. */
public class LibroDAO extends BaseDAO<Libro, Integer> implements CrudDAO<Libro, Integer> {

    static final String COLUMNAS =
            "l.id_libro AS lib_id, l.isbn AS lib_isbn, l.titulo AS lib_titulo, l.editorial AS lib_editorial, "
            + "l.anio_publicacion AS lib_anio, l.fecha_registro AS lib_fecha, "
            + "v.total_ejemplares AS lib_total, v.disponibles AS lib_disponibles, "
            + AutorDAO.COLUMNAS + ", " + CategoriaDAO.COLUMNAS;

    static final String JOINS = "JOIN autores a ON a.id_autor = l.id_autor "
            + "JOIN categorias c ON c.id_categoria = l.id_categoria "
            + "JOIN v_catalogo v ON v.id_libro = l.id_libro";

    static final String TABLAS = "libros l " + JOINS;

    private final AutorDAO autorDAO = new AutorDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Override
    protected String sqlSelect() {
        return "SELECT " + COLUMNAS + " FROM " + TABLAS;
    }

    @Override
    protected String columnaId() {
        return "l.id_libro";
    }

    @Override
    protected String orden() {
        return "l.titulo";
    }

    @Override
    protected Libro mapear(ResultSet rs) throws SQLException {
        Libro l = new Libro(rs.getString("lib_isbn"), rs.getString("lib_titulo"),
                autorDAO.mapear(rs), categoriaDAO.mapear(rs));
        l.setId(rs.getInt("lib_id"));
        l.setEditorial(rs.getString("lib_editorial"));
        l.setAnio(enteroONulo(rs, "lib_anio"));
        l.setFechaRegistro(fecha(rs, "lib_fecha"));
        l.setEjemplaresTotales(rs.getInt("lib_total"));
        l.setEjemplaresDisponibles(rs.getInt("lib_disponibles"));
        return l;
    }

    @Override
    public Libro insertar(Libro l) {
        l.setId(insertarConId("INSERT INTO libros (isbn, titulo, id_autor, id_categoria, editorial, anio_publicacion) "
                + "VALUES (?,?,?,?,?,?)", l.getIsbn(), l.getTitulo(), l.getAutor().getId(),
                l.getCategoria().getId(), l.getEditorial(), l.getAnio()));
        return l;
    }

    /** Crea el libro y sus ejemplares (pueden ser cero) en una sola transacción. */
    public void insertarConEjemplares(Libro l, List<modelo.Ejemplar> ejemplares) {
        EjemplarDAO ejemplarDAO = new EjemplarDAO();
        try {
            enTransaccion(cn -> {
                l.setId(insertarConId(cn, "INSERT INTO libros (isbn, titulo, id_autor, id_categoria, editorial, "
                        + "anio_publicacion) VALUES (?,?,?,?,?,?)", l.getIsbn(), l.getTitulo(), l.getAutor().getId(),
                        l.getCategoria().getId(), l.getEditorial(), l.getAnio()));
                for (modelo.Ejemplar e : ejemplares) {
                    e.setLibro(l);
                    ejemplarDAO.insertar(cn, e);
                }
            });
        } catch (RuntimeException ex) {
            l.setId(0);
            ejemplares.forEach(e -> e.setId(0));
            throw ex;
        }
    }

    @Override
    public void actualizar(Libro l) {
        ejecutar("UPDATE libros SET isbn = ?, titulo = ?, id_autor = ?, id_categoria = ?, editorial = ?, "
                + "anio_publicacion = ? WHERE id_libro = ?", l.getIsbn(), l.getTitulo(), l.getAutor().getId(),
                l.getCategoria().getId(), l.getEditorial(), l.getAnio(), l.getId());
    }

    @Override
    public void eliminar(Integer id) {
        ejecutar("DELETE FROM libros WHERE id_libro = ?", id);
    }

    public List<Libro> buscar(String texto) {
        String t = like(texto);
        return consultarLista(sqlSelect() + " WHERE l.titulo LIKE ? OR l.isbn LIKE ? "
                + "OR CONCAT(a.nombres, ' ', a.apellidos) LIKE ? ORDER BY " + orden(), t, t, t);
    }
}
