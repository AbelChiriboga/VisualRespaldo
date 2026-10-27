package reportes;

import java.util.*;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.*;
import net.sf.jasperreports.view.save.*;

/** Visor de Jasper que guarda en PDF por defecto (también Word y Excel). */
public class VisorReporte extends JasperViewer {

    public VisorReporte(JasperPrint impreso, String titulo) {
        super(impreso, false);
        setTitle(titulo);
        Locale local = Locale.getDefault();
        ResourceBundle textos = ResourceBundle.getBundle("net/sf/jasperreports/view/viewer", local);
        viewer.setSaveContributors(new JRSaveContributor[]{
            new JRPdfSaveContributor(local, textos),
            new JRDocxSaveContributor(local, textos),
            new JRSingleSheetXlsSaveContributor(local, textos)
        });
    }
}
