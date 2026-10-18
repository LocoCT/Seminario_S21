package controller;
import model.Usuario;
import util.Dialogs;
import view.MenuPrincipalView;
import view.GestionClientesView;
import view.GestionFacturaView;
import view.GestionProductosView;
import view.GestionOrdenVentaView;
import view.GestionStockView;
import view.LoginView;
import javax.swing.*;
import dao.DAOFactory;
import dao.OrdenVentaDAO;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ControllerMenuPrincipal implements ActionListener {

    private MenuPrincipalView vistaMenu;
    private final Usuario usuario;
    private final OrdenVentaDAO daoOrdenes = DAOFactory.ordenes();

    public ControllerMenuPrincipal(MenuPrincipalView vistaMenu, Usuario usuario) {
        this.vistaMenu = vistaMenu;
        this.usuario = usuario;

        registrarListeners();
        refrescarDashboard();
    }

    // Registramos los listeners de eventos para cada botón de la vista
    private void registrarListeners() {
        //Panel Izquierdo
        this.vistaMenu.getBtnClientes().addActionListener(this);
        this.vistaMenu.getBtnProductos().addActionListener(this);
        this.vistaMenu.getBtnVentas().addActionListener(this);
        this.vistaMenu.getBtnFacturacion().addActionListener(this);
        this.vistaMenu.getBtnStock().addActionListener(this);
        this.vistaMenu.getBtnCerrarSesion().addActionListener(this);

        // Botones de acciones rapidas
        this.vistaMenu.getBtnNuevaOrden().addActionListener(this);
        this.vistaMenu.getBtnNuevoCliente().addActionListener(this);
        this.vistaMenu.getBtnMovStock().addActionListener(this);

        if (this.vistaMenu.getLblVerTodas() != null) {
            this.vistaMenu.getLblVerTodas().addMouseListener(new MouseAdapter() {
                @Override 
                public void mouseClicked(MouseEvent e) {
                    abrirGestionOrdenes();
                }
            });
        }
    }

    // recargamos la tabla de ultimas ordenes de venta con datos actuales.
    public void refrescarDashboard() {
        vistaMenu.actualizarTablaOrdenes(daoOrdenes.listar());
    }

    // Abrimos pantallas dependiendo de que boton apretamos.
    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vistaMenu.getBtnClientes()) {
            abrirGestionClientes();
        } else if (origen == vistaMenu.getBtnProductos()) {
            abrirGestionProductos();
        } else if (origen == vistaMenu.getBtnVentas()) {
            abrirGestionOrdenes();
        } else if (origen == vistaMenu.getBtnNuevaOrden()) {
            registrarNuevaOrden();
        } else if (origen == vistaMenu.getBtnStock()) {
            abrirControlStock();
        } else if (origen == vistaMenu.getBtnMovStock()){
            registrarNuevoMovimientoStock();
        } else if (origen == vistaMenu.getBtnNuevoCliente()) {
            registrarNuevoCliente();
        } else if (origen == vistaMenu.getBtnFacturacion()) {
            abrirGestionFacturacion();
        } else if (origen == vistaMenu.getBtnCerrarSesion()) {
            if (Dialogs.confirmar(vistaMenu,"¿Desea cerrar sesión?", "Confirmar salida")) {
                cerrarSesion();
            }
        }
    }

    // Cierre de sesion + nuevo login
    private void cerrarSesion() {
        LoginView login = new LoginView();
        new ControllerLogin(login);
        login.setVisible(true);

        // Cerramos el menu y cualquier ventana secundaria que haya quedado abierta
        for (Frame f : Frame.getFrames()) {
            if (f instanceof JFrame && f != login) {
                f.dispose();
            }
        }
    }

    // Abre la ventana de clientes y devuelve su controller
    private ControllerClientes abrirGestionClientes() {
        GestionClientesView clientesView = new GestionClientesView();
        ControllerClientes controller = new ControllerClientes(clientesView);
        clientesView.setVisible(true);
        return controller;
    }

    // Boton de acción rapida Nuevo Cliente - abre Clientes y formulario de alta
    private void registrarNuevoCliente() {
        ControllerClientes controller = abrirGestionClientes();
        controller.abrirNuevoCliente();
    }

    // Abre ventana de productos y devuelve su controller
    private ControllerProductos abrirGestionProductos() {
        GestionProductosView productosView = new GestionProductosView();
        ControllerProductos controller = new ControllerProductos(productosView);
        productosView.setVisible(true);
        return controller;
    }

    // Abre ventana de ordenes de venta + actualiza tabla de ultimas ordenes de venta
    private ControllerOrdenVenta abrirGestionOrdenes() {
        GestionOrdenVentaView gestionView = new GestionOrdenVentaView();
        ControllerOrdenVenta controller = new ControllerOrdenVenta(gestionView, this.usuario);
        gestionView.addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                refrescarDashboard();
            }
        });
        gestionView.setVisible(true);
        return controller;
    }

    // Boton de accion rapida registrar nueva orden venta : abre GestionOrdenes y vista de nuevo Orden
    private void registrarNuevaOrden() {
        ControllerOrdenVenta controller = abrirGestionOrdenes();
        controller.abrirNuevaOrden();
    }

    // Abre la ventana de control de stock y devuelve su controller
    private ControllerStock abrirControlStock() {
        GestionStockView stockView = new GestionStockView();
        ControllerStock controller = new ControllerStock(stockView, this.usuario);
        stockView.setVisible(true);
        return controller;
    }

    // Boton de accion rapida registrar movimiento stock: abre la pantalla de stock y vista de nuevo movimiento
    private void registrarNuevoMovimientoStock() {
        ControllerStock controller = abrirControlStock();
        controller.abrirNuevoMovimiento();
    }

    // Abre ventana de facturacion y cobros.    
    private void abrirGestionFacturacion() {
        GestionFacturaView vistaFactura = new GestionFacturaView();
        new ControllerFactura(vistaFactura);
        vistaFactura.setVisible(true);
    }
}