package reportes;

import excepciones.DatosException;
import java.awt.Window;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import modelo.Usuario;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import servicio.Sesion;

/** Pasos comunes de todos los reportes Jasper: filtros, parámetros, llenado y visor (método plantilla). */
public abstract class ReporteBase {

    private static final String CARPETA = "/reportes/";

    /** Nombre del diseño sin extensión (por ejemplo "Inventario" → Inventario.jasper / Inventario.jrxml). */
    protected abstract String archivo();

    public abstract String getTitulo();

    protected List<FiltroReporte> filtros() {
        return List.of();
    }

    protected void validar(List<FiltroReporte> filtros) {
    }

    public final void generar(Window padre) {
        Sesion.getInstancia().exigir(Usuario::puedeGenerarReportes, "generar reportes");
        List<FiltroReporte> filtros = filtros();
        if (!filtros.isEmpty() && !DialogoReporte.pedir(padre, getTitulo(), filtros)) {
            return;
        }
        validar(filtros);
        Map<String, Object> parametros = new HashMap<>();
        filtros.forEach(f -> parametros.put(f.getParametro(), f.getValor()));
        parametros.put("P_USUARIO", Sesion.getInstancia().getUsuario().getNombreCompleto());
        parametros.put("P_FILTROS", filtros.stream().map(FiltroReporte::getDescripcion).collect(Collectors.joining("   ·   ")));
        parametros.put("P_LOGO", logo());
        mostrar(new dao.ReporteDAO().llenar(cargar(), parametros));
    }

    /** Usa el .jasper ya compilado; si no existe, compila el .jrxml. */
    private JasperReport cargar() {
        try (InputStream compilado = getClass().getResourceAsStream(CARPETA + archivo() + ".jasper")) {
            if (compilado != null) {
                return (JasperReport) JRLoader.loadObject(compilado);
            }
            try (InputStream diseno = getClass().getResourceAsStream(CARPETA + archivo() + ".jrxml")) {
                if (diseno == null) {
                    throw new DatosException("No se encontró el diseño del reporte " + archivo() + ".");
                }
                return JasperCompileManager.compileReport(diseno);
            }
        } catch (JRException | java.io.IOException e) {
            throw new DatosException("No se pudo cargar el reporte " + archivo() + ": " + e.getMessage(), e);
        }
    }

    private java.awt.Image logo() {
        try {
            return javax.imageio.ImageIO.read(getClass().getResource("/imagenes/logo.png"));
        } catch (Exception e) {
            return null;
        }
    }

    private void mostrar(JasperPrint impreso) {
        VisorReporte visor = new VisorReporte(impreso, getTitulo());
        visor.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        visor.setVisible(true);
        visor.toFront();
    }
}
