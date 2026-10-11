package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import view.estilos.*;
import view.estilos.Estilos.BotonRedondeado;

public class MenuPrincipal extends JFrame {

  private JPanel contentPane;
  private JButton btnInicio;
  private JButton btnClientes;
  private JButton btnProductos;
  private JButton btnVentas;
  private JButton btnFacturacion;
  private JButton btnStock;
  private JButton btnCerrarSesion;
  private JButton btnNuevaOrden;
  private JButton btnNuevoCliente;
  private JButton btnMovStock;

  // Textos que dependen del usuario logueado
  private JLabel lblSaludo;
  private JLabel lblRol;
  private JLabel lblUsuarioBarra;

  public MenuPrincipal() {
    configurarVentana();
    inicializarComponentes();
  }

  private void configurarVentana() {
    setTitle("Dashboard - Sistema de Gestión Mayorista");
    setSize(1024, 768);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    contentPane = new JPanel(new BorderLayout());
    setContentPane(contentPane);
  }

  private void inicializarComponentes() {
    contentPane.add(crearMenuLateral(), BorderLayout.WEST);
    contentPane.add(crearContenido(), BorderLayout.CENTER);
    contentPane.add(crearBarraEstado(), BorderLayout.SOUTH);
  }

  // Lo llama quien tenga el usuario logueado (login / MainApp)
  public void setUsuario(String username, String rol) {
    lblSaludo.setText("Bienvenido/a, " + username);
    lblRol.setText("Rol: " + rol);
    lblUsuarioBarra.setText("Usuario: " + username);
  }

