package vista;

/** Copias físicas de un libro. */
public class CRUDEjemplares extends componentes.VentanaBase {

    private final componentes.Formulario formulario = new componentes.Formulario(this);

    private final servicio.EjemplaresDeLibro ejemplares;
    private final componentes.TablaObjetos<modelo.Ejemplar> tabla;
    private final componentes.ControlCrud control;
    private final modelo.Libro libro;
    private modelo.Ejemplar actual;

    public CRUDEjemplares(servicio.EjemplaresDeLibro ejemplares) {
        this.ejemplares = util.Validaciones.requerido(ejemplares, "los ejemplares");
        this.libro = ejemplares.getLibro();
        initComponents();
        configurarEntradas();
        montar("EJEMPLARES DEL LIBRO", jpnlContenido);
        componentes.Tablas.soloLectura(jtblEjemplares, jScrollPane1, "Código de inventario", "Ubicación", "Estado");
        tabla = new componentes.TablaObjetos<>(jtblEjemplares,
                e -> new Object[]{e.getCodigoInventario(), e.getUbicacion(), e.getEstado()});
        componentes.ControlCrud.permitirEscribir(jtxtCodInv, false);
        jtxtCodInv.setFocusable(false);
        control = new componentes.ControlCrud(new javax.swing.JComponent[]{jtxtUbicacion, jcbxEstado},
                jbtnNuevo, jbtnGuardar, jbtnEditar, jbtnEliminar, jbtnCancelar);
        control.bloquearAlEditar(jtblEjemplares);
        llenarEstados(false);
        tabla.alSeleccionar(this::mostrar);
        mostrarLibro();
        cancelar();
    }

    /** Caracteres permitidos, máximo de cada caja (según la base de datos) y validación con Enter. */
    private void configurarEntradas() {
        util.TipoEntrada.LIBRE.aplicar(jtxtUbicacion, 50);
        formulario
                .campo(jtxtUbicacion, () -> actual.setUbicacion(jtxtUbicacion.getText()))
                .campo(jcbxEstado, () -> actual.setEstado(componentes.Combos.seleccionado(jcbxEstado)))
                .terminarEn(jbtnGuardar)
                .armar();
    }

    private void nuevo() {
        actual = new modelo.Ejemplar();
        actual.setLibro(libro);
        limpiar();
        tabla.limpiarSeleccion();
        llenarEstados(true);
        control.nuevo();
        componentes.Combos.seleccionar(jcbxEstado, modelo.EstadoEjemplar.DISPONIBLE);
        componentes.ControlCrud.permitirEscribir(jcbxEstado, false);
        ejecutar(() -> jtxtCodInv.setText(ejemplares.siguienteCodigo()));
        jtxtUbicacion.requestFocus();
    }

    private void editar() {
        ejecutar(() -> {
            actual = tabla.exigirSeleccionado("un ejemplar");
            ejemplares.verificarEditable(actual);
            llenarEstados(true);
            componentes.Combos.seleccionar(jcbxEstado, actual.getEstado());
            control.edicion();
            jtxtUbicacion.requestFocus();
        });
    }

    private void guardar() {
        if (!formulario.validar()) {
            return;
        }
        ejecutar(() -> {
            actual.setCodigoInventario(jtxtCodInv.getText());
            actual.setUbicacion(jtxtUbicacion.getText());
            actual.setEstado(componentes.Combos.seleccionado(jcbxEstado));
            ejemplares.guardar(actual);
            util.Mensajes.info(this, "Ejemplar guardado.");
            cancelar();
        });
    }

    private void eliminar() {
        ejecutar(() -> {
            modelo.Ejemplar e = tabla.exigirSeleccionado("un ejemplar");
            if (util.Mensajes.confirmar(this, "¿Eliminar el ejemplar " + e.getCodigoInventario() + "?")) {
                ejemplares.eliminar(e);
                cancelar();
            }
        });
    }

    private void cancelar() {
        actual = null;
        llenarEstados(false);
        limpiar();
        cargar();
        control.consulta(false);
    }

    private void cargar() {
        ejecutar(() -> {
            java.util.List<modelo.Ejemplar> lista = ejemplares.listar();
            tabla.mostrar(lista);
            long disponibles = lista.stream().filter(modelo.Ejemplar::estaDisponible).count();
            jlblResumen.setText(lista.size() + " ejemplar(es), " + disponibles
                    + " disponible(s). Un ejemplar prestado no se puede editar hasta que se devuelva.");
        });
    }

    private void mostrarLibro() {
        jlblTitulo.setText(libro.getTitulo());
        jlblAutor.setText(libro.getAutor().getNombreCompleto());
        jlblIsbn.setText(libro.getIsbn() == null ? "—" : libro.getIsbn());
    }

