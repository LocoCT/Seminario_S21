package util;
import java.awt.Component;
import javax.swing.JOptionPane;

// Mensajes y confirmaciones reutilizables. El orden de los parámetros es el mismo
// que usa JOptionPane: (ventana padre, mensaje, título).
public final class Dialogs {

    private Dialogs() {}

    public static void info(Component padre, String mensaje, String titulo) {
        JOptionPane.showMessageDialog(padre, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void advertencia(Component padre, String mensaje, String titulo) {
        JOptionPane.showMessageDialog(padre, mensaje, titulo, JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component padre, String mensaje, String titulo) {
        JOptionPane.showMessageDialog(padre, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

    // true si el usuario eligió "Sí"
    public static boolean confirmar(Component padre, String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(padre, mensaje, titulo,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}