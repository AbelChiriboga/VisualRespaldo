package vista;

import java.util.*;
import java.util.function.Supplier;
import modelo.*;

/** Decide qué menú abrir después del login SIN if/else ni instanceof. */
public final class MenuPorRol {

    private static final Map<Rol, Supplier<javax.swing.JFrame>> MENUS = new EnumMap<>(Rol.class);

    static {
        MENUS.put(Rol.ADMINISTRADOR, MenuAdmin::new);
        MENUS.put(Rol.BIBLIOTECARIO, MenuBibliotecario::new);
    }

    private MenuPorRol() {
    }

    public static javax.swing.JFrame crearPara(Usuario usuario) {
        return MENUS.get(usuario.getRol()).get();
    }
}
