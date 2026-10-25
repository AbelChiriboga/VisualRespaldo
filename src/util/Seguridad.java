package util;

import excepciones.ValidacionException;
import java.nio.charset.StandardCharsets;
import java.security.*;

/** Contraseñas: nunca se guardan ni se comparan en texto plano. */
public final class Seguridad {

    public static final int LONGITUD_MINIMA = 6;

    private Seguridad() {
    }

    public static String sha256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(64);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    public static boolean coincide(String claveEscrita, String hashGuardado) {
        if (claveEscrita == null || hashGuardado == null) {
            return false;
        }
        return MessageDigest.isEqual(sha256(claveEscrita).getBytes(StandardCharsets.UTF_8),
                hashGuardado.toLowerCase().getBytes(StandardCharsets.UTF_8));
    }

    public static int fortaleza(String clave) {
        if (clave == null || clave.isEmpty()) {
            return 0;
        }
        int puntos = Math.min(clave.length(), 12) * 4;
        puntos += clave.matches(".*[a-z].*") ? 10 : 0;
        puntos += clave.matches(".*[A-Z].*") ? 14 : 0;
        puntos += clave.matches(".*\\d.*") ? 14 : 0;
        puntos += clave.matches(".*[^A-Za-z0-9].*") ? 14 : 0;
        return Math.min(puntos, 100);
    }

    public static String nivel(int fortaleza) {
        return fortaleza < 40 ? "Débil" : fortaleza < 70 ? "Media" : "Fuerte";
    }

    public static String validarLongitud(String clave) {
        String c = Validaciones.obligatorio(clave, "contraseña");
        if (c.length() < LONGITUD_MINIMA) {
            throw new ValidacionException("La contraseña debe tener al menos " + LONGITUD_MINIMA + " caracteres.");
        }
        return c;
    }

    public static String validarNueva(String clave, String repetida) {
        String c = validarLongitud(clave);
        if (!c.equals(repetida)) {
            throw new ValidacionException("Las contraseñas no coinciden.");
        }
        return c;
    }
}
