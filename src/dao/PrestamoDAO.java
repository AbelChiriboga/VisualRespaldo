package dao;

import java.sql.*;
import java.util.List;
import modelo.*;

/** DAO de préstamos. */
public class PrestamoDAO extends BaseDAO<Prestamo, Integer> {

    static final String COLUMNAS =
            "p.id_prestamo AS pre_id, p.fecha_prestamo AS pre_fecha, p.fecha_limite AS pre_limite, "
            + "p.fecha_devolucion AS pre_devolucion, p.estado AS pre_estado, p.huella_prestamo AS pre_huella_p, "
            + "p.huella_devolucion AS pre_huella_d, p.observaciones AS pre_obs, "
            + EjemplarDAO.COLUMNAS + ", " + EstudianteDAO.COLUMNAS + ", "
            + UsuarioDAO.COLUMNAS + ", " + MultaDAO.COLUMNAS;

    static final String TABLAS = "prestamos p "
            + "JOIN ejemplares ej ON ej.id_ejemplar = p.id_ejemplar "
            + "JOIN libros l ON l.id_libro = ej.id_libro " + LibroDAO.JOINS + " "
            + "JOIN estudiantes e ON e.id_estudiante = p.id_estudiante "
            + "JOIN carreras ca ON ca.id_carrera = e.id_carrera "
            + "LEFT JOIN usuarios u ON u.cedula = p.cedula_usuario "
            + "LEFT JOIN multas m ON m.id_prestamo = p.id_prestamo";

    private static final String INSERT = "INSERT INTO prestamos "
            + "(id_estudiante, id_ejemplar, cedula_usuario, huella_prestamo, observaciones) VALUES (?,?,?,?,?)";

    private static final String DEVOLVER = "UPDATE prestamos SET estado = ?, huella_devolucion = 1 "
            + "WHERE id_prestamo = ? AND estado = ?";

    private final EjemplarDAO ejemplarDAO = new EjemplarDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected String sqlSelect() {
        return "SELECT " + COLUMNAS + " FROM " + TABLAS;
    }

    @Override
    protected String columnaId() {
        return "p.id_prestamo";
    }

    @Override
    protected String orden() {
        return "p.fecha_prestamo DESC";
    }

    @Override
    protected Prestamo mapear(ResultSet rs) throws SQLException {
        Prestamo p = new Prestamo(ejemplarDAO.mapear(rs), estudianteDAO.mapear(rs),
                rs.getString("usu_cedula") == null ? null : usuarioDAO.mapear(rs));
        p.setId(rs.getInt("pre_id"));
        p.setFechaPrestamo(fecha(rs, "pre_fecha"));
        p.setFechaLimite(fecha(rs, "pre_limite"));
        p.setFechaDevolucion(fecha(rs, "pre_devolucion"));
        p.setEstado(ValorBD.desde(EstadoPrestamo.class, rs.getString("pre_estado")));
        p.setHuellaPrestamo(rs.getBoolean("pre_huella_p"));
        p.setHuellaDevolucion(rs.getBoolean("pre_huella_d"));
        p.setObservaciones(rs.getString("pre_obs"));
        if (rs.getObject("mul_id") != null) {
            Multa m = MultaDAO.mapearMulta(rs);
            m.setPrestamo(p);
            p.setMulta(m);
        }
        return p;
    }

    public Prestamo insertar(Prestamo p) {
        p.setId(insertarConId(INSERT, valoresInsert(p)));
        return p;
    }

    public void insertarVarios(List<Prestamo> prestamos) {
        enTransaccion(cn -> {
            for (Prestamo p : prestamos) {
                p.setId(insertarConId(cn, INSERT, valoresInsert(p)));
            }
        });
    }

    private Object[] valoresInsert(Prestamo p) {
        return new Object[]{p.getEstudiante().getIdEstudiante(), p.getEjemplar().getId(),
            p.getUsuario() == null ? null : p.getUsuario().getCedula(), p.isHuellaPrestamo(), p.getObservaciones()};
    }

    public void registrarDevoluciones(List<Integer> idsPrestamo) {
        enTransaccion(cn -> {
            for (Integer id : idsPrestamo) {
                ejecutar(cn, DEVOLVER, bd(EstadoPrestamo.DEVUELTO), id, bd(EstadoPrestamo.ACTIVO));
            }
        });
    }

    public List<Prestamo> listarActivosPorEstudiante(int idEstudiante) {
        return consultarLista(sqlSelect() + " WHERE p.id_estudiante = ? AND p.estado = ? ORDER BY p.fecha_limite",
                idEstudiante, bd(EstadoPrestamo.ACTIVO));
    }

    public int contarActivos(int idEstudiante) {
        return consultarEntero("SELECT COUNT(*) FROM prestamos WHERE id_estudiante = ? AND estado = ?",
                idEstudiante, bd(EstadoPrestamo.ACTIVO));
    }

    public List<Prestamo> listarPorEstudiante(int idEstudiante) {
        return consultarLista(sqlSelect() + " WHERE p.id_estudiante = ? ORDER BY " + orden(), idEstudiante);
    }

    public List<Prestamo> listarConMultaPendiente() {
        return consultarLista(sqlSelect() + " WHERE m.id_multa IS NOT NULL AND m.pagada = 0 ORDER BY m.fecha_generacion");
    }

    public List<Prestamo> listarActivos() {
        return consultarLista(sqlSelect() + " WHERE p.estado = ? ORDER BY p.fecha_limite", bd(EstadoPrestamo.ACTIVO));
    }

    public List<Prestamo> listarAtrasados(String texto) {
        String t = like(texto);
        return consultarLista(sqlSelect()
                + " JOIN v_prestamos_activos va ON va.id_prestamo = p.id_prestamo"
                + " WHERE va.dias_restantes < 0 AND (e.cedula LIKE ? OR CONCAT(e.nombres, ' ', e.apellidos) LIKE ?"
                + " OR l.titulo LIKE ?) ORDER BY va.dias_restantes", t, t, t);
    }
}
