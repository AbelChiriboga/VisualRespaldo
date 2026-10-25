package util;

import excepciones.BibliotecaException;
import java.awt.Component;
import java.util.function.*;
import javax.swing.SwingWorker;

/** Ejecuta un trabajo lento (por ejemplo, esperar el dedo en el lector) en segundo plano, para que la ventana no. */
public final class Tareas {

    private Tareas() {
    }

    public static <T> void enSegundoPlano(Component padre, Supplier<T> trabajo, Consumer<T> alTerminar, Runnable siempre) {
        enSegundoPlano(padre, trabajo, alTerminar, e -> Mensajes.error(padre, e), siempre);
    }

    public static <T> void enSegundoPlano(Component padre, Supplier<T> trabajo, Consumer<T> alTerminar,
                                          Consumer<BibliotecaException> alFallar, Runnable siempre) {
        new SwingWorker<T, Void>() {
            private BibliotecaException error;

            @Override
            protected T doInBackground() {
                try {
                    return trabajo.get();
                } catch (BibliotecaException e) {
                    error = e;
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                    if (error == null) {
                        Mensajes.ejecutar(padre, () -> alTerminar.accept(getResultado()));
                    } else {
                        alFallar.accept(error);
                    }
                } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
                    Mensajes.error(padre, new excepciones.DatosException("Error inesperado: " + e.getMessage(), e));
                } finally {
                    siempre.run();
                }
            }

            private T getResultado() {
                try {
                    return get();
                } catch (InterruptedException | java.util.concurrent.ExecutionException e) {
                    throw new IllegalStateException(e);
                }
            }
        }.execute();
    }
}
