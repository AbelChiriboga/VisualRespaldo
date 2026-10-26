package dao;

import java.util.*;

/** Operaciones de SOLO LECTURA, comunes a todos los DAO. */
public interface ConsultaDAO<T, ID> {

    Optional<T> buscarPorId(ID id);

    List<T> listar();
}
