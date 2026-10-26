package dao;

import java.util.*;
import modelo.Indicador;

/** Conteos para las tarjetas de los menús. */
public class IndicadorDAO extends AccesoBD {

    private static final Map<Indicador, String> CONSULTAS = new EnumMap<>(Indicador.class);

    static {
        CONSULTAS.put(Indicador.ESTUDIANTES_REGISTRADOS, "SELECT COUNT(*) FROM estudiantes");
        CONSULTAS.put(Indicador.BIBLIOTECARIOS_ACTIVOS,
                "SELECT COUNT(*) FROM usuarios WHERE rol = 'BIBLIOTECARIO' AND activo = 1");
        CONSULTAS.put(Indicador.PRESTAMOS_ACTIVOS, "SELECT COUNT(*) FROM prestamos WHERE estado = 'ACTIVO'");
        CONSULTAS.put(Indicador.PRESTAMOS_ATRASADOS, "SELECT COUNT(*) FROM v_prestamos_activos WHERE dias_restantes < 0");
        CONSULTAS.put(Indicador.DEVOLUCIONES_HOY, "SELECT COUNT(*) FROM prestamos WHERE DATE(fecha_devolucion) = CURDATE()");
        CONSULTAS.put(Indicador.EJEMPLARES_DISPONIBLES, "SELECT COUNT(*) FROM ejemplares WHERE estado = 'DISPONIBLE'");
        CONSULTAS.put(Indicador.HUELLAS_FALLIDAS_HOY,
                "SELECT COUNT(*) FROM registro_autenticacion WHERE resultado = 'FALLO' AND DATE(fecha) = CURDATE()");
    }

    public int contar(Indicador indicador) {
        return consultarEntero(CONSULTAS.get(indicador));
    }
}
