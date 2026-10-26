package servicio;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import modelo.*;

/** Cálculos del panel de la Bitácora a partir de los intentos ya filtrados: totales, tasa de éxito, intentos de. */
public final class ResumenBitacora {

    private static final int DIAS_GRAFICO = 14;

    private final List<RegistroAutenticacion> registros;
    private final LocalDate desde;
    private final LocalDate hasta;

    public ResumenBitacora(List<RegistroAutenticacion> registros, LocalDate desde, LocalDate hasta) {
        this.registros = List.copyOf(registros);
        this.desde = desde;
        this.hasta = hasta;
    }

    public List<RegistroAutenticacion> getRegistros() {
        return registros;
    }

    public int getTotal() {
        return registros.size();
    }

    public int getExitos() {
        return contar(ResumenBitacora::esExito);
    }

    public int getFallos() {
        return getTotal() - getExitos();
    }

    public int getTasaExito() {
        return porcentaje(getExitos(), getTotal());
    }

    public int getIntentosHoy() {
        return contar(ResumenBitacora::esDeHoy);
    }

    public int getFallosHoy() {
        return contar(esDeHoy().and(r -> !esExito(r)));
    }

    public OptionalDouble getPuntajeAceptados() {
        return promedio(ResumenBitacora::esExito);
    }

    public OptionalDouble getPuntajeRechazados() {
        return promedio(r -> !esExito(r));
    }

    public int getTotal(OperacionAutenticacion op) {
        return contar(r -> r.getOperacion() == op);
    }

    public int getTasaExito(OperacionAutenticacion op) {
        return porcentaje(contar(r -> r.getOperacion() == op && esExito(r)), getTotal(op));
    }

    public List<LocalDate> getDiasGrafico() {
        LocalDate fin = hasta != null ? hasta : LocalDate.now();
        LocalDate inicio = desde != null && !desde.isBefore(fin.minusDays(DIAS_GRAFICO - 1))
                ? desde : fin.minusDays(DIAS_GRAFICO - 1);
        return inicio.datesUntil(fin.plusDays(1)).collect(java.util.stream.Collectors.toList());
    }

    public String[] getEtiquetasGrafico() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM");
        return getDiasGrafico().stream().map(f::format).toArray(String[]::new);
    }

    public int[] getExitosPorDia() {
        return getDiasGrafico().stream().mapToInt(d -> contar(r -> esDelDia(r, d) && esExito(r))).toArray();
    }

    public int[] getFallosPorDia() {
        return getDiasGrafico().stream().mapToInt(d -> contar(r -> esDelDia(r, d) && !esExito(r))).toArray();
    }

    private int contar(Predicate<RegistroAutenticacion> condicion) {
        return (int) registros.stream().filter(condicion).count();
    }

    private OptionalDouble promedio(Predicate<RegistroAutenticacion> condicion) {
        return registros.stream().filter(condicion).filter(r -> r.getPuntaje() != null)
                .mapToInt(RegistroAutenticacion::getPuntaje).average();
    }

    private static boolean esExito(RegistroAutenticacion r) {
        return r.getResultado() == ResultadoAutenticacion.EXITO;
    }

    private static boolean esDeHoy(RegistroAutenticacion r) {
        return esDelDia(r, LocalDate.now());
    }

    private static Predicate<RegistroAutenticacion> esDeHoy() {
        return ResumenBitacora::esDeHoy;
    }

    private static boolean esDelDia(RegistroAutenticacion r, LocalDate dia) {
        return r.getFechaHora() != null && r.getFechaHora().toLocalDate().equals(dia);
    }

    private static int porcentaje(int parte, int total) {
        return total == 0 ? 0 : Math.round(parte * 100f / total);
    }
}
