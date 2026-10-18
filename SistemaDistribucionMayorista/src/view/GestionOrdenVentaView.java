package view;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import view.estilos.*;

public class GestionOrdenVentaView extends JFrame {

    private JTextField txtBuscar;
    private JButton btnBuscar;
    private DefaultTableModel modeloTablaOrdenes;
    private JTable tablaOrdenes;

    // Campos de detalle
    private JTextField txtNumeroOrden;
    private JTextField txtFecha;
    private JTextField txtCliente;
    private JTextField txtVendedor;
    private JTextField txtEstado;
    private CampoMoneda txtTotal;

    // Tabla 
    private DefaultTableModel modeloTablaDetalle;
    private JTable tablaDetalle;

    private JButton btnNuevaOrden;
    private JButton btnCerrar;

    public GestionOrdenVentaView() {
        setTitle("Gestión de Órdenes de Venta");
        setSize(1040, 640);
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

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        //Lista de ordenes
        gbc.gridx = 0;
        gbc.weightx = 0.52;
        gbc.insets = new Insets(0, 0, 0, 7);
        centro.add(crearPanelListado(), gbc);

        //Detalle
        gbc.gridx = 1;
        gbc.weightx = 0.48;
        gbc.insets = new Insets(0, 7, 0, 0);
        centro.add(crearPanelDetalle(), gbc);

        contenido.add(centro, BorderLayout.CENTER);
    }

    private JPanel crearEncabezado() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel titulo = new JLabel("Gestión de Órdenes de Venta");
        titulo.setFont(Estilos.fuente(Font.BOLD, 22));
        titulo.setForeground(Estilos.TEXTO);

        JLabel sub = new JLabel("Consulta de pedidos registrados, detalle de ítems y emisión de nuevas ventas");
        sub.setFont(Estilos.fuente(Font.PLAIN, 12));
        sub.setForeground(Estilos.TEXTO_SUAVE);

        p.add(titulo);
        p.add(sub);
        return p;
    }

    //Buscar y tabla
    private JPanel crearPanelListado() {
        JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 10));

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(Estilos.fuente(Font.BOLD, 12));
        lblBuscar.setForeground(Estilos.TEXTO);

        txtBuscar = new JTextField();
        Estilos.estilizarCampo(txtBuscar);
        btnBuscar = Estilos.botonPrimario("Buscar");

        JPanel busqueda = new JPanel(new BorderLayout(8, 0));
        busqueda.setOpaque(false);
        busqueda.add(lblBuscar, BorderLayout.WEST);
        busqueda.add(txtBuscar, BorderLayout.CENTER);
        busqueda.add(btnBuscar, BorderLayout.EAST);

        String[] columnas = {"N° Orden", "Fecha", "Cliente", "Total", "Estado"};
        modeloTablaOrdenes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int col) {
                return false;
            }
        };
        tablaOrdenes = new JTable(modeloTablaOrdenes);
        tablaOrdenes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnNuevaOrden = Estilos.botonPrimario("+ Nueva Orden de Venta");
        JPanel barraInferior = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraInferior.setOpaque(false);
        barraInferior.add(btnNuevaOrden);

        p.add(busqueda, BorderLayout.NORTH);
        p.add(Estilos.estilizarTabla(tablaOrdenes), BorderLayout.CENTER);
        p.add(barraInferior, BorderLayout.SOUTH);
        return p;
    }

    //Detalle
    private JPanel crearPanelDetalle() {
        JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 10));

        JLabel titulo = new JLabel("Detalle de la Orden");
        titulo.setFont(Estilos.fuente(Font.BOLD, 13));
        titulo.setForeground(Estilos.TEXTO);

        txtNumeroOrden = new JTextField();
        txtFecha = new JTextField();
        txtCliente = new JTextField();
        txtVendedor = new JTextField();
        txtEstado = new JTextField();
        txtTotal = new CampoMoneda();
        txtTotal.setEditable(false);

        //Bloquea edicion
        JTextField[] campos = {txtNumeroOrden, txtFecha, txtCliente, txtVendedor, txtEstado};
        for (JTextField f : campos) {
            Estilos.habilitar(f, false);
        }

        JPanel formCabecera = new JPanel(new GridBagLayout());
        formCabecera.setOpaque(false);
        Estilos.agregarCampo(formCabecera, 0, "N° Orden", txtNumeroOrden);
        Estilos.agregarCampo(formCabecera, 1, "Fecha", txtFecha);
        Estilos.agregarCampo(formCabecera, 2, "Cliente", txtCliente);
        Estilos.agregarCampo(formCabecera, 3, "Vendedor", txtVendedor);
        Estilos.agregarCampo(formCabecera, 4, "Estado", txtEstado);
        Estilos.agregarCampo(formCabecera, 5, "Total", txtTotal);

        //Tabla de ordenes
        String[] columnasDetalle = {"Código", "Descripción", "Cant", "P. Unit", "Subtotal"};
        modeloTablaDetalle = new DefaultTableModel(columnasDetalle, 0) {
            @Override
            public boolean isCellEditable(int fila, int col) {
                return false;
            }
        };
        tablaDetalle = new JTable(modeloTablaDetalle);
        tablaDetalle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel panelItems = new JPanel(new BorderLayout(0, 4));
        panelItems.setOpaque(false);
        panelItems.setBorder(BorderFactory.createTitledBorder("Ítems del Pedido"));
        panelItems.add(Estilos.estilizarTabla(tablaDetalle), BorderLayout.CENTER);

        btnCerrar = Estilos.botonSecundario("Cerrar");
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botones.setOpaque(false);
        botones.add(btnCerrar);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 10));
        cuerpo.setOpaque(false);
        cuerpo.add(formCabecera, BorderLayout.NORTH);
        cuerpo.add(panelItems, BorderLayout.CENTER);

        p.add(titulo, BorderLayout.NORTH);
        p.add(cuerpo, BorderLayout.CENTER);
        p.add(botones, BorderLayout.SOUTH);
        return p;
    }

    
    public JTextField getTxtBuscar() { return txtBuscar; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JTable getTablaOrdenes() { return tablaOrdenes; }
    public DefaultTableModel getModeloTablaOrdenes() { return modeloTablaOrdenes; }
    public JTextField getTxtNumeroOrden() { return txtNumeroOrden; }
    public JTextField getTxtFecha() { return txtFecha; }
    public JTextField getTxtCliente() { return txtCliente; }
    public JTextField getTxtVendedor() { return txtVendedor; }
    public JTextField getTxtEstado() { return txtEstado; }
    public CampoMoneda getTxtTotal() { return txtTotal; }
    public JTable getTablaDetalle() { return tablaDetalle; }
    public DefaultTableModel getModeloTablaDetalle() { return modeloTablaDetalle; }
    public JButton getBtnNuevaOrden() { return btnNuevaOrden; }
    public JButton getBtnCerrar() { return btnCerrar; }
}