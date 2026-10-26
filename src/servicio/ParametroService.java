package servicio;

import dao.ParametroDAO;
import java.util.*;
import modelo.*;

/** Lectura y cambio de los parámetros del sistema (solo el administrador). */
public class ParametroService {

    private final ParametroDAO dao = new ParametroDAO();

    public Map<ParametroEditable, String> obtenerTodos() {
        Map<ParametroEditable, String> valores = new EnumMap<>(ParametroEditable.class);
        for (ParametroEditable p : ParametroEditable.values()) {
            valores.put(p, dao.obtener(p.getClave()).getValor());
        }
        return valores;
    }

    public void guardar(Map<ParametroEditable, String> escritos) {
        Sesion.getInstancia().exigir(Usuario::puedeConfigurarParametros, "cambiar los parámetros");
        List<Parametro> parametros = new ArrayList<>();
        escritos.forEach((p, valor) -> parametros.add(new Parametro(p.getClave(), p.validar(valor))));
        dao.actualizarVarios(parametros);
    }
}
