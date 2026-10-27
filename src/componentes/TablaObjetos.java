package componentes;

import componentes.BarraBusqueda;
import excepciones.ValidacionException;
import java.util.*;
import java.util.function.*;
import javax.swing.*;
import javax.swing.table.*;

/** Une una JTable con la lista de OBJETOS que muestra (genérica: TablaObjetos, TablaObjetos...). */
public class TablaObjetos<T> {

    private final JTable tabla;
    private final Function<T, Object[]> fila;
    private final List<T> datos = new ArrayList<>();
    private final TableRowSorter<TableModel> ordenador;

    public TablaObjetos(JTable tabla, Function<T, Object[]> fila) {
        this.tabla = tabla;
        this.fila = fila;
        this.ordenador = new TableRowSorter<>(tabla.getModel());
        tabla.setRowSorter(ordenador);
    }

    public void mostrar(List<T> lista) {
        tabla.clearSelection();
        tabla.getSelectionModel().setAnchorSelectionIndex(-1);
        tabla.getSelectionModel().setLeadSelectionIndex(-1);
        datos.clear();
        datos.addAll(lista);
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);
        for (T objeto : lista) {
            modelo.addRow(fila.apply(objeto));
        }
    }

    public List<T> getDatos() {
        return Collections.unmodifiableList(datos);
    }

    public Optional<T> getSeleccionado() {
        int fila = tabla.getSelectedRow();
        return fila < 0 ? Optional.empty() : Optional.of(datos.get(tabla.convertRowIndexToModel(fila)));
    }

    public T exigirSeleccionado(String que) {
        return getSeleccionado().orElseThrow(() -> new ValidacionException("Seleccione " + que + "."));
    }

    public void alSeleccionar(Consumer<T> accion) {
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                getSeleccionado().ifPresent(accion);
            }
        });
    }

    public void seleccionar(T objeto) {
        int indice = datos.indexOf(objeto);
        if (indice >= 0) {
            int vista = tabla.convertRowIndexToView(indice);
            if (vista >= 0) {
                tabla.setRowSelectionInterval(vista, vista);
                tabla.scrollRectToVisible(tabla.getCellRect(vista, 0, true));
            }
        }
    }

    public void limpiarSeleccion() {
        tabla.clearSelection();
    }

    public List<T> getMarcados() {
        List<T> marcados = new ArrayList<>();
        TableModel modelo = tabla.getModel();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if (Boolean.TRUE.equals(modelo.getValueAt(i, 0))) {
                marcados.add(datos.get(i));
            }
        }
        return marcados;
    }

    public void marcarTodos(boolean marcado) {
        for (int i = 0; i < tabla.getModel().getRowCount(); i++) {
            tabla.getModel().setValueAt(marcado, i, 0);
        }
    }

    /**
     * No deja marcar más filas que {@code maximo}: si se pasa, desmarca la última y avisa con {@code alExceder}
     * (que recibe el máximo permitido).
     */
    public void limitarMarcados(IntSupplier maximo, IntConsumer alExceder) {
        tabla.getModel().addTableModelListener(e -> {
            if (e.getType() != javax.swing.event.TableModelEvent.UPDATE || e.getColumn() != 0
                    || e.getFirstRow() != e.getLastRow() || e.getFirstRow() < 0) {
                return;
            }
            int fila = e.getFirstRow();
            int tope = maximo.getAsInt();
            if (Boolean.TRUE.equals(tabla.getModel().getValueAt(fila, 0)) && getMarcados().size() > tope) {
                SwingUtilities.invokeLater(() -> {
                    tabla.getModel().setValueAt(Boolean.FALSE, fila, 0);
                    alExceder.accept(tope);
                });
            }
        });
    }

    public void alMarcar(Runnable accion) {
        tabla.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 0) {
                accion.run();
            }
        });
    }

    public void filtrar(String texto) {
        filtrar(texto, new int[0]);
    }

    public void filtrar(String texto, int... columnas) {
        String buscado = normalizar(texto);
        ordenador.setRowFilter(buscado.isEmpty() ? null : new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> fila) {
                for (int c : columnasABuscar(columnas, fila.getValueCount())) {
                    if (empiezaCon(fila.getStringValue(c), buscado)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private static int[] columnasABuscar(int[] columnas, int total) {
        return columnas.length > 0 ? columnas : java.util.stream.IntStream.range(0, total).toArray();
    }

    private static boolean empiezaCon(String valor, String buscado) {
        String v = normalizar(valor);
        if (v.startsWith(buscado)) {
            return true;
        }
        for (String palabra : v.split("\\s+")) {
            if (palabra.startsWith(buscado)) {
                return true;
            }
        }
        return false;
    }

    private static String normalizar(String texto) {
        String t = texto == null ? "" : texto.trim().toLowerCase();
        return java.text.Normalizer.normalize(t, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    public void usarBarra(BarraBusqueda barra) {
        List<Integer> indices = new ArrayList<>();
        List<String> nombres = new ArrayList<>();
        for (int i = 0; i < tabla.getModel().getColumnCount(); i++) {
            String nombre = tabla.getModel().getColumnName(i);
            if (nombre != null && !nombre.isBlank()) {
                indices.add(i);
                nombres.add(nombre);
            }
        }
        barra.configurarFiltro(nombres);
        barra.alBuscar(texto -> filtrar(texto, barra.getColumnasSeleccionadas().stream()
                .mapToInt(indices::get).toArray()));
    }
}
