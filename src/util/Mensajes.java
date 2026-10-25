package util;

import excepciones.BibliotecaException;
import java.awt.Component;
import javax.swing.JOptionPane;

/** Mensajes al usuario en un solo lugar. */
public final class Mensajes {

    private Mensajes() {
    }

    public static boolean ejecutar(Component padre, Runnable accion) {
        try {
            accion.run();
            return true;
        } catch (BibliotecaException e) {
            error(padre, e);
            return false;
        }
    }

    public static void error(Component padre, BibliotecaException e) {
        int tipo = e.esErrorTecnico() ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE;
        JOptionPane.showMessageDialog(padre, e.getMessage(), e.getTitulo(), tipo);
    }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Biblioteca FISEI", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component padre, String pregunta) {
        return JOptionPane.showConfirmDialog(padre, pregunta, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
