package util;

import java.math.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/** Cómo se muestran fechas y dinero en todo el sistema (un solo lugar). */
public final class Formatos {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Formatos() {
    }

    public static String fecha(TemporalAccessor f) {
        return f == null ? "—" : FECHA.format(f);
    }

    public static String fechaHora(TemporalAccessor f) {
        return f == null ? "—" : FECHA_HORA.format(f);
    }

    public static java.time.LocalDate aLocalDate(java.util.Date fecha) {
        return fecha == null ? null : fecha.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    public static java.util.Date aDate(java.time.LocalDate fecha) {
        return fecha == null ? null : java.util.Date.from(fecha.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
    }

    public static String dinero(BigDecimal valor) {
        return "$" + (valor == null ? BigDecimal.ZERO : valor).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
