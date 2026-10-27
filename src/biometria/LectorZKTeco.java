package biometria;

import com.zkteco.biometric.*;
import excepciones.LectorException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/** Lector USB ZKTeco usando el SDK ZKFinger (ZKFingerReader.jar + libzkfp.dll del driver). */
public class LectorZKTeco extends LectorBase {

    private static final int TAMANO_PLANTILLA = 2048;
    private static final int ESPERA_DEDO_MS = 15_000;
    private static final int ESPERA_LEVANTAR_MS = 5_000;
    private static final int PAUSA_MS = 150;
    private static final int PARAM_ANCHO = 1;
    private static final int PARAM_ALTO = 2;
    private static final int REVISION_COMPLETA_MS = 4_000;
    private static final java.util.Set<Integer> LECTOR_RESPONDE = java.util.Set.of(
            0,
            FingerprintSensorErrorCode.ZKFP_ERR_CAPTURE,
            FingerprintSensorErrorCode.ZKFP_ERR_EXTRACT_FP,
            FingerprintSensorErrorCode.ZKFP_ERR_ABSORT,
            FingerprintSensorErrorCode.ZKFP_ERR_BUSY,
            FingerprintSensorErrorCode.ZKFP_ERR_TIMEOUT);

    private final ReentrantLock candado = new ReentrantLock();
    private long dispositivo;
    private long baseMemoria;
    private byte[] imagen;
    private int ancho;
    private int alto;
    private volatile java.awt.image.BufferedImage ultimaImagen;
    private boolean driverInstalado = true;
    private volatile EstadoLector ultimoEstado = EstadoLector.DESCONECTADO;
    private long proximaRevisionCompleta;
    private int ultimoCodigo = Integer.MIN_VALUE;
    private volatile boolean cancelada;

    @Override
    public String getNombre() {
        return "ZKTeco";
    }

    @Override
    public void abrir() {
        candado.lock();
        try {
            abrirDispositivo();
        } finally {
            candado.unlock();
        }
    }

    @Override
    public void cerrar() {
        candado.lock();
        try {
            liberarDispositivo();
        } finally {
            candado.unlock();
        }
    }

    @Override
    public boolean estaAbierto() {
        return dispositivo != 0 && baseMemoria != 0;
    }

    @Override
    public EstadoLector verificarEstado() {
        if (!candado.tryLock()) {
            return ultimoEstado;
        }
        try {
            if (!driverInstalado) {
                return ultimoEstado = EstadoLector.SIN_DRIVER;
            }
            if (estaAbierto()) {
                revisarQueSigaConectado();
            } else {
                intentarAbrir();
            }
            return ultimoEstado = estaAbierto() ? EstadoLector.CONECTADO
                    : driverInstalado ? EstadoLector.DESCONECTADO : EstadoLector.SIN_DRIVER;
        } finally {
            candado.unlock();
        }
    }

    @Override
    public byte[] capturar() {
        candado.lock();
        try {
            exigirAbierto();
            long limite = System.currentTimeMillis() + ESPERA_DEDO_MS;
            while (System.currentTimeMillis() < limite) {
                if (cancelada) {
                    throw new excepciones.CapturaCanceladaException();
                }
                byte[] plantilla = intentarCaptura();
                if (plantilla != null) {
                    ultimaImagen = copiarImagen();
                    esperarQueLevante();
                    return plantilla;
                }
                exigirConectado();
                pausa();
            }
            throw new excepciones.SinDedoException();
        } finally {
            candado.unlock();
        }
    }

    @Override
    public Optional<java.awt.image.BufferedImage> getUltimaImagen() {
        return Optional.ofNullable(ultimaImagen);
    }

    private java.awt.image.BufferedImage copiarImagen() {
        if (ancho <= 0 || alto <= 0) {
            return null;
        }
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(ancho, alto,
                java.awt.image.BufferedImage.TYPE_BYTE_GRAY);
        img.getRaster().setDataElements(0, 0, ancho, alto, Arrays.copyOf(imagen, ancho * alto));
        return img;
    }

    @Override
    public void nuevaCaptura() {
        cancelada = false;
    }

    @Override
    public void cancelarCaptura() {
        cancelada = true;
    }

    @Override
    protected boolean mismoDedo(byte[] a, byte[] b) {
        candado.lock();
        try {
            exigirAbierto();
            return FingerprintSensorEx.DBMatch(baseMemoria, a, b) > 0;
        } finally {
            candado.unlock();
        }
    }

    @Override
    protected byte[] fusionar(byte[][] m) {
        candado.lock();
        try {
            exigirAbierto();
            byte[] plantilla = new byte[TAMANO_PLANTILLA];
            int[] largo = {TAMANO_PLANTILLA};
            if (FingerprintSensorEx.DBMerge(baseMemoria, m[0], m[1], m[2], plantilla, largo) != 0) {
                throw new LectorException("No se pudo registrar la huella. Intente de nuevo.");
            }
            return Arrays.copyOf(plantilla, largo[0]);
        } finally {
            candado.unlock();
        }
    }

