package view;
import view.estilos.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class NuevaFacturaView extends JDialog {

    private JComboBox<String> cmbOrdenes;
    private JTextField txtNumeroFactura;
    private JTextField txtCliente;
    private JTextField txtCuit;
    private JTextField txtFechaEmision;

    private JTable tablaItems;
    private DefaultTableModel modeloItems;

    private CampoMoneda txtSubtotal;
    private CampoMoneda txtIva;
    private CampoMoneda txtTotalFinal;

    private JButton btnEmitir;
    private JButton btnCancelar;

    public NuevaFacturaView(Frame parent) {
        super(parent, "Emitir Factura de Venta", true);
        setSize(880, 580);
        setLocationRelativeTo(parent);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBackground(Estilos.FONDO);
        contenido.setBorder(new EmptyBorder(16, 18, 16, 18));
        setContentPane(contenido);

        //Encabezado
        JLabel titulo = new JLabel("Emitir Factura de Venta");
        titulo.setFont(Estilos.fuente(Font.BOLD, 18));
        titulo.setForeground(Estilos.TEXTO);
        contenido.add(titulo, BorderLayout.NORTH);

        //Formulario + Tabla
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);

        //seleccionar orden y datos
        JPanel panelCabecera = Estilos.crearTarjeta(new GridBagLayout());
        panelCabecera.setBorder(BorderFactory.createTitledBorder("Datos del Comprobante y Orden"));

        cmbOrdenes = new JComboBox<>();
        cmbOrdenes.setFont(Estilos.fuente(Font.PLAIN, 12));
        cmbOrdenes.setBackground(Color.WHITE);

        txtNumeroFactura = new JTextField();
        txtCliente = new JTextField();
        txtCuit = new JTextField();
        txtFechaEmision = new JTextField();

        JTextField[] bloqueados = {txtNumeroFactura, txtCliente, txtCuit, txtFechaEmision};
        for (JTextField f : bloqueados) {
            Estilos.habilitar(f, false);
        }

        Estilos.agregarCampo(panelCabecera, 0, "Orden de Venta", cmbOrdenes);
        Estilos.agregarCampo(panelCabecera, 1, "N° Factura", txtNumeroFactura);
        Estilos.agregarCampo(panelCabecera, 2, "Cliente", txtCliente);
        Estilos.agregarCampo(panelCabecera, 3, "CUIT", txtCuit);
        Estilos.agregarCampo(panelCabecera, 4, "Fecha", txtFechaEmision);

        //Tabla de la orden a facturar
        String[] columnas = {"Código", "Descripción", "Cantidad", "Precio Unit.", "Subtotal"};
        modeloItems = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaItems = new JTable(modeloItems);

        JPanel panelItems = new JPanel(new BorderLayout());
        panelItems.setOpaque(false);
        panelItems.setBorder(BorderFactory.createTitledBorder("Detalle de Mercadería"));
        panelItems.add(Estilos.estilizarTabla(tablaItems), BorderLayout.CENTER);

        centro.add(panelCabecera, BorderLayout.NORTH);
        centro.add(panelItems, BorderLayout.CENTER);
        contenido.add(centro, BorderLayout.CENTER);

        //Totales - IVA + Botones
        JPanel panelPie = new JPanel(new BorderLayout(0, 8));
        panelPie.setOpaque(false);

        JPanel panelTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 4));
        panelTotales.setOpaque(false);

        txtSubtotal = new CampoMoneda();
        txtSubtotal.setEditable(false);
        txtSubtotal.setPreferredSize(new Dimension(110, 26));

        txtIva = new CampoMoneda();
        txtIva.setEditable(false);
        txtIva.setPreferredSize(new Dimension(110, 26));

        txtTotalFinal = new CampoMoneda();
        txtTotalFinal.setEditable(false);
        txtTotalFinal.setPreferredSize(new Dimension(130, 28));

        panelTotales.add(new JLabel("Subtotal:"));
        panelTotales.add(txtSubtotal);
        panelTotales.add(new JLabel("IVA (21%):"));
        panelTotales.add(txtIva);
        panelTotales.add(new JLabel("Total Final:"));
        panelTotales.add(txtTotalFinal);

        btnEmitir = Estilos.botonPrimario("Emitir Factura");
        btnCancelar = Estilos.botonSecundario("Cancelar");

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setOpaque(false);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnEmitir);

        panelPie.add(panelTotales, BorderLayout.NORTH);
        panelPie.add(panelBotones, BorderLayout.SOUTH);
        contenido.add(panelPie, BorderLayout.SOUTH);
    }

    public JComboBox<String> getCmbOrdenes() { return cmbOrdenes; }
    public JTextField getTxtNumeroFactura() { return txtNumeroFactura; }
    public JTextField getTxtCliente() { return txtCliente; }
    public JTextField getTxtCuit() { return txtCuit; }
    public JTextField getTxtFechaEmision() { return txtFechaEmision; }
    public DefaultTableModel getModeloItems() { return modeloItems; }
    public CampoMoneda getTxtSubtotal() { return txtSubtotal; }
    public CampoMoneda getTxtIva() { return txtIva; }
    public CampoMoneda getTxtTotalFinal() { return txtTotalFinal; }
    public JButton getBtnEmitir() { return btnEmitir; }
    public JButton getBtnCancelar() { return btnCancelar; }
}