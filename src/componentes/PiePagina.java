package componentes;

import java.awt.*;
import javax.swing.*;

/** Barra verde inferior común (reemplaza el jPanel6 repetido en todos los frames). */
public class PiePagina extends JPanel {

    private final JLabel lblSistema = new JLabel("Sistema de Préstamos biométrico · FISEI");
    private final JLabel lblLector = new JLabel("Lector: verificando...");
    private final java.util.function.Consumer<biometria.EstadoLector> oyente = this::mostrarEstado;

    public PiePagina() {
        super(new BorderLayout());
        setBackground(util.Estilos.VERDE);
        setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        for (JLabel l : new JLabel[]{lblSistema, lblLector}) {
            l.setForeground(Color.WHITE);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        add(lblSistema, BorderLayout.WEST);
        add(lblLector, BorderLayout.EAST);
    }

    public void setTextoLector(String texto) {
        lblLector.setText("Lector: " + texto);
    }

    private void mostrarEstado(biometria.EstadoLector estado) {
        String color = estado.permiteCapturar() ? "#B9F6CA" : "#FFCC80";
        lblLector.setText("<html><span style='color:" + color + "'>&#9679;</span> Lector: <b>"
                + estado.getDescripcion() + "</b></html>");
    }

    @Override
    public void addNotify() {
        super.addNotify();
        biometria.MonitorLector.getInstancia().agregarOyente(oyente);
    }

    @Override
    public void removeNotify() {
        biometria.MonitorLector.getInstancia().quitarOyente(oyente);
        super.removeNotify();
    }
}
