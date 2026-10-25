package util;

import java.util.Locale;
import javax.swing.text.*;

/** Qué caracteres acepta una caja de texto mientras se escribe (cada tipo decide su regla). */
public enum TipoEntrada {

    NUMEROS {
        @Override
        protected boolean admite(char c) {
            return Character.isDigit(c);
        }
    },
    TEXTO {
        @Override
        protected boolean admite(char c) {
            return Character.isLetter(c) || c == ' ';
        }

        @Override
        protected String ajustar(String texto) {
            return texto.toUpperCase(ES);
        }
    },
    CORREO {
        @Override
        protected boolean admite(char c) {
            return Character.isLetterOrDigit(c) || c == '@' || c == '.';
        }
    },
    DECIMAL {
        @Override
        protected boolean admite(char c) {
            return Character.isDigit(c) || c == '.';
        }

        @Override
        public String filtrar(String actual, String nuevo) {
            String limpio = super.filtrar(actual, nuevo.replace(',', '.'));
            return actual.contains(".") ? limpio.replace(".", "") : soloPrimerPunto(limpio);
        }
    },
    USUARIO {
        @Override
        protected boolean admite(char c) {
            return Character.isLetterOrDigit(c) || c == '.' || c == '_' || c == '-';
        }
    },
    ISBN {
        private static final int MAX_DIGITOS = 13;

        @Override
        protected boolean admite(char c) {
            return Character.isDigit(c) || c == '-' || c == 'X' || c == 'x';
        }

        @Override
        protected String ajustar(String texto) {
            return texto.toUpperCase(ES);
        }

        /** Los guiones no cuentan: deja escribir hasta 13 dígitos (la X cuenta como dígito). */
        @Override
        public String filtrar(String actual, String nuevo) {
            int libres = MAX_DIGITOS - (int) actual.chars().filter(c -> c != '-').count();
            StringBuilder sb = new StringBuilder();
            for (char c : super.filtrar(actual, nuevo).toCharArray()) {
                if (c == '-') {
                    sb.append(c);
                } else if (libres > 0) {
                    sb.append(c);
                    libres--;
                }
            }
            return sb.toString();
        }
    },
    /** Texto libre (títulos, direcciones, búsquedas...): cualquier carácter, en MAYÚSCULAS. */
    LIBRE {
        @Override
        protected boolean admite(char c) {
            return !Character.isISOControl(c);
        }

        @Override
        protected String ajustar(String texto) {
            return texto.toUpperCase(ES);
        }
    },
    /** Contraseñas: cualquier carácter, tal como se escribe (no se cambia a mayúsculas). */
    CLAVE {
        @Override
        protected boolean admite(char c) {
            return !Character.isISOControl(c);
        }
    };

    private static final Locale ES = new Locale("es", "EC");

    protected abstract boolean admite(char c);

    protected String ajustar(String texto) {
        return texto;
    }

    /** Deja solo lo permitido de lo que se va a escribir; {@code actual} es el resto del texto de la caja. */
    public String filtrar(String actual, String nuevo) {
        StringBuilder sb = new StringBuilder();
        for (char c : nuevo.toCharArray()) {
            if (admite(c)) {
                sb.append(c);
            }
        }
        return ajustar(sb.toString());
    }

    /** Aplica este tipo y el máximo de caracteres (el de la base de datos) a una caja de texto. */
    public void aplicar(JTextComponent caja, int maximo) {
        ((AbstractDocument) caja.getDocument()).setDocumentFilter(new FiltroEntrada(this, maximo));
    }

    private static String soloPrimerPunto(String s) {
        int punto = s.indexOf('.');
        return punto < 0 ? s : s.substring(0, punto + 1) + s.substring(punto + 1).replace(".", "");
    }
}
