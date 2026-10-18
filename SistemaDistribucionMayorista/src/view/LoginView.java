package view;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import view.estilos.*;

public class LoginView extends JFrame {

  private JTextField txtUsuario;
  private JPasswordField txtClave;
  private JButton btnIngresar;
  private JButton btnSalir;
  private JLabel lblError;

  public LoginView() {
    setTitle("Iniciar sesión - Sistema de Gestión Mayorista");
    setSize(420, 460);
    setResizable(false);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    inicializarComponentes();

    //Credenciales precargadas.
    txtUsuario.setText("Admin");
    txtClave.setText("admin123");
  }

  private void inicializarComponentes() {
    JPanel fondo = new JPanel(new GridBagLayout());
    fondo.setBackground(Estilos.FONDO);
    setContentPane(fondo);

    JPanel tarjeta = Estilos.crearTarjeta(null);
    tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
    tarjeta.setBorder(new CompoundBorder(new LineBorder(Estilos.BORDE), new EmptyBorder(26, 28, 26, 28)));
    tarjeta.setPreferredSize(new Dimension(340, 380));

    JLabel sub = new JLabel("Sistema de Gestión Mayorista");
    sub.setFont(Estilos.fuente(Font.PLAIN, 11));
    sub.setForeground(Estilos.TEXTO_SUAVE);
    sub.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel titulo = new JLabel("Iniciar sesión");
    titulo.setFont(Estilos.fuente(Font.BOLD, 24));
    titulo.setForeground(Estilos.TEXTO);
    titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

    txtUsuario = new JTextField();
    txtClave = new JPasswordField();
    Estilos.estilizarCampo(txtUsuario);
    Estilos.estilizarCampo(txtClave);

    lblError = new JLabel(" ");
    lblError.setFont(Estilos.fuente(Font.PLAIN, 11));
    lblError.setForeground(new Color(185, 28, 28));
    lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

    btnIngresar = Estilos.botonPrimario("Ingresar");
    btnSalir = Estilos.botonSecundario("Salir");

    JPanel botones = new JPanel(new GridLayout(1, 2, 10, 0));
    botones.setOpaque(false);
    botones.add(btnSalir);
    botones.add(btnIngresar);
    botones.setAlignmentX(Component.LEFT_ALIGNMENT);
    botones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

    tarjeta.add(sub);
    tarjeta.add(titulo);
    tarjeta.add(Box.createVerticalStrut(24));
    tarjeta.add(crearEtiqueta("Usuario"));
    tarjeta.add(Box.createVerticalStrut(4));
    tarjeta.add(alinear(txtUsuario));
    tarjeta.add(Box.createVerticalStrut(14));
    tarjeta.add(crearEtiqueta("Contraseña"));
    tarjeta.add(Box.createVerticalStrut(4));
    tarjeta.add(alinear(txtClave));
    tarjeta.add(Box.createVerticalStrut(10));
    tarjeta.add(lblError);
    tarjeta.add(Box.createVerticalGlue());
    tarjeta.add(botones);

    fondo.add(tarjeta);

    // Enter en cualquier campo equivale a presionar "Ingresar"
    getRootPane().setDefaultButton(btnIngresar);
  }

  private JLabel crearEtiqueta(String texto) {
    JLabel l = new JLabel(texto);
    l.setFont(Estilos.fuente(Font.BOLD, 12));
    l.setForeground(Estilos.TEXTO);
    l.setAlignmentX(Component.LEFT_ALIGNMENT);
    return l;
  }

  private JComponent alinear(JComponent c) {
    c.setAlignmentX(Component.LEFT_ALIGNMENT);
    c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
    return c;
  }

  public String getUsername() {
    return txtUsuario.getText().trim();
  }

  public char[] getClave() {
    return txtClave.getPassword();
  }

  public void mostrarError(String mensaje) {
    lblError.setText(mensaje);
  }

  public void limpiarClave() {
    txtClave.setText("");
    txtClave.requestFocusInWindow();
  }

  public JButton getBtnIngresar() { return btnIngresar; }
  public JButton getBtnSalir() { return btnSalir; }
}