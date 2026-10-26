package biometria;

/** Entrega el lector que usa todo el sistema (una sola instancia: el dispositivo se abre una vez). */
public final class FabricaLector {

    public static final String PROPIEDAD = "biblioteca.lector";

    private static LectorBiometrico lector;

    private FabricaLector() {
    }

    public static synchronized LectorBiometrico obtener() {
        if (lector == null) {
            lector = "simulado".equalsIgnoreCase(System.getProperty(PROPIEDAD))
                    ? new LectorSimulado() : new LectorZKTeco();
            Runtime.getRuntime().addShutdownHook(new Thread(lector::cerrar));
        }
        return lector;
    }
}
