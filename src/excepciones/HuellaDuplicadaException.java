package excepciones;

/** Al registrar: la huella capturada ya pertenece a OTRO estudiante. */
public class HuellaDuplicadaException extends NegocioException {

    public HuellaDuplicadaException(String nombreEstudiante) {
        super("Esta huella ya pertenece a " + nombreEstudiante + ". Use otro dedo.");
    }

    @Override
    public Reaccion getReaccion() {
        return Reaccion.ALERTA;
    }
}
