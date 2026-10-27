package reportes;

import java.awt.*;
import java.util.List;
import javax.swing.*;

/** Ventana donde se eligen los filtros del reporte antes de generarlo. */
public class DialogoReporte extends JDialog {

    private boolean aceptado;

    private DialogoReporte(Window padre, String titulo, List<FiltroReporte> filtros) {
        super(padre, titulo, ModalityType.APPLICATION_MODAL);
        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(Color.WHITE);
        campos.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(6, 4, 6, 12);
        for (int i = 0; i < filtros.size(); i++) {
            JLabel etiqueta = new JLabel(filtros.get(i).getEtiqueta());
            etiqueta.setFont(util.Estilos.FUENTE);
            g.gridx = 0;
            g.gridy = i;
            g.fill = GridBagConstraints.NONE;
            campos.add(etiqueta, g);
            g.gridx = 1;
            g.fill = GridBagConstraints.HORIZONTAL;
            campos.add(filtros.get(i).getComponente(), g);
        }

        componentes.BotonSistema generar = new componentes.BotonSistema("Generar");
        generar.setPrimario(true);
        componentes.BotonSistema cancelar = new componentes.BotonSistema("Cancelar");
        for (componentes.BotonSistema b : new componentes.BotonSistema[]{cancelar, generar}) {
            b.setPreferredSize(new java.awt.Dimension(120, 36));
        }
        generar.addActionListener(e -> cerrar(true));
        cancelar.addActionListener(e -> cerrar(false));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        botones.setBackground(Color.WHITE);
        botones.add(cancelar);
        botones.add(generar);

        getContentPane().setBackground(Color.WHITE);
        add(campos, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(generar);
        pack();
        setResizable(false);
        setLocationRelativeTo(padre);
    }

    private void cerrar(boolean generar) {
        aceptado = generar;
        dispose();
    }

    /** Muestra los filtros; devuelve true si el usuario presionó Generar. */
    public static boolean pedir(Window padre, String titulo, List<FiltroReporte> filtros) {
        DialogoReporte d = new DialogoReporte(padre, titulo, filtros);
        d.setVisible(true);
        return d.aceptado;
    }
}
