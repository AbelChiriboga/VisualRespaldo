package reportes;

/** Multas generadas que todavía no se pagan. */
public class ReporteMultas extends ReporteBase {

    @Override
    protected String archivo() {
        return "MultasPendientes";
    }

    @Override
    public String getTitulo() {
        return "Multas pendientes de pago";
    }
}
