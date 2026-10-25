package util;

import java.awt.*;
import java.awt.event.*;

/** Las ventanas no tienen la barra de título de Windows (setUndecorated). */
public final class Arrastre {

    private Arrastre() {
    }

    public static void permitir(Window ventana, Component zona) {
        MouseAdapter mover = new MouseAdapter() {
            private Point distancia;

            @Override
            public void mousePressed(MouseEvent e) {
                Point mouse = e.getLocationOnScreen();
                distancia = new Point(mouse.x - ventana.getX(), mouse.y - ventana.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point mouse = e.getLocationOnScreen();
                ventana.setLocation(mouse.x - distancia.x, mouse.y - distancia.y);
            }
        };
        zona.addMouseListener(mover);
        zona.addMouseMotionListener(mover);
    }
}
