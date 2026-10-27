package vista;

/** Registro de usuarios del sistema (administradores y bibliotecarios). */
public class CRUDBibliotecarios extends componentes.VentanaBase {

    private final componentes.Formulario formulario = new componentes.Formulario(this);

    private final servicio.UsuarioService servicio = new servicio.UsuarioService();
    private final componentes.TablaObjetos<modelo.Usuario> tabla;
    private final componentes.ControlCrud control;
    private java.util.function.Consumer<modelo.Usuario> alGuardar = u -> { };
    private boolean activoActual = true;
    private boolean nuevoUsuario;

    public CRUDBibliotecarios() {
        initComponents();
        configurarEntradas();
        montar("GESTIÓN DE USUARIOS", jpnlContenido);
        componentes.Tablas.soloLectura(jtblUsuarios, jScrollPane1,
                "Cédula", "Nombres", "Apellidos", "Usuario", "Rol", "Estado");
        util.Estilos.barra(jpgrSeguridad);
        tabla = new componentes.TablaObjetos<>(jtblUsuarios, u -> new Object[]{u.getCedula(), u.getNombres(),
            u.getApellidos(), u.getUsuario(), u.getRol(), u.isActivo() ? "Activo" : "Inactivo"});
        control = new componentes.ControlCrud(new javax.swing.JComponent[]{jtxtCedula, jtxtNombres, jtxtApellidos,
            jtxtCorreo, jtxtDireccion, jtxtTelefono, jtxtUsuario, jcbxRol, jpswClave, jpswRepClave},
                jbtnNuevo, jbtnGuardar, jbtnEditar, jbtnDesactivar, jbtnCancelar);
        control.bloquearAlEditar(jtblUsuarios);
        componentes.Combos.llenarSinElegir(jcbxRol, java.util.List.of(modelo.Rol.values()));
        tabla.alSeleccionar(this::mostrar);
        tabla.usarBarra(barraBuscar);
        util.Eventos.alCambiar(jpswClave, this::mostrarFortaleza);
        cancelar();
    }

    /** Caracteres permitidos, máximo de cada caja (según la base de datos) y validación con Enter. */
    private void configurarEntradas() {
        util.TipoEntrada.NUMEROS.aplicar(jtxtCedula, 10);
        util.TipoEntrada.TEXTO.aplicar(jtxtNombres, 60);
        util.TipoEntrada.TEXTO.aplicar(jtxtApellidos, 60);
        util.TipoEntrada.CORREO.aplicar(jtxtCorreo, 100);
        util.TipoEntrada.LIBRE.aplicar(jtxtDireccion, 150);
        util.TipoEntrada.NUMEROS.aplicar(jtxtTelefono, 10);
        util.TipoEntrada.USUARIO.aplicar(jtxtUsuario, 50);
        util.TipoEntrada.CLAVE.aplicar(jpswClave, 50);
        util.TipoEntrada.CLAVE.aplicar(jpswRepClave, 50);
        formulario
                .campo(jtxtCedula, () -> servicio.validarCedulaNueva(jtxtCedula.getText()))
                .campo(jtxtNombres, () -> muestra().setNombres(jtxtNombres.getText()))
                .campo(jtxtApellidos, () -> muestra().setApellidos(jtxtApellidos.getText()))
                .campo(jtxtCorreo, () -> muestra().setCorreo(jtxtCorreo.getText()))
                .campo(jtxtDireccion, () -> muestra().setDireccion(jtxtDireccion.getText()))
                .campo(jtxtTelefono, () -> muestra().setTelefono(util.Validaciones.obligatorio(jtxtTelefono.getText(), "teléfono")))
                .campo(jtxtUsuario, () -> muestra().setUsuario(jtxtUsuario.getText()))
                .campo(jcbxRol, () -> util.Validaciones.requerido(componentes.Combos.seleccionado(jcbxRol), "el rol"))
                .campo(jpswClave, () -> {
                    if (cambiaClave()) {
                        util.Seguridad.validarLongitud(clave());
                    }
                })
                .campo(jpswRepClave, () -> {
                    if (cambiaClave()) {
                        util.Seguridad.validarNueva(clave(), claveRepetida());
                    }
                })
                .terminarEn(jbtnGuardar)
                .armar();
    }

