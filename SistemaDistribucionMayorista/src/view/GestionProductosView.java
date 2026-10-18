package view;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import view.estilos.CampoMoneda;
import view.estilos.Estilos;
import java.awt.*;

public class GestionProductosView extends JFrame {

    private JTextField txtBuscar;
    private JButton btnBuscar;
    private DefaultTableModel modeloTabla;
    private JTable tablaProductos;

    private JTextField txtCodigo;
    private JTextField txtDescripcion;
    private JComboBox<String> cmbCategoria;
    private CampoMoneda txtCostoCompra;
    private CampoMoneda txtPrecioBase;
    private JTextField txtStockActual;
    private JTextField txtStockMinimo;

    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnGuardar;
    private JButton btnCancelar;

    public GestionProductosView() {
        setTitle("Catálogo e Inventario de Productos");
        setSize(980, 620);
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

        gbc.gridx = 0;
        gbc.weightx = 0.55;
        gbc.insets = new Insets(0, 0, 0, 7);
        centro.add(crearPanelListado(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.45;
        gbc.insets = new Insets(0, 7, 0, 0);
        centro.add(crearPanelDetalle(), gbc);

        contenido.add(centro, BorderLayout.CENTER);
    }

    private JPanel crearEncabezado() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel titulo = new JLabel("Catálogo e Inventario de Productos");
        titulo.setFont(Estilos.fuente(Font.BOLD, 22));
        titulo.setForeground(Estilos.TEXTO);

        JLabel sub = new JLabel("Consulta, alta y modificación de productos del catálogo");
        sub.setFont(Estilos.fuente(Font.PLAIN, 12));
        sub.setForeground(Estilos.TEXTO_SUAVE);

        p.add(titulo);
        p.add(sub);
        return p;
    }

    //Buscar y tabla
    private JPanel crearPanelListado() {
        JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 10));

        JLabel lblBuscar = new JLabel("Código o Descripción");
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

        //Columnas
        String[] columnas = {"Código", "Descripción", "Categoría", "Precio Base", "Stock"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int col) {
                return false;
            }
        };
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        p.add(busqueda, BorderLayout.NORTH);
        p.add(Estilos.estilizarTabla(tablaProductos), BorderLayout.CENTER);
        return p;
    }

    //Detalle
    private JPanel crearPanelDetalle() {
        JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 12));

        JLabel titulo = new JLabel("Detalle de datos del Producto");
        titulo.setFont(Estilos.fuente(Font.BOLD, 13));
        titulo.setForeground(Estilos.TEXTO);

        txtCodigo = new JTextField();
        txtDescripcion = new JTextField();
        cmbCategoria = new JComboBox<>();
        cmbCategoria.setFont(Estilos.fuente(Font.PLAIN, 12));
        cmbCategoria.setBackground(Color.WHITE);

        txtCostoCompra = new CampoMoneda();
        txtPrecioBase = new CampoMoneda();
        txtStockActual = new JTextField();
        txtStockMinimo = new JTextField();

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        Estilos.agregarCampo(formulario, 0, "Código", txtCodigo);
        Estilos.agregarCampo(formulario, 1, "Descripción", txtDescripcion);
        Estilos.agregarCampo(formulario, 2, "Categoría", cmbCategoria);
        Estilos.agregarCampo(formulario, 3, "Costo de Compra", txtCostoCompra);
        Estilos.agregarCampo(formulario, 4, "Precio Base", txtPrecioBase);
        Estilos.agregarCampo(formulario, 5, "Stock Actual", txtStockActual);
        Estilos.agregarCampo(formulario, 6, "Stock Mínimo", txtStockMinimo);

        btnNuevo = Estilos.botonSecundario("Nuevo");
        btnEditar = Estilos.botonSecundario("Editar");
        btnGuardar = Estilos.botonPrimario("Guardar");
        btnCancelar = Estilos.botonSecundario("Cancelar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnCancelar);
        botones.add(btnGuardar);

        JPanel arriba = new JPanel(new BorderLayout(0, 12));
        arriba.setOpaque(false);
        arriba.add(titulo, BorderLayout.NORTH);
        arriba.add(formulario, BorderLayout.CENTER);

        p.add(arriba, BorderLayout.NORTH);
        p.add(botones, BorderLayout.SOUTH);

        habilitarEdicion(false);
        btnEditar.setEnabled(false);
        return p;
    }


    public void habilitarEdicion(boolean editando) {
        Estilos.habilitar(txtCodigo, false);
        Estilos.habilitar(txtDescripcion, editando);
        Estilos.habilitar(cmbCategoria, editando);
        Estilos.habilitar(txtCostoCompra, editando);
        Estilos.habilitar(txtPrecioBase, editando);
        Estilos.habilitar(txtStockActual, editando);
        Estilos.habilitar(txtStockMinimo, editando);

        btnGuardar.setEnabled(editando);
        btnCancelar.setEnabled(editando);
        btnNuevo.setEnabled(!editando);
        btnEditar.setEnabled(!editando);

        //Mientras se edita no se puede cambiar de producto ni buscar
        txtBuscar.setEnabled(!editando);
        btnBuscar.setEnabled(!editando);
        tablaProductos.setEnabled(!editando);
    }

    public void habilitarEditar(boolean habilitado) {
        btnEditar.setEnabled(habilitado);
    }

    public JTextField getTxtBuscar() { return txtBuscar; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public JTable getTablaProductos() { return tablaProductos; }
    public JTextField getTxtCodigo() { return txtCodigo; }
    public JTextField getTxtDescripcion() { return txtDescripcion; }
    public JComboBox<String> getCmbCategoria() { return cmbCategoria; }
    public CampoMoneda getTxtCostoCompra() { return txtCostoCompra; }
    public CampoMoneda getTxtPrecioBase() { return txtPrecioBase; }
    public JTextField getTxtStockActual() { return txtStockActual; }
    public JTextField getTxtStockMinimo() { return txtStockMinimo; }
    public JButton getBtnNuevo() { return btnNuevo; }
    public JButton getBtnEditar() { return btnEditar; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnCancelar() { return btnCancelar; }
}