package util;

import javax.swing.text.*;

/** Filtro de una caja de texto: bloquea caracteres no permitidos y corta al llegar al máximo. */
public class FiltroEntrada extends DocumentFilter {

    private final TipoEntrada tipo;
    private final int maximo;

    public FiltroEntrada(TipoEntrada tipo, int maximo) {
        this.tipo = tipo;
        this.maximo = maximo;
    }

    @Override
    public void insertString(FilterBypass fb, int pos, String texto, AttributeSet attr) throws BadLocationException {
        replace(fb, pos, 0, texto, attr);
    }

    @Override
    public void replace(FilterBypass fb, int pos, int largo, String texto, AttributeSet attr)
            throws BadLocationException {
        if (texto == null || texto.isEmpty()) {
            super.replace(fb, pos, largo, texto, attr);
            return;
        }
        String todo = fb.getDocument().getText(0, fb.getDocument().getLength());
        String resto = todo.substring(0, pos) + todo.substring(pos + largo);
        String permitido = tipo.filtrar(resto, texto);
        int espacio = Math.max(0, maximo - resto.length());
        if (permitido.length() > espacio) {
            permitido = permitido.substring(0, espacio);
        }
        if (permitido.length() < texto.length()) {
            java.awt.Toolkit.getDefaultToolkit().beep();
        }
        super.replace(fb, pos, largo, permitido, attr);
    }
}
