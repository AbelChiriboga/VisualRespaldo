package servicio;

import dao.IndicadorDAO;

/** Números de las tarjetas de los menús (estudiantes registrados, préstamos activos...). */
public class IndicadorService {

    private final IndicadorDAO dao = new IndicadorDAO();

    public int valor(modelo.Indicador indicador) {
        return dao.contar(indicador);
    }
}
