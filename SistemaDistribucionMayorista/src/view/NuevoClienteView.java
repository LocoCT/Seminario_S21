package view;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import view.estilos.*;

public class NuevoClienteView extends JDialog {

  private JTextField txtCuit;
  private JTextField txtRazonSocial;
  private JTextField txtTelefono;
  private JTextField txtEmail;
  private JTextField txtDireccion;
  private CampoMoneda txtLimiteCredito;
  private JComboBox<String> cmbCategoria;
  private JButton btnGuardar;
  private JButton btnCancelar;
  private JLabel lblError;

  public NuevoClienteView(Frame padre) {
    super(padre, "Nuevo cliente", true);
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
    tarjeta.setPreferredSize(new Dimension(400, 470));

    JLabel sub = new JLabel("Alta de cliente");
    sub.setFont(Estilos.fuente(Font.PLAIN, 11));
    sub.setForeground(Estilos.TEXTO_SUAVE);
    JLabel titulo = new JLabel("Nuevo cliente");
    titulo.setFont(Estilos.fuente(Font.BOLD, 20));
    titulo.setForeground(Estilos.TEXTO);

    JPanel encabezado = new JPanel();
    encabezado.setLayout(new BoxLayout(encabezado, BoxLayout.Y_AXIS));
    encabezado.setOpaque(false);
    encabezado.add(sub);
    encabezado.add(titulo);

    txtCuit = new JTextField();
    txtRazonSocial = new JTextField();
    txtTelefono = new JTextField();
    txtEmail = new JTextField();
    txtDireccion = new JTextField();
    txtLimiteCredito = new CampoMoneda();
    cmbCategoria = new JComboBox<>();
    cmbCategoria.setFont(Estilos.fuente(Font.PLAIN, 12));
    cmbCategoria.setBackground(Color.WHITE);

    JPanel formulario = new JPanel(new GridBagLayout());
    formulario.setOpaque(false);
    Estilos.agregarCampo(formulario, 0, "CUIT", txtCuit);
    Estilos.agregarCampo(formulario, 1, "Razón Social", txtRazonSocial);
    Estilos.agregarCampo(formulario, 2, "Teléfono", txtTelefono);
    Estilos.agregarCampo(formulario, 3, "Email", txtEmail);
    Estilos.agregarCampo(formulario, 4, "Dirección", txtDireccion);
    Estilos.agregarCampo(formulario, 5, "Límite de Crédito", txtLimiteCredito);
    Estilos.agregarCampo(formulario, 6, "Categoría", cmbCategoria);

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

  public void marcarEmailInvalido(boolean invalido) {
    Estilos.marcarError(txtEmail, invalido);

    txtEmail.setToolTipText(
        invalido ? "Email inválido (ejemplo: nombre@empresa.com)" : null);
  }

  public JTextField getTxtCuit() { return txtCuit; }
  public JTextField getTxtRazonSocial() { return txtRazonSocial; }
  public JTextField getTxtTelefono() { return txtTelefono; }
  public JTextField getTxtEmail() { return txtEmail; }
  public JTextField getTxtDireccion() { return txtDireccion; }
  public CampoMoneda getTxtLimiteCredito() { return txtLimiteCredito; }
  public JComboBox<String> getCmbCategoria() { return cmbCategoria; }
  public JButton getBtnGuardar() { return btnGuardar; }
  public JButton getBtnCancelar() { return btnCancelar; }
}