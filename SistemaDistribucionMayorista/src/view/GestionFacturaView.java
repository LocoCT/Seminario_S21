package view;
import view.estilos.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GestionFacturaView extends JFrame {

    private JTabbedPane pestania;

    //Pestaña de Facturas
    
    private JButton btnBuscarFactura;
    private JButton btnNuevaFactura;
    private JTable tablaFacturas;
    private JTable tablaItemsFactura;
    private DefaultTableModel modeloFacturas;
    private DefaultTableModel modeloItemsFactura;
    private JTextField txtBuscarFactura;
    private JTextField txtDetalleFacturaNumero;
    private JTextField txtDetalleFacturaFecha;
    private JTextField txtDetalleFacturaCliente;
    private JTextField txtDetalleFacturaOrden;
    private JTextField txtDetalleFacturaEstado;
    private CampoMoneda txtDetalleFacturaSubtotal;
    private CampoMoneda txtDetalleFacturaIva;
    private CampoMoneda txtDetalleFacturaTotal;

    //Pestaña Historial Cobros
    private JTable tablaPagos;
    private DefaultTableModel modeloPagos;
    private JButton btnRegistrarPago;
    private JButton btnCerrar;

    public GestionFacturaView() {
        setTitle("Gestión de Facturación y Cobros");
        setSize(1060, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setBackground(Estilos.FONDO);
        contenido.setBorder(new EmptyBorder(18, 20, 18, 20));
        setContentPane(contenido);

        contenido.add(crearEncabezado(), BorderLayout.NORTH);

        pestania = new JTabbedPane();
        pestania.setFont(Estilos.fuente(Font.BOLD, 12));
        pestania.addTab("Facturas Emitidas", crearPanelFacturas());
        pestania.addTab("Historial de Cobros", crearPanelCobros());

        contenido.add(pestania, BorderLayout.CENTER);

        //Barra inferior con boton cerrar
        btnCerrar = Estilos.botonSecundario("Cerrar");
        JPanel barraInf = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        barraInf.setOpaque(false);
        barraInf.add(btnCerrar);
        contenido.add(barraInf, BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel titulo = new JLabel("Facturación y Cobros");
        titulo.setFont(Estilos.fuente(Font.BOLD, 22));
        titulo.setForeground(Estilos.TEXTO);

        JLabel sub = new JLabel("Emisión fiscal de facturas sobre órdenes de venta, seguimiento y registro de pagos");
        sub.setFont(Estilos.fuente(Font.PLAIN, 12));
        sub.setForeground(Estilos.TEXTO_SUAVE);

        p.add(titulo);
        p.add(sub);
        return p;
    }

    //Pestaña Factura
    private JPanel crearPanelFacturas() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        //Listado
        gbc.gridx = 0; gbc.weightx = 0.52;
        gbc.insets = new Insets(8, 0, 0, 6);
        p.add(crearListadoFacturas(), gbc);

        //Detalle
        gbc.gridx = 1; gbc.weightx = 0.48;
        gbc.insets = new Insets(8, 6, 0, 0);
        p.add(crearDetalleFactura(), gbc);

        return p;
    }

    private JPanel crearListadoFacturas() {
        JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 10));

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(Estilos.fuente(Font.BOLD, 12));
        lblBuscar.setForeground(Estilos.TEXTO);

        txtBuscarFactura = new JTextField();
        Estilos.estilizarCampo(txtBuscarFactura);
        btnBuscarFactura = Estilos.botonPrimario("Buscar");

        JPanel busqueda = new JPanel(new BorderLayout(8, 0));
        busqueda.setOpaque(false);
        busqueda.add(lblBuscar, BorderLayout.WEST);
        busqueda.add(txtBuscarFactura, BorderLayout.CENTER);
        busqueda.add(btnBuscarFactura, BorderLayout.EAST);

        String[] cols = {"N° Factura", "Fecha", "Cliente", "Total Final", "Estado"};
        modeloFacturas = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaFacturas = new JTable(modeloFacturas);
        tablaFacturas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnNuevaFactura = Estilos.botonPrimario("+ Emitir Factura");
        JPanel barraBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBoton.setOpaque(false);
        barraBoton.add(btnNuevaFactura);

        p.add(busqueda, BorderLayout.NORTH);
        p.add(Estilos.estilizarTabla(tablaFacturas), BorderLayout.CENTER);
        p.add(barraBoton, BorderLayout.SOUTH);
        return p;
    }

    private JPanel crearDetalleFactura() {
        JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 10));

        JLabel titulo = new JLabel("Detalle del Comprobante");
        titulo.setFont(Estilos.fuente(Font.BOLD, 13));
        titulo.setForeground(Estilos.TEXTO);

        txtDetalleFacturaNumero = new JTextField();
        txtDetalleFacturaFecha = new JTextField();
        txtDetalleFacturaCliente = new JTextField();
        txtDetalleFacturaOrden = new JTextField();
        txtDetalleFacturaEstado = new JTextField();

        JTextField[] bloqueados = {txtDetalleFacturaNumero, txtDetalleFacturaFecha, txtDetalleFacturaCliente, txtDetalleFacturaOrden, txtDetalleFacturaEstado};
        for (JTextField f : bloqueados) Estilos.habilitar(f, false);

        txtDetalleFacturaSubtotal = new CampoMoneda();
        txtDetalleFacturaSubtotal.setEditable(false);
        txtDetalleFacturaIva = new CampoMoneda();
        txtDetalleFacturaIva.setEditable(false);
        txtDetalleFacturaTotal = new CampoMoneda();
        txtDetalleFacturaTotal.setEditable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        Estilos.agregarCampo(form, 0, "N° Factura", txtDetalleFacturaNumero);
        Estilos.agregarCampo(form, 1, "Fecha", txtDetalleFacturaFecha);
        Estilos.agregarCampo(form, 2, "Cliente", txtDetalleFacturaCliente);
        Estilos.agregarCampo(form, 3, "Orden Base", txtDetalleFacturaOrden);
        Estilos.agregarCampo(form, 4, "Estado", txtDetalleFacturaEstado);
        Estilos.agregarCampo(form, 5, "Subtotal", txtDetalleFacturaSubtotal);
        Estilos.agregarCampo(form, 6, "IVA (21%)", txtDetalleFacturaIva);
        Estilos.agregarCampo(form, 7, "Total Final", txtDetalleFacturaTotal);

        String[] colsItems = {"Código", "Producto", "Cant", "P. Unit", "Subtotal"};
        modeloItemsFactura = new DefaultTableModel(colsItems, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaItemsFactura = new JTable(modeloItemsFactura);

        JPanel panelItems = new JPanel(new BorderLayout());
        panelItems.setOpaque(false);
        panelItems.setBorder(BorderFactory.createTitledBorder("Ítems Facturados"));
        panelItems.add(Estilos.estilizarTabla(tablaItemsFactura), BorderLayout.CENTER);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 8));
        cuerpo.setOpaque(false);
        cuerpo.add(form, BorderLayout.NORTH);
        cuerpo.add(panelItems, BorderLayout.CENTER);

        p.add(titulo, BorderLayout.NORTH);
        p.add(cuerpo, BorderLayout.CENTER);
        return p;
    }

    //Pestaña Historial de Cobros
    private JPanel crearPanelCobros() {
        JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 10));

        JLabel titulo = new JLabel("Registro Histórico de Pagos y Cobranzas");
        titulo.setFont(Estilos.fuente(Font.BOLD, 13));
        titulo.setForeground(Estilos.TEXTO);

        String[] colsPagos = {"ID", "Fecha", "Cliente", "N° Factura", "Comprobante / Ref.", "Medio de Pago", "Monto Cobrado"};
        modeloPagos = new DefaultTableModel(colsPagos, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaPagos = new JTable(modeloPagos);

        //Centra contenido
        DefaultTableCellRenderer renderCentro = new DefaultTableCellRenderer();
        renderCentro.setHorizontalAlignment(SwingConstants.CENTER);
        // Centramos: ID (0), Fecha (1), N° Factura (3), Comprobante/Ref (4) y Medio de Pago (5)
        tablaPagos.getColumnModel().getColumn(0).setCellRenderer(renderCentro);
        tablaPagos.getColumnModel().getColumn(1).setCellRenderer(renderCentro);
        tablaPagos.getColumnModel().getColumn(3).setCellRenderer(renderCentro);
        tablaPagos.getColumnModel().getColumn(4).setCellRenderer(renderCentro);
        tablaPagos.getColumnModel().getColumn(5).setCellRenderer(renderCentro);

        btnRegistrarPago = Estilos.botonPrimario("+ Registrar Cobro a Cliente");
        JPanel barraBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBoton.setOpaque(false);
        barraBoton.add(btnRegistrarPago);

        p.add(titulo, BorderLayout.NORTH);
        p.add(Estilos.estilizarTabla(tablaPagos), BorderLayout.CENTER);
        p.add(barraBoton, BorderLayout.SOUTH);
        return p;
    }

    
    public JTextField getTxtBuscarFactura() { return txtBuscarFactura; }
    public JButton getBtnBuscarFactura() { return btnBuscarFactura; }
    public JTable getTablaFacturas() { return tablaFacturas; }
    public DefaultTableModel getModeloFacturas() { return modeloFacturas; }
    public JTextField getTxtDetalleFacturaNumero() { return txtDetalleFacturaNumero; }
    public JTextField getTxtDetalleFacturaFecha() { return txtDetalleFacturaFecha; }
    public JTextField getTxtDetalleFacturaCliente() { return txtDetalleFacturaCliente; }
    public JTextField getTxtDetalleFacturaOrden() { return txtDetalleFacturaOrden; }
    public JTextField getTxtDetalleFacturaEstado() { return txtDetalleFacturaEstado; }
    public CampoMoneda getTxtDetalleFacturaSubtotal() { return txtDetalleFacturaSubtotal; }
    public CampoMoneda getTxtDetalleFacturaIva() { return txtDetalleFacturaIva; }
    public CampoMoneda getTxtDetalleFacturaTotal() { return txtDetalleFacturaTotal; }
    public JTable getTablaItemsFactura() { return tablaItemsFactura; }
    public DefaultTableModel getModeloItemsFactura() { return modeloItemsFactura; }
    public JButton getBtnNuevaFactura() { return btnNuevaFactura; }
    public JTable getTablaPagos() { return tablaPagos; }
    public DefaultTableModel getModeloPagos() { return modeloPagos; }
    public JButton getBtnRegistrarPago() { return btnRegistrarPago; }
    public JButton getBtnCerrar() { return btnCerrar; }
}