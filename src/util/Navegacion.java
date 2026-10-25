package util;

import javax.swing.*;

/** Abre y cierra las ventanas del sistema (oculta o vuelve a mostrar la anterior). */
public class Navegacion {

    public static void abrir(JFrame origen, JFrame destino) {
        destino.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        destino.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                origen.setVisible(true);
            }
        });
        destino.setLocationRelativeTo(origen);
        destino.setVisible(true);
        origen.setVisible(false);
    }

    public static void abrir(JFrame origen, JFrame destino, Runnable alVolver) {
        destino.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                alVolver.run();
            }
        });
        abrir(origen, destino);
    }

    public static void cambiar(JFrame origen, JFrame destino) {
        destino.setLocationRelativeTo(origen);
        destino.setVisible(true);
        origen.dispose();
    }
}