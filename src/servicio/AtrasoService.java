package servicio;

import dao.*;
import excepciones.NegocioException;
import java.math.BigDecimal;
import java.util.List;
import modelo.*;

/** Préstamos atrasados, multas pendientes y registro del pago de una multa. */
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
        Sesion.getInstancia().exigir(Usuario::puedeGestionarPrestamos, "registrar pagos");
        if (p.getMulta() == null || p.getMulta().isPagada()) {
            throw new NegocioException("Esa multa ya está pagada.");
        }
        multaDAO.marcarPagada(p.getMulta().getId());
        p.getMulta().setPagada(true);
    }
}
