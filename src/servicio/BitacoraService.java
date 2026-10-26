package servicio;

import dao.*;
import excepciones.ValidacionException;
import java.time.LocalDate;
import modelo.*;

/** Bitácora de autenticación biométrica (solo el administrador). */
public class BitacoraService {

    private final RegistroAutenticacionDAO dao = new RegistroAutenticacionDAO();
    private final ParametroDAO parametroDAO = new ParametroDAO();

    public ResumenBitacora consultar(LocalDate desde, LocalDate hasta, OperacionAutenticacion operacion,
                                     ResultadoAutenticacion resultado) {
        Sesion.getInstancia().exigir(Usuario::puedeVerBitacora, "ver la bitácora");
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ValidacionException("La fecha Desde no puede ser mayor que Hasta.");
        }
        return new ResumenBitacora(dao.listar(desde, hasta, operacion, resultado), desde, hasta);
    }

    public int umbralHuella() {
        return parametroDAO.obtenerEntero(Parametro.UMBRAL_HUELLA);
    }
}
