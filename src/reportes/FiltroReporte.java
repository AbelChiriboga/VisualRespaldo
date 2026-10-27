package reportes;

import javax.swing.JComponent;

/** Un dato que el usuario elige antes de generar un reporte (se envía como parámetro al .jrxml). */
public interface FiltroReporte {

    String getEtiqueta();

    String getParametro();

    JComponent getComponente();

    Object getValor();

    String getDescripcion();
}
