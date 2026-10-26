package servicio;

import java.math.BigDecimal;
import java.util.List;
import modelo.*;

/** Qué se ve en la pantalla Atrasos. */
public enum TipoAtraso {

    PRESTAMOS_ATRASADOS("Préstamos atrasados") {
        @Override
        public List<Prestamo> listar(AtrasoService s) {
            return s.listarAtrasados();
        }

        @Override
        public int dias(Prestamo p) {
            return p.diasAtraso();
        }

        @Override
        public BigDecimal multa(Prestamo p, BigDecimal multaPorDia) {
            return Multa.calcularMonto(p.diasAtraso(), multaPorDia);
        }

        @Override
        public boolean permitePago() {
            return false;
        }
    },

    MULTAS_PENDIENTES("Multas pendientes") {
        @Override
        public List<Prestamo> listar(AtrasoService s) {
            return s.listarMultasPendientes();
        }

        @Override
        public int dias(Prestamo p) {
            return p.getMulta().getDiasAtraso();
        }

        @Override
        public BigDecimal multa(Prestamo p, BigDecimal multaPorDia) {
            return p.getMulta().getMonto();
        }

        @Override
        public boolean permitePago() {
            return true;
        }
    };

    private final String descripcion;

    TipoAtraso(String descripcion) {
        this.descripcion = descripcion;
    }

    public abstract List<Prestamo> listar(AtrasoService servicio);

    public abstract int dias(Prestamo p);

    public abstract BigDecimal multa(Prestamo p, BigDecimal multaPorDia);

    public abstract boolean permitePago();

    @Override
    public String toString() {
        return descripcion;
    }
}
