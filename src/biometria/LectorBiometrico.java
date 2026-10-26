package biometria;

import java.util.*;

/** Contrato de un lector de huella. */
public interface LectorBiometrico extends AutoCloseable {

    String getNombre();

    void abrir();

    void cerrar();

    boolean estaAbierto();

    EstadoLector verificarEstado();

    byte[] capturar();

    byte[] registrar(AvisoCaptura aviso);

    void cargarPlantillas(Map<Integer, byte[]> plantillas);

    Optional<Coincidencia> identificar(byte[] muestra);

    default Optional<java.awt.image.BufferedImage> getUltimaImagen() {
        return Optional.empty();
    }

    default void nuevaCaptura() {
    }

    default void cancelarCaptura() {
    }

    @Override
    default void close() {
        cerrar();
    }
}
