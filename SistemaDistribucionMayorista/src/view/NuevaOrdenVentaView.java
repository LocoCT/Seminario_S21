package view;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import view.estilos.*;
import java.awt.*;

public class NuevaOrdenVentaView extends JFrame {

    private JComboBox<String> cmbCliente;
    private JLabel lblLimiteCredito;
    private JLabel lblSaldoActual;
    private JLabel lblCreditoDisponible;

    private JComboBox<String> cmbProducto;
    private JTextField txtCantidad;
    private JButton btnAgregarProducto;
    private JButton btnQuitarProducto;

    private JTable tablaDetalle;
    private DefaultTableModel modeloDetalle;

    private CampoMoneda txtSubtotal;
    private CampoMoneda txtTotal;

    private JButton btnConfirmarOrden;
    private JButton btnCancelar;

    public NuevaOrdenVentaView() {
        setTitle("Emitir Nueva Orden de Venta");
        setSize(1024, 680);
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

        //Clientes + Producto + Carrito
        gbc.gridx = 0;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(0, 0, 0, 7);
        centro.add(crearPanelIzquierdo(), gbc);

        //Detalle total
        gbc.gridx = 1;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(0, 7, 0, 0);
        centro.add(crearPanelDerecho(), gbc);

        contenido.add(centro, BorderLayout.CENTER);
    }

    private JPanel crearEncabezado() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel titulo = new JLabel("Emitir Orden de Venta");
        titulo.setFont(Estilos.fuente(Font.BOLD, 22));
        titulo.setForeground(Estilos.TEXTO);

        JLabel sub = new JLabel("Registro de pedidos de mercadería con validación de crédito y reserva de stock");
        sub.setFont(Estilos.fuente(Font.PLAIN, 12));
        sub.setForeground(Estilos.TEXTO_SUAVE);

