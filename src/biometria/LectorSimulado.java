package biometria;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import javax.swing.*;

/** Lector SIMULADO para probar el sistema sin el lector conectado. */
public class LectorSimulado extends LectorBase {

    private volatile javax.swing.JDialog dialogo;
    private volatile java.awt.image.BufferedImage ultimaImagen;

    @Override
    public Optional<java.awt.image.BufferedImage> getUltimaImagen() {
        return Optional.ofNullable(ultimaImagen);
    }

    private static java.awt.image.BufferedImage dibujarHuella(String codigo) {
        int w = 220, h = 260;
        java.util.Random azar = new java.util.Random(codigo.hashCode());
        double cx = w / 2.0 + azar.nextInt(30) - 15, cy = h / 2.0 + azar.nextInt(30) - 15;
        double giro = 2 + azar.nextDouble() * 3, separacion = 0.55 + azar.nextDouble() * 0.2;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                double dx = (x - w / 2.0) / (w / 2.2), dy = (y - h / 2.0) / (h / 2.1);
                int gris = 255;
                if (dx * dx + dy * dy <= 1) {
                    double r = Math.hypot(x - cx, y - cy), ang = Math.atan2(y - cy, x - cx);
                    double cresta = Math.sin(r * separacion + ang * giro);
                    gris = cresta > 0.15 ? 40 : 235;
                }
                img.getRaster().setSample(x, y, 0, gris);
            }
        }
        return img;
    }

    private static final int PUNTAJE_COINCIDENCIA = 90;

    private boolean abierto;
    private String ultimoCodigo = "";
    private final Map<Integer, byte[]> plantillas = new HashMap<>();

    @Override
    public String getNombre() {
        return "Simulado";
    }

    @Override
    public void abrir() {
        abierto = true;
    }

    @Override
    public void cerrar() {
        abierto = false;
    }

    @Override
    public boolean estaAbierto() {
        return abierto;
    }

    @Override
    public EstadoLector verificarEstado() {
        abierto = true;
        return EstadoLector.SIMULADO;
    }

    @Override
    public byte[] capturar() {
        exigirAbierto();
        String[] respuesta = new String[1];
        Runnable pedir = () -> respuesta[0] = pedirCodigo();
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                pedir.run();
            } else {
                SwingUtilities.invokeAndWait(pedir);
            }
        } catch (InterruptedException | InvocationTargetException e) {
            throw new excepciones.CapturaCanceladaException();
        }
        if (respuesta[0] == null || respuesta[0].isBlank()) {
            throw new excepciones.CapturaCanceladaException();
        }
        ultimoCodigo = respuesta[0].trim();
        ultimaImagen = dibujarHuella(ultimoCodigo);
        return util.Seguridad.sha256("HUELLA:" + ultimoCodigo).getBytes(java.nio.charset.StandardCharsets.US_ASCII);
    }

    private String pedirCodigo() {
        JOptionPane panel = new JOptionPane(
                "LECTOR SIMULADO\nEscriba el código del dedo (por ejemplo, la cédula del estudiante):",
                JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        panel.setWantsInput(true);
        panel.setInitialSelectionValue(ultimoCodigo);
        javax.swing.JDialog d = panel.createDialog(null, "Lector simulado");
        dialogo = d;
        d.setVisible(true);
        d.dispose();
        dialogo = null;
        Object valor = panel.getInputValue();
        return valor == JOptionPane.UNINITIALIZED_VALUE || !Integer.valueOf(JOptionPane.OK_OPTION).equals(panel.getValue())
                ? null : (String) valor;
    }

    @Override
    public void cancelarCaptura() {
        SwingUtilities.invokeLater(() -> {
            if (dialogo != null) {
                dialogo.dispose();
            }
        });
    }

    @Override
    protected boolean mismoDedo(byte[] a, byte[] b) {
        return Arrays.equals(a, b);
    }

    @Override
    protected byte[] fusionar(byte[][] muestras) {
        return muestras[0].clone();
    }

    @Override
    public void cargarPlantillas(Map<Integer, byte[]> plantillas) {
        this.plantillas.clear();
        this.plantillas.putAll(plantillas);
    }

    @Override
    public Optional<Coincidencia> identificar(byte[] muestra) {
        return plantillas.entrySet().stream()
                .filter(e -> Arrays.equals(e.getValue(), muestra))
                .map(e -> new Coincidencia(e.getKey(), PUNTAJE_COINCIDENCIA))
                .findFirst();
    }
}
