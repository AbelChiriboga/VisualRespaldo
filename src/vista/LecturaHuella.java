package vista;

import excepciones.*;
import java.util.*;
import java.util.function.*;

/** Lectura AUTOMÁTICA de la huella en Préstamos y Devoluciones (sin botón): - mientras no hay estudiante. */
public class LecturaHuella {

    private static final int PAUSA_AVISO_MS = 1500;

    private final java.awt.Window ventana;
    private final componentes.PanelHuella panel;
    private final Supplier<servicio.FichaEstudiante> identificar;
    private final Consumer<servicio.FichaEstudiante> alIdentificar;
    private final Map<Reaccion, Consumer<BibliotecaException>> reacciones = new EnumMap<>(Reaccion.class);
    private servicio.FichaEstudiante ficha;
    private boolean leyendo;
    private boolean activa = true;

    public LecturaHuella(java.awt.Window ventana, componentes.PanelHuella panel,
                         Supplier<servicio.FichaEstudiante> identificar,
                         Consumer<servicio.FichaEstudiante> alIdentificar, Runnable cancelar) {
        this.ventana = ventana;
        this.panel = panel;
        this.identificar = identificar;
        this.alIdentificar = alIdentificar;
        reacciones.put(Reaccion.REINTENTAR, e -> escuchar());
        reacciones.put(Reaccion.REINTENTAR_AVISANDO, e -> avisarYReintentar(e.getMessage()));
        reacciones.put(Reaccion.DETENER_LECTURA, e -> panel.mostrarResultado(e.getMessage(), false));
        reacciones.put(Reaccion.ALERTA, e -> panel.mostrarResultado(e.getMessage(), false));
        reacciones.put(Reaccion.MOSTRAR_ERROR, e -> {
            panel.mostrarResultado("Sin identificar", false);
            util.Mensajes.error(ventana, e);
        });
        ventana.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                activa = false;
                cancelar.run();
            }
        });
        limpiar();
    }

    public servicio.FichaEstudiante getFicha() {
        return ficha;
    }

    public void limpiar() {
        ficha = null;
        panel.reiniciar();
        panel.limpiarEstudiante();
        escuchar();
    }

    public void lectorCambio(biometria.EstadoLector estado) {
        if (ficha == null && !leyendo) {
            panel.mostrarLector(estado);
        }
        if (estado.permiteCapturar()) {
            escuchar();
        }
    }

    private void escuchar() {
        if (!activa || leyendo || ficha != null
                || !biometria.MonitorLector.getInstancia().getEstado().permiteCapturar()) {
            return;
        }
        leyendo = true;
        panel.reiniciar();
        util.Tareas.enSegundoPlano(ventana, identificar, this::identificado, this::fallo, () -> leyendo = false);
    }

    private void identificado(servicio.FichaEstudiante f) {
        if (!activa) {
            return;
        }
        ficha = f;
        panel.mostrarResultado("Huella reconocida", true);
        panel.mostrarAvance(0, " ");
        alIdentificar.accept(f);
    }

    private void fallo(BibliotecaException e) {
        if (activa) {
            javax.swing.SwingUtilities.invokeLater(() -> reacciones.get(e.getReaccion()).accept(e));
        }
    }

    private void avisarYReintentar(String mensaje) {
        panel.mostrarResultado(mensaje, false);
        javax.swing.Timer espera = new javax.swing.Timer(PAUSA_AVISO_MS, ev -> escuchar());
        espera.setRepeats(false);
        espera.start();
    }
}
