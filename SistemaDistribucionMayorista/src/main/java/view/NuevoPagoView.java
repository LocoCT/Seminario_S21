package view;
import view.estilos.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NuevoPagoView extends JDialog {

    private JComboBox<String> cmbClientes;
    private JLabel lblSaldoActual;
    private JLabel lblLimiteCredito;

    private JComboBox<String> cmbFacturasPendientes;
    private JComboBox<String> cmbMedioPago;
    private JTextField txtReferencia;
    private CampoMoneda txtMonto;

    private JButton btnRegistrar;
    private JButton btnCancelar;

    public NuevoPagoView(Frame parent) {
        super(parent, "Registrar Cobro a Cliente", true);
        setSize(540, 460);
        setLocationRelativeTo(parent);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setBackground(Estilos.FONDO);
        contenido.setBorder(new EmptyBorder(18, 20, 18, 20));
        setContentPane(contenido);

        // Encabezado
        JLabel titulo = new JLabel("Registrar Cobro / Pago");
        titulo.setFont(Estilos.fuente(Font.BOLD, 18));
        titulo.setForeground(Estilos.TEXTO);

        JLabel sub = new JLabel("Ingreso de dinero a cuenta corriente y cancelación de facturas");
        sub.setFont(Estilos.fuente(Font.PLAIN, 12));
        sub.setForeground(Estilos.TEXTO_SUAVE);

        JPanel enc = new JPanel();
        enc.setLayout(new BoxLayout(enc, BoxLayout.Y_AXIS));
        enc.setOpaque(false);
        enc.add(titulo);
        enc.add(sub);
        contenido.add(enc, BorderLayout.NORTH);

        // Formulario
        JPanel tarjeta = Estilos.crearTarjeta(new GridBagLayout());

        cmbClientes = new JComboBox<>();
        cmbClientes.setFont(Estilos.fuente(Font.PLAIN, 12));
        cmbClientes.setBackground(Color.WHITE);

        lblSaldoActual = new JLabel("$ 0,00");
        lblSaldoActual.setFont(Estilos.fuente(Font.BOLD, 12));
        lblSaldoActual.setForeground(new Color(185, 28, 28)); // Rojo para saldo deudor

        lblLimiteCredito = new JLabel("$ 0,00");
        lblLimiteCredito.setFont(Estilos.fuente(Font.PLAIN, 12));
        lblLimiteCredito.setForeground(Estilos.TEXTO);

        cmbFacturasPendientes = new JComboBox<>();
        cmbFacturasPendientes.setFont(Estilos.fuente(Font.PLAIN, 12));
        cmbFacturasPendientes.setBackground(Color.WHITE);

        cmbMedioPago = new JComboBox<>(new String[]{"Transferencia Bancaria", "Efectivo", "Cheque"});
        cmbMedioPago.setFont(Estilos.fuente(Font.PLAIN, 12));
        cmbMedioPago.setBackground(Color.WHITE);

        txtReferencia = new JTextField();
        Estilos.estilizarCampo(txtReferencia);

        txtMonto = new CampoMoneda();

        //Armado formulario
        Estilos.agregarCampo(tarjeta, 0, "Cliente", cmbClientes);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 1; gbc.gridx = 0;
        tarjeta.add(new JLabel("Saldo Deudor:"), gbc);
        gbc.gridx = 1;
        tarjeta.add(lblSaldoActual, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        tarjeta.add(new JLabel("Límite Crédito:"), gbc);
        gbc.gridx = 1;
        tarjeta.add(lblLimiteCredito, gbc);

        Estilos.agregarCampo(tarjeta, 3, "Imputar a Factura", cmbFacturasPendientes);
        Estilos.agregarCampo(tarjeta, 4, "Medio de Pago", cmbMedioPago);
        Estilos.agregarCampo(tarjeta, 5, "N° Comprobante / Ref.", txtReferencia);
        Estilos.agregarCampo(tarjeta, 6, "Monto a Cobrar", txtMonto);

        contenido.add(tarjeta, BorderLayout.CENTER);

        //Botones
        btnRegistrar = Estilos.botonPrimario("Confirmar Cobro");
        btnCancelar = Estilos.botonSecundario("Cancelar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnCancelar);
        botones.add(btnRegistrar);

        contenido.add(botones, BorderLayout.SOUTH);
    }

    public JComboBox<String> getCmbClientes() { return cmbClientes; }
    public JLabel getLblSaldoActual() { return lblSaldoActual; }
    public JLabel getLblLimiteCredito() { return lblLimiteCredito; }
    public JComboBox<String> getCmbFacturasPendientes() { return cmbFacturasPendientes; }
    public JComboBox<String> getCmbMedioPago() { return cmbMedioPago; }
    public JTextField getTxtReferencia() { return txtReferencia; }
    public CampoMoneda getTxtMonto() { return txtMonto; }
    public JButton getBtnRegistrar() { return btnRegistrar; }
    public JButton getBtnCancelar() { return btnCancelar; }
}