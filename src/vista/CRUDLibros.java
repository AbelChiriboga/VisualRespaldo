package vista;

/** Registro de libros. */
public class CRUDLibros extends componentes.VentanaBase {

    private final componentes.Formulario formulario = new componentes.Formulario(this);

    private final servicio.LibroService servicio = new servicio.LibroService();
    private final componentes.TablaObjetos<modelo.Libro> tabla;
    private final componentes.ControlCrud control;
    private modelo.Libro actual;
    private servicio.EjemplaresDeLibro ejemplares;

    public CRUDLibros() {
        initComponents();
        configurarEntradas();
        montar("GESTIÓN DE LIBROS", jpnlContenido);
        componentes.Tablas.soloLectura(jtblLibros, jScrollPane1,
                "ISBN", "Título", "Autor", "Categoría", "Año", "Disponibles");
        componentes.Tablas.anchos(jtblLibros, 120, 220, 160, 130, 50, 80);
        tabla = new componentes.TablaObjetos<>(jtblLibros, l -> new Object[]{l.getIsbn(), l.getTitulo(),
            l.getAutor(), l.getCategoria(), l.getAnio(), l.getResumenDisponibles()});
        control = new componentes.ControlCrud(new javax.swing.JComponent[]{jtxtISBN, jtxtTitulo, jcbxAutor, jbtnAutor,
            jcbxCategoria, jbtnCategoria, jtxtEditorial, jtxtAnio, jbtnEjemplares},
                jbtnNuevo, jbtnGuardar, jbtnEditar, jbtnEliminar, jbtnCancelar);
        control.bloquearAlEditar(jtblLibros);
        tabla.alSeleccionar(this::mostrar);
        tabla.usarBarra(barraBuscar);
        cargarCombos();
        cancelar();
    }

    /** Caracteres permitidos, máximo de cada caja (según la base de datos) y validación con Enter. */
    private void configurarEntradas() {
        util.TipoEntrada.ISBN.aplicar(jtxtISBN, 17);
        util.TipoEntrada.LIBRE.aplicar(jtxtTitulo, 150);
        util.TipoEntrada.LIBRE.aplicar(jtxtEditorial, 100);
        util.TipoEntrada.NUMEROS.aplicar(jtxtAnio, 4);
        formulario
                .campo(jtxtISBN, () -> actual.setIsbn(util.Validaciones.obligatorio(jtxtISBN.getText(), "ISBN")))
                .campo(jtxtTitulo, () -> actual.setTitulo(jtxtTitulo.getText()))
                .campo(jcbxAutor, () -> actual.setAutor(componentes.Combos.seleccionado(jcbxAutor)))
                .campo(jcbxCategoria, () -> actual.setCategoria(componentes.Combos.seleccionado(jcbxCategoria)))
                .campo(jtxtEditorial, () -> actual.setEditorial(jtxtEditorial.getText()))
                .campo(jtxtAnio, () -> actual.setAnio(util.Validaciones.enteroOpcional(jtxtAnio.getText(), "año", 0, 9999)))
                .terminarEn(jbtnGuardar)
                .armar();
    }

    private void nuevo() {
        actual = new modelo.Libro();
        ejemplares = new servicio.EjemplaresPendientes(actual);
        limpiar();
        tabla.limpiarSeleccion();
        control.nuevo();
        jtxtISBN.requestFocus();
    }

    private void editar() {
        ejecutar(() -> {
            actual = tabla.exigirSeleccionado("un libro");
            ejemplares = new servicio.EjemplaresGuardados(actual);
            control.edicion();
            componentes.ControlCrud.permitirEscribir(jtxtISBN, false);
            jtxtTitulo.requestFocus();
        });
    }

