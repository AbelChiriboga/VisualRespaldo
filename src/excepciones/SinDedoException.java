package excepciones;

/** Se venció el tiempo y nadie colocó el dedo en el lector. */
public class SinDedoException extends LectorException {

    public SinDedoException() {
        super("No se detectó el dedo. Intente de nuevo.");
    }

    @Override
    public Reaccion getReaccion() {
        return Reaccion.REINTENTAR;
    }

    @Override
    public boolean esIntentoConDedo() {
        return false;
    }
}
