package componentes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

/** Caja de búsqueda + botón de filtro (embudo). */
public class BarraBusqueda extends JPanel {

    private final JTextField campo = new CampoConAyuda();
    private final BotonSistema botonFiltro = new BotonSistema("");
    private final JPopupMenu menuFiltro = new JPopupMenu();
    private final List<JCheckBoxMenuItem> columnas = new ArrayList<>();
    private final List<Runnable> alCambiar = new ArrayList<>();
    private String ayuda = "Escriba para buscar";

    public BarraBusqueda() {
        super(new BorderLayout(8, 0));
        setOpaque(false);

        campo.setFont(util.Estilos.FUENTE);
        campo.setPreferredSize(new Dimension(200, 32));

        botonFiltro.setToolTipText("Elegir en qué columnas buscar");
        botonFiltro.setPreferredSize(new Dimension(36, 32));
        botonFiltro.setRutaIcono("filtro.png");
        botonFiltro.setTamanoIcono(18);
        botonFiltro.addActionListener(e -> menuFiltro.show(botonFiltro, 0, botonFiltro.getHeight()));

        add(campo, BorderLayout.CENTER);
        util.TipoEntrada.LIBRE.aplicar(campo, 100);
        add(botonFiltro, BorderLayout.EAST);

        util.Eventos.alCambiar(campo, this::avisarCambio);
    }

    public void configurarFiltro(List<String> nombresColumnas) {
        menuFiltro.removeAll();
        columnas.clear();

        JMenuItem titulo = new JMenuItem("Buscar en:");
        titulo.setEnabled(false);
        menuFiltro.add(titulo);
        for (String nombre : nombresColumnas) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(nombre, true);
            item.putClientProperty("CheckBoxMenuItem.doNotCloseOnMouseClick", Boolean.TRUE);
            item.addActionListener(e -> cambioDeColumnas(item));
            columnas.add(item);
            menuFiltro.add(item);
        }
        menuFiltro.addSeparator();
        JMenuItem todas = new JMenuItem("Todas las columnas");
        todas.addActionListener(e -> marcarTodas());
        menuFiltro.add(todas);
        actualizarBotonFiltro();
    }

    public void alBuscar(Consumer<String> accion) {
        alCambiar.add(() -> accion.accept(getTexto()));
    }

    public String getTexto() {
        return campo.getText();
    }

    public List<Integer> getColumnasSeleccionadas() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < columnas.size(); i++) {
            if (columnas.get(i).isSelected()) {
                indices.add(i);
            }
        }
        return indices;
    }

    public void limpiar() {
        campo.setText("");
    }

    public JTextField getCampo() { return campo; }
    public BotonSistema getBotonFiltro() { return botonFiltro; }

    public String getAyuda() { return ayuda; }

    public void setAyuda(String ayuda) {
        this.ayuda = ayuda == null ? "" : ayuda;
        campo.setToolTipText(this.ayuda);
        campo.repaint();
    }

    public boolean isFiltroVisible() { return botonFiltro.isVisible(); }
    public void setFiltroVisible(boolean visible) { botonFiltro.setVisible(visible); }

    private void cambioDeColumnas(JCheckBoxMenuItem item) {
        if (getColumnasSeleccionadas().isEmpty()) {
            item.setSelected(true);
        }
        actualizarBotonFiltro();
        avisarCambio();
    }

    private void marcarTodas() {
        columnas.forEach(c -> c.setSelected(true));
        actualizarBotonFiltro();
        avisarCambio();
    }

    private void actualizarBotonFiltro() {
        boolean filtrado = getColumnasSeleccionadas().size() < columnas.size();
        botonFiltro.setPrimario(filtrado);
        botonFiltro.setToolTipText(filtrado ? "Buscando en: " + nombresSeleccionados() : "Elegir en qué columnas buscar");
    }

    private String nombresSeleccionados() {
        List<String> nombres = new ArrayList<>();
        columnas.stream().filter(JCheckBoxMenuItem::isSelected).forEach(c -> nombres.add(c.getText()));
        return String.join(", ", nombres);
    }

    private void avisarCambio() {
        alCambiar.forEach(Runnable::run);
    }

    /** Caja de texto que muestra la ayuda en gris mientras está vacía. */
    private class CampoConAyuda extends JTextField {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !ayuda.isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new Color(150, 160, 168));
                g2.setFont(getFont());
                Insets m = getInsets();
                int y = (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
                g2.drawString(ayuda, m.left + 2, y);
                g2.dispose();
            }
        }
    }
}
