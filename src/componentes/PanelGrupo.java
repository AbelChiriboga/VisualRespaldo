package componentes;

import javax.swing.JPanel;

/** Panel blanco con borde verde y título (reemplaza a util.Estilos.grupo en cada frame). */
public class PanelGrupo extends JPanel {

    private String titulo = "Grupo";

    public PanelGrupo() {
        util.Estilos.grupo(this, titulo);
    }

    public PanelGrupo(String titulo) {
        this.titulo = titulo;
        util.Estilos.grupo(this, titulo);
    }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
        util.Estilos.grupo(this, titulo);
    }
}
