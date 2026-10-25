package excepciones;

/** Una regla de negocio impide la operación (máximo de préstamos, estudiante inactivo...). */
public class NegocioException extends BibliotecaException {

    public NegocioException(String mensaje) {
        super(mensaje);
    }

    public NegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    @Override
    public String getTitulo() {
        return "Operación no permitida";
    }
}
