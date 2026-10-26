package componentes;

import java.awt.Component;
import java.util.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;

/** Cajas de un formulario en orden: Enter valida la caja y pasa a la siguiente; validar() revisa todas. */
public class Formulario {

    /** Una caja con su regla (la regla lanza ValidacionException si el dato está mal). */
    private record Campo(JComponent caja, Runnable regla) {
    }

    private final Component ventana;
    private final List<Campo> campos = new ArrayList<>();
    private AbstractButton boton;

    public Formulario(Component ventana) {
        this.ventana = ventana;
    }

    public Formulario campo(JComponent caja, Runnable regla) {
        campos.add(new Campo(caja, regla));
        return this;
    }

    /** Termina el recorrido del Enter en un botón (por ejemplo, Guardar): Enter sobre él lo presiona. */
    public Formulario terminarEn(AbstractButton boton) {
        this.boton = boton;
        return this;
    }

    /** Activa el Enter en todas las cajas; se llama una vez, después de agregar los campos. */
    public void armar() {
        for (int i = 0; i < campos.size(); i++) {
            Campo c = campos.get(i);
            int siguiente = i + 1;
            if (siguiente < campos.size() || boton != null) {
                util.Enter.alPresionar(c.caja(), () -> alPresionarEnter(c, siguiente));
            }
        }
        if (boton != null) {
            util.Enter.alPresionar(boton, boton::doClick);
        }
    }

    /** Revisa todas las cajas habilitadas; si una falla, muestra el aviso, la deja seleccionada y devuelve false. */
    public boolean validar() {
        for (Campo c : campos) {
            if (c.caja().isEnabled() && !util.Mensajes.ejecutar(ventana, c.regla())) {
                enfocar(c.caja());
                return false;
            }
        }
        return true;
    }

    private void alPresionarEnter(Campo c, int desde) {
        if (c.caja() instanceof JComboBox<?> combo && !comboListo(combo)) {
            return;
        }
        if (util.Mensajes.ejecutar(ventana, c.regla())) {
            irDesde(desde);
        } else {
            enfocar(c.caja());
        }
    }

    /**
     * Enter en un combo: si la lista está abierta, elige la opción sombreada; si no hay nada elegido, abre la
     * lista en la primera opción (se navega con las flechas). Solo con una opción ya elegida pasa a la siguiente.
     */
    private static boolean comboListo(JComboBox<?> combo) {
        javax.swing.JList<?> lista = listaDe(combo);
        if (combo.isPopupVisible()) {
            if (lista != null && lista.getSelectedIndex() >= 0) {
                combo.setSelectedIndex(lista.getSelectedIndex());
            }
            combo.setPopupVisible(false);
            return false;
        }
        if (combo.getSelectedIndex() < 0 && combo.getItemCount() > 0) {
            combo.setPopupVisible(true);
            if (lista != null) {
                lista.setSelectedIndex(0);
                lista.ensureIndexIsVisible(0);
            }
            return false;
        }
        return true;
    }

    private static javax.swing.JList<?> listaDe(JComboBox<?> combo) {
        Object popup = combo.getUI().getAccessibleChild(combo, 0);
        return popup instanceof javax.swing.plaf.basic.ComboPopup p ? p.getList() : null;
    }

    private void irDesde(int desde) {
        for (int i = desde; i < campos.size(); i++) {
            JComponent caja = campos.get(i).caja();
            if (disponible(caja)) {
                caja.requestFocusInWindow();
                return;
            }
        }
        if (boton != null && disponible(boton)) {
            boton.requestFocusInWindow();
        }
    }

    private static boolean disponible(JComponent c) {
        return c.isShowing() && c.isEnabled() && c.isFocusable();
    }

    private static void enfocar(JComponent caja) {
        caja.requestFocus();
        if (caja instanceof JTextComponent texto) {
            texto.selectAll();
        }
    }
}
