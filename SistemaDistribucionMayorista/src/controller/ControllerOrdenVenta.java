package controller;
import model.Cliente;
import model.DetalleOrdenVenta;
import model.OrdenVenta;
import model.Producto;
import model.Usuario;
import view.GestionOrdenVentaView;
import view.NuevaOrdenVentaView;
import view.estilos.CampoMoneda;
import java.math.BigDecimal;
import util.Dialogs;
import dao.ClienteDAO;
import dao.DAOFactory;
import dao.OrdenVentaDAO;
import dao.ProductoDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ControllerOrdenVenta implements ActionListener {

    private final GestionOrdenVentaView vistaGestion;
    private final Usuario usuario;
    private final OrdenVentaDAO daoOrdenes = DAOFactory.ordenes();
    private final ClienteDAO daoClientes = DAOFactory.clientes();
    private final ProductoDAO daoProductos = DAOFactory.productos();
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    private NuevaOrdenVentaView vistaEmision;
    private List<OrdenVenta> ordenesMostradas = new ArrayList<>();
    private List<Cliente> clientesDisponibles;
    private List<Producto> productosDisponibles;
    private List<DetalleOrdenVenta> detallesEnArmado;

    public ControllerOrdenVenta(GestionOrdenVentaView vistaGestion, Usuario usuario) {
        this.vistaGestion = vistaGestion;
        this.usuario = usuario;
        registrarListenersGestion();
        cargarTablaGestion(daoOrdenes.listar());
    }

    // Registramos listeners para botones de pantalla de gestion
    private void registrarListenersGestion() {
        vistaGestion.getBtnBuscar().addActionListener(this);
        vistaGestion.getTxtBuscar().addActionListener(this);
        vistaGestion.getBtnNuevaOrden().addActionListener(this);
        vistaGestion.getBtnCerrar().addActionListener(this);

        vistaGestion.getTablaOrdenes().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                alSeleccionarOrden();
            }
        });
    }

    // Registramos listeners para botones de pantalla de nueva orden
    private void registrarListenersEmision() {
        vistaEmision.getBtnAgregarProducto().addActionListener(this);
        vistaEmision.getBtnQuitarProducto().addActionListener(this);
        vistaEmision.getBtnConfirmarOrden().addActionListener(this);
        vistaEmision.getBtnCancelar().addActionListener(this);

        vistaEmision.getCmbCliente().addActionListener(e -> {
            int index = vistaEmision.getCmbCliente().getSelectedIndex();
            if (index >= 0 && index < clientesDisponibles.size()) {
                actualizarInfoClienteEmision(index);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        // Eventos de ventana gestion de ordenes
        if (src == vistaGestion.getBtnBuscar() || src == vistaGestion.getTxtBuscar()) {
            buscarOrdenes();
        } else if (src == vistaGestion.getBtnNuevaOrden()) {
            abrirNuevaOrden();
        } else if (src == vistaGestion.getBtnCerrar()) {
            vistaGestion.dispose();
        // Eventos de ventana de emision de nueva orden
        } else if (vistaEmision != null) {
            if (src == vistaEmision.getBtnAgregarProducto()) {
                agregarProducto();
            } else if (src == vistaEmision.getBtnQuitarProducto()) {
                quitarProducto();
            } else if (src == vistaEmision.getBtnConfirmarOrden()) {
                confirmarOrden();
            } else if (src == vistaEmision.getBtnCancelar()) {
                vistaEmision.dispose();
            }
        }
    }

    // poblamos la tabla de ordenes con lista
    public void cargarTablaGestion(List<OrdenVenta> lista) {
        vistaGestion.getModeloTablaOrdenes().setRowCount(0);
        ordenesMostradas = lista;

        for (OrdenVenta o : lista) {
            String num = OrdenVentaDAO.formatearNumero(o.getNumeroOrden());
            String fecha = (o.getFechaEmision() != null) ? formatoFecha.format(o.getFechaEmision()) : "-";
            String cliente = (o.getCliente() != null) ? o.getCliente().getRazonSocial() : "-";
            String total = "$ " + CampoMoneda.formatear(o.getTotal());
            String estado = o.getEstado();

            vistaGestion.getModeloTablaOrdenes().addRow(new Object[]{num, fecha, cliente, total, estado});
        }

        limpiarDetalleGestion();
        if (!ordenesMostradas.isEmpty()) {
            vistaGestion.getTablaOrdenes().setRowSelectionInterval(0, 0);
        }
    }

    //al seleccionar una orden mostramos detalle
    private void alSeleccionarOrden() {
        int fila = vistaGestion.getTablaOrdenes().getSelectedRow();
        if (fila < 0 || fila >= ordenesMostradas.size()) {
            limpiarDetalleGestion();
            return;
        }
        mostrarDetalleGestion(ordenesMostradas.get(fila));
    }

    // poblamos tabla de detalle
    private void mostrarDetalleGestion(OrdenVenta o) {
        vistaGestion.getTxtNumeroOrden().setText(OrdenVentaDAO.formatearNumero(o.getNumeroOrden()));
        vistaGestion.getTxtFecha().setText((o.getFechaEmision() != null) ? formatoFecha.format(o.getFechaEmision()) : "-");
        vistaGestion.getTxtCliente().setText((o.getCliente() != null) ? o.getCliente().getRazonSocial() : "-");
        vistaGestion.getTxtVendedor().setText((o.getUsuario() != null) ? o.getUsuario().getUsername() : "-");
        vistaGestion.getTxtEstado().setText(o.getEstado());
        vistaGestion.getTxtTotal().setMonto(o.getTotal());

        vistaGestion.getModeloTablaDetalle().setRowCount(0);
        for (DetalleOrdenVenta d : o.getItems()) {
            vistaGestion.getModeloTablaDetalle().addRow(new Object[]{
                d.getProducto().getCodigo(),
                d.getProducto().getDescripcion(),
                d.getCantidad(),
                "$ " + CampoMoneda.formatear(d.getPrecioUnitario()),
                "$ " + CampoMoneda.formatear(d.getSubtotal())
            });
        }
    }

    //Limpiamos tabla de detalle
    private void limpiarDetalleGestion() {
        vistaGestion.getTxtNumeroOrden().setText("");
        vistaGestion.getTxtFecha().setText("");
        vistaGestion.getTxtCliente().setText("");
        vistaGestion.getTxtVendedor().setText("");
        vistaGestion.getTxtEstado().setText("");
        vistaGestion.getTxtTotal().setMonto(null);
        vistaGestion.getModeloTablaDetalle().setRowCount(0);
    }

    // Buscamos por numero, cliente, CUIT o estado
    private void buscarOrdenes() {
        String texto = vistaGestion.getTxtBuscar().getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            cargarTablaGestion(daoOrdenes.listar());
            return;
        }

        List<OrdenVenta> filtradas = new ArrayList<>();
        for (OrdenVenta o : daoOrdenes.listar()) {
            String num = OrdenVentaDAO.formatearNumero(o.getNumeroOrden()).toLowerCase();
            String cliente = (o.getCliente() != null) ? o.getCliente().getRazonSocial().toLowerCase() : "";
            String cuit = (o.getCliente() != null) ? o.getCliente().getCUIT().replaceAll("\\D", "") : "";
            String estado = (o.getEstado() != null) ? o.getEstado().toLowerCase() : "";

            if (num.contains(texto) || cliente.contains(texto) || cuit.contains(texto) || estado.contains(texto)) {
                filtradas.add(o);
            }
        }
        cargarTablaGestion(filtradas);
    }

    // Abrimos ventana de nueva orden -  al cerrar refresca tabla principal
    public void abrirNuevaOrden() {
        vistaEmision = new NuevaOrdenVentaView();
        clientesDisponibles = daoClientes.listar();
        productosDisponibles = daoProductos.listar();
        detallesEnArmado = new ArrayList<>();

        poblarCombosEmision();
        registrarListenersEmision();
        actualizarTotalesEmision();

        vistaEmision.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                cargarTablaGestion(daoOrdenes.listar());
                vistaEmision = null;
            }
        });

        vistaEmision.setVisible(true);
    }

    // Llenamos los cambos de producto y de clientes 
    private void poblarCombosEmision() {
        vistaEmision.getCmbCliente().removeAllItems();
        for (Cliente c : clientesDisponibles) {
            vistaEmision.getCmbCliente().addItem(c.getCUIT() + " - " + c.getRazonSocial());
        }

        vistaEmision.getCmbProducto().removeAllItems();
        for (Producto p : productosDisponibles) {
            vistaEmision.getCmbProducto().addItem(p.getCodigo() + " - " + p.getDescripcion() + " ($ " + CampoMoneda.formatear(p.getPrecioBase()) + ")");
        }

        if (!clientesDisponibles.isEmpty()) {
            actualizarInfoClienteEmision(0);
        }
    }

    // Mostramos limite de credito saldo y credito disponible de cliente en pantalla de nueva orden
    private void actualizarInfoClienteEmision(int index) {
        Cliente c = clientesDisponibles.get(index);
        vistaEmision.getLblLimiteCredito().setText("$ " + CampoMoneda.formatear(c.getLimiteCredito()));
        vistaEmision.getLblSaldoActual().setText("$ " + CampoMoneda.formatear(c.getSaldo()));
        vistaEmision.getLblCreditoDisponible().setText("$ " + CampoMoneda.formatear(c.getCreditoDisponible()));
    }

    // contador para unidades de producto en carrito
    private int cantidadEnOrden(Producto p) {
        int total = 0;
        for (DetalleOrdenVenta d : detallesEnArmado) {
            if (d.getProducto().getCodigo().equals(p.getCodigo())) {
                total += d.getCantidad();
            }
        }
        return total;
    }

    // Agregamos el producto seleccionado al carrito - validamos que la cantidad sea mayor a cero y que alcance stock
    private void agregarProducto() {
        int idxProd = vistaEmision.getCmbProducto().getSelectedIndex();
        if (idxProd < 0) return;

        Producto prodSel = productosDisponibles.get(idxProd);

        try {
            int cantidad = Integer.parseInt(vistaEmision.getTxtCantidad().getText().trim());
            if (cantidad <= 0) {
                Dialogs.error(vistaEmision, "La cantidad debe ser mayor a 0.", "Error");
                return;
            }

            int disponible = prodSel.getStockActual() - cantidadEnOrden(prodSel);
            if (cantidad > disponible) {
                Dialogs.advertencia(vistaEmision, "Stock insuficiente. Stock disponible restante: " + disponible, "Validación de Stock");
                return;
            }

            // Si ya esta el producto sumamos a la cantidad
            DetalleOrdenVenta detalleExistente = null;
            int filaExistente = -1;
            for (int i = 0; i < detallesEnArmado.size(); i++) {
                if (detallesEnArmado.get(i).getProducto().getCodigo().equals(prodSel.getCodigo())) {
                    detalleExistente = detallesEnArmado.get(i);
                    filaExistente = i;
                    break;
                }
            }

            if (detalleExistente != null) {
                int nuevaCantidad = detalleExistente.getCantidad() + cantidad;
                detalleExistente.setCantidad(nuevaCantidad);
                vistaEmision.getModeloDetalle().setValueAt(nuevaCantidad, filaExistente, 2);
                vistaEmision.getModeloDetalle().setValueAt("$ " + CampoMoneda.formatear(detalleExistente.getSubtotal()), filaExistente, 4);
            } else {
                DetalleOrdenVenta detalle = new DetalleOrdenVenta(cantidad, prodSel, BigDecimal.ZERO);
                detallesEnArmado.add(detalle);
                vistaEmision.getModeloDetalle().addRow(new Object[]{
                    prodSel.getCodigo(),
                    prodSel.getDescripcion(),
                    cantidad,
                    "$ " + CampoMoneda.formatear(detalle.getPrecioUnitario()),
                    "$ " + CampoMoneda.formatear(detalle.getSubtotal())
                });
            }

            actualizarTotalesEmision();
            vistaEmision.getTxtCantidad().setText("1");

        } catch (NumberFormatException ex) {
            Dialogs.error(vistaEmision, "Ingrese una cantidad numérica válida.", "Error");
        }
    }

    // Sacamos del carrito el producto seleccionado
    private void quitarProducto() {
        int filaSel = vistaEmision.getTablaDetalle().getSelectedRow();
        if (filaSel >= 0) {
            detallesEnArmado.remove(filaSel);
            vistaEmision.getModeloDetalle().removeRow(filaSel);
            actualizarTotalesEmision();
        } else {
            Dialogs.info(vistaEmision, "Seleccione un producto del detalle para quitar.", "Aviso");
        }
    }

    // calculamos total y subtotal del carrito
    private void actualizarTotalesEmision() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleOrdenVenta d : detallesEnArmado) {
            total = total.add(d.getSubtotal());
        }
        vistaEmision.getTxtSubtotal().setMonto(total);
        vistaEmision.getTxtTotal().setMonto(total);
    }

    // Para confirmar la orden:
    // 1. Validamos Stock. 2. Armamos orden. 3. Validamos limite de credito.
    // 4. Descuenta Stock. 5. Suma total a saldo de cliente. 6. Guardamos orden.
    // Si falla, cancelamos todo. 
    private void confirmarOrden() {
        if (detallesEnArmado.isEmpty()) {
            Dialogs.advertencia(vistaEmision, "Debe agregar al menos un producto a la orden.", "Validación");
            return;
        }

        int idxCliente = vistaEmision.getCmbCliente().getSelectedIndex();
        if (idxCliente < 0) {
            Dialogs.advertencia(vistaEmision, "Seleccione un cliente.", "Validación");
            return;
        }
        Cliente clienteSel = clientesDisponibles.get(idxCliente);

        // 1. Validar disponibilidad real de stock
        for (DetalleOrdenVenta d : detallesEnArmado) {
            if (!d.getProducto().verificarDisponibilidad(d.getCantidad())) {
                Dialogs.error(vistaEmision,
                    "El producto " + d.getProducto().getDescripcion() + " no cuenta con stock suficiente (" 
                    + d.getProducto().getStockActual() + " disponibles).",
                    "Stock Insuficiente");
                return;
            }
        }

        // 2. Armamos orden con productos
        OrdenVenta orden = new OrdenVenta(daoOrdenes.siguienteNumero(), clienteSel, usuario);
        for (DetalleOrdenVenta d : detallesEnArmado) {
            orden.agregarItem(d);
        }
        BigDecimal totalOrden = orden.getTotal();

        // 3. Validamos limite de crédito
        if (!clienteSel.verificarLimiteCredito(totalOrden)) {
            Dialogs.error(vistaEmision,
                "La orden supera el crédito disponible del cliente."
                    + "\nLímite de crédito: $ " + CampoMoneda.formatear(clienteSel.getLimiteCredito())
                    + "\nSaldo actual: $ " + CampoMoneda.formatear(clienteSel.getSaldo())
                    + "\nCrédito disponible: $ " + CampoMoneda.formatear(clienteSel.getCreditoDisponible())
                    + "\nTotal de la orden: $ " + CampoMoneda.formatear(totalOrden),
                "Límite de Crédito Excedido");
            return;
        }

        // 4. Descuento stock físico 
        for (DetalleOrdenVenta d : orden.getItems()) {
            d.getProducto().actualizarStock(d.getCantidad(), "Egreso");
        }
        // 5. Actualizamos saldo
        clienteSel.actualizarSaldo(totalOrden);

        // 6. Guardamos Orden
        daoOrdenes.agregar(orden);

        Dialogs.info(vistaEmision,
            "Orden de Venta " + OrdenVentaDAO.formatearNumero(orden.getNumeroOrden())
                + " emitida correctamente.\nTotal: $ " + CampoMoneda.formatear(orden.getTotal())
                + "\nNuevo saldo del cliente: $ " + CampoMoneda.formatear(clienteSel.getSaldo())
                + "\nReserva de stock realizada.",
            "Éxito");

        vistaEmision.dispose();
    }
}