package view;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import view.estilos.CampoMoneda;
import view.estilos.Estilos;
import java.awt.*;

public class NuevoProductoView extends JDialog {

    private JTextField txtCodigo;
    private JTextField txtDescripcion;
    private JComboBox<String> cmbCategoria;
    private CampoMoneda txtCostoCompra;
    private CampoMoneda txtPrecioBase;
    private JTextField txtStockActual;
    private JTextField txtStockMinimo;

    private JButton btnGuardar;
    private JButton btnCancelar;
    private JLabel lblError;

    public NuevoProductoView(Frame padre) {
        super(padre, "Nuevo producto", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        inicializarComponentes();
        pack();
        setResizable(false);
        setLocationRelativeTo(padre);
    }

    private void inicializarComponentes() {
        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setBackground(Estilos.FONDO);
        fondo.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(fondo);

        JPanel tarjeta = Estilos.crearTarjeta(new BorderLayout(0, 12));
        tarjeta.setPreferredSize(new Dimension(420, 490));

        JLabel sub = new JLabel("Alta de producto");
        sub.setFont(Estilos.fuente(Font.PLAIN, 11));
        sub.setForeground(Estilos.TEXTO_SUAVE);

        JLabel titulo = new JLabel("Nuevo producto");
        titulo.setFont(Estilos.fuente(Font.BOLD, 20));
        titulo.setForeground(Estilos.TEXTO);

        JPanel encabezado = new JPanel();
        encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
        encabezado.setOpaque(false);
        encabezado.add(sub);
        encabezado.add(titulo);

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
        Estilos.agregarCampo(formulario, 5, "Stock Inicial", txtStockActual);
        Estilos.agregarCampo(formulario, 6, "Stock Mínimo", txtStockMinimo);

        Estilos.habilitar(txtCodigo, false);

        lblError = new JLabel(" ");
        lblError.setFont(Estilos.fuente(Font.PLAIN, 11));
        lblError.setForeground(new Color(185, 28, 28));

        btnCancelar = Estilos.botonSecundario("Cancelar");
        btnGuardar = Estilos.botonPrimario("Guardar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnCancelar);
        botones.add(btnGuardar);

        JPanel abajo = new JPanel(new BorderLayout(0, 8));
        abajo.setOpaque(false);
        abajo.add(lblError, BorderLayout.NORTH);
        abajo.add(botones, BorderLayout.SOUTH);

        tarjeta.add(encabezado, BorderLayout.NORTH);
        tarjeta.add(formulario, BorderLayout.CENTER);
        tarjeta.add(abajo, BorderLayout.SOUTH);

        fondo.add(tarjeta, BorderLayout.CENTER);

        getRootPane().setDefaultButton(btnGuardar);
    }

    public void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }

    public JTextField getTxtCodigo() { return txtCodigo; }
    public JTextField getTxtDescripcion() { return txtDescripcion; }
    public JComboBox<String> getCmbCategoria() { return cmbCategoria; }
    public CampoMoneda getTxtCostoCompra() { return txtCostoCompra; }
    public CampoMoneda getTxtPrecioBase() { return txtPrecioBase; }
    public JTextField getTxtStockActual() { return txtStockActual; }
    public JTextField getTxtStockMinimo() { return txtStockMinimo; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnCancelar() { return btnCancelar; }
}