    @Override
    public void cargarPlantillas(Map<Integer, byte[]> plantillas) {
        candado.lock();
        try {
            exigirAbierto();
            FingerprintSensorEx.DBClear(baseMemoria);
            plantillas.forEach((id, plantilla) -> FingerprintSensorEx.DBAdd(baseMemoria, id, plantilla));
        } finally {
            candado.unlock();
        }
    }

    @Override
    public Optional<Coincidencia> identificar(byte[] muestra) {
        candado.lock();
        try {
            exigirAbierto();
            int[] id = new int[1];
            int[] puntaje = new int[1];
            int r = FingerprintSensorEx.DBIdentify(baseMemoria, muestra, id, puntaje);
            return r == 0 ? Optional.of(new Coincidencia(id[0], puntaje[0])) : Optional.empty();
        } finally {
            candado.unlock();
        }
    }

    private void abrirDispositivo() {
        if (estaAbierto()) {
            return;
        }
        try {
            FingerprintSensorEx.Terminate();
            if (FingerprintSensorEx.Init() != FingerprintSensorErrorCode.ZKFP_ERR_OK) {
                throw new LectorException("No se pudo iniciar el lector.", null);
            }
            if (FingerprintSensorEx.GetDeviceCount() <= 0) {
                FingerprintSensorEx.Terminate();
                throw new LectorException("Lector desconectado.");
            }
            dispositivo = FingerprintSensorEx.OpenDevice(0);
            if (dispositivo == 0) {
                FingerprintSensorEx.Terminate();
                throw new LectorException("El lector está siendo usado por otro programa.", null);
            }
            baseMemoria = FingerprintSensorEx.DBInit();
            if (baseMemoria == 0) {
                liberarDispositivo();
                throw new LectorException("No se pudo iniciar el lector.", null);
            }
            ancho = leerParametro(PARAM_ANCHO);
            alto = leerParametro(PARAM_ALTO);
            imagen = new byte[ancho * alto];
            ultimoCodigo = 0;
            proximaRevisionCompleta = System.currentTimeMillis() + REVISION_COMPLETA_MS;
        } catch (LinkageError e) {
            driverInstalado = false;
            throw new LectorException("Falta instalar el driver del lector.", e);
        }
    }

    private void intentarAbrir() {
        try {
            abrirDispositivo();
        } catch (LectorException e) {
        }
    }

    private void liberarDispositivo() {
        if (baseMemoria != 0) {
            FingerprintSensorEx.DBFree(baseMemoria);
            baseMemoria = 0;
        }
        if (dispositivo != 0) {
            FingerprintSensorEx.CloseDevice(dispositivo);
            dispositivo = 0;
        }
        if (driverInstalado) {
            FingerprintSensorEx.Terminate();
        }
    }

    private void revisarQueSigaConectado() {
        intentarCaptura();
        if (!LECTOR_RESPONDE.contains(ultimoCodigo)) {
            liberarDispositivo();
            return;
        }
        if (System.currentTimeMillis() >= proximaRevisionCompleta) {
            liberarDispositivo();
            intentarAbrir();
            if (!estaAbierto()) {
                pausa();
                intentarAbrir();
            }
        }
    }

    private void exigirConectado() {
        if (!LECTOR_RESPONDE.contains(ultimoCodigo)) {
            liberarDispositivo();
            ultimoEstado = EstadoLector.DESCONECTADO;
            throw new LectorException("Lector desconectado.");
        }
    }

    private byte[] intentarCaptura() {
        byte[] plantilla = new byte[TAMANO_PLANTILLA];
        int[] largo = {TAMANO_PLANTILLA};
        int r = FingerprintSensorEx.AcquireFingerprint(dispositivo, imagen, plantilla, largo);
        if (r != ultimoCodigo && !LECTOR_RESPONDE.contains(r)) {
            System.err.println("Lector ZKTeco respondió el código " + r);
        }
        ultimoCodigo = r;
        return r == 0 ? Arrays.copyOf(plantilla, largo[0]) : null;
    }

    private void esperarQueLevante() {
        long limite = System.currentTimeMillis() + ESPERA_LEVANTAR_MS;
        while (System.currentTimeMillis() < limite && intentarCaptura() != null) {
            pausa();
        }
    }

    private int leerParametro(int codigo) {
        byte[] valor = new byte[4];
        int[] tamano = {4};
        FingerprintSensorEx.GetParameters(dispositivo, codigo, valor, tamano);
        return (valor[0] & 0xFF) | (valor[1] & 0xFF) << 8 | (valor[2] & 0xFF) << 16 | (valor[3] & 0xFF) << 24;
    }

    private static void pausa() {
        try {
            Thread.sleep(PAUSA_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new excepciones.CapturaCanceladaException();
        }
    }
}