    private void nuevo() {
        limpiar();
        tabla.limpiarSeleccion();
        activoActual = true;
        alGuardar = u -> servicio.registrar(u, clave(), claveRepetida());
        nuevoUsuario = true;
        control.nuevo();
        jtxtCedula.requestFocus();
    }

    private void editar() {
        ejecutar(() -> {
            tabla.exigirSeleccionado("un usuario");
            alGuardar = u -> servicio.actualizar(u, clave(), claveRepetida());
            nuevoUsuario = false;
            control.edicion();
            componentes.ControlCrud.permitirEscribir(jtxtCedula, false);
            jlblSeguridad.setText("Deje la contraseña vacía para no cambiarla");
            jtxtNombres.requestFocus();
        });
    }

    private void guardar() {
        if (!formulario.validar()) {
            return;
        }
        ejecutar(() -> {
            modelo.Usuario u = leerFormulario();
            alGuardar.accept(u);
            util.Mensajes.info(this, "Usuario guardado.");
            cancelar();
        });
    }

    private void cambiarEstado() {
        ejecutar(() -> {
            modelo.Usuario u = tabla.exigirSeleccionado("un usuario");
            String accion = u.isActivo() ? "desactivar" : "activar";
            if (util.Mensajes.confirmar(this, "¿Desea " + accion + " al usuario " + u.getNombreCompleto() + "?")) {
                servicio.cambiarEstado(u);
                recargarYSeleccionar(u);
            }
        });
    }

    private void cancelar() {
        limpiar();
        cargar();
        control.consulta(false);
    }

    private void cargar() {
        ejecutar(() -> tabla.mostrar(servicio.listar()));
    }

    private void recargarYSeleccionar(modelo.Usuario u) {
        limpiar();
        cargar();
        control.consulta(false);
        tabla.seleccionar(u);
    }

    private modelo.Usuario leerFormulario() {
        modelo.Usuario u = servicio.crear(componentes.Combos.seleccionado(jcbxRol));
        u.setCedula(jtxtCedula.getText());
        u.setNombres(jtxtNombres.getText());
        u.setApellidos(jtxtApellidos.getText());
        u.setCorreo(jtxtCorreo.getText());
        u.setDireccion(jtxtDireccion.getText());
        u.setTelefono(jtxtTelefono.getText());
        u.setUsuario(jtxtUsuario.getText());
        u.setActivo(activoActual);
        return u;
    }

    private void mostrar(modelo.Usuario u) {
        activoActual = u.isActivo();
        jtxtCedula.setText(u.getCedula());
        jtxtNombres.setText(u.getNombres());
        jtxtApellidos.setText(u.getApellidos());
        jtxtCorreo.setText(u.getCorreo());
        jtxtDireccion.setText(u.getDireccion());
        jtxtTelefono.setText(u.getTelefono());
        jtxtUsuario.setText(u.getUsuario());
        componentes.Combos.seleccionar(jcbxRol, u.getRol());
        limpiarClaves();
        jbtnDesactivar.setText(u.isActivo() ? "Desactivar" : "Activar");
        control.consulta(true);
    }

    private void limpiar() {
        for (javax.swing.JTextField t : new javax.swing.JTextField[]{jtxtCedula, jtxtNombres, jtxtApellidos,
            jtxtCorreo, jtxtDireccion, jtxtTelefono, jtxtUsuario}) {
            t.setText("");
        }
        jcbxRol.setSelectedIndex(-1);
        jbtnDesactivar.setText("Desactivar");
        limpiarClaves();
    }

    private void limpiarClaves() {
        jpswClave.setText("");
        jpswRepClave.setText("");
        mostrarFortaleza();
    }

