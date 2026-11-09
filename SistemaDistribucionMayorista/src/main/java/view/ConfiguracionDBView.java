package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import view.estilos.Estilos;

public class ConfiguracionDBView extends JDialog {

    private JTextField txtHost;
    private JTextField txtPuerto;
    private JTextField txtBaseDatos;
    private JTextField txtUsuario;
    private JPasswordField txtClave;

    private JButton btnProbar;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JLabel lblMensaje;

    public ConfiguracionDBView(Frame parent) {
        super(parent, "Configuración de Base de Datos MySQL", true);
        setSize(480, 540);
        setResizable(false);
        setLocationRelativeTo(parent);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(Estilos.FONDO);
        setContentPane(fondo);

        JPanel tarjeta = Estilos.crearTarjeta(null);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new CompoundBorder(new LineBorder(Estilos.BORDE), new EmptyBorder(20, 24, 20, 24)));
        tarjeta.setPreferredSize(new Dimension(430, 480));

        JLabel subtitulo = new JLabel("Parámetros de conexión");
        subtitulo.setFont(Estilos.fuente(Font.PLAIN, 11));
        subtitulo.setForeground(Estilos.TEXTO_SUAVE);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Conexión MySQL");
        titulo.setFont(Estilos.fuente(Font.BOLD, 20));
        titulo.setForeground(Estilos.TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtHost = new JTextField();
        txtPuerto = new JTextField();
        txtBaseDatos = new JTextField();
        txtUsuario = new JTextField();
        txtClave = new JPasswordField();

        Estilos.estilizarCampo(txtHost);
        Estilos.estilizarCampo(txtPuerto);
        Estilos.estilizarCampo(txtBaseDatos);
        Estilos.estilizarCampo(txtUsuario);
        Estilos.estilizarCampo(txtClave);

        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(Estilos.fuente(Font.PLAIN, 11));
        lblMensaje.setForeground(Estilos.TEXTO_SUAVE);
        lblMensaje.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnProbar = Estilos.botonSecundario("Probar Conexión");
        btnGuardar = Estilos.botonPrimario("Guardar");
        btnCancelar = Estilos.botonSecundario("Cancelar");

        // Fila inferior con distribución adecuada: Probar a la izquierda, Cancelar y Guardar a la derecha
        JPanel panelAcciones = new JPanel(new BorderLayout());
        panelAcciones.setOpaque(false);
        panelAcciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelAcciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelDerecha.setOpaque(false);
        panelDerecha.add(btnCancelar);
        panelDerecha.add(btnGuardar);

        panelAcciones.add(btnProbar, BorderLayout.WEST);
        panelAcciones.add(panelDerecha, BorderLayout.EAST);

        tarjeta.add(subtitulo);
        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(14));

        tarjeta.add(crearEtiqueta("Host / Servidor"));
        tarjeta.add(Box.createVerticalStrut(3));
        tarjeta.add(alinear(txtHost));
        tarjeta.add(Box.createVerticalStrut(8));

        tarjeta.add(crearEtiqueta("Puerto"));
        tarjeta.add(Box.createVerticalStrut(3));
        tarjeta.add(alinear(txtPuerto));
        tarjeta.add(Box.createVerticalStrut(8));

        tarjeta.add(crearEtiqueta("Base de Datos"));
        tarjeta.add(Box.createVerticalStrut(3));
        tarjeta.add(alinear(txtBaseDatos));
        tarjeta.add(Box.createVerticalStrut(8));

        tarjeta.add(crearEtiqueta("Usuario MySQL"));
        tarjeta.add(Box.createVerticalStrut(3));
        tarjeta.add(alinear(txtUsuario));
        tarjeta.add(Box.createVerticalStrut(8));

        tarjeta.add(crearEtiqueta("Contraseña MySQL"));
        tarjeta.add(Box.createVerticalStrut(3));
        tarjeta.add(alinear(txtClave));
        tarjeta.add(Box.createVerticalStrut(8));

        tarjeta.add(lblMensaje);
        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(panelAcciones);

        fondo.add(tarjeta);
        getRootPane().setDefaultButton(btnGuardar);
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
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        return c;
    }

    public void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.setText(mensaje);
        lblMensaje.setForeground(esError ? new java.awt.Color(185, 28, 28) : new java.awt.Color(22, 101, 52));
    }

    // Getters y setters de campos
    public String getHost() { return txtHost.getText().trim(); }
    public void setHost(String val) { txtHost.setText(val); }

    public String getPuerto() { return txtPuerto.getText().trim(); }
    public void setPuerto(String val) { txtPuerto.setText(val); }

    public String getBaseDatos() { return txtBaseDatos.getText().trim(); }
    public void setBaseDatos(String val) { txtBaseDatos.setText(val); }

    public String getUsuario() { return txtUsuario.getText().trim(); }
    public void setUsuario(String val) { txtUsuario.setText(val); }

    public char[] getClave() { return txtClave.getPassword(); }
    public void setClave(String val) { txtClave.setText(val); }

    public JButton getBtnProbar() { return btnProbar; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnCancelar() { return btnCancelar; }
}
