package componentes;

import javax.swing.JButton;

/** Botón con el estilo del sistema (blanco, verde suave al pasar el mouse, verde al hacer clic). */
public class BotonSistema extends JButton {

    private String rutaIcono = "";
    private int tamanoIcono = 20;
    private boolean textoAbajo = false;
    private boolean primario = false;

    public BotonSistema() {
        this("Botón");
    }

    public BotonSistema(String texto) {
        super(texto);
        util.Estilos.boton(this);
    }

    public String getRutaIcono() { return rutaIcono; }

    public void setRutaIcono(String rutaIcono) {
        this.rutaIcono = rutaIcono == null ? "" : rutaIcono;
        aplicarIcono();
    }

    public int getTamanoIcono() { return tamanoIcono; }

    public void setTamanoIcono(int tamanoIcono) {
        this.tamanoIcono = tamanoIcono;
        aplicarIcono();
    }

    public boolean isTextoAbajo() { return textoAbajo; }

    public void setTextoAbajo(boolean textoAbajo) {
        this.textoAbajo = textoAbajo;
        aplicarIcono();
    }

    public boolean isPrimario() { return primario; }

    public void setPrimario(boolean primario) {
        this.primario = primario;
        putClientProperty("primario", primario);
        util.Estilos.pintarBoton(this);
    }

    @Override
    public java.awt.Dimension getMinimumSize() {
        java.awt.Dimension min = super.getMinimumSize();
        return new java.awt.Dimension(min.width, Math.max(min.height, getPreferredSize().height));
    }

    private void aplicarIcono() {
        if (!rutaIcono.isEmpty()) {
            util.Estilos.icono(this, rutaIcono, tamanoIcono, textoAbajo);
        }
    }
}
