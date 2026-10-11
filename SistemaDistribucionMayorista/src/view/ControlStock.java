package view;
import javax.swing.*;
import java.awt.*;

public class ControlStock extends JFrame {

    public ControlStock() {
        setTitle("Control de stock");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new JMenu("Archivo"));
        menuBar.add(new JMenu("Movimientos"));
        menuBar.add(new JMenu("Ayuda"));
        setJMenuBar(menuBar);

        // Panel Principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 15));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Cabecera
        JPanel panelCabecera = new JPanel(new GridLayout(2, 1, 0, 5));
        JLabel lblTitulo = new JLabel("Control de Stock");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel lblSubtitulo = new JLabel("Registrá los movimientos físicos de mercadería.");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitulo.setForeground(Color.DARK_GRAY);
        panelCabecera.add(lblTitulo);
        panelCabecera.add(lblSubtitulo);

        // Formulario de Datos del Movimiento
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Datos del movimiento"));
        panelFormulario.setBackground(new Color(245, 247, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 1
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panelFormulario.add(new JLabel("Producto"), gbc);
        gbc.gridy = 1;
        // Simulamos informacion
        JComboBox<String> cmbProducto = new JComboBox<>(new String[]{
            "BEB-001 · Agua mineral 500ml", 
            "PROD-002 · Puré de Tomate"
        });
        panelFormulario.add(cmbProducto, gbc);

        // Fila 2
        gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0.5;
        panelFormulario.add(new JLabel("Cantidad"), gbc);
        
        gbc.gridx = 1; 
        panelFormulario.add(new JLabel("Tipo de movimiento"), gbc);

        gbc.gridx = 0; gbc.gridy = 3; 
        JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(24, 1, 10000, 1));
        panelFormulario.add(spnCantidad, gbc);

        gbc.gridx = 1; 
        JComboBox<String> cmbTipoMovimiento = new JComboBox<>(new String[]{
            "Ingreso por compra", 
            "Egreso por venta", 
            "Ajuste manual"
        });
        panelFormulario.add(cmbTipoMovimiento, gbc);

        // Fila 3
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panelFormulario.add(new JLabel("Depósito de destino"), gbc);
        gbc.gridy = 5;
        JComboBox<String> cmbDeposito = new JComboBox<>(new String[]{"Depósito Central", "Depósito Sur"});
        panelFormulario.add(cmbDeposito, gbc);

        // Fila 4
        gbc.gridx = 0; gbc.gridy = 6;
        panelFormulario.add(new JLabel("Motivo del movimiento"), gbc);
        gbc.gridy = 7;
        JTextArea txtMotivo = new JTextArea("Reposición de stock semanal", 4, 20);
        txtMotivo.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panelFormulario.add(new JScrollPane(txtMotivo), gbc);

        // Alineamos en el centro
        JPanel panelCentro = new JPanel(new BorderLayout(0, 15));
        panelCentro.add(panelCabecera, BorderLayout.NORTH);
        panelCentro.add(panelFormulario, BorderLayout.CENTER);
        panelPrincipal.add(panelCentro, BorderLayout.CENTER);

        // Panel Inferior (Estado y Boton de Accion)
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel lblEstadoIzquierda = new JLabel("Listo");
        lblEstadoIzquierda.setForeground(Color.GRAY);
        
        JButton btnRegistrar = new JButton("Registrar Movimiento");
        btnRegistrar.setBackground(new Color(28, 78, 216));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setPreferredSize(new Dimension(200, 40));

        panelInferior.add(lblEstadoIzquierda, BorderLayout.WEST);
        panelInferior.add(btnRegistrar, BorderLayout.EAST);

        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal);
    }
}