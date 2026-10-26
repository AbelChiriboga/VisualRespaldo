package componentes;

import java.util.List;
import javax.swing.*;

/** Llenado de combos con OBJETOS (Autor, Categoria, Carrera, enums...). */
public final class Combos {

    private Combos() {
    }

    public static <T> void llenar(JComboBox<T> combo, List<T> elementos) {
        Object anterior = combo.getSelectedItem();
        combo.setModel(new DefaultComboBoxModel<>(new java.util.Vector<>(elementos)));
        combo.setSelectedItem(anterior);
        if (combo.getSelectedIndex() < 0 && !elementos.isEmpty()) {
            combo.setSelectedIndex(0);
        }
    }

    /** Para formularios: llena sin elegir nada (conserva lo que estaba elegido, si sigue en la lista). */
    public static <T> void llenarSinElegir(JComboBox<T> combo, List<T> elementos) {
        Object anterior = combo.getSelectedItem();
        combo.setModel(new DefaultComboBoxModel<>(new java.util.Vector<>(elementos)));
        combo.setSelectedItem(anterior);
        if (anterior == null) {
            combo.setSelectedIndex(-1);
        }
    }

    public static <E extends Enum<E>> void llenar(JComboBox<E> combo, Class<E> tipoEnum) {
        llenar(combo, List.of(tipoEnum.getEnumConstants()));
    }

    public static <T> void ajustarAncho(JComboBox<T> combo) {
        java.awt.FontMetrics fm = combo.getFontMetrics(combo.getFont());
        T masLargo = null;
        for (int i = 0; i < combo.getItemCount(); i++) {
            T item = combo.getItemAt(i);
            if (masLargo == null || fm.stringWidth(String.valueOf(item)) > fm.stringWidth(String.valueOf(masLargo))) {
                masLargo = item;
            }
        }
        if (masLargo == null) {
            return;
        }
        int alto = combo.getPreferredSize().height;
        combo.setPrototypeDisplayValue(masLargo);
        combo.setPreferredSize(null);
        java.awt.Dimension justo = new java.awt.Dimension(combo.getPreferredSize().width + 8, alto);
        combo.setPreferredSize(justo);
        combo.setMinimumSize(justo);
        combo.revalidate();
    }

    public static <T> T seleccionado(JComboBox<T> combo) {
        return combo.getItemAt(combo.getSelectedIndex());
    }

    public static <T> void seleccionar(JComboBox<T> combo, T elemento) {
        combo.setSelectedItem(elemento);
    }
}
