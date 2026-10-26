package biometria;

import excepciones.LectorException;

/** Lo común a cualquier lector (clase ABSTRACTA + método plantilla). */
public abstract class LectorBase implements LectorBiometrico {

    public static final int MUESTRAS_REGISTRO = 3;

    protected abstract boolean mismoDedo(byte[] a, byte[] b);

    protected abstract byte[] fusionar(byte[][] muestras);

    @Override
    public byte[] registrar(AvisoCaptura aviso) {
        exigirAbierto();
        byte[][] muestras = new byte[MUESTRAS_REGISTRO][];
        for (int i = 0; i < MUESTRAS_REGISTRO; i++) {
            aviso.avisar(i, "Coloque el dedo (" + (i + 1) + " de " + MUESTRAS_REGISTRO + ").");
            muestras[i] = capturar();
            getUltimaImagen().ifPresent(aviso::imagen);
            if (i > 0 && !mismoDedo(muestras[i - 1], muestras[i])) {
                aviso.avisar(i, "Las muestras no coinciden.");
                throw new LectorException("Las muestras no son del mismo dedo. Intente de nuevo.");
            }
            aviso.avisar(i + 1, i + 1 < MUESTRAS_REGISTRO ? "Levante el dedo y colóquelo otra vez."
                    : "Procesando huella...");
        }
        return fusionar(muestras);
    }

    protected void exigirAbierto() {
        if (!estaAbierto()) {
            throw new LectorException("Lector desconectado.");
        }
    }
}
