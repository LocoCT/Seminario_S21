package view;
import view.estilos.Estilos;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GestionStockView extends JFrame {

    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JTable tablaMovimientos;
    private DefaultTableModel modeloMovimientos;
    private JButton btnNuevoMovimiento;
    private JButton btnCerrar;

    public GestionStockView() {
        setTitle("Control e Inventario de Stock en Depósitos");
        setSize(1024, 640);
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
        contenido.add(crearPanelCentro(), BorderLayout.CENTER);

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

        JLabel titulo = new JLabel("Control de Stock y Movimientos de Depósito");
        titulo.setFont(Estilos.fuente(Font.BOLD, 22));
        titulo.setForeground(Estilos.TEXTO);

        JLabel sub = new JLabel("Historial de ingresos, egresos y ajustes físicos de inventario");
        sub.setFont(Estilos.fuente(Font.PLAIN, 12));
        sub.setForeground(Estilos.TEXTO_SUAVE);

        p.add(titulo);
        p.add(sub);
        return p;
    }

    private JPanel crearPanelCentro() {
        JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 10));

        JLabel lblBuscar = new JLabel("Buscar Producto / Depósito:");
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

        String[] cols = {"ID", "Fecha / Hora", "Producto", "Tipo Movimiento", "Cantidad", "Depósito", "Motivo"};
        modeloMovimientos = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaMovimientos = new JTable(modeloMovimientos);
        tablaMovimientos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer renderCentro = new DefaultTableCellRenderer();
        renderCentro.setHorizontalAlignment(SwingConstants.CENTER);
        tablaMovimientos.getColumnModel().getColumn(0).setCellRenderer(renderCentro);
        tablaMovimientos.getColumnModel().getColumn(1).setCellRenderer(renderCentro);
        tablaMovimientos.getColumnModel().getColumn(3).setCellRenderer(renderCentro);
        tablaMovimientos.getColumnModel().getColumn(4).setCellRenderer(renderCentro);

        btnNuevoMovimiento = Estilos.botonPrimario("+ Registrar Movimiento de Stock");
        JPanel barraBoton = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraBoton.setOpaque(false);
        barraBoton.add(btnNuevoMovimiento);

        p.add(busqueda, BorderLayout.NORTH);
        p.add(Estilos.estilizarTabla(tablaMovimientos), BorderLayout.CENTER);
        p.add(barraBoton, BorderLayout.SOUTH);

        return p;
    }

    public JTextField getTxtBuscar() { return txtBuscar; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JTable getTablaMovimientos() { return tablaMovimientos; }
    public DefaultTableModel getModeloMovimientos() { return modeloMovimientos; }
    public JButton getBtnNuevoMovimiento() { return btnNuevoMovimiento; }
    public JButton getBtnCerrar() { return btnCerrar; }
}