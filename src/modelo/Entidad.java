package modelo;

/** Base de las clases que en la BD tienen una llave numérica autoincremental (carrera, autor, categoría, libro. */
public abstract class Entidad {

    private int id;

    public int getId() {
        return id;
    }

    public final void setId(int id) {
        if (id < 0) {
            throw new excepciones.ValidacionException("El id no puede ser negativo.");
        }
        this.id = id;
    }

    public boolean esNuevo() {
        return id == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Entidad otra = (Entidad) o;
        return id != 0 && id == otra.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
