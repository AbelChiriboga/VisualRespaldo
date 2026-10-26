package modelo;

import util.Validaciones;

/** Categoría de libros (Ingeniería de Software, Bases de Datos, Literatura...). */
public class Categoria extends Entidad {

    private String nombre;

    private int cantidadLibros;
    private int cantidadEjemplares;

    public Categoria() {
    }

    public Categoria(String nombre) {
        setNombre(nombre);
    }

    public String getNombre() {
        return nombre;
    }

    public final void setNombre(String nombre) {
        this.nombre = Validaciones.maximo(Validaciones.obligatorio(nombre, "nombre de la categoría"), 60, "nombre");
    }

    public int getCantidadLibros() {
        return cantidadLibros;
    }

    public void setCantidadLibros(int cantidadLibros) {
        this.cantidadLibros = cantidadLibros;
    }

    public int getCantidadEjemplares() {
        return cantidadEjemplares;
    }

    public void setCantidadEjemplares(int cantidadEjemplares) {
        this.cantidadEjemplares = cantidadEjemplares;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