    private void guardar() {
        if (!formulario.validar()) {
            return;
        }
        ejecutar(() -> {
            actual.setIsbn(jtxtISBN.getText());
            actual.setTitulo(jtxtTitulo.getText());
            actual.setAutor(componentes.Combos.seleccionado(jcbxAutor));
            actual.setCategoria(componentes.Combos.seleccionado(jcbxCategoria));
            actual.setEditorial(jtxtEditorial.getText());
            actual.setAnio(util.Validaciones.enteroOpcional(jtxtAnio.getText(), "año", 0, 9999));
            if (actual.esNuevo()) {
                servicio.crear(actual, ejemplares.listar());
            } else {
                servicio.guardar(actual);
            }
            int cantidad = ejemplares.listar().size();
            util.Mensajes.info(this, "Libro guardado correctamente\n" + cantidad + (cantidad == 1 ? " ejemplar" : " ejemplares"));
            cancelar();
        });
    }

    private void eliminar() {
        ejecutar(() -> {
            modelo.Libro l = tabla.exigirSeleccionado("un libro");
            if (util.Mensajes.confirmar(this, "¿Eliminar el libro \"" + l.getTitulo() + "\"?")) {
                servicio.eliminar(l);
                cancelar();
            }
        });
    }

    private void cancelar() {
        actual = null;
        ejemplares = null;
        limpiar();
        cargar();
        control.consulta(false);
    }

    private void agregarAutor() {
        util.Navegacion.abrir(this, new CRUDAutores(), this::cargarCombos);
    }

    private void agregarCategoria() {
        util.Navegacion.abrir(this, new CRUDCategoria(), this::cargarCombos);
    }

    /** En Nuevo los ejemplares quedan pendientes hasta Guardar; en Editar se guardan al momento. */
    private void abrirEjemplares() {
        if (formulario.validar()) {
            util.Navegacion.abrir(this, new CRUDEjemplares(ejemplares));
        }
    }

    private void cargar() {
        ejecutar(() -> tabla.mostrar(servicio.listar()));
    }

    private void cargarCombos() {
        ejecutar(() -> {
            componentes.Combos.llenarSinElegir(jcbxAutor, servicio.listarAutores());
            componentes.Combos.llenarSinElegir(jcbxCategoria, servicio.listarCategorias());
        });
    }

    private void mostrar(modelo.Libro l) {
        actual = l;
        jtxtISBN.setText(l.getIsbn());
        jtxtTitulo.setText(l.getTitulo());
        componentes.Combos.seleccionar(jcbxAutor, l.getAutor());
        componentes.Combos.seleccionar(jcbxCategoria, l.getCategoria());
        jtxtEditorial.setText(l.getEditorial());
        jtxtAnio.setText(l.getAnio() == null ? "" : String.valueOf(l.getAnio()));
        control.consulta(true);
    }

    private void limpiar() {
        jtxtISBN.setText("");
        jtxtTitulo.setText("");
        jcbxAutor.setSelectedIndex(-1);
        jcbxCategoria.setSelectedIndex(-1);
        jtxtEditorial.setText("");
        jtxtAnio.setText("");
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
        jpnlDatosLibro = new componentes.PanelGrupo();
        jlblISBN = new javax.swing.JLabel();
        jtxtISBN = new javax.swing.JTextField();
        jlblTitulo = new javax.swing.JLabel();
        jtxtTitulo = new javax.swing.JTextField();
        jlblAutor = new javax.swing.JLabel();
        jpnlAutor = new javax.swing.JPanel();
        jcbxAutor = new javax.swing.JComboBox<>();
        jbtnAutor = new componentes.BotonSistema();
        jlblCategoria = new javax.swing.JLabel();
        jpnlCategoria = new javax.swing.JPanel();
        jcbxCategoria = new javax.swing.JComboBox<>();
        jbtnCategoria = new componentes.BotonSistema();
        jlblEditorial = new javax.swing.JLabel();
        jtxtEditorial = new javax.swing.JTextField();
        jlblAnio = new javax.swing.JLabel();
        jtxtAnio = new javax.swing.JTextField();
        jpnlBotones = new javax.swing.JPanel();
        jbtnNuevo = new componentes.BotonSistema();
        jbtnGuardar = new componentes.BotonSistema();
        jbtnEditar = new componentes.BotonSistema();
        jbtnEliminar = new componentes.BotonSistema();
        jbtnCancelar = new componentes.BotonSistema();
        jbtnEjemplares = new componentes.BotonSistema();
        jpnlLibrosReg = new componentes.PanelGrupo();
        barraBuscar = new componentes.BarraBusqueda();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblLibros = new javax.swing.JTable();
        jlblAyuda = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jpnlContenido.setBackground(new java.awt.Color(255, 255, 255));
        jpnlContenido.setPreferredSize(new java.awt.Dimension(1098, 520));
        jpnlContenido.setLayout(new java.awt.BorderLayout(16, 0));

        jpnlDatosLibro.setTitulo("Datos del libro");
        jpnlDatosLibro.setPreferredSize(new java.awt.Dimension(340, 400));
        jpnlDatosLibro.setLayout(new java.awt.GridBagLayout());

        jlblISBN.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblISBN.setText("ISBN");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 4, 6);
        jpnlDatosLibro.add(jlblISBN, gridBagConstraints);

