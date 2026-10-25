package excepciones;

/** Qué debe hacer la pantalla de lectura de huella (Préstamos / Devoluciones) cuando falla una lectura. */
public enum Reaccion {
    MOSTRAR_ERROR,
    DETENER_LECTURA,
    REINTENTAR,
    REINTENTAR_AVISANDO,
    ALERTA
}
