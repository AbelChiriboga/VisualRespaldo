package componentes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Configuración de tablas en un solo lugar. */
public final class Tablas {

    private Tablas() {
    }

    public static void soloLectura(JTable tabla, JScrollPane scroll, String... columnas) {
        util.Estilos.tabla(tabla, scroll, columnas);
    }

    public static void conSeleccion(JTable tabla, JScrollPane scroll, String... columnas) {
        util.Estilos.tabla(tabla, scroll, columnas);
        String[] todas = new String[columnas.length + 1];
        todas[0] = "";
        System.arraycopy(columnas, 0, todas, 1, columnas.length);

        tabla.setModel(new DefaultTableModel(new Object[][]{}, todas) {
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int fila, int col) {
                return col == 0;
            }
        });
        tabla.getColumnModel().getColumn(0).setMaxWidth(40);
    }

    public static void anchos(JTable tabla, int... anchos) {
        for (int i = 0; i < anchos.length && i < tabla.getColumnCount(); i++) {
            if (anchos[i] > 0) {
                tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
            }
        }
    }
}
