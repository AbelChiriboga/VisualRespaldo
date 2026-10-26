package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import modelo.*;

/** Bitácora de autenticación biométrica. */
public class RegistroAutenticacionDAO extends BaseDAO<RegistroAutenticacion, Integer> {

    @Override
    protected String sqlSelect() {
        return "SELECT r.id_registro, r.id_estudiante, r.operacion, r.resultado, r.puntaje, r.detalle, r.fecha, "
                + "e.cedula, CONCAT(e.nombres, ' ', e.apellidos) AS estudiante "
                + "FROM registro_autenticacion r LEFT JOIN estudiantes e ON e.id_estudiante = r.id_estudiante";
    }

    @Override
    protected String columnaId() {
        return "r.id_registro";
    }

    @Override
    protected String orden() {
        return "r.fecha DESC";
    }

    @Override
    protected RegistroAutenticacion mapear(ResultSet rs) throws SQLException {
        RegistroAutenticacion r = new RegistroAutenticacion(enteroONulo(rs, "id_estudiante"),
                ValorBD.desde(OperacionAutenticacion.class, rs.getString("operacion")),
                ValorBD.desde(ResultadoAutenticacion.class, rs.getString("resultado")),
                enteroONulo(rs, "puntaje"), rs.getString("detalle"));
        r.setId(rs.getInt("id_registro"));
        r.setFechaHora(fecha(rs, "fecha"));
        r.setCedulaEstudiante(rs.getString("cedula"));
        r.setNombreEstudiante(rs.getString("estudiante"));
        return r;
    }

    public RegistroAutenticacion insertar(RegistroAutenticacion r) {
        r.setId(insertarConId("INSERT INTO registro_autenticacion (id_estudiante, operacion, resultado, puntaje, detalle) "
                + "VALUES (?,?,?,?,?)", r.getIdEstudiante(), bd(r.getOperacion()), bd(r.getResultado()),
                r.getPuntaje(), r.getDetalle()));
        return r;
    }

    public List<RegistroAutenticacion> listar(LocalDate desde, LocalDate hasta,
                                              OperacionAutenticacion operacion, ResultadoAutenticacion resultado) {
        Date d = desde == null ? null : Date.valueOf(desde);
        Date h = hasta == null ? null : Date.valueOf(hasta);
        return consultarLista(sqlSelect()
                + " WHERE (? IS NULL OR r.fecha >= ?) AND (? IS NULL OR r.fecha < DATE_ADD(?, INTERVAL 1 DAY))"
                + " AND (? IS NULL OR r.operacion = ?) AND (? IS NULL OR r.resultado = ?) ORDER BY " + orden(),
                d, d, h, h, bd(operacion), bd(operacion), bd(resultado), bd(resultado));
    }
}
