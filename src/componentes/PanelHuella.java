package componentes;

import java.awt.*;
import javax.swing.*;

/** Panel del lector de huella. */
public class PanelHuella extends PanelGrupo implements biometria.AvisoCaptura {

    private static final Color GRIS = new Color(60, 75, 85);

    private final JLabel lblHuella = new JLabel("Coloque el dedo");
    private final JLabel lblEstado = new JLabel(" ");
    private final BotonSistema botonRepetir = new BotonSistema("Registrar otra vez");

    private final JPanel pnlEstudiante = new JPanel(new GridLayout(4, 1, 0, 4));
    private final JLabel lblNombre = new JLabel("—");
    private final JLabel lblDatos = new JLabel("<html>Cédula: —<br>Carrera: —</html>");
    private final JLabel lblActivos = new JLabel("Préstamos activos: — de 3");
    private final JLabel lblEstadoEstudiante = new JLabel("SIN IDENTIFICAR");

    private final JLabel lblDedo = new JLabel("Dedo");
    private final JComboBox<modelo.Dedo> cbxDedo = new JComboBox<>(modelo.Dedo.values());
    private final JProgressBar barMuestras = new JProgressBar(0, 3);
    private final JTextArea txtInfo = new JTextArea(3, 20);
    private final JScrollPane scrInfo = new JScrollPane(txtInfo);

    private boolean modoRegistro = false;

    public PanelHuella() {
        super("Huella");
        setLayout(new GridBagLayout());
        armar();
        setModoRegistro(false);
    }

