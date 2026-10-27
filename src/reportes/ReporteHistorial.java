package reportes;

import excepciones.ValidacionException;
import java.time.LocalDate;
import java.util.List;
import modelo.*;

/** Historial de préstamos entre dos fechas, por carrera y estado. */
public class ReporteHistorial extends ReporteBase {

    private static final int DIAS_POR_DEFECTO = 30;

    private FiltroFecha desde;
    private FiltroFecha hasta;

    @Override
    protected String archivo() {
        return "Historial";
    }

    @Override
    public String getTitulo() {
        return "Historial de préstamos";
    }

    @Override
    protected List<FiltroReporte> filtros() {
        desde = new FiltroFecha("P_DESDE", "Desde", LocalDate.now().minusDays(DIAS_POR_DEFECTO));
        hasta = new FiltroFecha("P_HASTA", "Hasta", LocalDate.now());
        return List.of(desde, hasta,
                new FiltroOpciones<Carrera>("P_CARRERA", "Carrera", "Todas",
                        new servicio.EstudianteService().listarCarreras(), Carrera::getId),
                new FiltroOpciones<EstadoPrestamo>("P_ESTADO", "Estado", "Todos",
                        List.of(EstadoPrestamo.values()), EstadoPrestamo::getValorBD));
    }

    @Override
    protected void validar(List<FiltroReporte> filtros) {
        if (desde.getFecha().isAfter(hasta.getFecha())) {
            throw new ValidacionException("La fecha Desde no puede ser mayor que Hasta.");
        }
    }
}