        jtxtISBN.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtISBN.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 7, 6);
        jpnlDatosLibro.add(jtxtISBN, gridBagConstraints);

        jlblTitulo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblTitulo.setText("Título");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 4, 6);
        jpnlDatosLibro.add(jlblTitulo, gridBagConstraints);

        jtxtTitulo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtTitulo.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 7, 6);
        jpnlDatosLibro.add(jtxtTitulo, gridBagConstraints);

        jlblAutor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblAutor.setText("Autor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 4, 6);
        jpnlDatosLibro.add(jlblAutor, gridBagConstraints);

        jpnlAutor.setOpaque(false);
        jpnlAutor.setLayout(new java.awt.BorderLayout(8, 0));

        jcbxAutor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jcbxAutor.setPreferredSize(new java.awt.Dimension(120, 32));
        jpnlAutor.add(jcbxAutor, java.awt.BorderLayout.CENTER);

        jbtnAutor.setText("+");
        jbtnAutor.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jbtnAutor.setToolTipText("Agregar autor");
        jbtnAutor.setPreferredSize(new java.awt.Dimension(36, 32));
        jbtnAutor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAutorActionPerformed(evt);
            }
        });
        jpnlAutor.add(jbtnAutor, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 7, 6);
        jpnlDatosLibro.add(jpnlAutor, gridBagConstraints);

        jlblCategoria.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblCategoria.setText("Categoría");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 4, 6);
        jpnlDatosLibro.add(jlblCategoria, gridBagConstraints);

        jpnlCategoria.setOpaque(false);
        jpnlCategoria.setLayout(new java.awt.BorderLayout(8, 0));

        jcbxCategoria.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jcbxCategoria.setPreferredSize(new java.awt.Dimension(120, 32));
        jpnlCategoria.add(jcbxCategoria, java.awt.BorderLayout.CENTER);

        jbtnCategoria.setText("+");
        jbtnCategoria.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jbtnCategoria.setToolTipText("Agregar categoría");
        jbtnCategoria.setPreferredSize(new java.awt.Dimension(36, 32));
        jbtnCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCategoriaActionPerformed(evt);
            }
        });
        jpnlCategoria.add(jbtnCategoria, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 7, 6);
        jpnlDatosLibro.add(jpnlCategoria, gridBagConstraints);

        jlblEditorial.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblEditorial.setText("Editorial");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 4, 6);
        jpnlDatosLibro.add(jlblEditorial, gridBagConstraints);

        jtxtEditorial.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtEditorial.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 7, 6);
        jpnlDatosLibro.add(jtxtEditorial, gridBagConstraints);

        jlblAnio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblAnio.setText("Año");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 4, 6);
        jpnlDatosLibro.add(jlblAnio, gridBagConstraints);

        jtxtAnio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtAnio.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 7, 6);
        jpnlDatosLibro.add(jtxtAnio, gridBagConstraints);

        jpnlBotones.setOpaque(false);
        jpnlBotones.setLayout(new java.awt.GridLayout(3, 2, 10, 10));

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

        jbtnCancelar.setText("Cancelar");
        jbtnCancelar.setPreferredSize(new java.awt.Dimension(100, 36));
        jbtnCancelar.addActionListener(this::jbtnCancelarActionPerformed);
        jpnlBotones.add(jbtnCancelar);

        jbtnEjemplares.setText("Ejemplares");
        jbtnEjemplares.setRutaIcono("Invetario.png");
        jbtnEjemplares.setTamanoIcono(20);
        jbtnEjemplares.setPreferredSize(new java.awt.Dimension(100, 36));
        jbtnEjemplares.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEjemplaresActionPerformed(evt);
            }
        });
        jpnlBotones.add(jbtnEjemplares);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 0, 6);
        jpnlDatosLibro.add(jpnlBotones, gridBagConstraints);

        jpnlContenido.add(jpnlDatosLibro, java.awt.BorderLayout.WEST);

        jpnlLibrosReg.setTitulo("Libros registrados");
        jpnlLibrosReg.setLayout(new java.awt.BorderLayout(0, 10));

        barraBuscar.setAyuda("Buscar por título, autor o ISBN");
        jpnlLibrosReg.add(barraBuscar, java.awt.BorderLayout.NORTH);

        jScrollPane1.setViewportView(jtblLibros);

        jpnlLibrosReg.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jlblAyuda.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jlblAyuda.setText("Seleccione un libro y presione Ejemplares para gestionar sus copias físicas.");
        jlblAyuda.setForeground(new java.awt.Color(90, 105, 115));
        jpnlLibrosReg.add(jlblAyuda, java.awt.BorderLayout.SOUTH);

        jpnlContenido.add(jpnlLibrosReg, java.awt.BorderLayout.CENTER);

        getContentPane().add(jpnlContenido, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnAutorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAutorActionPerformed
        agregarAutor();
    }//GEN-LAST:event_jbtnAutorActionPerformed

    private void jbtnCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCategoriaActionPerformed
        agregarCategoria();
    }//GEN-LAST:event_jbtnCategoriaActionPerformed

    private void jbtnEjemplaresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEjemplaresActionPerformed
        abrirEjemplares();
    }//GEN-LAST:event_jbtnEjemplaresActionPerformed

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
    private componentes.BarraBusqueda barraBuscar;
    private javax.swing.JScrollPane jScrollPane1;
    private componentes.BotonSistema jbtnAutor;
    private componentes.BotonSistema jbtnCancelar;
    private componentes.BotonSistema jbtnCategoria;
    private componentes.BotonSistema jbtnEditar;
    private componentes.BotonSistema jbtnEjemplares;
    private componentes.BotonSistema jbtnEliminar;
    private componentes.BotonSistema jbtnGuardar;
    private componentes.BotonSistema jbtnNuevo;
    private javax.swing.JComboBox<modelo.Autor> jcbxAutor;
    private javax.swing.JComboBox<modelo.Categoria> jcbxCategoria;
    private javax.swing.JLabel jlblAnio;
    private javax.swing.JLabel jlblAutor;
    private javax.swing.JLabel jlblAyuda;
    private javax.swing.JLabel jlblCategoria;
    private javax.swing.JLabel jlblEditorial;
    private javax.swing.JLabel jlblISBN;
    private javax.swing.JLabel jlblTitulo;
    private javax.swing.JPanel jpnlAutor;
    private javax.swing.JPanel jpnlBotones;
    private javax.swing.JPanel jpnlCategoria;
    private javax.swing.JPanel jpnlContenido;
    private componentes.PanelGrupo jpnlDatosLibro;
    private componentes.PanelGrupo jpnlLibrosReg;
    private javax.swing.JTable jtblLibros;
    private javax.swing.JTextField jtxtAnio;
    private javax.swing.JTextField jtxtEditorial;
    private javax.swing.JTextField jtxtISBN;
    private javax.swing.JTextField jtxtTitulo;
    // End of variables declaration//GEN-END:variables
}
