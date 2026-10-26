package modelo;

import util.Validaciones;

/** Copia física de un libro, identificada por su código de inventario. */
public class Ejemplar extends Entidad {

    private Libro libro;
    private String codigoInventario;
    private String ubicacion;
    private EstadoEjemplar estado = EstadoEjemplar.DISPONIBLE;

    public Ejemplar() {
    }

    public Ejemplar(Libro libro, String codigoInventario, String ubicacion) {
        setLibro(libro);
        setCodigoInventario(codigoInventario);
        setUbicacion(ubicacion);
    }

    public boolean estaDisponible() {
        return estado.esPrestable();
    }

    public Libro getLibro() {
        return libro;
    }

    public final void setLibro(Libro libro) {
        this.libro = Validaciones.requerido(libro, "el libro");
    }

    public String getCodigoInventario() {
        return codigoInventario;
    }

    public final void setCodigoInventario(String codigoInventario) {
        this.codigoInventario = normalizarCodigo(codigoInventario);
    }

    public static final String PREFIJO = "LIB";
    private static final java.util.regex.Pattern FORMATO =
            java.util.regex.Pattern.compile("^(?:([A-Z]{2,5})\\s*-?\\s*)?0*(\\d{1,6})$");

    public static String normalizarCodigo(String codigo) {
        String escrito = Validaciones.obligatorio(codigo, "código de inventario").toUpperCase();
        java.util.regex.Matcher m = FORMATO.matcher(escrito);
        if (!m.matches() || Integer.parseInt(m.group(2)) == 0) {
            throw new excepciones.ValidacionException("El código debe ser como LIB-0001.");
        }
        String prefijo = m.group(1) != null ? m.group(1) : PREFIJO;
        return prefijo + "-" + String.format("%04d", Integer.parseInt(m.group(2)));
    }

    public static String codigoNumero(int numero) {
        return PREFIJO + "-" + String.format("%04d", numero);
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public final void setUbicacion(String ubicacion) {
        this.ubicacion = Validaciones.maximo(Validaciones.opcional(ubicacion), 50, "ubicación");
    }

    public EstadoEjemplar getEstado() {
        return estado;
    }

    public void setEstado(EstadoEjemplar estado) {
        this.estado = Validaciones.requerido(estado, "el estado");
    }

    @Override
    public String toString() {
        return codigoInventario + (libro == null ? "" : " - " + libro.getTitulo());
    }
}
