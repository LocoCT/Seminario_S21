package view;
import view.estilos.Estilos;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NuevoMovimientoStockView extends JDialog {

    private JComboBox<String> cmbProductos;
    private JComboBox<String> cmbTipoMovimiento;
    private JComboBox<String> cmbDeposito;
    private JTextField txtCantidad;
    private JTextField txtMotivo;

    private JButton btnRegistrar;
    private JButton btnCancelar;
    private JLabel lblError;

    public NuevoMovimientoStockView(Frame parent) {
        super(parent, "Registrar Movimiento de Stock", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        inicializarComponentes();
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void inicializarComponentes() {
        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setBackground(Estilos.FONDO);
        fondo.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(fondo);

        JPanel tarjeta = Estilos.crearTarjeta(new BorderLayout(0, 12));
        tarjeta.setPreferredSize(new Dimension(460, 480));

        JLabel sub = new JLabel("Movimientos físicos de mercadería");
        sub.setFont(Estilos.fuente(Font.PLAIN, 11));
        sub.setForeground(Estilos.TEXTO_SUAVE);

        JLabel titulo = new JLabel("Nuevo Movimiento de Stock");
        titulo.setFont(Estilos.fuente(Font.BOLD, 18));
        titulo.setForeground(Estilos.TEXTO);

        JPanel enc = new JPanel();
        enc.setLayout(new BoxLayout(enc, BoxLayout.Y_AXIS));
        enc.setOpaque(false);
        enc.add(sub);
        enc.add(titulo);

        cmbProductos = new JComboBox<>();
        cmbProductos.setFont(Estilos.fuente(Font.PLAIN, 12));
        cmbProductos.setBackground(Color.WHITE);

        cmbTipoMovimiento = new JComboBox<>(new String[]{"Ingreso por compra", "Egreso por venta", "Ajuste manual"});
        cmbTipoMovimiento.setFont(Estilos.fuente(Font.PLAIN, 12));
        cmbTipoMovimiento.setBackground(Color.WHITE);

        cmbDeposito = new JComboBox<>(new String[]{"Depósito Central", "Depósito Secundario"});
        cmbDeposito.setFont(Estilos.fuente(Font.PLAIN, 12));
        cmbDeposito.setBackground(Color.WHITE);

        txtCantidad = new JTextField();
        Estilos.estilizarCampo(txtCantidad);

        txtMotivo = new JTextField();
        txtMotivo.setFont(Estilos.fuente(Font.PLAIN, 12));
        Estilos.estilizarCampo(txtMotivo);
        
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);

        Estilos.agregarCampo(formulario, 0, "Producto", cmbProductos);
        Estilos.agregarCampo(formulario, 1, "Tipo de movimiento", cmbTipoMovimiento);
        Estilos.agregarCampo(formulario, 2, "Depósito de destino", cmbDeposito);
        Estilos.agregarCampo(formulario, 3, "Cantidad", txtCantidad);
        Estilos.agregarCampo(formulario, 4, "Motivo del movimiento", txtMotivo);

        lblError = new JLabel(" ");
        lblError.setFont(Estilos.fuente(Font.PLAIN, 11));
        lblError.setForeground(new Color(185, 28, 28));

        btnCancelar = Estilos.botonSecundario("Cancelar");
        btnRegistrar = Estilos.botonPrimario("Registrar Movimiento");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnCancelar);
        botones.add(btnRegistrar);

        JPanel abajo = new JPanel(new BorderLayout(0, 8));
        abajo.setOpaque(false);
        abajo.add(lblError, BorderLayout.NORTH);
        abajo.add(botones, BorderLayout.SOUTH);

        tarjeta.add(enc, BorderLayout.NORTH);
        tarjeta.add(formulario, BorderLayout.CENTER);
        tarjeta.add(abajo, BorderLayout.SOUTH);

        fondo.add(tarjeta, BorderLayout.CENTER);
        getRootPane().setDefaultButton(btnRegistrar);
    }

    public void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }

    public JComboBox<String> getCmbProductos() { return cmbProductos; }
    public JComboBox<String> getCmbTipoMovimiento() { return cmbTipoMovimiento; }
    public JComboBox<String> getCmbDeposito() { return cmbDeposito; }
    public JTextField getTxtCantidad() { return txtCantidad; }
    public JTextField getTxtMotivo() { return txtMotivo; }
    public JButton getBtnRegistrar() { return btnRegistrar; }
    public JButton getBtnCancelar() { return btnCancelar; }
}