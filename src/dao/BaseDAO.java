package dao;

import java.sql.*;
import java.util.*;

/** DAO de una tabla de ENTIDADES (autores, libros, estudiantes...). */
public abstract class BaseDAO<T, ID> extends AccesoBD implements ConsultaDAO<T, ID> {

    protected abstract String sqlSelect();

    protected abstract String columnaId();

    protected abstract String orden();

    protected abstract T mapear(ResultSet rs) throws SQLException;

    @Override
    public Optional<T> buscarPorId(ID id) {
        return consultarUno(sqlSelect() + " WHERE " + columnaId() + " = ?", id);
    }

    @Override
    public List<T> listar() {
        return consultarLista(sqlSelect() + " ORDER BY " + orden());
    }

    protected List<T> consultarLista(String sql, Object... parametros) {
        return consultar(sql, this::mapear, parametros);
    }

    protected Optional<T> consultarUno(String sql, Object... parametros) {
        List<T> lista = consultarLista(sql, parametros);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }
}
