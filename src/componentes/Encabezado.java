package componentes;

import componentes.BotonSistema;
import java.awt.*;
import javax.swing.*;

/** Encabezado verde común a todas las ventanas. */
public class Encabezado extends JPanel {

    private final BotonSistema botonRegresar = new BotonSistema("Regresar");
    private final BotonSistema botonCerrarSesion = new BotonSistema("Cerrar sesión");
    private final BotonSistema botonSalir = new BotonSistema("Salir");

    private final JLabel lblTitulo = new JLabel("BIBLIOTECA DIGITAL FISEI");
    private final JLabel lblSubtitulo = new JLabel("SUBTÍTULO");
    private final JLabel lblRol = new JLabel("USUARIO");
    private final JLabel lblNombre = new JLabel("NOMBRE_APELLIDO");
    private final JLabel lblCedula = new JLabel("C.I: 0000000000");
    private final JLabel lblFechaHora = new JLabel(" ");

    private final Timer reloj;
    private boolean modoMenu = false;
    private String rol = "USUARIO";

    public Encabezado() {
        super(new BorderLayout(20, 0));
        setBackground(util.Estilos.VERDE);
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 16));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izquierda.setOpaque(false);
        botonRegresar.setRutaIcono("Regresar.png");
        botonCerrarSesion.setRutaIcono("Cerrar.png");
        botonSalir.setRutaIcono("Exit.png");
        for (BotonSistema b : new BotonSistema[]{botonRegresar, botonCerrarSesion, botonSalir}) {
            b.setTamanoIcono(44);
            b.setTextoAbajo(true);
            b.setPreferredSize(new Dimension(110, 100));
            izquierda.add(b);
        }
        botonSalir.setForeground(new Color(140, 30, 30));

        JPanel centro = new JPanel(new GridLayout(2, 1));
        centro.setOpaque(false);
        estiloTexto(lblTitulo, new Font("Segoe UI Black", Font.BOLD, 36), SwingConstants.CENTER);
        estiloTexto(lblSubtitulo, new Font("Segoe UI", Font.PLAIN, 18), SwingConstants.CENTER);
        centro.add(lblTitulo);
        centro.add(lblSubtitulo);

        JPanel derecha = new JPanel(new GridLayout(4, 1));
        derecha.setOpaque(false);
        estiloTexto(lblRol, new Font("Segoe UI", Font.BOLD, 26), SwingConstants.RIGHT);
        estiloTexto(lblNombre, new Font("Segoe UI", Font.PLAIN, 16), SwingConstants.RIGHT);
        estiloTexto(lblCedula, new Font("Segoe UI", Font.PLAIN, 14), SwingConstants.RIGHT);
        estiloTexto(lblFechaHora, new Font("Segoe UI", Font.PLAIN, 14), SwingConstants.RIGHT);
        derecha.add(lblRol);
        derecha.add(lblNombre);
        derecha.add(lblCedula);
        derecha.add(lblFechaHora);

        add(izquierda, BorderLayout.WEST);
        add(centro, BorderLayout.CENTER);
        add(derecha, BorderLayout.EAST);

        reloj = new Timer(1000, e -> actualizarHora());
        reloj.setInitialDelay(0);
        setModoMenu(false);
    }

    private void estiloTexto(JLabel l, Font f, int alineacion) {
        l.setFont(f);
        l.setForeground(Color.WHITE);
        l.setHorizontalAlignment(alineacion);
    }

    private void actualizarHora() {
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        lblFechaHora.setText("Día: " + ahora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + "    Hora: " + ahora.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    @Override
    public void addNotify() {
        super.addNotify();
        reloj.start();
    }

    @Override
    public void removeNotify() {
        reloj.stop();
        super.removeNotify();
    }

    public String getSubtitulo() { return lblSubtitulo.getText(); }
    public void setSubtitulo(String subtitulo) { lblSubtitulo.setText(subtitulo); }

    public boolean isModoMenu() { return modoMenu; }

    public void setModoMenu(boolean modoMenu) {
        this.modoMenu = modoMenu;
        botonRegresar.setVisible(!modoMenu);
        botonCerrarSesion.setVisible(modoMenu);
        botonSalir.setVisible(modoMenu);
        lblRol.setText(modoMenu ? "BIENVENIDO" : rol);
    }

    public void setUsuario(String nombre, String cedula, String rol) {
        lblNombre.setText(nombre);
        lblCedula.setText("C.I: " + cedula);
        this.rol = rol.toUpperCase();
        lblRol.setText(modoMenu ? "BIENVENIDO" : this.rol);
    }

    public BotonSistema getBotonRegresar() { return botonRegresar; }
    public BotonSistema getBotonCerrarSesion() { return botonCerrarSesion; }
    public BotonSistema getBotonSalir() { return botonSalir; }
}
