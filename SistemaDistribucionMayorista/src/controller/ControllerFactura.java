package controller;
import model.Cliente;
import model.DetalleOrdenVenta;
import model.Factura;
import model.OrdenVenta;
import model.Pago;
import view.GestionFacturaView;
import view.NuevaFacturaView;
import view.NuevoPagoView;
import view.estilos.CampoMoneda;
import java.math.BigDecimal;
import javax.swing.*;
import util.Dialogs;
import dao.ClienteDAO;
import dao.DAOFactory;
import dao.FacturaDAO;
import dao.OrdenVentaDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ControllerFactura implements ActionListener {

    private final GestionFacturaView vista;
    private final FacturaDAO daoFacturas = DAOFactory.facturas();
    private final OrdenVentaDAO daoOrdenes = DAOFactory.ordenes();
    private final ClienteDAO daoClientes =  DAOFactory.clientes();
    private List<Factura> facturasMostradas = new ArrayList<>();
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    private NuevaFacturaView dialogoNuevaFactura;
    private List<OrdenVenta> ordenesPendientesFacturar;
    private NuevoPagoView nuevoPago;
    private List<Cliente> clientesPago;
    private List<Factura> facturasDelCliente;
    private boolean cargandoFacturas = false;

    public ControllerFactura(GestionFacturaView vista) {
        this.vista = vista;
        registrarListeners();
        cargarTablaFacturas(daoFacturas.listarFacturas());
        cargarTablaPagos(daoFacturas.listarPagos());
    }

    // Registramos eventos de Botones 
    private void registrarListeners() {
        vista.getBtnBuscarFactura().addActionListener(this);
        vista.getTxtBuscarFactura().addActionListener(this);
        vista.getBtnNuevaFactura().addActionListener(this);
        vista.getBtnRegistrarPago().addActionListener(this);
        vista.getBtnCerrar().addActionListener(this);

        vista.getTablaFacturas().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFactura();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == vista.getBtnBuscarFactura() || src == vista.getTxtBuscarFactura()) {
            buscarFacturas();
        } else if (src == vista.getBtnNuevaFactura()) {
            abrirNuevaFactura();
        } else if (src == vista.getBtnRegistrarPago()) {
            registrarNuevoPago();
        } else if (src == vista.getBtnCerrar()) {
            vista.dispose();
        }
    }

    // Carga de facturas
    public void cargarTablaFacturas(List<Factura> lista) {
        vista.getModeloFacturas().setRowCount(0);
        facturasMostradas = (lista != null) ? lista : new ArrayList<>();

        for (Factura f : facturasMostradas) {
            String num = FacturaDAO.formatearNumeroFactura(f.getNumeroFactura());
            String fecha = (f.getFechaEmision() != null) ? formatoFecha.format(f.getFechaEmision()) : "-";
            String cliente = (f.getOrden() != null && f.getOrden().getCliente() != null) 
                    ? f.getOrden().getCliente().getRazonSocial() : "-";
            String total = "$ " + CampoMoneda.formatear(f.getTotalFinal());
            String estado = f.getEstado();

            vista.getModeloFacturas().addRow(new Object[]{num, fecha, cliente, total, estado});
        }

        limpiarDetalleFactura();
        if (!facturasMostradas.isEmpty()) {
            vista.getTablaFacturas().setRowSelectionInterval(0, 0);
        }
    }

    // Cuando elegimos una factura mostramos el detalle
    private void seleccionarFactura() {
        int fila = vista.getTablaFacturas().getSelectedRow();
        if (fila < 0 || fila >= facturasMostradas.size()) {
            limpiarDetalleFactura();
            return;
        }
        mostrarDetalleFactura(facturasMostradas.get(fila));
    }

    // Cargamos el detalle de la factura seleccionada
    private void mostrarDetalleFactura(Factura f) {
        vista.getTxtDetalleFacturaNumero().setText(FacturaDAO.formatearNumeroFactura(f.getNumeroFactura()));
        vista.getTxtDetalleFacturaFecha().setText((f.getFechaEmision() != null) ? formatoFecha.format(f.getFechaEmision()) : "-");
        
        Cliente c = (f.getOrden() != null) ? f.getOrden().getCliente() : null;
        vista.getTxtDetalleFacturaCliente().setText((c != null) ? c.getRazonSocial() : "-");
        vista.getTxtDetalleFacturaOrden().setText((f.getOrden() != null) 
                ? OrdenVentaDAO.formatearNumero(f.getOrden().getNumeroOrden()) : "-");
        vista.getTxtDetalleFacturaEstado().setText(f.getEstado());

        vista.getTxtDetalleFacturaSubtotal().setMonto(f.getSubtotal());
        vista.getTxtDetalleFacturaIva().setMonto(f.getIva());
        vista.getTxtDetalleFacturaTotal().setMonto(f.getTotalFinal());

        // Cargar ítems facturados
        vista.getModeloItemsFactura().setRowCount(0);
        if (f.getOrden() != null && f.getOrden().getItems() != null) {
            for (DetalleOrdenVenta d : f.getOrden().getItems()) {
                vista.getModeloItemsFactura().addRow(new Object[]{
                    d.getProducto().getCodigo(),
                    d.getProducto().getDescripcion(),
                    d.getCantidad(),
                    "$ " + CampoMoneda.formatear(d.getPrecioUnitario()),
                    "$ " + CampoMoneda.formatear(d.getSubtotal())
                });
            }
        }
    }

    //Limpiamos detalle
    private void limpiarDetalleFactura() {
        vista.getTxtDetalleFacturaNumero().setText("");
        vista.getTxtDetalleFacturaFecha().setText("");
        vista.getTxtDetalleFacturaCliente().setText("");
        vista.getTxtDetalleFacturaOrden().setText("");
        vista.getTxtDetalleFacturaEstado().setText("");
        vista.getTxtDetalleFacturaSubtotal().setMonto(null);
        vista.getTxtDetalleFacturaIva().setMonto(null);
        vista.getTxtDetalleFacturaTotal().setMonto(null);
        vista.getModeloItemsFactura().setRowCount(0);
    }

    // Buscamos facturas por numero o cliente
    private void buscarFacturas() {
        String texto = vista.getTxtBuscarFactura().getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            cargarTablaFacturas(daoFacturas.listarFacturas());
            return;
        }

        List<Factura> filtradas = new ArrayList<>();
        for (Factura f : daoFacturas.listarFacturas()) {
            String num = FacturaDAO.formatearNumeroFactura(f.getNumeroFactura()).toLowerCase();
            String cliente = (f.getOrden() != null && f.getOrden().getCliente() != null) 
                    ? f.getOrden().getCliente().getRazonSocial().toLowerCase() : "";
            if (num.contains(texto) || cliente.contains(texto)) {
                filtradas.add(f);
            }
        }
        cargarTablaFacturas(filtradas);
    }

    // Cargamos Historial de pagos
    public void cargarTablaPagos(List<Pago> lista) {
        vista.getModeloPagos().setRowCount(0);
        if (lista == null) return;

        for (Pago p : lista) {
            String fecha = (p.getFechaPago() != null) ? formatoFecha.format(p.getFechaPago()) : "-";
            String cliente = (p.getCliente() != null) ? p.getCliente().getRazonSocial() : "-";

            String numFactura = (p.getFactura() != null)
                ? FacturaDAO.formatearNumeroFactura(p.getFactura().getNumeroFactura())
                : "-";

            String ref = (p.getComprobanteReferencia() != null) ? p.getComprobanteReferencia() : "-";
            String medio = p.getMedioPago();
            String monto = "$ " + CampoMoneda.formatear(p.getMonto());

            vista.getModeloPagos().addRow(new Object[]{p.getId(), fecha, cliente, numFactura, ref, medio, monto});
        }
    }

    // Abre ventana para cargar nueva factura que todavia no fuero facturadas
    private void abrirNuevaFactura() {
        ordenesPendientesFacturar = new ArrayList<>();
        for (OrdenVenta ov : daoOrdenes.listar()) {
            if (!"Facturada".equalsIgnoreCase(ov.getEstado())) {
                ordenesPendientesFacturar.add(ov);
            }
        }

        if (ordenesPendientesFacturar.isEmpty()) {
            Dialogs.error(vista, "No hay órdenes de venta pendientes de facturar.", "ERROR");
            return;
        }

        dialogoNuevaFactura = new NuevaFacturaView(vista);
        dialogoNuevaFactura.getTxtNumeroFactura().setText(FacturaDAO.formatearNumeroFactura(daoFacturas.siguienteNumeroFactura()));
        dialogoNuevaFactura.getTxtFechaEmision().setText(formatoFecha.format(new Date()));

        dialogoNuevaFactura.getCmbOrdenes().removeAllItems();
        for (OrdenVenta ov : ordenesPendientesFacturar) {
            String cli = (ov.getCliente() != null) ? ov.getCliente().getRazonSocial() : "";
            dialogoNuevaFactura.getCmbOrdenes().addItem(OrdenVentaDAO.formatearNumero(ov.getNumeroOrden()) + " - " + cli);
        }

        dialogoNuevaFactura.getCmbOrdenes().addActionListener(e -> seleccionarOrdenAFacturar());
        dialogoNuevaFactura.getBtnCancelar().addActionListener(e -> dialogoNuevaFactura.dispose());
        dialogoNuevaFactura.getBtnEmitir().addActionListener(e -> confirmarEmisionFactura());

        seleccionarOrdenAFacturar();
        dialogoNuevaFactura.setVisible(true);
    }

    // Cuando elegimos una orden mostramos cliente, CUIT , produdctos y totales
    private void seleccionarOrdenAFacturar() {
        int idx = dialogoNuevaFactura.getCmbOrdenes().getSelectedIndex();
        if (idx < 0 || idx >= ordenesPendientesFacturar.size()) return;

        OrdenVenta ordenSel = ordenesPendientesFacturar.get(idx);
        Cliente cli = ordenSel.getCliente();

        dialogoNuevaFactura.getTxtCliente().setText((cli != null) ? cli.getRazonSocial() : "-");
        dialogoNuevaFactura.getTxtCuit().setText((cli != null) ? cli.getCUIT() : "-");

        dialogoNuevaFactura.getModeloItems().setRowCount(0);
        for (DetalleOrdenVenta d : ordenSel.getItems()) {
            dialogoNuevaFactura.getModeloItems().addRow(new Object[]{
                d.getProducto().getCodigo(),
                d.getProducto().getDescripcion(),
                d.getCantidad(),
                "$ " + CampoMoneda.formatear(d.getPrecioUnitario()),
                "$ " + CampoMoneda.formatear(d.getSubtotal())
            });
        }

        BigDecimal subtotal = ordenSel.getTotal();
        BigDecimal iva = Factura.calcularIva(subtotal);
        BigDecimal totalFinal = subtotal.add(iva);

        dialogoNuevaFactura.getTxtSubtotal().setMonto(subtotal);
        dialogoNuevaFactura.getTxtIva().setMonto(iva);
        dialogoNuevaFactura.getTxtTotalFinal().setMonto(totalFinal);
    }

    // Creamos Factura - se deja pendiente y marcamos orden como "Facturada"
    private void confirmarEmisionFactura() {
        int idx = dialogoNuevaFactura.getCmbOrdenes().getSelectedIndex();
        if (idx < 0 || idx >= ordenesPendientesFacturar.size()) return;

        OrdenVenta ordenSel = ordenesPendientesFacturar.get(idx);
        Factura nueva = new Factura(daoFacturas.siguienteNumeroFactura(), ordenSel);
        daoFacturas.agregarFactura(nueva);

        JOptionPane.showMessageDialog(dialogoNuevaFactura, 
                "Factura " + FacturaDAO.formatearNumeroFactura(nueva.getNumeroFactura()) + " emitida con éxito.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

        dialogoNuevaFactura.dispose();
        cargarTablaFacturas(daoFacturas.listarFacturas());
    }

    // Abre ventana para registrar nuevo pago con lista de clientes
    private void registrarNuevoPago() {
        clientesPago = daoClientes.listar();
        if (clientesPago.isEmpty()) {
            Dialogs.info(vista, "No hay clientes registrados en el sistema.", "Aviso");
            return;
        }

        nuevoPago = new NuevoPagoView(vista);

        nuevoPago.getCmbClientes().removeAllItems();
        for (Cliente c : clientesPago) {
            nuevoPago.getCmbClientes().addItem(c.getCUIT() + " - " + c.getRazonSocial());
        }

        nuevoPago.getCmbClientes().addActionListener(e -> cambiarClienteEnPago());
        nuevoPago.getCmbFacturasPendientes().addActionListener(e -> cambiarFacturaEnPago());
        nuevoPago.getBtnCancelar().addActionListener(e -> nuevoPago.dispose());
        nuevoPago.getBtnRegistrar().addActionListener(e -> confirmarPago());

        cambiarClienteEnPago();
        nuevoPago.setVisible(true);
    }

    // Podemos cambiar de cliente y al cambiar cliente carga sus facturas no pagadas
    private void cambiarClienteEnPago() {
        int idx = nuevoPago.getCmbClientes().getSelectedIndex();
        if (idx < 0 || idx >= clientesPago.size()) return;

        Cliente cli = clientesPago.get(idx);
        nuevoPago.getLblSaldoActual().setText("$ " + CampoMoneda.formatear(cli.getSaldo()));
        nuevoPago.getLblLimiteCredito().setText("$ " + CampoMoneda.formatear(cli.getLimiteCredito()));

        facturasDelCliente = new ArrayList<>();
        // Este flag evita que el combo de facturas dispare su evento mientras se esta completando
        cargandoFacturas = true; 
        nuevoPago.getCmbFacturasPendientes().removeAllItems();
        nuevoPago.getCmbFacturasPendientes().addItem("(Pago a cuenta corriente general)");

        String cuitCli = (cli.getCUIT() != null) ? cli.getCUIT().replaceAll("\\D", "") : "";

        List<Factura> todasLasFacturas = daoFacturas.listarFacturas();

        for (Factura f : todasLasFacturas) {
            if (f.getOrden() != null && f.getOrden().getCliente() != null) {
                String cuitFactura = f.getOrden().getCliente().getCUIT().replaceAll("\\D", "");
                
                boolean esMismoCliente = cuitCli.equals(cuitFactura);
                boolean noEstaPagada = !"Pagada".equalsIgnoreCase(f.getEstado());

                if (esMismoCliente && noEstaPagada) {
                    facturasDelCliente.add(f);
                    String num = FacturaDAO.formatearNumeroFactura(f.getNumeroFactura());
                    String monto = "$ " + CampoMoneda.formatear(f.getTotalFinal());
                    String estado = (f.getEstado() != null) ? f.getEstado() : "PENDIENTE";
                    
                    nuevoPago.getCmbFacturasPendientes().addItem(num + " - " + monto + " [" + estado + "]");
                }
            }
        }

        nuevoPago.getCmbFacturasPendientes().setSelectedIndex(0);
        nuevoPago.getTxtMonto().setMonto(null);
        cargandoFacturas = false;
    }

    // Cuando elegimos una factura completamos el monto con el total de la factura
    private void cambiarFacturaEnPago() {
        if (cargandoFacturas) return;

        int idx = nuevoPago.getCmbFacturasPendientes().getSelectedIndex();
        if (idx > 0 && idx - 1 < facturasDelCliente.size()) {
            Factura f = facturasDelCliente.get(idx - 1);
            nuevoPago.getTxtMonto().setMonto(f.getTotalFinal());
        } else if (idx == 0) {
            nuevoPago.getTxtMonto().setMonto(null);            
        }
    }

    // Pasos:
    // 1. Validamos monto. 2. Generamos una referencia 3. Registramos el pago.
    // Si el monto a pagar cubre total de la factura la marcamos como Pagada.
    // El saldo del cliente se descuenta desde Pago al registrarse
    private void confirmarPago() {
        int idxCli = nuevoPago.getCmbClientes().getSelectedIndex();
        if (idxCli < 0 || idxCli >= clientesPago.size()) return;

        Cliente cli = clientesPago.get(idxCli);
        BigDecimal monto = nuevoPago.getTxtMonto().getMonto();

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            Dialogs.advertencia(nuevoPago, "Ingrese un monto a cobrar válido y mayor a $ 0,00.", "Validación");
            return;
        }

        String medio = (String) nuevoPago.getCmbMedioPago().getSelectedItem();
        String ref = nuevoPago.getTxtReferencia().getText().trim();
        
        // Generamos referencia automaticamente si no se informa
        if (ref.isEmpty()) {
            ref = "REC-" + (System.currentTimeMillis() % 1000000);
        }

        int idxFac = nuevoPago.getCmbFacturasPendientes().getSelectedIndex();
        Factura facturaAsociada = null;
        if (idxFac > 0 && idxFac - 1 < facturasDelCliente.size()) {
            facturaAsociada = facturasDelCliente.get(idxFac - 1);
            if (monto.compareTo(facturaAsociada.getTotalFinal()) >= 0) {
                facturaAsociada.setEstado("Pagada");
            }
        }

        Pago pago = new Pago(daoFacturas.siguienteIdPago(), monto, medio, ref, cli, facturaAsociada);
        daoFacturas.agregarPago(pago);

        JOptionPane.showMessageDialog(nuevoPago, 
                "Pago registrado exitosamente.\nNuevo saldo del cliente: $ " + CampoMoneda.formatear(cli.getSaldo()),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

        nuevoPago.dispose();
        cargarTablaPagos(daoFacturas.listarPagos());
        cargarTablaFacturas(daoFacturas.listarFacturas());
    }
}