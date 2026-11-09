package app;
import java.awt.KeyboardFocusManager;

import javax.swing.SwingUtilities;

import controller.ControllerLogin;
import dao.DAOException;
import util.Dialogs;
import view.LoginView;

public class MainApp {
    public static void main(String[] args) {
        // Red de seguridad: cualquier error que no haya atrapado un controller termina aca.
        // Se le muestra un mensaje al usuario (y el detalle queda en la consola para depurar)
        // en lugar de que la pantalla simplemente no responda.
        Thread.setDefaultUncaughtExceptionHandler((hilo, error) -> {
            error.printStackTrace();
            SwingUtilities.invokeLater(() -> mostrarError(error));
        });

        // Ejecuta la interfaz en el hilo de eventos seguro de Swing
        SwingUtilities.invokeLater(() -> {
            // Instancia la vista de inicio de sesion
            LoginView loginView = new LoginView();

            // Instancia el controller inyectando la vista
            new ControllerLogin(loginView).iniciar();

            // Hace visible la ventana de login
            loginView.setVisible(true);
        });
    }

    // Muestra el error sobre la ventana que el usuario tiene activa en ese momento
    private static void mostrarError(Throwable error) {
        String mensaje;
        if (error instanceof DAOException) {
            mensaje = "No se pudo completar la operación con la base de datos.\n"
                    + error.getMessage()
                    + "\nVerificá que MySQL esté en ejecución.";
        } else {
            mensaje = "Ocurrió un error inesperado: " + error;
        }
        Dialogs.error(KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow(),
                mensaje, "Error");
    }
}