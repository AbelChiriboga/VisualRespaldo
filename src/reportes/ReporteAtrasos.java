package reportes;

/** Préstamos que pasaron su fecha límite, con la multa estimada a hoy. */
public class ReporteAtrasos extends ReporteBase {

    @Override
    protected String archivo() {
        return "Atrasos";
    }

    @Override
    public String getTitulo() {
        return "Préstamos atrasados";
    }
}
