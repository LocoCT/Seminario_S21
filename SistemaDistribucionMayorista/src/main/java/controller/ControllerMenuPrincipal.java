package controller;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

import javax.swing.JFrame;

import dao.ClienteDAO;
import dao.DAOFactory;
import dao.FacturaDAO;
import dao.OrdenVentaDAO;
import dao.ProductoDAO;
import model.Cliente;
import model.OrdenVenta;
import model.Pago;
import model.Producto;
import model.Usuario;
import util.Dialogs;
import view.GestionClientesView;
import view.GestionFacturaView;
import view.GestionOrdenVentaView;
import view.GestionProductosView;
import view.GestionStockView;
import view.LoginView;
import view.MenuPrincipalView;
import view.estilos.CampoMoneda;

public class ControllerMenuPrincipal implements ActionListener {

    private final MenuPrincipalView vistaMenu;
    private final Usuario usuario;
    private final OrdenVentaDAO daoOrdenes = DAOFactory.ordenes();
    private final FacturaDAO daoFacturas = DAOFactory.facturas();
    private final ProductoDAO daoProductos = DAOFactory.productos();
    private final ClienteDAO daoClientes = DAOFactory.clientes();

    public ControllerMenuPrincipal(MenuPrincipalView vistaMenu, Usuario usuario) {
        this.vistaMenu = vistaMenu;
        this.usuario = usuario;
    }

    public void iniciar (){
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

    // Recargamos la tabla de ultimas ordenes y las tarjetas de resumen con datos actuales.
    public void refrescarDashboard() {
        List<OrdenVenta> ordenes = daoOrdenes.listar();
        vistaMenu.actualizarTablaOrdenes(ordenes);
        actualizarTarjetas(ordenes);
    }

    // Calcula los valores de las tarjetas de resumen a partir de los datos de la base
    private void actualizarTarjetas(List<OrdenVenta> ordenes) {

        // Ordenes pendientes: las que todavia no se facturaron
        int ordenesPendientes = 0;
        for (OrdenVenta o : ordenes) {
            if (!"Facturada".equalsIgnoreCase(o.getEstado())) {
                ordenesPendientes++;
            }
        }

        // Cobros del dia: suma de los pagos de hoy, comparada con la de ayer
        LocalDate hoy = LocalDate.now();
        BigDecimal cobrosHoy = BigDecimal.ZERO;
        BigDecimal cobrosAyer = BigDecimal.ZERO;
        for (Pago p : daoFacturas.listarPagos()) {
            LocalDate fecha = p.getFechaPago().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (fecha.equals(hoy)) {
                cobrosHoy = cobrosHoy.add(p.getMonto());
            } else if (fecha.equals(hoy.minusDays(1))) {
                cobrosAyer = cobrosAyer.add(p.getMonto());
            }
        }

        // Productos con stock igual o por debajo del minimo
        int productosBajoMinimo = 0;
        for (Producto p : daoProductos.listar()) {
            if (p.getStockActual() <= p.getStockMinimo()) {
                productosBajoMinimo++;
            }
        }

        // Clientes con saldo pendiente de pago
        int clientesDeudores = 0;
        for (Cliente c : daoClientes.listar()) {
            if (c.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                clientesDeudores++;
            }
        }

        vistaMenu.actualizarTarjetas(ordenesPendientes, "$ " + CampoMoneda.formatear(cobrosHoy),
                detalleCobros(cobrosHoy, cobrosAyer), productosBajoMinimo, clientesDeudores);
    }

    // Texto de la tarjeta de cobros: variacion porcentual contra el dia anterior
    private String detalleCobros(BigDecimal cobrosHoy, BigDecimal cobrosAyer) {
        if (cobrosAyer.signum() == 0) {
            return (cobrosHoy.signum() == 0) ? "Sin cobros hoy ni ayer" : "Sin cobros ayer";
        }
        BigDecimal variacion = cobrosHoy.subtract(cobrosAyer)
                .multiply(BigDecimal.valueOf(100))
                .divide(cobrosAyer, 1, RoundingMode.HALF_UP);
        return String.format(Locale.forLanguageTag("es-AR"), "%+.1f%% vs. ayer", variacion);
    }

    // Al cerrar una ventana de gestion se vuelven a calcular los datos del dashboard
    private void refrescarAlCerrar(JFrame ventana) {
        ventana.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                refrescarDashboard();
            }
        });
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
        new ControllerLogin(login).iniciar();
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
        ControllerClientes controller = new ControllerClientes(clientesView, this.usuario);
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
        refrescarAlCerrar(productosView);
        productosView.setVisible(true);
        return controller;
    }

    // Abre ventana de ordenes de venta + actualiza tabla de ultimas ordenes de venta
    private ControllerOrdenVenta abrirGestionOrdenes() {
        GestionOrdenVentaView gestionView = new GestionOrdenVentaView();
        ControllerOrdenVenta controller = new ControllerOrdenVenta(gestionView, this.usuario);
        controller.iniciar();
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
        controller.iniciar();
        refrescarAlCerrar(stockView);
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
        new ControllerFactura(vistaFactura).iniciar();
        refrescarAlCerrar(vistaFactura);
        vistaFactura.setVisible(true);
    }
}