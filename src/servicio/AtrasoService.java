package servicio;

import dao.*;
import excepciones.NegocioException;
import excepciones.ValidacionException;
import java.math.BigDecimal;
import java.util.List;
import modelo.*;

/** Préstamos atrasados, multas pendientes y registro del pago de una o varias multas. */
public class AtrasoService {

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final MultaDAO multaDAO = new MultaDAO();
    private final ParametroDAO parametroDAO = new ParametroDAO();

    public List<Prestamo> listarAtrasados() {
        return prestamoDAO.listarAtrasados("");
    }

    public List<Prestamo> listarMultasPendientes() {
        return prestamoDAO.listarConMultaPendiente();
    }

    public BigDecimal multaPorDia() {
        return parametroDAO.obtenerDecimal(Parametro.MULTA_POR_DIA);
    }

    public void registrarPago(Prestamo p) {
        registrarPagos(List.of(p));
    }

    /** Revisa que se pueda pagar: al menos una multa, todas de un solo estudiante y ninguna ya pagada. */
    public void verificarPago(List<Prestamo> prestamos) {
        if (prestamos.isEmpty()) {
            throw new ValidacionException("Seleccione al menos una multa.");
        }
        int estudiante = prestamos.get(0).getEstudiante().getIdEstudiante();
        for (Prestamo p : prestamos) {
            if (p.getEstudiante().getIdEstudiante() != estudiante) {
                throw new NegocioException("Seleccione multas de un solo estudiante.");
            }
            if (p.getMulta() == null || p.getMulta().isPagada()) {
                throw new NegocioException("Una de las multas ya está pagada.");
            }
        }
    }

    /** Registra el pago de una o varias multas del mismo estudiante. */
    public void registrarPagos(List<Prestamo> prestamos) {
        Sesion.getInstancia().exigir(Usuario::puedeGestionarPrestamos, "registrar pagos");
        verificarPago(prestamos);
        List<Integer> ids = new java.util.ArrayList<>();
        for (Prestamo p : prestamos) {
            ids.add(p.getMulta().getId());
        }
        multaDAO.marcarPagadas(ids);
        for (Prestamo p : prestamos) {
            p.getMulta().setPagada(true);
        }
    }

    /** Suma de los montos de las multas. */
    public BigDecimal total(List<Prestamo> prestamos) {
        BigDecimal total = BigDecimal.ZERO;
        for (Prestamo p : prestamos) {
            total = total.add(p.getMulta().getMonto());
        }
        return total;
    }
}
