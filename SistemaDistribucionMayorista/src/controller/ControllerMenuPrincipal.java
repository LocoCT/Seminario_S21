package controller;

import view.MenuPrincipal;
import view.GestionClientes;
import view.GestionProductos;
import view.NuevaOrdenVenta;
import view.ControlStock;
import view.Login;

import javax.swing.*;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerMenuPrincipal implements ActionListener {
    private MenuPrincipal vistaMenu;

    public ControllerMenuPrincipal(MenuPrincipal vistaMenu) {
        this.vistaMenu = vistaMenu;
        registrarListeners();
    }

    // Registra los listeners de eventos para cada boton de la vista
    private void registrarListeners() {
        this.vistaMenu.getBtnClientes().addActionListener(this);
        this.vistaMenu.getBtnProductos().addActionListener(this);
        this.vistaMenu.getBtnVentas().addActionListener(this);
        this.vistaMenu.getBtnStock().addActionListener(this);
        this.vistaMenu.getBtnCerrarSesion().addActionListener(this);
        // Acciones rapidas 
        this.vistaMenu.getBtnNuevoCliente().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vistaMenu.getBtnClientes()) {
            abrirGestionClientes();
        } else if (origen == vistaMenu.getBtnProductos()) {
            abrirGestionProductos();
        } else if (origen == vistaMenu.getBtnVentas()) {
            abrirNuevaOrdenVenta();
        } else if (origen == vistaMenu.getBtnStock()) {
            abrirControlStock();
        } else if (origen == vistaMenu.getBtnNuevoCliente()) {
            registrarNuevoCliente();
        } else if (origen == vistaMenu.getBtnCerrarSesion()) {
            int confirmacion = JOptionPane.showConfirmDialog(
                vistaMenu,
                "¿Desea cerrar sesión?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION
            );
            if (confirmacion == JOptionPane.YES_OPTION) {
                cerrarSesion();
            }
        }
    }

    private void cerrarSesion() {
        Login login = new Login();
        new ControllerLogin(login);
        login.setVisible(true);

        // Cierra el menu y cualquier ventana secundaria que haya quedado abierta
        for (Frame f : Frame.getFrames()) {
            if (f instanceof JFrame && f != login) {
                f.dispose();
            }
        }
    }

    // Abre la ventana de clientes y devuelve su controller
    private ControllerClientes abrirGestionClientes() {
        GestionClientes clientesView = new GestionClientes();
        ControllerClientes controller = new ControllerClientes(clientesView);
        clientesView.setVisible(true);
        return controller;
    }

    // Acción rapida - abre Clientes y directamente el formulario de alta
    private void registrarNuevoCliente() {
        ControllerClientes controller = abrirGestionClientes();
        controller.abrirNuevoCliente();
    }

    private ControllerProductos abrirGestionProductos() {
        GestionProductos productosView = new GestionProductos();
        ControllerProductos controller = new ControllerProductos(productosView);
        productosView.setVisible(true);
        return controller;
    }

    private void abrirNuevaOrdenVenta() {
        NuevaOrdenVenta ordenView = new NuevaOrdenVenta();
        ordenView.setVisible(true);
    }

    private void abrirControlStock() {
        ControlStock stockView = new ControlStock();
        stockView.setVisible(true);
    }
}