        p.add(titulo);
        p.add(sub);
        return p;
    }

    private JPanel crearPanelIzquierdo() {
        JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 10));

        //Datos Cliente
        JPanel panelCliente = new JPanel(new GridBagLayout());
        panelCliente.setOpaque(false);
        panelCliente.setBorder(BorderFactory.createTitledBorder("1. Selección de Cliente"));

        cmbCliente = new JComboBox<>();
        cmbCliente.setFont(Estilos.fuente(Font.PLAIN, 12));
        cmbCliente.setBackground(Color.WHITE);

        lblLimiteCredito = new JLabel("$ 0,00");
        lblLimiteCredito.setFont(Estilos.fuente(Font.BOLD, 12));
        lblLimiteCredito.setForeground(Estilos.TEXTO);

        lblSaldoActual = new JLabel("$ 0,00");
        lblSaldoActual.setFont(Estilos.fuente(Font.BOLD, 12));
        lblSaldoActual.setForeground(Estilos.TEXTO_SUAVE);

        lblCreditoDisponible = new JLabel("$ 0,00");
        lblCreditoDisponible.setFont(Estilos.fuente(Font.BOLD, 12));
        lblCreditoDisponible.setForeground(Estilos.TEXTO);

        Estilos.agregarCampo(panelCliente, 0, "Cliente", cmbCliente);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 1; gbc.gridx = 0;
        panelCliente.add(new JLabel("Límite Crédito:"), gbc);
        gbc.gridx = 1;
        panelCliente.add(lblLimiteCredito, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panelCliente.add(new JLabel("Saldo Actual:"), gbc);
        gbc.gridx = 1;
        panelCliente.add(lblSaldoActual, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        panelCliente.add(new JLabel("Crédito Disponible:"), gbc);
        gbc.gridx = 1;
        panelCliente.add(lblCreditoDisponible, gbc);

        //Select de Productos ---
        JPanel panelAgregarProd = new JPanel(new GridLayout(2, 1, 0, 6));
        panelAgregarProd.setOpaque(false);
        panelAgregarProd.setBorder(BorderFactory.createTitledBorder("2. Agregar Ítems al Pedido"));

        cmbProducto = new JComboBox<>();
        cmbProducto.setPreferredSize(new Dimension(320, 28));
        cmbProducto.setFont(Estilos.fuente(Font.PLAIN, 12));
        cmbProducto.setBackground(Color.WHITE);

        txtCantidad = new JTextField("1", 4);
        Estilos.estilizarCampo(txtCantidad);

        btnAgregarProducto = Estilos.botonPrimario("+ Agregar");
        btnQuitarProducto = Estilos.botonSecundario("Quitar Seleccionado");

        //Producto y Cantidad
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        fila1.setOpaque(false);
        fila1.add(new JLabel("Producto:"));
        fila1.add(cmbProducto);
        fila1.add(new JLabel("Cant:"));
        fila1.add(txtCantidad);

        //Botones
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        fila2.setOpaque(false);
        fila2.add(btnQuitarProducto);
        fila2.add(btnAgregarProducto);

        panelAgregarProd.add(fila1);
        panelAgregarProd.add(fila2);

        //Detalle de Venta
        String[] columnas = {"Código", "Descripción", "Cantidad", "Precio Unit.", "Subtotal"};
        modeloDetalle = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaDetalle = new JTable(modeloDetalle);
        tablaDetalle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel centroIzquierdo = new JPanel(new BorderLayout(0, 8));
        centroIzquierdo.setOpaque(false);
        centroIzquierdo.add(panelAgregarProd, BorderLayout.NORTH);
        centroIzquierdo.add(Estilos.estilizarTabla(tablaDetalle), BorderLayout.CENTER);

        p.add(panelCliente, BorderLayout.NORTH);
        p.add(centroIzquierdo, BorderLayout.CENTER);

        return p;
    }

    private JPanel crearPanelDerecho() {
        JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 12));

        JLabel titulo = new JLabel("Resumen del Pedido");
        titulo.setFont(Estilos.fuente(Font.BOLD, 14));
        titulo.setForeground(Estilos.TEXTO);

        txtSubtotal = new CampoMoneda();
        txtSubtotal.setEditable(false);
        txtTotal = new CampoMoneda();
        txtTotal.setEditable(false);

        JPanel formTotales = new JPanel(new GridBagLayout());
        formTotales.setOpaque(false);

        Estilos.agregarCampo(formTotales, 0, "Subtotal", txtSubtotal);
        Estilos.agregarCampo(formTotales, 1, "Total Final", txtTotal);

        btnConfirmarOrden = Estilos.botonPrimario("Confirmar Orden");
        btnCancelar = Estilos.botonSecundario("Cancelar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnCancelar);
        botones.add(btnConfirmarOrden);

        JPanel arriba = new JPanel(new BorderLayout(0, 12));
        arriba.setOpaque(false);
        arriba.add(titulo, BorderLayout.NORTH);
        arriba.add(formTotales, BorderLayout.CENTER);

        p.add(arriba, BorderLayout.NORTH);
        p.add(botones, BorderLayout.SOUTH);

        return p;
    }

    public JComboBox<String> getCmbCliente() { return cmbCliente; }
    public JLabel getLblLimiteCredito() { return lblLimiteCredito; }
    public JLabel getLblSaldoActual() { return lblSaldoActual; }
    public JLabel getLblCreditoDisponible() { return lblCreditoDisponible; }
    public JComboBox<String> getCmbProducto() { return cmbProducto; }
    public JTextField getTxtCantidad() { return txtCantidad; }
    public JButton getBtnAgregarProducto() { return btnAgregarProducto; }
    public JButton getBtnQuitarProducto() { return btnQuitarProducto; }
    public JTable getTablaDetalle() { return tablaDetalle; }
    public DefaultTableModel getModeloDetalle() { return modeloDetalle; }
    public CampoMoneda getTxtSubtotal() { return txtSubtotal; }
    public CampoMoneda getTxtTotal() { return txtTotal; }
    public JButton getBtnConfirmarOrden() { return btnConfirmarOrden; }
    public JButton getBtnCancelar() { return btnCancelar; }
}