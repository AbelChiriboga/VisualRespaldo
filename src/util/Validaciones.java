package util;

import excepciones.ValidacionException;
import java.math.BigDecimal;

/** Validaciones reutilizables. */
public final class Validaciones {

    private Validaciones() {
    }

    public static String obligatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionException(nombre(campo) + " es obligatorio.");
        }
        return valor.trim();
    }

    private static String nombre(String campo) {
        return campo == null || campo.isEmpty() ? "El dato" : Character.toUpperCase(campo.charAt(0)) + campo.substring(1);
    }

    public static String opcional(String valor) {
        return (valor == null || valor.trim().isEmpty()) ? null : valor.trim();
    }

    public static String maximo(String valor, int max, String campo) {
        if (valor != null && valor.length() > max) {
            throw new ValidacionException(nombre(campo) + " admite máximo " + max + " caracteres.");
        }
        return valor;
    }

    public static String formatoCedula(String cedula) {
        String c = obligatorio(cedula, "cédula");
        if (!c.matches("\\d{10}")) {
            throw new ValidacionException("La cédula debe tener 10 dígitos.");
        }
        return c;
    }

    public static String cedulaEcuatoriana(String cedula) {
        String c = formatoCedula(cedula);
        int provincia = Integer.parseInt(c.substring(0, 2));
        if ((provincia < 1 || provincia > 24) && provincia != 30) {
            throw new ValidacionException("La cédula no es válida.");
        }
        if (c.charAt(2) - '0' >= 6) {
            throw new ValidacionException("La cédula no es válida.");
        }
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int d = (c.charAt(i) - '0') * (i % 2 == 0 ? 2 : 1);
            suma += d > 9 ? d - 9 : d;
        }
        int verificador = (10 - suma % 10) % 10;
        if (verificador != c.charAt(9) - '0') {
            throw new ValidacionException("La cédula no es válida.");
        }
        return c;
    }

    public static String correo(String correo) {
        String c = opcional(correo);
        if (c != null && !c.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new ValidacionException("El correo electrónico no es válido.");
        }
        return c;
    }

    public static String telefono(String telefono) {
        String t = opcional(telefono);
        if (t != null && !t.matches("\\d{7,10}")) {
            throw new ValidacionException("El teléfono debe tener entre 7 y 10 dígitos.");
        }
        return t;
    }

    public static int entero(String valor, String campo, int min, int max) {
        String v = obligatorio(valor, campo);
        try {
            return rango(Integer.parseInt(v), campo, min, max);
        } catch (NumberFormatException e) {
            throw new ValidacionException(nombre(campo) + " debe ser un número entero.");
        }
    }

    public static Integer enteroOpcional(String valor, String campo, int min, int max) {
        return opcional(valor) == null ? null : entero(valor, campo, min, max);
    }

    public static int rango(int valor, String campo, int min, int max) {
        if (valor < min || valor > max) {
            throw new ValidacionException(nombre(campo) + " debe estar entre " + min + " y " + max + ".");
        }
        return valor;
    }

    public static BigDecimal decimal(String valor, String campo) {
        String v = obligatorio(valor, campo).replace(',', '.');
        try {
            return noNegativo(new BigDecimal(v), campo);
        } catch (NumberFormatException e) {
            throw new ValidacionException(nombre(campo) + " debe ser un número.");
        }
    }

    public static BigDecimal noNegativo(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() < 0) {
            throw new ValidacionException(nombre(campo) + " no puede ser negativo.");
        }
        return valor;
    }

    public static <T> T requerido(T valor, String campo) {
        if (valor == null) {
            throw new ValidacionException("Debe seleccionar " + campo + ".");
        }
        return valor;
    }
}
