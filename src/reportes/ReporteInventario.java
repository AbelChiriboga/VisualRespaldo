package reportes;

import java.util.List;
import modelo.*;

/** Inventario de ejemplares por categoría y estado. */
public class ReporteInventario extends ReporteBase {

    @Override
    protected String archivo() {
        return "Inventario";
    }

    @Override
    public String getTitulo() {
        return "Inventario de ejemplares";
    }

    @Override
    protected List<FiltroReporte> filtros() {
        return List.of(
                new FiltroOpciones<Categoria>("P_CATEGORIA", "Categoría", "Todas",
                        new servicio.CategoriaService().listarConTotales(), Categoria::getId),
                new FiltroOpciones<EstadoEjemplar>("P_ESTADO", "Estado", "Todos",
                        List.of(EstadoEjemplar.values()), EstadoEjemplar::getValorBD));
    }
}
