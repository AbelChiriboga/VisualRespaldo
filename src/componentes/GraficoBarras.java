package componentes;

import java.awt.*;
import javax.swing.JPanel;

/** Gráfico de barras agrupadas (éxitos y fallos por día), sin librerías externas. */
public class GraficoBarras extends JPanel {

    private static final Color NARANJA = new Color(204, 90, 30);
    private static final Color GRIS = new Color(90, 106, 116);
    private static final Color LINEA = new Color(227, 232, 235);

    private String[] etiquetas = {};
    private int[] exitos = {};
    private int[] fallos = {};

    public GraficoBarras() {
        setBackground(Color.WHITE);
    }

    public void setDatos(String[] etiquetas, int[] exitos, int[] fallos) {
        this.etiquetas = etiquetas;
        this.exitos = exitos;
        this.fallos = fallos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        FontMetrics fm = g.getFontMetrics();

        int w = getWidth(), h = getHeight();
        int izq = 36, der = 10, arriba = 30, abajo = 26;
        int ancho = w - izq - der;
        int alto = h - arriba - abajo;

        leyenda(g, izq);

        int max = 1;
        for (int i = 0; i < exitos.length; i++) {
            max = Math.max(max, Math.max(exitos[i], fallos[i]));
        }
        max = ((max + 4) / 5) * 5;

        for (int i = 0; i <= 5; i++) {
            int y = arriba + alto - alto * i / 5;
            g.setColor(LINEA);
            g.drawLine(izq, y, w - der, y);
            String v = String.valueOf(max * i / 5);
            g.setColor(GRIS);
            g.drawString(v, izq - 6 - fm.stringWidth(v), y + 4);
        }

        int n = etiquetas.length;
        if (n == 0) {
            String msg = "Sin datos para el periodo seleccionado";
            g.setColor(GRIS);
            g.drawString(msg, izq + (ancho - fm.stringWidth(msg)) / 2, arriba + alto / 2);
            g.dispose();
            return;
        }

        double grupo = (double) ancho / n;
        int barra = (int) Math.max(4, grupo * 0.3);
        for (int i = 0; i < n; i++) {
            int x = (int) (izq + i * grupo + grupo * 0.18);
            int h1 = alto * exitos[i] / max;
            int h2 = alto * fallos[i] / max;
            g.setColor(util.Estilos.VERDE);
            g.fillRoundRect(x, arriba + alto - h1, barra, h1, 4, 4);
            g.setColor(NARANJA);
            g.fillRoundRect(x + barra + 4, arriba + alto - h2, barra, h2, 4, 4);
            g.setColor(GRIS);
            g.drawString(etiquetas[i], x + barra + 2 - fm.stringWidth(etiquetas[i]) / 2, h - 8);
        }
        g.dispose();
    }

    private void leyenda(Graphics2D g, int x) {
        g.setColor(util.Estilos.VERDE);
        g.fillRoundRect(x, 6, 12, 12, 3, 3);
        g.setColor(GRIS);
        g.drawString("Éxitos", x + 18, 17);
        g.setColor(NARANJA);
        g.fillRoundRect(x + 80, 6, 12, 12, 3, 3);
        g.setColor(GRIS);
        g.drawString("Fallos", x + 98, 17);
    }
}
