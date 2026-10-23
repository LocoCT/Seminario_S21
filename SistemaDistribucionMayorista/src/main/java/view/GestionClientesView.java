
package view;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import view.estilos.*;

public class GestionClientesView extends JFrame {

  //Campos del formulario
  private DefaultTableModel modeloTabla;
  private JTable tablaClientes;
  private JTextField txtBuscar;
  private JTextField txtCuit;
  private JTextField txtRazonSocial;
  private JTextField txtTelefono;
  private JTextField txtEmail;
  private JTextField txtDireccion;
  private CampoMoneda txtLimiteCredito;
  private JComboBox<String> cmbCategoria;

  //Botones
  private JButton btnBuscar;
  private JButton btnNuevo;
  private JButton btnEditar;
  private JButton btnGuardar;
  private JButton btnCancelar;

  //Constructor
  public GestionClientesView() {
    //Título
    setTitle("Gestión de Clientes");
    //Ancho y alto
    setSize(980, 620); 
    //Centra la ventana
    setLocationRelativeTo(null); 
    //Cierra esta ventana sin cerrar toda la aplicacion
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
    inicializarComponentes();
  }

  //Inicializa la estructura general de la ventana
  private void inicializarComponentes() {

    JPanel contenido = new JPanel(new BorderLayout(0, 14));
    contenido.setBackground(Estilos.FONDO);
    contenido.setBorder(new EmptyBorder(18, 20, 18, 20));
    setContentPane(contenido);

    //Agrega título y subtítulo en la parte superior
    contenido.add(crearEncabezado(), BorderLayout.NORTH);

    //Panel central que divide la pantalla en dos columnas
    JPanel centro = new JPanel(new GridBagLayout());
    centro.setOpaque(false);

    // Configura cómo se ubicarán y dimensionarán los paneles dentro del centro
    GridBagConstraints gbc = new GridBagConstraints();
    //Ocupa todo el espacio disponible en ambas direcciones
    gbc.fill = GridBagConstraints.BOTH;
    //Permite que los paneles ocupen el espacio vertical disponible
    gbc.weighty = 1; 

    //Primer columna -  listado y busqueda de clientes
    //Columna 0
    gbc.gridx = 0;
    gbc.weightx = 0.55;
    //Deja separacion a la derecha
    gbc.insets = new Insets(0, 0, 0, 7);
    centro.add(crearPanelListado(), gbc);

    //Segunda columna - formulario con datos del cliente
    //Columna 1
    gbc.gridx = 1; 
    gbc.weightx = 0.45;
    //Deja separacion a la izquierda
    gbc.insets = new Insets(0, 7, 0, 0);
    centro.add(crearPanelDetalle(), gbc);

    //Pone las dos columnas abajo del encabezado
    contenido.add(centro, BorderLayout.CENTER);
  }

  //Crea encabezado con el titulo y subtitulo
  private JPanel crearEncabezado() {

    //BoxLayout ordena los componentes uno abajo del otro
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

  //Panel Izquierda - busqueda + tabla
  private JPanel crearPanelListado() {

    JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 10));

    //Etiqueta de buscador
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

    //Etiquetas de columnas
    String[] columnas = {"CUIT", "Razón Social", "Saldo"};
    modeloTabla = new DefaultTableModel(columnas, 0) {

      //Determina si una celda se puede editar
      @Override
      public boolean isCellEditable(int fila, int col) {
        return false;
      }
    };

    //Creamos tabla de clientes
    tablaClientes = new JTable(modeloTabla);

    //Permite seleccionar un solo cliente
    tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    p.add(busqueda, BorderLayout.NORTH);

    p.add(Estilos.estilizarTabla(tablaClientes), BorderLayout.CENTER);

