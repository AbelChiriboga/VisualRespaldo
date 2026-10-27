package reportes;

import com.toedter.calendar.JDateChooser;
import java.time.LocalDate;
import javax.swing.JComponent;

/** Filtro de fecha (obligatorio) con calendario. */
public class FiltroFecha implements FiltroReporte {

    private final String parametro;
    private final String etiqueta;
    private final JDateChooser fecha = new JDateChooser();

    public FiltroFecha(String parametro, String etiqueta, LocalDate porDefecto) {
        this.parametro = parametro;
        this.etiqueta = etiqueta;
        fecha.setDateFormatString("dd/MM/yyyy");
        fecha.setDate(util.Formatos.aDate(porDefecto));
        fecha.setFont(util.Estilos.FUENTE);
        fecha.setPreferredSize(new java.awt.Dimension(160, 32));
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
        return fecha;
    }

    @Override
    public Object getValor() {
        return util.Validaciones.requerido(fecha.getDate(), "la fecha " + etiqueta.toLowerCase());
    }

    public LocalDate getFecha() {
        return util.Formatos.aLocalDate((java.util.Date) getValor());
    }

    @Override
    public String getDescripcion() {
        return etiqueta + ": " + util.Formatos.fecha(getFecha());
    }
}
