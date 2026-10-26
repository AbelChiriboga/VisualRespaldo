package biometria;

/** Estado del lector de huella. */
public enum EstadoLector {

    CONECTADO("Conectado", true),
    DESCONECTADO("Desconectado", false),
    SIN_DRIVER("Sin driver instalado", false),
    SIMULADO("Simulado", true);

    private final String descripcion;
    private final boolean permiteCapturar;

    EstadoLector(String descripcion, boolean permiteCapturar) {
        this.descripcion = descripcion;
        this.permiteCapturar = permiteCapturar;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean permiteCapturar() {
        return permiteCapturar;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
