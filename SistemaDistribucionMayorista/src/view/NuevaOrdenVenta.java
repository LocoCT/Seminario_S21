package view;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class NuevaOrdenVenta extends JFrame{

  public NuevaOrdenVenta(){

    setTitle("Nueva Orden de Venta");
    setSize(850, 650);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLayout(new BorderLayout(10, 10));
    inicializarComponentes();
  }

  private void inicializarComponentes(){

    JPanel panelSuperior = new JPanel(new GridLayout(2, 1, 5, 5));
    panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

    JPanel panelInforOrden = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelInforOrden.add(new JLabel("N° de Orden: OV-001 | Fecha: 2023-10-01"));

    JPanel panelCliente = new JPanel(new BorderLayout(10, 10));
    panelCliente.setBorder(BorderFactory.createTitledBorder("Cliente"));
    panelCliente.add(new JLabel("Buscar Cliente:"), BorderLayout.WEST);
    panelCliente.add(new JTextField("Distribuidora Norte S.A."), BorderLayout.CENTER);

    JLabel lblCredito = new JLabel("Limite de Credito disponible: $ 5,000.00");
    lblCredito.setForeground(new Color(39, 174,96));
    lblCredito.setFont(new Font("Arial", Font.BOLD, 12));
    panelCliente.add(lblCredito, BorderLayout.EAST);

    panelSuperior.add(panelInforOrden);
    panelSuperior.add(panelCliente);
    add(panelSuperior, BorderLayout.NORTH);

    JPanel panelCentral = new JPanel(new BorderLayout(5, 5));
    panelCentral.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

    JPanel panelBuscarProducto = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelBuscarProducto.add(new JLabel("Producto (codigo o nombre):"));
    panelBuscarProducto.add(new JTextField(20));
    panelBuscarProducto.add(new JLabel("Cantidad:"));
    panelBuscarProducto.add(new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1)));
    panelBuscarProducto.add(new JButton("Agregar Producto"));

    String[] columnasCarrito = {"Código", "Producto", "Cantidad", "Precio Unitario", "SubTotal"};
    DefaultTableModel modeloCarrito = new DefaultTableModel(columnasCarrito, 0);
    JTable tablaCarrito = new JTable(modeloCarrito);

    panelCentral.add(panelBuscarProducto, BorderLayout.NORTH);
    panelCentral.add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);
    add(panelCentral, BorderLayout.CENTER);

    JPanel panelInferior = new JPanel(new BorderLayout());
    panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JPanel panelTotales = new JPanel(new GridLayout(3, 2, 5, 5));
    panelTotales.add(new JLabel("Subtotal:"));
    panelTotales.add(new JLabel("$  18.900"));
    panelTotales.add(new JLabel("Descuento:"));
    panelTotales.add(new JLabel("-$ 1.890"));

    JLabel lblTotalTexto = new JLabel("Total Final:");
    lblTotalTexto.setFont(new Font("Arial", Font.BOLD, 16));
    JLabel lblTotalMonto = new JLabel("$ 17.010");
    lblTotalMonto.setFont(new Font("Arial", Font.BOLD, 16));
    panelTotales.add(lblTotalTexto);
    panelTotales.add(lblTotalMonto);

    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelBotones.add(new JButton("Cancelar"));
    panelBotones.add(new JButton("Confirmar Orden"));

    panelInferior.add(panelTotales, BorderLayout.WEST);
    panelInferior.add(panelBotones, BorderLayout.EAST);
    add(panelInferior, BorderLayout.SOUTH);

  }
  
}