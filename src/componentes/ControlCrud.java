package componentes;

import java.util.*;
import javax.swing.*;

/** Habilita y deshabilita los campos y botones de un formulario CRUD según el modo. */
public class ControlCrud {

    /** Estados del formulario. */
    public enum Modo {
        CONSULTA {
            @Override boolean camposEditables() { return false; }
            @Override boolean puedeEmpezar(boolean haySeleccion) { return true; }
            @Override boolean puedeActuarSobreSeleccion(boolean haySeleccion) { return haySeleccion; }
        },
        NUEVO {
            @Override boolean camposEditables() { return true; }
            @Override boolean puedeEmpezar(boolean haySeleccion) { return false; }
            @Override boolean puedeActuarSobreSeleccion(boolean haySeleccion) { return false; }
        },
        EDICION {
            @Override boolean camposEditables() { return true; }
            @Override boolean puedeEmpezar(boolean haySeleccion) { return false; }
            @Override boolean puedeActuarSobreSeleccion(boolean haySeleccion) { return false; }
        };

        abstract boolean camposEditables();

        abstract boolean puedeEmpezar(boolean haySeleccion);

        abstract boolean puedeActuarSobreSeleccion(boolean haySeleccion);
    }

    private final List<JComponent> campos;
    private final JButton nuevo;
    private final JButton guardar;
    private final JButton editar;
    private final JButton eliminar;
    private final JButton cancelar;
    private final List<JComponent> conSeleccion = new ArrayList<>();
    private final List<JComponent> bloqueadosAlEditar = new ArrayList<>();
    private Modo modo = Modo.CONSULTA;

    public ControlCrud(JComponent[] campos, JButton nuevo, JButton guardar, JButton editar,
                       JButton eliminar, JButton cancelar) {
        this.campos = Arrays.asList(campos);
        this.nuevo = nuevo;
        this.guardar = guardar;
        this.editar = editar;
        this.eliminar = eliminar;
        this.cancelar = cancelar;
    }

    public void agregarBotonDeSeleccion(JComponent componente) {
        conSeleccion.add(componente);
    }

    public void bloquearAlEditar(JComponent... componentes) {
        bloqueadosAlEditar.addAll(Arrays.asList(componentes));
    }

    public void consulta(boolean haySeleccion) {
        aplicar(Modo.CONSULTA, haySeleccion);
    }

    public void nuevo() {
        aplicar(Modo.NUEVO, false);
    }

    public void edicion() {
        aplicar(Modo.EDICION, true);
    }

    public Modo getModo() {
        return modo;
    }

    public static void permitirEscribir(JComponent c, boolean editable) {
        c.setEnabled(editable);
        if (c instanceof javax.swing.text.JTextComponent) {
            ((javax.swing.text.JTextComponent) c).setDisabledTextColor(util.Estilos.TEXTO);
        }
    }

    private void aplicar(Modo modo, boolean haySeleccion) {
        this.modo = modo;
        boolean editable = modo.camposEditables();
        campos.forEach(c -> permitirEscribir(c, editable));
        guardar.setEnabled(editable);
        cancelar.setEnabled(editable);
        nuevo.setEnabled(modo.puedeEmpezar(haySeleccion));
        boolean sobreSeleccion = modo.puedeActuarSobreSeleccion(haySeleccion);
        editar.setEnabled(sobreSeleccion);
        eliminar.setEnabled(sobreSeleccion);
        conSeleccion.forEach(b -> b.setEnabled(sobreSeleccion));
        bloqueadosAlEditar.forEach(c -> c.setEnabled(!editable));
    }
}
