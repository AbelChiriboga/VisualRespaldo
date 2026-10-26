package servicio;

import java.math.BigDecimal;
import java.util.List;
import modelo.*;

/** Cálculo de lo que se va a cobrar al devolver los libros marcados. */
public final class ResumenDevolucion {

    private final int diasRetraso;
    private final BigDecimal multaPorDia;
    private final BigDecimal total;

    public ResumenDevolucion(List<Prestamo> prestamos, BigDecimal multaPorDia) {
        this.multaPorDia = multaPorDia;
        this.diasRetraso = prestamos.stream().mapToInt(Prestamo::diasAtraso).sum();
        this.total = prestamos.stream()
                .map(p -> Multa.calcularMonto(p.diasAtraso(), multaPorDia))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getDiasRetraso() {
        return diasRetraso;
    }

    public BigDecimal getMultaPorDia() {
        return multaPorDia;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public boolean hayMulta() {
        return total.signum() > 0;
    }
}
