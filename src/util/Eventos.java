package util;

import javax.swing.event.*;
import javax.swing.text.JTextComponent;

/** Atajos para eventos que Swing hace largos de escribir. */
public final class Eventos {

    private Eventos() {
    }

    public static void alCambiar(JTextComponent campo, Runnable accion) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { accion.run(); }
            @Override public void removeUpdate(DocumentEvent e) { accion.run(); }
            @Override public void changedUpdate(DocumentEvent e) { accion.run(); }
        });
    }
}
