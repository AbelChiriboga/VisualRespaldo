package componentes;

import java.awt.*;
import javax.swing.*;

/** Tarjeta con título pequeño, número grande y una línea de detalle opcional. */
public class TarjetaIndicador extends JPanel {

    private static final Color ROJO = new Color(198, 40, 40);
    private static final Color GRIS_TEXTO = new Color(80, 90, 100);

    private final JLabel lblTitulo = new JLabel("Título");
    private final JLabel lblValor = new JLabel("—");
    private final JLabel lblDetalle = new JLabel(" ");

    private boolean alerta = false;
    private boolean centrado = false;

    public TarjetaIndicador() {
        super(new BorderLayout(0, 2));
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitulo.setForeground(new Color(80, 90, 100));
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDetalle.setForeground(new Color(80, 90, 100));
        add(lblTitulo, BorderLayout.NORTH);
        add(lblValor, BorderLayout.CENTER);
        add(lblDetalle, BorderLayout.SOUTH);
        aplicarColores();
    }

    public TarjetaIndicador(String titulo) {
        this();
        setTitulo(titulo);
    }

    public String getTitulo() { return lblTitulo.getText(); }
    public void setTitulo(String titulo) { lblTitulo.setText(titulo); }

    public String getValor() { return lblValor.getText(); }
    public void setValor(String valor) { lblValor.setText(valor); }

    public String getDetalle() { return lblDetalle.getText(); }
    public void setDetalle(String detalle) {
        lblDetalle.setText(detalle == null || detalle.isEmpty() ? " " : detalle);
    }

    public boolean isAlerta() { return alerta; }
    public void setAlerta(boolean alerta) {
        this.alerta = alerta;
        aplicarColores();
    }

    public boolean isCentrado() { return centrado; }
    public void setCentrado(boolean centrado) {
        this.centrado = centrado;
        int a = centrado ? SwingConstants.CENTER : SwingConstants.LEFT;
        lblTitulo.setHorizontalAlignment(a);
        lblValor.setHorizontalAlignment(a);
        lblDetalle.setHorizontalAlignment(a);
    }

    private void aplicarColores() {
        Color borde = alerta ? ROJO : util.Estilos.VERDE;
        setBackground(alerta ? ROJO : Color.WHITE);
        lblValor.setForeground(alerta ? Color.WHITE : util.Estilos.VERDE_TITULO);
        lblTitulo.setForeground(alerta ? Color.WHITE : GRIS_TEXTO);
        lblDetalle.setForeground(alerta ? Color.WHITE : GRIS_TEXTO);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
    }
}
