package modelo;

import modelo.Administrador;
import modelo.Bibliotecario;
import modelo.Usuario;
import modelo.ValorBD;

/** Rol del personal. */
public enum Rol implements ValorBD {

    ADMINISTRADOR("ADMIN", "Administrador") {
        @Override
        public Usuario crearUsuario() {
            return new Administrador();
        }
    },
    BIBLIOTECARIO("BIBLIOTECARIO", "Bibliotecario") {
        @Override
        public Usuario crearUsuario() {
            return new Bibliotecario();
        }
    };

    private final String valorBD;
    private final String descripcion;

    Rol(String valorBD, String descripcion) {
        this.valorBD = valorBD;
        this.descripcion = descripcion;
    }

    public abstract Usuario crearUsuario();

    @Override
    public String getValorBD() {
        return valorBD;
    }

    @Override
    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
