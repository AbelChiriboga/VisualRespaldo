package biblioteca;

import javax.swing.JOptionPane;

/** Punto de entrada: aplica el estilo visual, vigila el lector y abre el Login. */
public class Main {

    public static void main(String[] args) {
        aplicarEstilo();
        iniciarMonitorLector();

        javax.swing.SwingUtilities.invokeLater(() -> {
            new vista.Login().setVisible(true);
        });
    }

    private static void iniciarMonitorLector() {
        biometria.MonitorLector monitor = biometria.MonitorLector.getInstancia();
        monitor.alCambiar(Main::avisarCambioLector);
        monitor.iniciar();
    }

    private static void avisarCambioLector(biometria.EstadoLector estado) {
        componentes.Aviso.mostrar(estado.permiteCapturar() ? "Lector conectado" : "Lector desconectado",
                estado.permiteCapturar());
    }

    private static void aplicarEstilo() {
        try {
            javax.swing.UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
        util.Estilos.textoDeshabilitadoLegible();
    }
}