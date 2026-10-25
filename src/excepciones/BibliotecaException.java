package excepciones;

/** Padre de las excepciones propias del sistema. */
public abstract class BibliotecaException extends RuntimeException {

    protected BibliotecaException(String mensaje) {
        super(mensaje);
    }

    protected BibliotecaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public abstract String getTitulo();

    public boolean esErrorTecnico() {
        return false;
    }

    public Reaccion getReaccion() {
        return Reaccion.MOSTRAR_ERROR;
    }
}
