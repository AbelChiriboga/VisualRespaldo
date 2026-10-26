package biometria;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

/** Vigila el lector de huella cada segundo, en un hilo aparte (no congela las ventanas). */
public final class MonitorLector {

    private static final MonitorLector INSTANCIA = new MonitorLector();
    private static final int INTERVALO_MS = 1_000;

    private final List<Consumer<EstadoLector>> oyentes = new CopyOnWriteArrayList<>();
    private final List<Consumer<EstadoLector>> avisos = new CopyOnWriteArrayList<>();
    private ScheduledExecutorService hilo;
    private volatile EstadoLector estado;

    private MonitorLector() {
    }

    public static MonitorLector getInstancia() {
        return INSTANCIA;
    }

    public synchronized void iniciar() {
        if (hilo != null) {
            return;
        }
        hilo = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "monitor-lector");
            t.setDaemon(true);
            return t;
        });
        hilo.scheduleWithFixedDelay(this::revisar, 0, INTERVALO_MS, TimeUnit.MILLISECONDS);
    }

    public EstadoLector getEstado() {
        return estado == null ? EstadoLector.DESCONECTADO : estado;
    }

    public void agregarOyente(Consumer<EstadoLector> oyente) {
        oyentes.add(oyente);
        if (estado != null) {
            oyente.accept(estado);
        }
    }

    public void quitarOyente(Consumer<EstadoLector> oyente) {
        oyentes.remove(oyente);
    }

    public void alCambiar(Consumer<EstadoLector> aviso) {
        avisos.add(aviso);
    }

    private void revisar() {
        try {
            EstadoLector nuevo = FabricaLector.obtener().verificarEstado();
            EstadoLector anterior = estado;
            if (nuevo != anterior) {
                estado = nuevo;
                SwingUtilities.invokeLater(() -> notificar(anterior, nuevo));
            }
        } catch (RuntimeException | LinkageError e) {
            System.err.println("Monitor del lector: " + e.getMessage());
        }
    }

    private void notificar(EstadoLector anterior, EstadoLector nuevo) {
        oyentes.forEach(o -> o.accept(nuevo));
        if (anterior != null) {
            avisos.forEach(a -> a.accept(nuevo));
        }
    }
}
