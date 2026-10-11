package controller;

import model.Usuario;
import repository.UsuarioRepositorio;
import view.Login;
import view.MenuPrincipal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class ControllerLogin implements ActionListener {

    private final Login vista;
    private final UsuarioRepositorio repositorio = new UsuarioRepositorio();

    public ControllerLogin(Login vista) {
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

        if (username.isEmpty() || clave.length == 0) {
            vista.mostrarError("Ingresá usuario y contraseña.");
            Arrays.fill(clave, ' ');
            return;
        }

        Usuario usuario = repositorio.buscarPorUsername(username);
        String claveTexto = new String(clave);
        Arrays.fill(clave, ' ');
        
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

    private void abrirMenuPrincipal(Usuario usuario) {
        MenuPrincipal menu = new MenuPrincipal();
        new ControllerMenuPrincipal(menu);
        menu.setUsuario(usuario.getUsername(), usuario.getRol());
        menu.setVisible(true);
        vista.dispose();
    }
}