    private void mostrarFortaleza() {
        int fortaleza = util.Seguridad.fortaleza(clave());
        jpgrSeguridad.setValue(fortaleza);
        jlblSeguridad.setText(fortaleza == 0 ? "Seguridad de la contraseña"
                : "Seguridad de la contraseña: " + util.Seguridad.nivel(fortaleza));
    }

    /** Usuario de prueba para revisar un dato con las mismas reglas del modelo. */
    private modelo.Usuario muestra() {
        return modelo.Rol.values()[0].crearUsuario();
    }

    /** Al crear la contraseña es obligatoria; al editar, solo si escribió una nueva. */
    private boolean cambiaClave() {
        return nuevoUsuario || !clave().isEmpty();
    }

    private String clave() {
        return new String(jpswClave.getPassword());
    }

    private String claveRepetida() {
        return new String(jpswRepClave.getPassword());
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
        jpnlDatosUsuario = new componentes.PanelGrupo();
        jlblCedula = new javax.swing.JLabel();
        jtxtCedula = new javax.swing.JTextField();
        jlblNombres = new javax.swing.JLabel();
        jtxtNombres = new javax.swing.JTextField();
        jlblApellidos = new javax.swing.JLabel();
        jtxtApellidos = new javax.swing.JTextField();
        jlblCorreo = new javax.swing.JLabel();
        jtxtCorreo = new javax.swing.JTextField();
        jlblDireccion = new javax.swing.JLabel();
        jtxtDireccion = new javax.swing.JTextField();
        jlblTelefono = new javax.swing.JLabel();
        jtxtTelefono = new javax.swing.JTextField();
        jlblCuenta = new javax.swing.JLabel();
        jsepCuenta = new javax.swing.JSeparator();
        jlblUsuario = new javax.swing.JLabel();
        jtxtUsuario = new javax.swing.JTextField();
        jlblRol = new javax.swing.JLabel();
        jcbxRol = new javax.swing.JComboBox<>();
        jlblClave = new javax.swing.JLabel();
        jpswClave = new javax.swing.JPasswordField();
        jlblRepClave = new javax.swing.JLabel();
        jpswRepClave = new javax.swing.JPasswordField();
        jlblSeguridad = new javax.swing.JLabel();
        jpgrSeguridad = new javax.swing.JProgressBar();
        jpnlBotones = new javax.swing.JPanel();
        jbtnNuevo = new componentes.BotonSistema();
        jbtnGuardar = new componentes.BotonSistema();
        jbtnEditar = new componentes.BotonSistema();
        jbtnDesactivar = new componentes.BotonSistema();
        jbtnCancelar = new componentes.BotonSistema();
        jpnlUsuariosReg = new componentes.PanelGrupo();
        barraBuscar = new componentes.BarraBusqueda();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblUsuarios = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jpnlContenido.setBackground(new java.awt.Color(255, 255, 255));
        jpnlContenido.setPreferredSize(new java.awt.Dimension(1098, 520));
        jpnlContenido.setLayout(new java.awt.BorderLayout(16, 0));

        jpnlDatosUsuario.setTitulo("Datos del usuario");
        jpnlDatosUsuario.setPreferredSize(new java.awt.Dimension(450, 400));
        jpnlDatosUsuario.setLayout(new java.awt.GridBagLayout());

        jlblCedula.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblCedula.setText("Cédula");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 3, 6);
        jpnlDatosUsuario.add(jlblCedula, gridBagConstraints);

