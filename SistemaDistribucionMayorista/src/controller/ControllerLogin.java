package controller;
import model.Usuario;
import view.LoginView;
import view.MenuPrincipalView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import dao.DAOFactory;
import dao.UsuarioDAO;

public class ControllerLogin implements ActionListener {

    private final LoginView vista;
    private final UsuarioDAO dao = DAOFactory.usuarios();

    // Registramos los listeners de eventos para cada boton de la vista
    public ControllerLogin(LoginView vista) {
        this.vista = vista;
        this.vista.getBtnIngresar().addActionListener(this);
        this.vista.getBtnSalir().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnSalir()) {
            System.exit(0);
        } else if (e.getSource() == vista.getBtnIngresar()) {
            intentarIngreso();
        }
    }

    private void intentarIngreso() {
        String username = vista.getUsername();
        char[] clave = vista.getClave();

        // Validamos si usuario y contraseña estan informados.
        if (username.isEmpty() || clave.length == 0) {
            vista.mostrarError("Ingresá usuario y contraseña.");
            Arrays.fill(clave, ' ');
            return;
        }

        //Buscamos al usuario y clave
        Usuario usuario = dao.buscarPorUsername(username);
        String claveTexto = new String(clave);
        Arrays.fill(clave, ' ');
        
        // Si no existe o esta inactivo o si la pw es incorrecta
        if (usuario == null || !usuario.iniciarSesion(username, claveTexto)) {
            if (usuario != null && !usuario.isEstado()) {
                vista.mostrarError("El usuario está inactivo.");
            } else {
                vista.mostrarError("Usuario o contraseña incorrectos.");
            }
            vista.limpiarClave();
            return;
        }

        abrirMenuPrincipal(usuario);
    }

    // abrimos el menu principal pasando el usuario logueado.
    private void abrirMenuPrincipal(Usuario usuario) {
        MenuPrincipalView menu = new MenuPrincipalView();
        new ControllerMenuPrincipal(menu, usuario);   
        menu.setUsuario(usuario.getUsername(), usuario.getRol());
        menu.setVisible(true);
        vista.dispose();
    }
}