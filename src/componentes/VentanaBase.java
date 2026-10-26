package componentes;

import java.awt.*;
import java.util.function.Supplier;
import javax.swing.*;

/** Clase padre de todas las ventanas internas del sistema (todas menos el Login). */
public class VentanaBase extends JFrame {

    public static final Dimension TAMANO = new Dimension(1130, 700);

    protected final Encabezado encabezado = new Encabezado();
    protected final PiePagina pie = new PiePagina();

    public VentanaBase() {
        setUndecorated(true);
        util.Arrastre.permitir(this, encabezado);
        encabezado.getBotonRegresar().addActionListener(e -> dispose());
    }

    protected void montar(String subtitulo, JComponent contenido) {
        encabezado.setSubtitulo(subtitulo);
        servicio.Sesion.getInstancia().getUsuarioActual().ifPresent(
                u -> encabezado.setUsuario(u.getNombreCompleto(), u.getCedula(), u.getTipo()));

        JScrollPane cuerpo = new JScrollPane(new Lienzo(contenido));
        cuerpo.setBorder(BorderFactory.createEmptyBorder());
        cuerpo.getViewport().setBackground(Color.WHITE);
        cuerpo.getVerticalScrollBar().setUnitIncrement(16);
        cuerpo.getHorizontalScrollBar().setUnitIncrement(16);

        Container cp = getContentPane();
        cp.removeAll();
        cp.setLayout(new BorderLayout());
        cp.add(encabezado, BorderLayout.NORTH);
        cp.add(cuerpo, BorderLayout.CENTER);
        cp.add(pie, BorderLayout.SOUTH);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        cp.setPreferredSize(TAMANO);
        pack();
        ajustarAPantalla();
        setLocationRelativeTo(null);
    }

    private void ajustarAPantalla() {
        java.awt.Rectangle util = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(getWidth(), util.width), Math.min(getHeight(), util.height));
    }

    /** Panel blanco con margen que envuelve el contenido. */
    private static final class Lienzo extends JPanel implements javax.swing.Scrollable {

        private static final int ALTO_MINIMO = 500;

        Lienzo(JComponent contenido) {
            super(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
            add(contenido, BorderLayout.CENTER);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle visible, int orientacion, int direccion) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle visible, int orientacion, int direccion) {
            return orientacion == javax.swing.SwingConstants.VERTICAL ? visible.height : visible.width;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return getParent().getWidth() >= getMinimumSize().width;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return getParent().getHeight() >= Math.max(ALTO_MINIMO, getMinimumSize().height);
        }
    }

    protected void montarMenu(String subtitulo, JComponent contenido, Supplier<JFrame> pantallaInicio) {
        encabezado.setModoMenu(true);
        encabezado.getBotonSalir().addActionListener(e -> confirmarSalida());
        encabezado.getBotonCerrarSesion().addActionListener(e -> cerrarSesion(pantallaInicio));
        montar(subtitulo, contenido);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    protected void alCambiarLector(java.util.function.Consumer<biometria.EstadoLector> accion) {
        biometria.MonitorLector monitor = biometria.MonitorLector.getInstancia();
        monitor.iniciar();
        monitor.agregarOyente(accion);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                monitor.quitarOyente(accion);
            }
        });
    }

    protected boolean ejecutar(Runnable accion) {
        return util.Mensajes.ejecutar(this, accion);
    }

    private void cerrarSesion(Supplier<JFrame> pantallaInicio) {
        if (util.Mensajes.confirmar(this, "¿Desea cerrar la sesión?")) {
            new servicio.AutenticacionService().cerrarSesion();
            util.Navegacion.cambiar(this, pantallaInicio.get());
        }
    }

    private void confirmarSalida() {
        int r = JOptionPane.showConfirmDialog(this, "¿Desea salir del sistema?",
                "Salir", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
