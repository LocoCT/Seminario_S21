package controller;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import dao.DAOException;
import dao.DAOFactory;
import dao.UsuarioDAO;
import model.Usuario;
import view.ConfiguracionDBView;
import view.LoginView;
import view.MenuPrincipalView;

public class ControllerLogin implements ActionListener {

    private final LoginView vista;
    private final UsuarioDAO dao = DAOFactory.usuarios();

    // Registramos los listeners de eventos para cada boton de la vista
    public ControllerLogin(LoginView vista) {
        this.vista = vista;
    }

    public void iniciar(){
        this.vista.getBtnIngresar().addActionListener(this);
        this.vista.getBtnSalir().addActionListener(this);
        this.vista.getBtnConfigDB().addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnSalir()) {
            System.exit(0);
        } else if (e.getSource() == vista.getBtnIngresar()) {
            intentarIngreso();
        } else if (e.getSource() == vista.getBtnConfigDB()) {
            abrirConfiguracionDB();
        }
    }

    private void abrirConfiguracionDB() {
        ConfiguracionDBView configView = new ConfiguracionDBView(vista);
        ControllerConfiguracionDB controller = new ControllerConfiguracionDB(configView);
        controller.iniciar();
        configView.setVisible(true);
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
        Usuario usuario;
        try {
            usuario = dao.buscarPorUsername(username);
        } catch (DAOException e) {
            vista.mostrarError("No se pudo conectar a la base de datos.");
            Arrays.fill(clave, ' ');
            return;
        }
        
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
        new ControllerMenuPrincipal(menu, usuario).iniciar();   
        menu.setUsuario(usuario.getUsername(), usuario.getRol());
        menu.setVisible(true);
        vista.dispose();
    }
}
