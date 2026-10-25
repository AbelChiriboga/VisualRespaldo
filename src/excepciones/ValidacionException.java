package excepciones;

/** Un dato ingresado no es válido (campo vacío, cédula incorrecta, correo mal escrito...). */
public class ValidacionException extends BibliotecaException {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }

    @Override
    public String getTitulo() {
        return "Dato no válido";
    }
}
