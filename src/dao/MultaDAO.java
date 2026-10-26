package dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import modelo.Multa;

/** Multas. */
public class MultaDAO extends BaseDAO<Multa, Integer> {

    static final String COLUMNAS = "m.id_multa AS mul_id, m.dias_retraso AS mul_dias, m.monto AS mul_monto, "
            + "m.pagada AS mul_pagada, m.fecha_generacion AS mul_fecha, m.fecha_pago AS mul_fecha_pago";

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    static Multa mapearMulta(ResultSet rs) throws SQLException {
        Multa m = new Multa();
        m.setId(rs.getInt("mul_id"));
        m.setDiasAtraso(rs.getInt("mul_dias"));
        m.setMonto(rs.getBigDecimal("mul_monto"));
        m.setPagada(rs.getBoolean("mul_pagada"));
        m.setFechaGeneracion(fecha(rs, "mul_fecha"));
        m.setFechaPago(fecha(rs, "mul_fecha_pago"));
        return m;
    }

    @Override
    protected String sqlSelect() {
        return "SELECT " + PrestamoDAO.COLUMNAS + " FROM "
                + PrestamoDAO.TABLAS.replace("LEFT JOIN multas", "JOIN multas");
    }

    @Override
    protected String columnaId() {
        return "m.id_multa";
    }

    @Override
    protected String orden() {
        return "m.fecha_generacion DESC";
    }

    @Override
    protected Multa mapear(ResultSet rs) throws SQLException {
        return prestamoDAO.mapear(rs).getMulta();
    }

    public List<Multa> listarPorEstudiante(int idEstudiante) {
        return consultarLista(sqlSelect() + " WHERE p.id_estudiante = ? ORDER BY " + orden(), idEstudiante);
    }

    public List<Multa> listarPendientesPorEstudiante(int idEstudiante) {
        return consultarLista(sqlSelect() + " WHERE p.id_estudiante = ? AND m.pagada = 0 ORDER BY " + orden(), idEstudiante);
    }

    public BigDecimal totalPendiente(int idEstudiante) {
        List<BigDecimal> r = consultar("SELECT COALESCE(SUM(m.monto), 0) FROM multas m "
                + "JOIN prestamos p ON p.id_prestamo = m.id_prestamo WHERE p.id_estudiante = ? AND m.pagada = 0",
                rs -> rs.getBigDecimal(1), idEstudiante);
        return r.get(0);
    }

    public void marcarPagada(int idMulta) {
        ejecutar("UPDATE multas SET pagada = 1, fecha_pago = NOW() WHERE id_multa = ? AND pagada = 0", idMulta);
    }
}
