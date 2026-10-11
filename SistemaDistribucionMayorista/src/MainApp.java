import view.Login;
import controller.ControllerLogin;
import javax.swing.SwingUtilities;

public class MainApp {
    public static void main(String[] args) {
        // Ejecutar la interfaz en el hilo de eventos seguro de Swing
        SwingUtilities.invokeLater(() -> {
            // Instanciar la Vista de inicio de sesión
            Login loginView = new Login();

            // Instanciar el Controlador inyectando la Vista
            new ControllerLogin(loginView);

            // Hace visible la ventana de login
            loginView.setVisible(true);
        });
    }
}