  private String fechaActual() {
    DateTimeFormatter f = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "AR"));
    String texto = LocalDate.now().format(f);
    return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
  }

  // Barra de estado 
  private JPanel crearBarraEstado() {
    JPanel barra = new JPanel(new BorderLayout());
    barra.setBackground(Estilos.FONDO_LATERAL);
    barra.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(1, 0, 0, 0, Estilos.BORDE),
        new EmptyBorder(4, 12, 4, 12)));

    JLabel izq = new JLabel("Listo");
    izq.setFont(Estilos.fuente(Font.PLAIN, 11));

    lblUsuarioBarra = new JLabel("Usuario: -");
    lblUsuarioBarra.setFont(Estilos.fuente(Font.PLAIN, 11));

    barra.add(izq, BorderLayout.WEST);
    barra.add(lblUsuarioBarra, BorderLayout.EAST);
    return barra;
  }

  // Panel lateral izquierdo
  private JPanel crearMenuLateral() {
    JPanel lateral = new JPanel();
    lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
    lateral.setBackground(Estilos.FONDO_LATERAL);
    lateral.setPreferredSize(new Dimension(190, 0));
    lateral.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 0, 1, Estilos.BORDE),
        new EmptyBorder(14, 8, 14, 8)));

    JLabel sub = new JLabel("Sistema de Gestión Mayorista");
    sub.setFont(Estilos.fuente(Font.PLAIN, 10));
    sub.setForeground(Estilos.TEXTO_SUAVE);
    sub.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel titulo = new JLabel("Panel principal");
    titulo.setFont(Estilos.fuente(Font.BOLD, 16));
    titulo.setForeground(Estilos.TEXTO);
    titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

    lateral.add(sub);
    lateral.add(titulo);
    lateral.add(Box.createVerticalStrut(20));

    btnInicio = crearBotonMenu("Inicio", true);
    btnClientes = crearBotonMenu("Clientes", false);
    btnProductos = crearBotonMenu("Productos", false);
    btnVentas = crearBotonMenu("Órdenes de Venta", false);
    btnFacturacion = crearBotonMenu("Facturación y Cobros", false);
    btnStock = crearBotonMenu("Depósito y Stock", false);
    btnCerrarSesion = crearBotonMenu("Cerrar sesión", false);
    btnCerrarSesion.setForeground(new Color(185, 28, 28));

    JButton[] botones = {btnInicio, btnClientes, btnProductos, btnVentas, btnFacturacion, btnStock};
    for (JButton b : botones) {
      lateral.add(b);
      lateral.add(Box.createVerticalStrut(4));
    }
    lateral.add(Box.createVerticalStrut(16));
    lateral.add(btnCerrarSesion);
    lateral.add(Box.createVerticalGlue());
    return lateral;
  }

  private JButton crearBotonMenu(String texto, boolean activo) {
    BotonRedondeado b = new BotonRedondeado(texto,
        activo ? Estilos.AZUL : null,
        activo ? Estilos.AZUL.darker() : new Color(190, 201, 216));
    b.setForeground(activo ? Color.WHITE : Estilos.TEXTO);
    b.setFont(Estilos.fuente(Font.BOLD, 12));
    b.setHorizontalAlignment(SwingConstants.LEFT);
    b.setAlignmentX(Component.LEFT_ALIGNMENT);
    b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
    b.setBorder(new EmptyBorder(6, 14, 6, 10));
    return b;
  }

  // Contenido central 
  private JPanel crearContenido() {
    JPanel contenido = new JPanel(new BorderLayout(0, 14));
    contenido.setBackground(Estilos.FONDO);
    contenido.setBorder(new EmptyBorder(18, 20, 18, 20));

    JPanel superior = new JPanel();
    superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
    superior.setOpaque(false);
    superior.add(crearEncabezado());
    superior.add(Box.createVerticalStrut(14));
    superior.add(crearTarjetas());
    superior.add(Box.createVerticalStrut(14));
    superior.add(crearAccionesRapidas());

    contenido.add(superior, BorderLayout.NORTH);
    contenido.add(crearUltimasOrdenes(), BorderLayout.CENTER);
    return contenido;
  }

  private JPanel crearEncabezado() {
    JPanel p = new JPanel(new BorderLayout());
    p.setOpaque(false);

    JPanel izq = new JPanel();
    izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
    izq.setOpaque(false);

    JLabel fecha = new JLabel(fechaActual());
    fecha.setFont(Estilos.fuente(Font.PLAIN, 12));
    fecha.setForeground(Estilos.TEXTO_SUAVE);

    lblSaludo = new JLabel("Bienvenido/a");
    lblSaludo.setFont(Estilos.fuente(Font.BOLD, 24));
    lblSaludo.setForeground(Estilos.TEXTO);

    lblRol = new JLabel("Rol: -");
    lblRol.setFont(Estilos.fuente(Font.PLAIN, 12));
    lblRol.setForeground(Estilos.TEXTO_SUAVE);

    izq.add(fecha);
    izq.add(lblSaludo);
    izq.add(lblRol);

    JLabel estado = new JLabel("Estado: Conectado");
    estado.setFont(Estilos.fuente(Font.BOLD, 12));
    estado.setForeground(new Color(21, 128, 61));
    estado.setOpaque(true);
    estado.setBackground(Estilos.TARJETA);
    estado.setBorder(new CompoundBorder(new LineBorder(Estilos.BORDE), new EmptyBorder(6, 10, 6, 10)));

    JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    der.setOpaque(false);
    der.add(estado);

    p.add(izq, BorderLayout.WEST);
    p.add(der, BorderLayout.EAST);
    return p;
  }

  private JPanel crearTarjetas() {
    JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
    fila.setOpaque(false);
    fila.add(crearTarjetaResumen("Órdenes pendientes", "12", "Por facturar"));
    fila.add(crearTarjetaResumen("Cobros del día", "$ 486.200", "+8,4% vs. ayer"));
    fila.add(crearTarjetaResumen("Stock mínimo", "7", "Productos a revisar"));
    fila.add(crearTarjetaResumen("Clientes deudores", "18", "Requieren seguimiento"));
    fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
    return fila;
  }

  private JPanel crearTarjetaResumen(String titulo, String valor, String detalle) {
    JPanel t = Estilos.crearTarjeta(null);
    t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));

    JLabel lblTitulo = new JLabel(titulo);
    lblTitulo.setFont(Estilos.fuente(Font.PLAIN, 12));
    lblTitulo.setForeground(Estilos.TEXTO);

    JLabel lblValor = new JLabel(valor);
    lblValor.setFont(Estilos.fuente(Font.BOLD, 22));
    lblValor.setForeground(Estilos.AZUL);

    JLabel lblDetalle = new JLabel(detalle);
    lblDetalle.setFont(Estilos.fuente(Font.PLAIN, 10));
    lblDetalle.setForeground(Estilos.TEXTO_SUAVE);

    t.add(lblTitulo);
    t.add(lblValor);
    t.add(lblDetalle);
    return t;
  }

  private JPanel crearAccionesRapidas() {
    JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 10));

    JLabel titulo = new JLabel("Acciones rápidas");
    titulo.setFont(Estilos.fuente(Font.BOLD, 13));
    titulo.setForeground(Estilos.TEXTO);

    btnNuevaOrden = Estilos.botonSecundario("Nueva Orden de Venta");
    btnNuevoCliente = Estilos.botonSecundario("Registrar Nuevo Cliente");
    btnMovStock = Estilos.botonSecundario("Registrar Movimiento de Stock");

    JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    botones.setOpaque(false);
    botones.add(btnNuevaOrden);
    botones.add(btnNuevoCliente);
    botones.add(btnMovStock);

    p.add(titulo, BorderLayout.NORTH);
    p.add(botones, BorderLayout.CENTER);
    p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
    return p;
  }

  private JPanel crearUltimasOrdenes() {
    JPanel p = Estilos.crearTarjeta(new BorderLayout(0, 10));

    JLabel titulo = new JLabel("Últimas órdenes de venta");
    titulo.setFont(Estilos.fuente(Font.BOLD, 13));
    titulo.setForeground(Estilos.TEXTO);

    JLabel verTodas = new JLabel("Ver todas");
    verTodas.setFont(Estilos.fuente(Font.BOLD, 11));
    verTodas.setForeground(Estilos.AZUL);
    verTodas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    JPanel cabecera = new JPanel(new BorderLayout());
    cabecera.setOpaque(false);
    cabecera.add(titulo, BorderLayout.WEST);
    cabecera.add(verTodas, BorderLayout.EAST);

    String[] columnas = {"Número", "Cliente", "Fecha", "Monto", "Estado"};
    Object[][] datos = {
      {"OV-000184", "Distribuidora Norte S.A.", "23/09/2026", "$ 184.500", "Pendiente"},
      {"OV-000183", "Almacén El Sol", "23/09/2026", "$ 96.200", "Facturada"},
      {"OV-000182", "Comercial Belgrano", "22/09/2026", "$ 241.800", "Pendiente"},
      {"OV-000181", "Mercado Central", "22/09/2026", "$ 72.450", "Entregada"},
      {"OV-000180", "Autoservicio La Plaza", "21/09/2026", "$ 132.900", "Facturada"}
    };

    JTable tabla = new JTable(new DefaultTableModel(datos, columnas) {
      @Override
      public boolean isCellEditable(int fila, int col) {
        return false;
      }
    });
    JScrollPane scroll = Estilos.estilizarTabla(tabla);

    DefaultTableCellRenderer numero = new DefaultTableCellRenderer();
    numero.setForeground(Estilos.AZUL);
    numero.setFont(Estilos.fuente(Font.BOLD, 11));
    tabla.getColumnModel().getColumn(0).setCellRenderer(numero);
    tabla.getColumnModel().getColumn(4).setCellRenderer(new BadgeRenderer());

    p.add(cabecera, BorderLayout.NORTH);
    p.add(scroll, BorderLayout.CENTER);
    return p;
  }

  // Muestra el estado de la orden con etiqueta de color
  private static class BadgeRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                   boolean foc, int fila, int col) {
      String estado = String.valueOf(v);
      Color fondo;
      Color texto;
      switch (estado) {
        case "Pendiente":
          fondo = new Color(254, 249, 195);
          texto = new Color(161, 98, 7);
          break;
        case "Entregada":
          fondo = new Color(220, 252, 231);
          texto = new Color(21, 128, 61);
          break;
        default:
          fondo = new Color(219, 234, 254);
          texto = new Color(29, 78, 216);
      }
      JLabel badge = new JLabel(estado);
      badge.setOpaque(true);
      badge.setBackground(fondo);
      badge.setForeground(texto);
      badge.setFont(Estilos.fuente(Font.PLAIN, 10));
      badge.setBorder(new CompoundBorder(new LineBorder(texto), new EmptyBorder(1, 6, 1, 6)));

      JPanel celda = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
      celda.setBackground(Color.WHITE);
      celda.add(badge);
      return celda;
    }
  }

  //  Getters para el controller 
  public JButton getBtnInicio() { return btnInicio; }
  public JButton getBtnClientes() { return btnClientes; }
  public JButton getBtnProductos() { return btnProductos; }
  public JButton getBtnVentas() { return btnVentas; }
  public JButton getBtnFacturacion() { return btnFacturacion; }
  public JButton getBtnStock() { return btnStock; }
  public JButton getBtnCerrarSesion() { return btnCerrarSesion; }
  public JButton getBtnNuevaOrden() { return btnNuevaOrden; }
  public JButton getBtnNuevoCliente() { return btnNuevoCliente; }
  public JButton getBtnMovStock() { return btnMovStock; }
}