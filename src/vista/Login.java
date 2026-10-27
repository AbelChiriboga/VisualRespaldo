package vista;

/** Inicio de sesión del personal y consulta del estudiante por cédula. */
public class Login extends javax.swing.JFrame {

    private final componentes.Formulario formulario = new componentes.Formulario(this);
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Login.class.getName());

    private final servicio.AutenticacionService autenticacion = new servicio.AutenticacionService();

    public Login() {
        setUndecorated(true);
        initComponents();
        configurarEntradas();
        util.Arrastre.permitir(this, getContentPane());
        InicializarElementos();
        armarLogin();
        estiloSalir();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                jtxtUsuario.requestFocusInWindow();
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                salir();
            }
        });
    }

    /** Caracteres permitidos, máximo de cada caja (según la base de datos) y validación con Enter. */
    private void configurarEntradas() {
        util.TipoEntrada.USUARIO.aplicar(jtxtUsuario, 50);
        util.TipoEntrada.CLAVE.aplicar(jpswClave, 50);
        util.TipoEntrada.NUMEROS.aplicar(jtxtCedulaE, 10);
        formulario
                .campo(jtxtUsuario, () -> util.Validaciones.obligatorio(jtxtUsuario.getText(), "usuario"))
                .campo(jpswClave, () -> util.Validaciones.obligatorio(new String(jpswClave.getPassword()), "contraseña"))
                .armar();
    }
    
    private void estiloSalir() {
        util.Estilos.boton(jbtnSalir);
        jbtnSalir.setToolTipText("Cerrar el sistema");
        jbtnSalir.setPreferredSize(new java.awt.Dimension(120, 40));
        util.Estilos.icono(jbtnSalir, "Exit.png", 50, false);  
        jbtnSalir.setBackground(util.Estilos.VERDE);
    }
    private void armarLogin() {
        java.awt.Color verde = new java.awt.Color(0, 153, 153);
        java.awt.Dimension campo = new java.awt.Dimension(320, 34);
        java.awt.Dimension boton = new java.awt.Dimension(320, 38);

        jLabel1.setText("Usuario:");
        jLabel6.setText("Contraseña:");
        jLabel8.setText("Cédula:");
        jtglPersonal.setText("Personal");
        jtglEstudiante.setText("Estudiante");
        jbtnIngresar.setText("Ingresar");
        jbtnConsultar.setText("Consultar");

        javax.swing.JComponent[] cajas = {jtxtUsuario, jpswClave, jtxtCedulaE};
        for (javax.swing.JComponent c : cajas) {
            c.setPreferredSize(campo);
            c.setMinimumSize(campo);
        }
        for (javax.swing.JButton b : new javax.swing.JButton[]{jbtnIngresar, jbtnConsultar}) {
            b.setPreferredSize(boton);
            b.setMinimumSize(boton);
            b.setBackground(verde);
            b.setForeground(java.awt.Color.WHITE);
            b.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15));
            b.setFocusPainted(false);
        }

        apilar(jpnlPersonal, jLabel1, jtxtUsuario, jLabel6, jpswClave, jbtnIngresar);

        apilar(jpnlEstudiante, jLabel8, jtxtCedulaE, jbtnConsultar);

        jpnlFormularios.removeAll();
        jpnlFormularios.setLayout(new java.awt.CardLayout());
        jpnlFormularios.setBackground(java.awt.Color.WHITE);
        jpnlFormularios.add(jpnlPersonal, "personal");
        jpnlFormularios.add(jpnlEstudiante, "estudiante");

        jsplSelector.setLeftComponent(jtglPersonal);
        jsplSelector.setRightComponent(jtglEstudiante);
        jsplSelector.setEnabled(false);
        jsplSelector.setDividerSize(0);
        jsplSelector.setResizeWeight(0.5);
        jsplSelector.setBorder(null);
        java.awt.Dimension selector = new java.awt.Dimension(320, 38);
        jsplSelector.setPreferredSize(selector);
        jsplSelector.setMinimumSize(selector);
        javax.swing.SwingUtilities.invokeLater(() -> jsplSelector.setDividerLocation(0.5));

        javax.swing.ButtonGroup grupoTipo = new javax.swing.ButtonGroup();
        grupoTipo.add(jtglPersonal);
        grupoTipo.add(jtglEstudiante);

        jpnlIniciarSesion.removeAll();
        jpnlIniciarSesion.setPreferredSize(null);
        jpnlIniciarSesion.setCursor(java.awt.Cursor.getDefaultCursor());
        jpnlIniciarSesion.setLayout(new java.awt.GridBagLayout());
        jpnlIniciarSesion.setBackground(java.awt.Color.WHITE);
        jpnlIniciarSesion.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(60, 60, 60), 1),
                javax.swing.BorderFactory.createEmptyBorder(36, 50, 40, 50)));

        java.awt.GridBagConstraints g = new java.awt.GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        g.insets = new java.awt.Insets(0, 0, 24, 0);
        jpnlIniciarSesion.add(jLabel2, g);

        g.gridy = 1;
        g.insets = new java.awt.Insets(0, 0, 24, 0);
        jpnlIniciarSesion.add(jsplSelector, g);

        g.gridy = 2;
        g.insets = new java.awt.Insets(0, 0, 0, 0);
        jpnlIniciarSesion.add(jpnlFormularios, g);

        jpnlPrincipal.removeAll();
        jpnlPrincipal.setLayout(new java.awt.GridBagLayout());
        jpnlPrincipal.add(jpnlIniciarSesion);

        jtglPersonal.setSelected(true);
        mostrarTarjeta("personal");

        jpnlPrincipal.revalidate();
        jpnlPrincipal.repaint();

    }

    private void apilar(javax.swing.JPanel panel, javax.swing.JComponent... comps) {
        panel.removeAll();
        panel.setLayout(new java.awt.GridBagLayout());
        panel.setBackground(java.awt.Color.WHITE);

        java.awt.GridBagConstraints g = new java.awt.GridBagConstraints();
        g.gridx = 0;
        g.fill = java.awt.GridBagConstraints.HORIZONTAL;
        g.anchor = java.awt.GridBagConstraints.WEST;

        int fila = 0;
        for (javax.swing.JComponent c : comps) {
            g.gridy = fila++;
            if (c instanceof javax.swing.JLabel) {
                ((javax.swing.JLabel) c).setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 15));
                g.insets = new java.awt.Insets(0, 0, 6, 0);
            } else if (c instanceof javax.swing.JButton) {
                g.insets = new java.awt.Insets(14, 0, 0, 0);
            } else {
                g.insets = new java.awt.Insets(0, 0, 16, 0);
            }
            panel.add(c, g);
        }

        g.gridy = fila;
        g.weighty = 1;
        g.fill = java.awt.GridBagConstraints.BOTH;
        javax.swing.JPanel relleno = new javax.swing.JPanel();
        relleno.setOpaque(false);
        panel.add(relleno, g);
    }

    private void mostrarTarjeta(String nombre) {
        java.awt.CardLayout cl = (java.awt.CardLayout) jpnlFormularios.getLayout();
        cl.show(jpnlFormularios, nombre);

        java.awt.Color verde = new java.awt.Color(0, 153, 153);
        estiloToggle(jtglPersonal, nombre.equals("personal"), verde);
        estiloToggle(jtglEstudiante, nombre.equals("estudiante"), verde);

        getRootPane().setDefaultButton(nombre.equals("personal") ? jbtnIngresar : jbtnConsultar);
        javax.swing.SwingUtilities.invokeLater(() -> (nombre.equals("personal") ? jtxtUsuario : jtxtCedulaE).requestFocusInWindow());
    }

    private void estiloToggle(javax.swing.JToggleButton t, boolean activo, java.awt.Color verde) {
        t.setContentAreaFilled(false);
        t.setOpaque(true);
        t.setFocusPainted(false);
        t.setFont(new java.awt.Font("Segoe UI", activo ? java.awt.Font.BOLD : java.awt.Font.PLAIN, 14));
        t.setBackground(activo ? verde : java.awt.Color.WHITE);
        t.setForeground(activo ? java.awt.Color.WHITE : new java.awt.Color(60, 70, 80));
        t.setBorder(javax.swing.BorderFactory.createLineBorder(verde, 1));
    }

    private void iniciarSesion() {
        if (!formulario.validar()) {
            return;
        }
        boolean ingreso = util.Mensajes.ejecutar(this, () -> {
            modelo.Usuario usuario = autenticacion.iniciarSesion(jtxtUsuario.getText(), new String(jpswClave.getPassword()));
            util.Navegacion.cambiar(this, MenuPorRol.crearPara(usuario));
        });
        if (!ingreso) {
            limpiarClave();
        }
    }

    private void consultarEstudiante() {
        util.Mensajes.ejecutar(this, () -> mostrarEstudiante(autenticacion.consultarEstudiante(jtxtCedulaE.getText())));
    }

    private void mostrarEstudiante(modelo.Estudiante estudiante) {
        jtxtCedulaE.setText("");
        util.Navegacion.abrir(this, new PanelEstudiante(estudiante));
    }

    private void limpiarClave() {
        jpswClave.setText("");
        jpswClave.requestFocus();
    }

    private void salir() {
        if (util.Mensajes.confirmar(this, "¿Está seguro de salir del sistema?")) {
            System.exit(0);
        }
    }

   public void InicializarElementos() {
        javax.swing.ImageIcon original = new javax.swing.ImageIcon(getClass().getResource("/imagenes/logo.png"));
        java.awt.Image escalada = original.getImage().getScaledInstance(213, 183, java.awt.Image.SCALE_SMOOTH);
        jlblLogo.setIcon(new javax.swing.ImageIcon(escalada));

        setPreferredSize(null);
        getContentPane().setPreferredSize(new java.awt.Dimension(1130, 700));
        setMinimumSize(new java.awt.Dimension(1000, 640));
        pack();
        setLocationRelativeTo(null);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpnlPrincipal = new javax.swing.JPanel();
        jpnlIniciarSesion = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jsplSelector = new javax.swing.JSplitPane();
        jtglPersonal = new javax.swing.JToggleButton();
        jtglEstudiante = new javax.swing.JToggleButton();
        jpnlFormularios = new javax.swing.JPanel();
        jpnlPersonal = new javax.swing.JPanel();
        jbtnIngresar = new javax.swing.JButton();
        jpswClave = new javax.swing.JPasswordField();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jtxtUsuario = new javax.swing.JTextField();
        jpnlEstudiante = new javax.swing.JPanel();
        jbtnConsultar = new javax.swing.JButton();
        jtxtCedulaE = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jlblLogo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jbtnSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(825, 510));

        jpnlPrincipal.setBackground(new java.awt.Color(248, 242, 242));
        jpnlPrincipal.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jpnlPrincipal.setPreferredSize(new java.awt.Dimension(670, 700));

        jpnlIniciarSesion.setBackground(new java.awt.Color(255, 255, 255));
        jpnlIniciarSesion.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jpnlIniciarSesion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jpnlIniciarSesion.setPreferredSize(new java.awt.Dimension(526, 102));

        jLabel2.setFont(new java.awt.Font("Yu Gothic", 1, 24)); // NOI18N
        jLabel2.setText("Iniciar Sesión");

        jsplSelector.setDividerSize(0);
        jsplSelector.setResizeWeight(0.5);
        jsplSelector.setEnabled(false);

        jtglPersonal.setText("Personal");
        jtglPersonal.addActionListener(this::jtglPersonalActionPerformed);
        jsplSelector.setLeftComponent(jtglPersonal);

        jtglEstudiante.setText("Estudiante");
        jtglEstudiante.addActionListener(this::jtglEstudianteActionPerformed);
        jsplSelector.setRightComponent(jtglEstudiante);

        jpnlFormularios.setBackground(new java.awt.Color(255, 255, 255));
        jpnlFormularios.setLayout(new java.awt.CardLayout());

        jpnlPersonal.setBackground(new java.awt.Color(255, 255, 255));

        jbtnIngresar.setBackground(new java.awt.Color(0, 153, 153));
        jbtnIngresar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbtnIngresar.setForeground(new java.awt.Color(255, 255, 255));
        jbtnIngresar.setText("Ingresar");
        jbtnIngresar.addActionListener(this::jbtnIngresarActionPerformed);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Contraseña:");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("Cedula:");

        javax.swing.GroupLayout jpnlPersonalLayout = new javax.swing.GroupLayout(jpnlPersonal);
        jpnlPersonal.setLayout(jpnlPersonalLayout);
        jpnlPersonalLayout.setHorizontalGroup(
            jpnlPersonalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlPersonalLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jpnlPersonalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnIngresar, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jpswClave, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpnlPersonalLayout.setVerticalGroup(
            jpnlPersonalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnlPersonalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(3, 3, 3)
                .addComponent(jtxtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpswClave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnIngresar)
                .addContainerGap())
        );

        jpnlFormularios.add(jpnlPersonal, "personal");

        jpnlEstudiante.setBackground(new java.awt.Color(255, 255, 255));

        jbtnConsultar.setText("Consultar");
        jbtnConsultar.addActionListener(this::jbtnConsultarActionPerformed);

        jLabel8.setText("Cédula:");

        javax.swing.GroupLayout jpnlEstudianteLayout = new javax.swing.GroupLayout(jpnlEstudiante);
        jpnlEstudiante.setLayout(jpnlEstudianteLayout);
        jpnlEstudianteLayout.setHorizontalGroup(
            jpnlEstudianteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlEstudianteLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jpnlEstudianteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnConsultar)
                    .addComponent(jtxtCedulaE, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(379, Short.MAX_VALUE))
        );
        jpnlEstudianteLayout.setVerticalGroup(
            jpnlEstudianteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnlEstudianteLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtxtCedulaE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnConsultar)
                .addGap(127, 127, 127))
        );

        jpnlFormularios.add(jpnlEstudiante, "estudiante");

        javax.swing.GroupLayout jpnlIniciarSesionLayout = new javax.swing.GroupLayout(jpnlIniciarSesion);
        jpnlIniciarSesion.setLayout(jpnlIniciarSesionLayout);
        jpnlIniciarSesionLayout.setHorizontalGroup(
            jpnlIniciarSesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlIniciarSesionLayout.createSequentialGroup()
                .addGroup(jpnlIniciarSesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpnlIniciarSesionLayout.createSequentialGroup()
                        .addGap(167, 167, 167)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpnlIniciarSesionLayout.createSequentialGroup()
                        .addGap(244, 244, 244)
                        .addComponent(jsplSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23)))
                .addComponent(jpnlFormularios, javax.swing.GroupLayout.PREFERRED_SIZE, 491, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jpnlIniciarSesionLayout.setVerticalGroup(
            jpnlIniciarSesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlIniciarSesionLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jsplSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jpnlIniciarSesionLayout.createSequentialGroup()
                .addComponent(jpnlFormularios, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 57, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jpnlPrincipalLayout = new javax.swing.GroupLayout(jpnlPrincipal);
        jpnlPrincipal.setLayout(jpnlPrincipalLayout);
        jpnlPrincipalLayout.setHorizontalGroup(
            jpnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlPrincipalLayout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(jpnlIniciarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(112, Short.MAX_VALUE))
        );
        jpnlPrincipalLayout.setVerticalGroup(
            jpnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpnlPrincipalLayout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addComponent(jpnlIniciarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(280, Short.MAX_VALUE))
        );

        getContentPane().add(jpnlPrincipal, java.awt.BorderLayout.CENTER);

        jPanel3.setBackground(new java.awt.Color(0, 153, 153));
        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setPreferredSize(new java.awt.Dimension(460, 700));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("BIBLIOTECA DIGITAL");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("FISEI");

        jLabel5.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Préstamos y devoluciones verificados con ");

        jLabel7.setFont(new java.awt.Font("Cambria", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("tu huella dactilar. Nadie presta tu nombre");

        jbtnSalir.setBackground(new java.awt.Color(0, 153, 153));
        jbtnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Exit.png"))); // NOI18N
        jbtnSalir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnSalir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbtnSalir.setIconTextGap(8);
        jbtnSalir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jbtnSalir.addActionListener(this::jbtnSalirActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jbtnSalir)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addComponent(jlblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(80, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addComponent(jlblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(24, 24, 24)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 228, Short.MAX_VALUE)
                .addComponent(jbtnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.WEST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnIngresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnIngresarActionPerformed
        iniciarSesion();
    }//GEN-LAST:event_jbtnIngresarActionPerformed

    private void jbtnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnConsultarActionPerformed
        consultarEstudiante();
    }//GEN-LAST:event_jbtnConsultarActionPerformed

    private void jtglEstudianteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtglEstudianteActionPerformed
        mostrarTarjeta("estudiante");
    }//GEN-LAST:event_jtglEstudianteActionPerformed

    private void jtglPersonalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtglPersonalActionPerformed
        mostrarTarjeta("personal");
    }//GEN-LAST:event_jtglPersonalActionPerformed

    private void jbtnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSalirActionPerformed
        salir();
    }//GEN-LAST:event_jbtnSalirActionPerformed

    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton jbtnConsultar;
    private javax.swing.JButton jbtnIngresar;
    private javax.swing.JButton jbtnSalir;
    private javax.swing.JLabel jlblLogo;
    private javax.swing.JPanel jpnlEstudiante;
    private javax.swing.JPanel jpnlFormularios;
    private javax.swing.JPanel jpnlIniciarSesion;
    private javax.swing.JPanel jpnlPersonal;
    private javax.swing.JPanel jpnlPrincipal;
    private javax.swing.JPasswordField jpswClave;
    private javax.swing.JSplitPane jsplSelector;
    private javax.swing.JToggleButton jtglEstudiante;
    private javax.swing.JToggleButton jtglPersonal;
    private javax.swing.JTextField jtxtCedulaE;
    private javax.swing.JTextField jtxtUsuario;
    // End of variables declaration//GEN-END:variables
}
