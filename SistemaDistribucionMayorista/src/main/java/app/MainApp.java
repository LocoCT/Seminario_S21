package app;
import view.LoginView;
import controller.ControllerLogin;
import javax.swing.SwingUtilities;

public class MainApp {
    public static void main(String[] args) {
        // Ejecuta la interfaz en el hilo de eventos seguro de Swing
        SwingUtilities.invokeLater(() -> {
            // Instancia la vista de inicio de sesion
            LoginView loginView = new LoginView();

            // Instancia el controller inyectando la vista
            new ControllerLogin(loginView);

            // Hace visible la ventana de login
            loginView.setVisible(true);
        });
    }
}