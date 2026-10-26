package biometria;

/** Lo usa el lector para avisar el avance del registro (se usa con una lambda). */
@FunctionalInterface
public interface AvisoCaptura {

    void avisar(int muestrasListas, String mensaje);

    default void imagen(java.awt.image.BufferedImage huella) {
    }
}
