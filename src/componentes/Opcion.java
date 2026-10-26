package componentes;

import java.util.*;

/** Elemento de un combo de FILTRO: un valor con su texto. */
public final class Opcion<T> {

    private final T valor;
    private final String texto;

    public Opcion(T valor, String texto) {
        this.valor = valor;
        this.texto = texto;
    }

    public T getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return texto;
    }

    public static <T> List<Opcion<T>> conTodas(String textoTodos, List<T> valores) {
        List<Opcion<T>> opciones = new ArrayList<>();
        opciones.add(new Opcion<>(null, textoTodos));
        valores.forEach(v -> opciones.add(new Opcion<>(v, String.valueOf(v))));
        return opciones;
    }
}
