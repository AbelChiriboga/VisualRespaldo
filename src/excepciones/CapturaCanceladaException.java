package excepciones;

/** La lectura se detuvo antes de leer un dedo (se cambió de dedo, se pulsó Cancelar o se cerró la ventana). */
public class CapturaCanceladaException extends LectorException {

    public CapturaCanceladaException() {
        super("Captura cancelada.");
    }

    @Override
    public boolean esIntentoConDedo() {
        return false;
    }
}