    return p;
  }

  //Panel derecho - detalle

  private JPanel crearPanelDetalle() {

    JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 12));

    JLabel titulo = new JLabel("Detalle de datos del Cliente");
    titulo.setFont(Estilos.fuente(Font.BOLD, 13));
    titulo.setForeground(Estilos.TEXTO);

    //Inicializamos los campos de detalle
    txtCuit = new JTextField();
    txtRazonSocial = new JTextField();
    txtTelefono = new JTextField();
    txtEmail = new JTextField();
    txtDireccion = new JTextField();
    txtLimiteCredito = new CampoMoneda();

    //ComboBox para categorias de cliente
    cmbCategoria = new JComboBox<>();
    cmbCategoria.setFont(Estilos.fuente(Font.PLAIN, 12));
    cmbCategoria.setBackground(Color.WHITE);

    JPanel formulario = new JPanel(new GridBagLayout());
    formulario.setOpaque(false);

    //Agregamos las etiquetas y su campo en una fila del formulario
    Estilos.agregarCampo(formulario, 0, "CUIT", txtCuit);
    Estilos.agregarCampo(formulario, 1, "Razón Social", txtRazonSocial);
    Estilos.agregarCampo(formulario, 2, "Teléfono", txtTelefono);
    Estilos.agregarCampo(formulario, 3, "Email", txtEmail);
    Estilos.agregarCampo(formulario, 4, "Dirección", txtDireccion);
    Estilos.agregarCampo(formulario, 5, "Límite de Crédito", txtLimiteCredito);
    Estilos.agregarCampo(formulario, 6, "Categoría", cmbCategoria);

    //Creamos los botones
    btnNuevo = Estilos.botonSecundario("Nuevo");
    btnEditar = Estilos.botonSecundario("Editar");
    btnGuardar = Estilos.botonPrimario("Guardar");
    btnCancelar = Estilos.botonSecundario("Cancelar");

    //Ordenamos los botones en una fila alineados a la derecha
    JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    botones.setOpaque(false);
    botones.add(btnNuevo);
    botones.add(btnEditar);
    botones.add(btnCancelar);
    botones.add(btnGuardar);

    //Agregamos titulos
    JPanel arriba = new JPanel(new BorderLayout(0, 12));
    arriba.setOpaque(false);
    arriba.add(titulo, BorderLayout.NORTH);
    arriba.add(formulario, BorderLayout.CENTER);

    //Ordenamos formulario y botones
    p.add(arriba, BorderLayout.NORTH);
    p.add(botones, BorderLayout.SOUTH);

    //Seteamos estado inicial de campos
    habilitarEdicion(false);
    btnEditar.setEnabled(false);

    return p;
  }

  //Activa o desactiva el modo editar del formulario
  public void habilitarEdicion(boolean editando) {

    //El CUIT no se puede modificar
    Estilos.habilitar(txtCuit, false);

    //Habilita los demas campos
    Estilos.habilitar(txtRazonSocial, editando);
    Estilos.habilitar(txtTelefono, editando);
    Estilos.habilitar(txtEmail, editando);
    Estilos.habilitar(txtDireccion, editando);
    Estilos.habilitar(txtLimiteCredito, editando);
    Estilos.habilitar(cmbCategoria, editando);

    //Los botones Guardar y Cancelar solo se habilitan durante modo editar
    btnGuardar.setEnabled(editando);
    btnCancelar.setEnabled(editando);

    //Los botones Nuevo y Editar solo se habilita cuando no se esta editando
    btnNuevo.setEnabled(!editando);
    btnEditar.setEnabled(!editando);

    //Lo mismo para buscar y tabla de clientes - Se habilitan cuando no se edita
    txtBuscar.setEnabled(!editando);
    btnBuscar.setEnabled(!editando);
    tablaClientes.setEnabled(!editando);

  }

  //Habilita o deshabilita el botón Editar si hay cliente seleccionado
  public void habilitarEditar(boolean habilitado) {
    btnEditar.setEnabled(habilitado);
  }

  //Resalta el campo de email cuando es invalido
  public void marcarEmailInvalido(boolean invalido) {
    Estilos.marcarError(txtEmail, invalido);
    // Si el email es inválido, muestra una explicación al pasar el cursor
    // Si es válido, elimina el mensaje emergente
    txtEmail.setToolTipText(
        invalido ? "Email inválido (ejemplo: nombre@empresa.com)" : null
    );
  }


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