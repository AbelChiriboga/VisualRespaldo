package reportes;

import componentes.Opcion;
import java.util.List;
import java.util.function.Function;
import javax.swing.*;

/** Filtro con un combo de opciones; la primera ("Todas") envía null, que en la consulta significa "sin filtrar". */
public class FiltroOpciones<T> implements FiltroReporte {

    private final String parametro;
    private final String etiqueta;
    private final Function<T, Object> aParametro;
    private final JComboBox<Opcion<T>> combo = new JComboBox<>();

    public FiltroOpciones(String parametro, String etiqueta, String textoTodos, List<T> valores,
                          Function<T, Object> aParametro) {
        this.parametro = parametro;
        this.etiqueta = etiqueta;
        this.aParametro = aParametro;
        componentes.Combos.llenar(combo, Opcion.conTodas(textoTodos, valores));
        combo.setSelectedIndex(0);
        combo.setFont(util.Estilos.FUENTE);
        componentes.Combos.ajustarAncho(combo);
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String getParametro() {
        return parametro;
    }

    @Override
    public JComponent getComponente() {
        return combo;
    }

    @Override
    public Object getValor() {
        T elegido = componentes.Combos.seleccionado(combo).getValor();
        return elegido == null ? null : aParametro.apply(elegido);
    }

    @Override
    public String getDescripcion() {
        return etiqueta + ": " + componentes.Combos.seleccionado(combo);
    }
}
