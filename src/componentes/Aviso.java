package componentes;

import java.awt.*;
import javax.swing.*;

/** Mensaje del estado del lector (conectado / desconectado) con un JOptionPane simple. */
public final class Aviso {

    private static JDialog actual;

    private Aviso() {
    }

    public static void mostrar(String texto, boolean exito) {
        if (actual != null) {
            actual.dispose();
        }
        JOptionPane mensaje = new JOptionPane(texto,
                exito ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        JDialog dialogo = mensaje.createDialog(ventanaActiva(), "Lector de huella");
        actual = dialogo;
        dialogo.setVisible(true);
        dialogo.dispose();
    }

    private static Window ventanaActiva() {
        Window activa = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
        if (activa != null) {
            return activa;
        }
        for (Frame f : Frame.getFrames()) {
            if (f.isShowing()) {
                return f;
            }
        }
        return null;
    }
}
