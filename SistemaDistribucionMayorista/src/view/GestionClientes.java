package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import view.estilos.*;

public class GestionClientes extends JFrame {

  private JTextField txtBuscar;
  private JButton btnBuscar;
  private DefaultTableModel modeloTabla;
  private JTable tablaClientes;

  private JTextField txtCuit;
  private JTextField txtRazonSocial;
  private JTextField txtTelefono;
  private JTextField txtEmail;
  private JTextField txtDireccion;
  private CampoMoneda txtLimiteCredito;
  private JComboBox<String> cmbCategoria;

  private JButton btnNuevo;
  private JButton btnEditar;
  private JButton btnGuardar;
  private JButton btnCancelar;

  public GestionClientes() {
    setTitle("Gestión de Clientes");
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

    JLabel titulo = new JLabel("Gestión de Clientes");
    titulo.setFont(Estilos.fuente(Font.BOLD, 22));
    titulo.setForeground(Estilos.TEXTO);

    JLabel sub = new JLabel("Consulta, alta y modificación de clientes");
    sub.setFont(Estilos.fuente(Font.PLAIN, 12));
    sub.setForeground(Estilos.TEXTO_SUAVE);

    p.add(titulo);
    p.add(sub);
    return p;
  }

  // ---------- Izquierda: búsqueda + tabla ----------
  private JPanel crearPanelListado() {
    JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 10));

    JLabel lblBuscar = new JLabel("CUIT o Razón Social");
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

    String[] columnas = {"CUIT", "Razón Social", "Saldo"};
    modeloTabla = new DefaultTableModel(columnas, 0) {
      @Override
      public boolean isCellEditable(int fila, int col) {
        return false;
      }
    };
    tablaClientes = new JTable(modeloTabla);
    tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    p.add(busqueda, BorderLayout.NORTH);
    p.add(Estilos.estilizarTabla(tablaClientes), BorderLayout.CENTER);
    return p;
  }

  // ---------- Derecha: formulario de detalle ----------
  private JPanel crearPanelDetalle() {
    JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 12));

    JLabel titulo = new JLabel("Detalle de datos del Cliente");
    titulo.setFont(Estilos.fuente(Font.BOLD, 13));
    titulo.setForeground(Estilos.TEXTO);

    txtCuit = new JTextField();
    txtRazonSocial = new JTextField();
    txtTelefono = new JTextField();
    txtEmail = new JTextField();
    txtDireccion = new JTextField();
    txtLimiteCredito = new CampoMoneda();
    cmbCategoria = new JComboBox<>(new String[]{"Minorista", "Mayorista", "Corporativo"});
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

    // Estado inicial: todo el detalle bloqueado y sin cliente seleccionado
    habilitarEdicion(false);
    btnEditar.setEnabled(false);
    return p;
  }

  // ---------- Estados de la pantalla ----------

  // true = modo edición (campos habilitados); false = solo lectura (en gris)
  public void habilitarEdicion(boolean editando) {
    Estilos.habilitar(txtCuit, false);   // el CUIT identifica al cliente: nunca se edita
    Estilos.habilitar(txtRazonSocial, editando);
    Estilos.habilitar(txtTelefono, editando);
    Estilos.habilitar(txtEmail, editando);
    Estilos.habilitar(txtDireccion, editando);
    Estilos.habilitar(txtLimiteCredito, editando);
    Estilos.habilitar(cmbCategoria, editando);

    btnGuardar.setEnabled(editando);
    btnCancelar.setEnabled(editando);
    btnNuevo.setEnabled(!editando);
    if (editando) {
      btnEditar.setEnabled(false);
    }

    // Mientras se edita no se puede cambiar de cliente ni buscar
    txtBuscar.setEnabled(!editando);
    btnBuscar.setEnabled(!editando);
    tablaClientes.setEnabled(!editando);
  }

  public void habilitarEditar(boolean habilitado) {
    btnEditar.setEnabled(habilitado);
  }

  // Borde rojo y tooltip cuando el email ingresado no es válido
  public void marcarEmailInvalido(boolean invalido) {
    Estilos.marcarError(txtEmail, invalido);
    txtEmail.setToolTipText(invalido ? "Email inválido (ejemplo: nombre@empresa.com)" : null);
  }

  // ---------- Getters para el controller ----------
  public JTextField getTxtBuscar() { return txtBuscar; }
  public JButton getBtnBuscar() { return btnBuscar; }
  public DefaultTableModel getModeloTabla() { return modeloTabla; }
  public JTable getTablaClientes() { return tablaClientes; }
  public JTextField getTxtCuit() { return txtCuit; }
  public JTextField getTxtRazonSocial() { return txtRazonSocial; }
  public JTextField getTxtTelefono() { return txtTelefono; }
  public JTextField getTxtEmail() { return txtEmail; }
  public JTextField getTxtDireccion() { return txtDireccion; }
  public CampoMoneda getTxtLimiteCredito() { return txtLimiteCredito; }
  public JComboBox<String> getCmbCategoria() { return cmbCategoria; }
  public JButton getBtnNuevo() { return btnNuevo; }
  public JButton getBtnEditar() { return btnEditar; }
  public JButton getBtnGuardar() { return btnGuardar; }
  public JButton getBtnCancelar() { return btnCancelar; }
}