        jtxtCedula.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtCedula.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 6);
        jpnlDatosUsuario.add(jtxtCedula, gridBagConstraints);

        jlblNombres.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblNombres.setText("Nombres");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 3, 6);
        jpnlDatosUsuario.add(jlblNombres, gridBagConstraints);

        jtxtNombres.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtNombres.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 6);
        jpnlDatosUsuario.add(jtxtNombres, gridBagConstraints);

        jlblApellidos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblApellidos.setText("Apellidos");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 3, 6);
        jpnlDatosUsuario.add(jlblApellidos, gridBagConstraints);

        jtxtApellidos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtApellidos.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 6);
        jpnlDatosUsuario.add(jtxtApellidos, gridBagConstraints);

        jlblCorreo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblCorreo.setText("Correo electrónico");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 3, 6);
        jpnlDatosUsuario.add(jlblCorreo, gridBagConstraints);

        jtxtCorreo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtCorreo.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 6);
        jpnlDatosUsuario.add(jtxtCorreo, gridBagConstraints);

        jlblDireccion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblDireccion.setText("Dirección");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 3, 6);
        jpnlDatosUsuario.add(jlblDireccion, gridBagConstraints);

        jtxtDireccion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtDireccion.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 6);
        jpnlDatosUsuario.add(jtxtDireccion, gridBagConstraints);

        jlblTelefono.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblTelefono.setText("Teléfono");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 3, 6);
        jpnlDatosUsuario.add(jlblTelefono, gridBagConstraints);

        jtxtTelefono.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtTelefono.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 6);
        jpnlDatosUsuario.add(jtxtTelefono, gridBagConstraints);

        jlblCuenta.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jlblCuenta.setText("Datos de la cuenta");
        jlblCuenta.setForeground(new java.awt.Color(0, 122, 122));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 2, 6);
        jpnlDatosUsuario.add(jlblCuenta, gridBagConstraints);

        jsepCuenta.setForeground(new java.awt.Color(210, 225, 228));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 6);
        jpnlDatosUsuario.add(jsepCuenta, gridBagConstraints);

        jlblUsuario.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblUsuario.setText("Usuario");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 3, 6);
        jpnlDatosUsuario.add(jlblUsuario, gridBagConstraints);

        jtxtUsuario.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jtxtUsuario.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 6);
        jpnlDatosUsuario.add(jtxtUsuario, gridBagConstraints);

        jlblRol.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblRol.setText("Rol");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 3, 6);
        jpnlDatosUsuario.add(jlblRol, gridBagConstraints);

        jcbxRol.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jcbxRol.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 6);
        jpnlDatosUsuario.add(jcbxRol, gridBagConstraints);

        jlblClave.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblClave.setText("Contraseña");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 3, 6);
        jpnlDatosUsuario.add(jlblClave, gridBagConstraints);

        jpswClave.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jpswClave.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 6);
        jpnlDatosUsuario.add(jpswClave, gridBagConstraints);

        jlblRepClave.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jlblRepClave.setText("Repetir contraseña");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 3, 6);
        jpnlDatosUsuario.add(jlblRepClave, gridBagConstraints);

        jpswRepClave.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jpswRepClave.setPreferredSize(new java.awt.Dimension(120, 32));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 5, 6);
        jpnlDatosUsuario.add(jpswRepClave, gridBagConstraints);

        jlblSeguridad.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jlblSeguridad.setText("Seguridad de la contraseña");
        jlblSeguridad.setForeground(new java.awt.Color(90, 105, 115));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 4, 6);
        jpnlDatosUsuario.add(jlblSeguridad, gridBagConstraints);

        jpgrSeguridad.setPreferredSize(new java.awt.Dimension(100, 6));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 8, 6);
        jpnlDatosUsuario.add(jpgrSeguridad, gridBagConstraints);

        jpnlBotones.setOpaque(false);
        jpnlBotones.setLayout(new java.awt.GridLayout(1, 5, 6, 0));

        jbtnNuevo.setText("Nuevo");
        jbtnNuevo.setPreferredSize(new java.awt.Dimension(70, 36));
        jbtnNuevo.addActionListener(this::jbtnNuevoActionPerformed);
        jpnlBotones.add(jbtnNuevo);

        jbtnGuardar.setText("Guardar");
        jbtnGuardar.setPrimario(true);
        jbtnGuardar.setPreferredSize(new java.awt.Dimension(70, 36));
        jbtnGuardar.addActionListener(this::jbtnGuardarActionPerformed);
        jpnlBotones.add(jbtnGuardar);

        jbtnEditar.setText("Editar");
        jbtnEditar.setPreferredSize(new java.awt.Dimension(70, 36));
        jbtnEditar.addActionListener(this::jbtnEditarActionPerformed);
        jpnlBotones.add(jbtnEditar);

        jbtnDesactivar.setText("Desactivar");
        jbtnDesactivar.setPreferredSize(new java.awt.Dimension(70, 36));
        jbtnDesactivar.addActionListener(this::jbtnDesactivarActionPerformed);
        jpnlBotones.add(jbtnDesactivar);

        jbtnCancelar.setText("Cancelar");
        jbtnCancelar.setPreferredSize(new java.awt.Dimension(70, 36));
        jbtnCancelar.addActionListener(this::jbtnCancelarActionPerformed);
        jpnlBotones.add(jbtnCancelar);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        jpnlDatosUsuario.add(jpnlBotones, gridBagConstraints);

        jpnlContenido.add(jpnlDatosUsuario, java.awt.BorderLayout.WEST);

        jpnlUsuariosReg.setTitulo("Usuarios registrados");
        jpnlUsuariosReg.setLayout(new java.awt.BorderLayout(0, 10));

        barraBuscar.setAyuda("Buscar por cédula, nombre o usuario");
        jpnlUsuariosReg.add(barraBuscar, java.awt.BorderLayout.NORTH);

        jScrollPane1.setViewportView(jtblUsuarios);

        jpnlUsuariosReg.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jpnlContenido.add(jpnlUsuariosReg, java.awt.BorderLayout.CENTER);

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

    private void jbtnDesactivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDesactivarActionPerformed
        cambiarEstado();
    }//GEN-LAST:event_jbtnDesactivarActionPerformed

    private void jbtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelarActionPerformed
        cancelar();
    }//GEN-LAST:event_jbtnCancelarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private componentes.BarraBusqueda barraBuscar;
    private javax.swing.JScrollPane jScrollPane1;
    private componentes.BotonSistema jbtnCancelar;
    private componentes.BotonSistema jbtnDesactivar;
    private componentes.BotonSistema jbtnEditar;
    private componentes.BotonSistema jbtnGuardar;
    private componentes.BotonSistema jbtnNuevo;
    private javax.swing.JComboBox<modelo.Rol> jcbxRol;
    private javax.swing.JLabel jlblApellidos;
    private javax.swing.JLabel jlblCedula;
    private javax.swing.JLabel jlblClave;
    private javax.swing.JLabel jlblCorreo;
    private javax.swing.JLabel jlblCuenta;
    private javax.swing.JLabel jlblDireccion;
    private javax.swing.JLabel jlblNombres;
    private javax.swing.JLabel jlblRepClave;
    private javax.swing.JLabel jlblRol;
    private javax.swing.JLabel jlblSeguridad;
    private javax.swing.JLabel jlblTelefono;
    private javax.swing.JLabel jlblUsuario;
    private javax.swing.JProgressBar jpgrSeguridad;
    private javax.swing.JPanel jpnlBotones;
    private javax.swing.JPanel jpnlContenido;
    private componentes.PanelGrupo jpnlDatosUsuario;
    private componentes.PanelGrupo jpnlUsuariosReg;
    private javax.swing.JPasswordField jpswClave;
    private javax.swing.JPasswordField jpswRepClave;
    private javax.swing.JSeparator jsepCuenta;
    private javax.swing.JTable jtblUsuarios;
    private javax.swing.JTextField jtxtApellidos;
    private javax.swing.JTextField jtxtCedula;
    private javax.swing.JTextField jtxtCorreo;
    private javax.swing.JTextField jtxtDireccion;
    private javax.swing.JTextField jtxtNombres;
    private javax.swing.JTextField jtxtTelefono;
    private javax.swing.JTextField jtxtUsuario;
    // End of variables declaration//GEN-END:variables
}
