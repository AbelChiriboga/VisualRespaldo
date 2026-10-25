package excepciones;

/** Se leyó un dedo, pero no coincide con ninguna huella registrada. */
public class HuellaNoReconocidaException extends NegocioException {

    public HuellaNoReconocidaException() {
        super("Huella no reconocida. Intente de nuevo.");
    }

    @Override
    public Reaccion getReaccion() {
        return Reaccion.REINTENTAR_AVISANDO;
    }
}
