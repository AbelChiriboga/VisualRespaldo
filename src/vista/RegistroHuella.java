package vista;

import excepciones.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.Consumer;
import modelo.Dedo;

/** Registro AUTOMÁTICO de huellas en Estudiantes: lee el dedo elegido; se pueden capturar varios antes de guardar. */
public class RegistroHuella {

    private static final int PAUSA_AVISO_MS = 1500;

    private final java.awt.Window ventana;
    private final componentes.PanelHuella panel;
    private final servicio.BiometriaService servicioHuella = new servicio.BiometriaService();
    private final Map<Reaccion, Consumer<BibliotecaException>> reacciones = new EnumMap<>(Reaccion.class);
    private final Map<Dedo, byte[]> capturadas = new EnumMap<>(Dedo.class);
    private final Map<Dedo, BufferedImage> vistas = new EnumMap<>(Dedo.class);
    private volatile BufferedImage ultimaImagen;
    private modelo.Estudiante estudiante;
    private boolean activo;
    private boolean leyendo;
    private boolean reiniciarAlTerminar;
    private int lecturaVigente;

    public RegistroHuella(java.awt.Window ventana, componentes.PanelHuella panel) {
        this.ventana = ventana;
        this.panel = panel;
        reacciones.put(Reaccion.REINTENTAR, e -> escuchar());
        reacciones.put(Reaccion.REINTENTAR_AVISANDO, e -> avisarYReintentar(e.getMessage()));
        reacciones.put(Reaccion.DETENER_LECTURA, this::lecturaDetenida);
        reacciones.put(Reaccion.ALERTA, e -> esperarOtraVez(e.getMessage()));
        reacciones.put(Reaccion.MOSTRAR_ERROR, e -> {
            util.Mensajes.error(ventana, e);
            esperarOtraVez("No se pudo registrar la huella.");
        });
        resaltarDedosConHuella();
        panel.getCbxDedo().addActionListener(e -> dedoCambiado());
        panel.getBotonRepetir().addActionListener(e -> registrarOtraVez());
        ventana.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                desactivar();
            }
        });
        desactivar();
    }

    /** Huellas capturadas que se guardan al presionar Guardar. */
    public Map<Dedo, byte[]> getCapturadas() {
        return capturadas;
    }

    /** Nuevo o Editar: empieza a leer el dedo elegido. */
    public void activar(modelo.Estudiante e) {
        desactivar();
        estudiante = e;
        panel.getCbxDedo().setSelectedItem(primerDedoLibre());
        panel.getCbxDedo().setEnabled(true);
        activo = true;
        escuchar();
    }

    /** Solo muestra las huellas del estudiante (sin leer). */
    public void mostrar(modelo.Estudiante e) {
        desactivar();
        estudiante = e;
        panel.getCbxDedo().repaint();
        panel.setInfo(registradas().isEmpty() ? "Sin huellas registradas." : "Registradas: " + registradas() + ".");
    }

    public void desactivar() {
        activo = false;
        estudiante = null;
        capturadas.clear();
        vistas.clear();
        detener();
        panel.getCbxDedo().setEnabled(false);
        panel.getCbxDedo().repaint();
        panel.mostrarRepetir(false);
        panel.reiniciar();
        panel.setInfo(" ");
    }

    public void lectorCambio(biometria.EstadoLector estado) {
        if (!leyendo && !capturadas.containsKey(dedo())) {
            panel.mostrarLector(estado);
        }
        if (estado.permiteCapturar()) {
            escuchar();
        }
    }

    /** Borra la captura del dedo elegido y lo vuelve a leer. */
    private void registrarOtraVez() {
        capturadas.remove(dedo());
        vistas.remove(dedo());
        panel.getCbxDedo().repaint();
        panel.mostrarRepetir(false);
        panel.reiniciar();
        reiniciarLectura();
    }

    /** Al cambiar de dedo: si ya se capturó, muestra su vista previa; si no, empieza a leerlo. */
    private void dedoCambiado() {
        if (!activo) {
            return;
        }
        panel.mostrarRepetir(false);
        if (capturadas.containsKey(dedo())) {
            detener();
            mostrarCapturada(dedo());
        } else {
            panel.reiniciar();
            reiniciarLectura();
        }
    }

    private Dedo dedo() {
        return (Dedo) panel.getCbxDedo().getSelectedItem();
    }

    private void escuchar() {
        if (!activo || leyendo || capturadas.containsKey(dedo()) || panel.getBotonRepetir().isVisible()
                || !biometria.MonitorLector.getInstancia().getEstado().permiteCapturar()) {
            return;
        }
        leyendo = true;
        int esta = ++lecturaVigente;
        Dedo dedo = dedo();
        modelo.Estudiante est = estudiante;
        ultimaImagen = null;
        panel.iniciarLectura();
        panel.mostrarAvance(0, nota(dedo) + "Coloque el dedo en el lector.");
        util.Tareas.enSegundoPlano(ventana,
                () -> servicioHuella.capturarHuella(est, avisoCon(nota(dedo))),
                plantilla -> siVigente(esta, () -> capturada(dedo, plantilla)),
                error -> siVigente(esta, () -> javax.swing.SwingUtilities.invokeLater(
                        () -> reacciones.get(error.getReaccion()).accept(error))),
                this::terminado);
    }

    private void terminado() {
        leyendo = false;
        if (reiniciarAlTerminar) {
            reiniciarAlTerminar = false;
            escuchar();
        }
    }

    private void reiniciarLectura() {
        detener();
        escuchar();
    }

    /** Cancela la lectura en curso; si sigue activo, se vuelve a leer cuando el lector la suelte. */
    private void detener() {
        lecturaVigente++;
        reiniciarAlTerminar = leyendo;
        servicioHuella.cancelarCaptura();
    }

    private void siVigente(int lectura, Runnable accion) {
        if (activo && lectura == lecturaVigente) {
            accion.run();
        }
    }

    private void capturada(Dedo dedo, byte[] plantilla) {
        capturadas.put(dedo, plantilla);
        if (ultimaImagen != null) {
            vistas.put(dedo, ultimaImagen);
        }
        mostrarCapturada(dedo);
    }

    private void mostrarCapturada(Dedo dedo) {
        panel.reiniciar();
        BufferedImage vista = vistas.get(dedo);
        if (vista != null) {
            panel.mostrarImagen(vista);
        }
        panel.mostrarResultado("Huella capturada", true);
        panel.mostrarAvance(biometria.LectorBase.MUESTRAS_REGISTRO,
                "Por guardar: " + pendientes() + ".\nPuede elegir otro dedo o presionar Guardar.");
        panel.mostrarRepetir(true);
        panel.getCbxDedo().repaint();
    }

    private void lecturaDetenida(BibliotecaException e) {
        biometria.EstadoLector estado = biometria.MonitorLector.getInstancia().getEstado();
        if (estado.permiteCapturar()) {
            avisarYReintentar(e.getMessage());
        } else {
            panel.mostrarLector(estado);
        }
    }

    /** Muestra el problema en rojo y espera a que pulse "Registrar otra vez" o elija otro dedo. */
    private void esperarOtraVez(String mensaje) {
        panel.mostrarAviso(mensaje, true);
        panel.mostrarRepetir(true);
    }

    private void avisarYReintentar(String mensaje) {
        panel.mostrarAviso(mensaje, false);
        javax.swing.Timer espera = new javax.swing.Timer(PAUSA_AVISO_MS, ev -> escuchar());
        espera.setRepeats(false);
        espera.start();
    }

    private biometria.AvisoCaptura avisoCon(String nota) {
        return new biometria.AvisoCaptura() {
            @Override
            public void avisar(int muestras, String mensaje) {
                panel.avisar(muestras, nota + mensaje);
            }

            @Override
            public void imagen(BufferedImage huella) {
                ultimaImagen = huella;
                panel.imagen(huella);
            }
        };
    }

    /** En la lista de dedos, los que ya tienen huella (guardada o capturada) se ven en negrita. */
    private void resaltarDedosConHuella() {
        javax.swing.ListCellRenderer<? super Dedo> base = panel.getCbxDedo().getRenderer();
        panel.getCbxDedo().setRenderer((lista, dedo, indice, elegido, foco) -> {
            java.awt.Component c = base.getListCellRendererComponent(lista, dedo, indice, elegido, foco);
            boolean conHuella = dedo != null && (capturadas.containsKey(dedo) || yaRegistrado(dedo));
            c.setFont(c.getFont().deriveFont(conHuella ? java.awt.Font.BOLD : java.awt.Font.PLAIN));
            return c;
        });
    }

    private String nota(Dedo dedo) {
        return yaRegistrado(dedo) ? "Este dedo ya está registrado; si lo lee, se reemplaza al guardar.\n" : "";
    }

    private boolean yaRegistrado(Dedo dedo) {
        return estudiante != null && estudiante.buscarHuella(dedo).isPresent();
    }

    private Dedo primerDedoLibre() {
        for (Dedo d : new Dedo[]{Dedo.INDICE_DERECHO, Dedo.INDICE_IZQUIERDO}) {
            if (!yaRegistrado(d)) {
                return d;
            }
        }
        for (Dedo d : Dedo.values()) {
            if (!yaRegistrado(d)) {
                return d;
            }
        }
        return Dedo.INDICE_DERECHO;
    }

    private String pendientes() {
        return capturadas.keySet().stream().map(Dedo::toString)
                .collect(java.util.stream.Collectors.joining(", "));
    }

    private String registradas() {
        return estudiante == null ? "" : estudiante.getHuellas().stream().map(h -> h.getDedo().toString())
                .collect(java.util.stream.Collectors.joining(", "));
    }
}