    private void mostrar(modelo.Ejemplar e) {
        actual = e;
        jtxtCodInv.setText(e.getCodigoInventario());
        jtxtUbicacion.setText(e.getUbicacion());
        componentes.Combos.seleccionar(jcbxEstado, e.getEstado());
        control.consulta(true);
    }

    private void llenarEstados(boolean soloEditables) {
        componentes.Combos.llenarSinElegir(jcbxEstado, java.util.Arrays.stream(modelo.EstadoEjemplar.values())
                .filter(estado -> !soloEditables || estado.esEditableManualmente())
                .collect(java.util.stream.Collectors.toList()));
    }

    private void limpiar() {
        jtxtCodInv.setText("");
        jtxtUbicacion.setText("");
        jcbxEstado.setSelectedIndex(-1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        java.awt.GridBagConstraints gridBagConstraints;

        jpnlContenido = new javax.swing.JPanel();
        jpnlLibro = new javax.swing.JPanel();
        jlblLibroTit = new javax.swing.JLabel();
        jlblTitulo = new javax.swing.JLabel();
        jlblAutorTit = new javax.swing.JLabel();
        jlblAutor = new javax.swing.JLabel();
        jlblIsbnTit = new javax.swing.JLabel();
        jlblIsbn = new javax.swing.JLabel();
        jpnlDatosEjemplar = new componentes.PanelGrupo();
        jlblCodigo = new javax.swing.JLabel();
        jtxtCodInv = new javax.swing.JTextField();
        jlblUbicacion = new javax.swing.JLabel();
        jtxtUbicacion = new javax.swing.JTextField();
        jlblEstado = new javax.swing.JLabel();
        jcbxEstado = new javax.swing.JComboBox<>();
        jpnlBotones = new javax.swing.JPanel();
        jbtnNuevo = new componentes.BotonSistema();
        jbtnGuardar = new componentes.BotonSistema();
        jbtnEditar = new componentes.BotonSistema();
        jbtnEliminar = new componentes.BotonSistema();
        jbtnCancelar = new componentes.BotonSistema();
        jpnlEjemplaresReg = new componentes.PanelGrupo();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblEjemplares = new javax.swing.JTable();
        jlblResumen = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jpnlContenido.setBackground(new java.awt.Color(255, 255, 255));
        jpnlContenido.setPreferredSize(new java.awt.Dimension(1098, 520));
        jpnlContenido.setLayout(new java.awt.BorderLayout(16, 16));

        jpnlLibro.setBackground(new java.awt.Color(227, 241, 241));
        jpnlLibro.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 16, 12, 16));
        jpnlLibro.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        jlblLibroTit.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jlblLibroTit.setText("Libro:");
        jlblLibroTit.setForeground(new java.awt.Color(10, 69, 69));
        jlblLibroTit.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 6));
        jpnlLibro.add(jlblLibroTit);

        jlblTitulo.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jlblTitulo.setText("—");
        jlblTitulo.setForeground(new java.awt.Color(10, 69, 69));
        jlblTitulo.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 36));
        jpnlLibro.add(jlblTitulo);

        jlblAutorTit.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jlblAutorTit.setText("Autor:");
        jlblAutorTit.setForeground(new java.awt.Color(10, 69, 69));
        jlblAutorTit.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 6));
        jpnlLibro.add(jlblAutorTit);

        jlblAutor.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jlblAutor.setText("—");
        jlblAutor.setForeground(new java.awt.Color(10, 69, 69));
        jlblAutor.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 36));
        jpnlLibro.add(jlblAutor);

        jlblIsbnTit.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jlblIsbnTit.setText("ISBN:");
        jlblIsbnTit.setForeground(new java.awt.Color(10, 69, 69));
        jlblIsbnTit.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 6));
        jpnlLibro.add(jlblIsbnTit);

        jlblIsbn.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jlblIsbn.setText("—");
        jlblIsbn.setForeground(new java.awt.Color(10, 69, 69));
        jlblIsbn.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 36));
        jpnlLibro.add(jlblIsbn);

        jpnlContenido.add(jpnlLibro, java.awt.BorderLayout.NORTH);

        jpnlDatosEjemplar.setTitulo("Datos del ejemplar");
        jpnlDatosEjemplar.setPreferredSize(new java.awt.Dimension(300, 400));
        jpnlDatosEjemplar.setLayout(new java.awt.GridBagLayout());

        jlblCodigo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblCodigo.setText("Código de inventario");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 4, 6);
        jpnlDatosEjemplar.add(jlblCodigo, gridBagConstraints);

        jtxtCodInv.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtCodInv.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 10, 6);
        jpnlDatosEjemplar.add(jtxtCodInv, gridBagConstraints);

        jlblUbicacion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblUbicacion.setText("Ubicación");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 4, 6);
        jpnlDatosEjemplar.add(jlblUbicacion, gridBagConstraints);

        jtxtUbicacion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtUbicacion.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 10, 6);
        jpnlDatosEjemplar.add(jtxtUbicacion, gridBagConstraints);

        jlblEstado.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblEstado.setText("Estado");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 4, 6);
        jpnlDatosEjemplar.add(jlblEstado, gridBagConstraints);

        jcbxEstado.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jcbxEstado.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 10, 6);
        jpnlDatosEjemplar.add(jcbxEstado, gridBagConstraints);

        jpnlBotones.setOpaque(false);
        jpnlBotones.setLayout(new java.awt.GridLayout(2, 2, 10, 10));

        jbtnNuevo.setText("Nuevo");
        jbtnNuevo.setPreferredSize(new java.awt.Dimension(100, 36));
        jbtnNuevo.addActionListener(this::jbtnNuevoActionPerformed);
        jpnlBotones.add(jbtnNuevo);

        jbtnGuardar.setText("Guardar");
        jbtnGuardar.setPrimario(true);
        jbtnGuardar.setPreferredSize(new java.awt.Dimension(100, 36));
        jbtnGuardar.addActionListener(this::jbtnGuardarActionPerformed);
        jpnlBotones.add(jbtnGuardar);

        jbtnEditar.setText("Editar");
        jbtnEditar.setPreferredSize(new java.awt.Dimension(100, 36));
        jbtnEditar.addActionListener(this::jbtnEditarActionPerformed);
        jpnlBotones.add(jbtnEditar);

        jbtnEliminar.setText("Eliminar");
        jbtnEliminar.setPreferredSize(new java.awt.Dimension(100, 36));
        jbtnEliminar.addActionListener(this::jbtnEliminarActionPerformed);
        jpnlBotones.add(jbtnEliminar);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 6, 10, 6);
        jpnlDatosEjemplar.add(jpnlBotones, gridBagConstraints);

        jbtnCancelar.setText("Cancelar");
        jbtnCancelar.setPreferredSize(new java.awt.Dimension(100, 36));
        jbtnCancelar.addActionListener(this::jbtnCancelarActionPerformed);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        jpnlDatosEjemplar.add(jbtnCancelar, gridBagConstraints);

        jpnlContenido.add(jpnlDatosEjemplar, java.awt.BorderLayout.WEST);

        jpnlEjemplaresReg.setTitulo("Ejemplares de este libro");
        jpnlEjemplaresReg.setLayout(new java.awt.BorderLayout(0, 10));

        jScrollPane1.setViewportView(jtblEjemplares);

        jpnlEjemplaresReg.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jlblResumen.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jlblResumen.setText("Un ejemplar prestado no se puede editar hasta que se devuelva.");
        jlblResumen.setForeground(new java.awt.Color(90, 105, 115));
        jpnlEjemplaresReg.add(jlblResumen, java.awt.BorderLayout.SOUTH);

        jpnlContenido.add(jpnlEjemplaresReg, java.awt.BorderLayout.CENTER);

        getContentPane().add(jpnlContenido, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNuevoActionPerformed
        nuevo();
    }//GEN-LAST:event_jbtnNuevoActionPerformed

    private void jbtnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGuardarActionPerformed
        guardar();
    }//GEN-LAST:event_jbtnGuardarActionPerformed

    private void jbtnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditarActionPerformed
        editar();
    }//GEN-LAST:event_jbtnEditarActionPerformed

    private void jbtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEliminarActionPerformed
        eliminar();
    }//GEN-LAST:event_jbtnEliminarActionPerformed

    private void jbtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelarActionPerformed
        cancelar();
    }//GEN-LAST:event_jbtnCancelarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private componentes.BotonSistema jbtnCancelar;
    private componentes.BotonSistema jbtnEditar;
    private componentes.BotonSistema jbtnEliminar;
    private componentes.BotonSistema jbtnGuardar;
    private componentes.BotonSistema jbtnNuevo;
    private javax.swing.JComboBox<modelo.EstadoEjemplar> jcbxEstado;
    private javax.swing.JLabel jlblAutor;
    private javax.swing.JLabel jlblAutorTit;
    private javax.swing.JLabel jlblCodigo;
    private javax.swing.JLabel jlblEstado;
    private javax.swing.JLabel jlblIsbn;
    private javax.swing.JLabel jlblIsbnTit;
    private javax.swing.JLabel jlblLibroTit;
    private javax.swing.JLabel jlblResumen;
    private javax.swing.JLabel jlblTitulo;
    private javax.swing.JLabel jlblUbicacion;
    private javax.swing.JPanel jpnlBotones;
    private javax.swing.JPanel jpnlContenido;
    private componentes.PanelGrupo jpnlDatosEjemplar;
    private componentes.PanelGrupo jpnlEjemplaresReg;
    private javax.swing.JPanel jpnlLibro;
    private javax.swing.JTable jtblEjemplares;
    private javax.swing.JTextField jtxtCodInv;
    private javax.swing.JTextField jtxtUbicacion;
    // End of variables declaration//GEN-END:variables
}