    private void armar() {
        lblHuella.setOpaque(true);
        lblHuella.setBackground(new Color(242, 249, 248));
        lblHuella.setHorizontalAlignment(SwingConstants.CENTER);
        lblHuella.setBorder(BorderFactory.createDashedBorder(new Color(127, 191, 186), 2, 6, 4, true));
        lblHuella.setFont(util.Estilos.FUENTE);
        lblHuella.setForeground(new Color(90, 110, 115));
        lblHuella.setPreferredSize(new Dimension(190, 190));
        lblHuella.setMinimumSize(new Dimension(190, 190));
        lblHuella.setHorizontalTextPosition(SwingConstants.CENTER);
        lblHuella.setVerticalTextPosition(SwingConstants.BOTTOM);

        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEstado.setForeground(GRIS);
        botonRepetir.setPreferredSize(new Dimension(200, 36));
        botonRepetir.setFocusable(false);

        pnlEstudiante.setBackground(new Color(247, 249, 250));
        pnlEstudiante.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(util.Estilos.BORDE, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombre.setForeground(util.Estilos.TEXTO);
        for (JLabel l : new JLabel[]{lblDatos, lblActivos}) {
            l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            l.setForeground(GRIS);
        }
        lblEstadoEstudiante.setOpaque(true);
        lblEstadoEstudiante.setHorizontalAlignment(SwingConstants.CENTER);
        lblEstadoEstudiante.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEstadoEstudiante.setBackground(new Color(225, 230, 233));
        lblEstadoEstudiante.setForeground(GRIS);
        lblNombre.setPreferredSize(new Dimension(10, 24));
        lblDatos.setPreferredSize(new Dimension(10, 40));
        lblActivos.setPreferredSize(new Dimension(10, 20));
        lblEstadoEstudiante.setPreferredSize(new Dimension(10, 40));
        pnlEstudiante.add(lblNombre);
        pnlEstudiante.add(lblDatos);
        pnlEstudiante.add(lblActivos);
        pnlEstudiante.add(lblEstadoEstudiante);

        lblDedo.setFont(util.Estilos.FUENTE);
        cbxDedo.setFont(util.Estilos.FUENTE);
        cbxDedo.setPreferredSize(new Dimension(100, 32));
        cbxDedo.setSelectedItem(modelo.Dedo.INDICE_DERECHO);
        barMuestras.setStringPainted(true);
        barMuestras.setString("0 de 3 muestras");
        barMuestras.setForeground(util.Estilos.VERDE);
        barMuestras.setPreferredSize(new Dimension(100, 20));
        util.Estilos.barra(barMuestras);
        barMuestras.setStringPainted(true);
        txtInfo.setText(" ");
        txtInfo.setEditable(false);
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        txtInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtInfo.setBackground(new Color(247, 249, 250));
        txtInfo.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        scrInfo.setBorder(BorderFactory.createLineBorder(util.Estilos.BORDE));
        scrInfo.setPreferredSize(new Dimension(100, 70));

        int fila = 0;
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.NONE;
        g.gridy = fila++; g.insets = new Insets(6, 6, 10, 6);  add(lblHuella, g);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridy = fila++; g.insets = new Insets(0, 6, 4, 6);   add(lblDedo, g);
        g.gridy = fila++; g.insets = new Insets(0, 6, 8, 6);   add(cbxDedo, g);
        g.gridy = fila++; g.insets = new Insets(0, 6, 8, 6);   add(barMuestras, g);
        g.gridy = fila++; g.insets = new Insets(0, 6, 8, 6);   add(lblEstado, g);
        g.gridy = fila++; g.insets = new Insets(0, 6, 8, 6);   add(scrInfo, g);
        g.gridy = fila++; g.insets = new Insets(0, 6, 14, 6);  add(botonRepetir, g);
        g.gridy = fila++; g.insets = new Insets(0, 6, 0, 6);   add(pnlEstudiante, g);

        g.gridy = fila;
        g.weighty = 1;
        g.fill = GridBagConstraints.BOTH;
        JPanel relleno = new JPanel();
        relleno.setOpaque(false);
        relleno.setPreferredSize(new Dimension(0, 0));
        add(relleno, g);
    }

    public boolean isModoRegistro() { return modoRegistro; }

    public void setModoRegistro(boolean modoRegistro) {
        this.modoRegistro = modoRegistro;
        lblDedo.setVisible(modoRegistro);
        cbxDedo.setVisible(modoRegistro);
        barMuestras.setVisible(modoRegistro);
        scrInfo.setVisible(modoRegistro);
        pnlEstudiante.setVisible(!modoRegistro);
        lblEstado.setVisible(!modoRegistro);
        botonRepetir.setVisible(false);
        revalidate();
        repaint();
    }

    private static final Color FONDO_NORMAL = new Color(242, 249, 248);
    private static final Color FONDO_EXITO = new Color(212, 238, 235);
    private static final Color FONDO_FALLO = new Color(253, 241, 232);

    @Override
    public void avisar(int muestrasListas, String mensaje) {
        javax.swing.SwingUtilities.invokeLater(() -> mostrarAvance(muestrasListas, mensaje));
    }

    @Override
    public void imagen(java.awt.image.BufferedImage huella) {
        javax.swing.SwingUtilities.invokeLater(() -> mostrarImagen(huella));
    }

    public void mostrarImagen(java.awt.image.BufferedImage huella) {
        int lado = 150;
        double escala = Math.min((double) lado / huella.getWidth(), (double) lado / huella.getHeight());
        java.awt.Image chica = huella.getScaledInstance((int) (huella.getWidth() * escala),
                (int) (huella.getHeight() * escala), java.awt.Image.SCALE_SMOOTH);
        lblHuella.setIcon(new javax.swing.ImageIcon(chica));
        lblHuella.setText("");
        lblHuella.setBackground(Color.WHITE);
    }

    public void reiniciar() {
        lblHuella.setIcon(null);
        pintarHuella("Coloque el dedo", FONDO_NORMAL, new Color(90, 110, 115));
        mostrarAvance(0, modoRegistro ? " " : "Coloque el dedo en el lector.");
    }

    public void iniciarLectura() {
        lblHuella.setIcon(null);
        pintarHuella("Coloque el dedo", FONDO_NORMAL, util.Estilos.VERDE_TITULO);
    }

    public void mostrarRepetir(boolean visible) {
        botonRepetir.setVisible(visible);
        revalidate();
        repaint();
    }

    public void mostrarAvance(int muestras, String mensaje) {
        barMuestras.setValue(muestras);
        barMuestras.setString(muestras + " de 3 muestras");
        txtInfo.setForeground(util.Estilos.TEXTO);
        txtInfo.setText(mensaje);
        lblEstado.setText(envolver(mensaje));
    }

    private static final Color ROJO_AVISO = new Color(198, 40, 40);

    public void mostrarAviso(String mensaje, boolean alerta) {
        lblHuella.setIcon(null);
        pintarHuella("Coloque el dedo", FONDO_NORMAL, new Color(90, 110, 115));
        barMuestras.setValue(0);
        barMuestras.setString("0 de 3 muestras");
        txtInfo.setForeground(alerta ? ROJO_AVISO : util.Estilos.TEXTO);
        txtInfo.setText(mensaje);
        lblEstado.setText(envolver(mensaje));
    }

    public void mostrarResultado(String texto, boolean exito) {
        pintarHuella(texto, exito ? FONDO_EXITO : FONDO_FALLO,
                exito ? util.Estilos.VERDE_TITULO : new Color(138, 58, 18));
    }

    public void mostrarLector(biometria.EstadoLector estado) {
        lblHuella.setIcon(null);
        if (estado.permiteCapturar()) {
            pintarHuella("Coloque el dedo", FONDO_NORMAL, new Color(90, 110, 115));
        } else {
            pintarHuella("Lector " + estado.getDescripcion().toLowerCase(), FONDO_FALLO, new Color(138, 58, 18));
        }
    }

    private static final Color FONDO_OK = new Color(212, 238, 235);
    private static final Color FONDO_NO = new Color(253, 241, 232);

    public void mostrarEstudiante(String nombre, String datos, String activos, String estado, boolean ok) {
        lblNombre.setText(nombre);
        lblNombre.setToolTipText(nombre);
        lblDatos.setText("<html>" + datos.replace("\n", "<br>") + "</html>");
        lblDatos.setToolTipText(datos);
        lblActivos.setText(activos);
        lblEstadoEstudiante.setText("<html><div style='text-align:center'>" + estado + "</div></html>");
        lblEstadoEstudiante.setBackground(ok ? FONDO_OK : FONDO_NO);
        lblEstadoEstudiante.setForeground(ok ? util.Estilos.VERDE_TITULO : new Color(138, 58, 18));
    }

    public void limpiarEstudiante() {
        lblNombre.setText("—");
        lblDatos.setText("<html>Cédula: —<br>Carrera: —</html>");
        lblActivos.setText("Préstamos activos: —");
        lblEstadoEstudiante.setText("SIN IDENTIFICAR");
        lblEstadoEstudiante.setBackground(new Color(225, 230, 233));
        lblEstadoEstudiante.setForeground(GRIS);
    }

    public void setInfo(String texto) {
        txtInfo.setForeground(util.Estilos.TEXTO);
        txtInfo.setText(texto);
        lblEstado.setText(envolver(texto));
    }

    private static String envolver(String texto) {
        return "<html><div style='text-align:center;width:150px'>" + texto + "</div></html>";
    }

    private void pintarHuella(String texto, Color fondo, Color letra) {
        lblHuella.setText("<html><div style='text-align:center'>" + texto + "</div></html>");
        lblHuella.setBackground(fondo);
        lblHuella.setForeground(letra);
    }

    public JLabel getLblHuella() { return lblHuella; }
    public JLabel getLblEstado() { return lblEstado; }
    public BotonSistema getBotonRepetir() { return botonRepetir; }
    public JLabel getLblNombre() { return lblNombre; }
    public JLabel getLblDatos() { return lblDatos; }
    public JLabel getLblActivos() { return lblActivos; }
    public JLabel getLblEstadoEstudiante() { return lblEstadoEstudiante; }
    public JComboBox<modelo.Dedo> getCbxDedo() { return cbxDedo; }
    public JProgressBar getBarMuestras() { return barMuestras; }
    public JTextArea getTxtInfo() { return txtInfo; }
}
