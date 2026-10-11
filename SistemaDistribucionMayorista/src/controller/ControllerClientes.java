package controller;

import model.Cliente;
import repository.ClienteRepositorio;
import view.GestionClientes;
import view.NuevoCliente;
import view.estilos.CampoMoneda;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ControllerClientes implements ActionListener {

    private final GestionClientes vista;
    private final ClienteRepositorio repo = new ClienteRepositorio();
    private List<Cliente> clientesMostrados = new ArrayList<>();
    private Cliente seleccionado;

    public ControllerClientes(GestionClientes vista) {
        this.vista = vista;
        registrarListeners();
        cargarTabla(repo.listar());
    }

    private void registrarListeners() {
        vista.getBtnBuscar().addActionListener(this);
        vista.getTxtBuscar().addActionListener(this);
        vista.getBtnNuevo().addActionListener(this);
        vista.getBtnEditar().addActionListener(this);
        vista.getBtnGuardar().addActionListener(this);
        vista.getBtnCancelar().addActionListener(this);
        vista.getTablaClientes().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                alSeleccionarFila();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vista.getBtnBuscar() || origen == vista.getTxtBuscar()) {
            cargarTabla(repo.buscar(vista.getTxtBuscar().getText()));
        } else if (origen == vista.getBtnNuevo()) {
            abrirNuevoCliente();
        } else if (origen == vista.getBtnEditar()) {
            if (seleccionado != null) {
                vista.habilitarEdicion(true);
            }
        } else if (origen == vista.getBtnGuardar()) {
            guardarEdicion();
        } else if (origen == vista.getBtnCancelar()) {
            cancelarEdicion();
        }
    }

    // Tabla y detalle 
    private void cargarTabla(List<Cliente> lista) {
        vista.getModeloTabla().setRowCount(0);
        clientesMostrados = lista;
        for (Cliente c : lista) {
            vista.getModeloTabla().addRow(new Object[]{
                c.getCUIT(), c.getRazonSocial(), "$ " + CampoMoneda.formatear(c.getSaldo())
            });
        }
        seleccionado = null;
        limpiarDetalle();
        vista.habilitarEditar(false);
    }

    private void alSeleccionarFila() {
        int fila = vista.getTablaClientes().getSelectedRow();
        if (fila < 0 || fila >= clientesMostrados.size()) {
            seleccionado = null;
            limpiarDetalle();
            vista.habilitarEditar(false);
            return;
        }
        seleccionado = clientesMostrados.get(fila);
        mostrarDetalle(seleccionado);
        vista.habilitarEditar(true);
    }

    private void mostrarDetalle(Cliente c) {
        vista.getTxtCuit().setText(c.getCUIT());
        vista.getTxtRazonSocial().setText(c.getRazonSocial());
        vista.getTxtTelefono().setText(c.getTelefono());
        vista.getTxtEmail().setText(c.getEmail());
        vista.getTxtDireccion().setText(c.getDireccion());
        vista.getTxtLimiteCredito().setMonto(c.getLimiteCredito());
        vista.getCmbCategoria().setSelectedItem(c.getCategoria());
    }

    private void limpiarDetalle() {
        vista.getTxtCuit().setText("");
        vista.getTxtRazonSocial().setText("");
        vista.getTxtTelefono().setText("");
        vista.getTxtEmail().setText("");
        vista.getTxtDireccion().setText("");
        vista.getTxtLimiteCredito().setMonto(null);
        vista.getCmbCategoria().setSelectedIndex(0);
    }

    private void volverAModoLectura() {
        vista.habilitarEdicion(false);
        vista.habilitarEditar(seleccionado != null);
    }

    // Editar 
    private void guardarEdicion() {
        String razon = vista.getTxtRazonSocial().getText().trim();
        String telefono = vista.getTxtTelefono().getText().trim();
        String email = vista.getTxtEmail().getText().trim();
        String direccion = vista.getTxtDireccion().getText().trim();
        Double limite = vista.getTxtLimiteCredito().getMonto();
        String categoria = (String) vista.getCmbCategoria().getSelectedItem();

        String error = validar(razon, email, limite);
        if (error != null) {
            JOptionPane.showMessageDialog(vista, error, "Datos inválidos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        seleccionado.setRazonSocial(razon);
        seleccionado.setTelefono(telefono); 
        seleccionado.setEmail(email);       
        seleccionado.setDireccion(direccion);
        seleccionado.setCategoria(categoria);
        seleccionado.setLimiteCredito(limite);
        repo.actualizar(seleccionado);

        int fila = vista.getTablaClientes().getSelectedRow();
        vista.getModeloTabla().setValueAt(razon, fila, 1);
        volverAModoLectura();
    }

    private void cancelarEdicion() {
        if (seleccionado != null) {
            mostrarDetalle(seleccionado); 
        }
        volverAModoLectura();
    }

    // Nuevo cliente 
    public void abrirNuevoCliente() {
        NuevoCliente dialogo = new NuevoCliente(vista);
        dialogo.getBtnCancelar().addActionListener(ev -> dialogo.dispose());
        dialogo.getBtnGuardar().addActionListener(ev -> guardarNuevo(dialogo));
        dialogo.setVisible(true);
    }

    private void guardarNuevo(NuevoCliente d) {
        String cuit = d.getTxtCuit().getText().replaceAll("\\D", "");
        String razon = d.getTxtRazonSocial().getText().trim();
        String telefono = d.getTxtTelefono().getText().trim();
        String email = d.getTxtEmail().getText().trim();
        String direccion = d.getTxtDireccion().getText().trim();
        String categoria = (String) d.getCmbCategoria().getSelectedItem();
        Double limite = d.getTxtLimiteCredito().getMonto();

        String error;
        if (cuit.length() != 11) {
            error = "El CUIT debe tener 11 dígitos.";
        } else if (repo.existeCuit(cuit)) {
            error = "Ya existe un cliente con ese CUIT.";
        } else {
            error = validar(razon, email, limite);
        }
        if (error != null) {
            d.mostrarError(error);
            return;
        }

        String cuitFormateado = cuit.substring(0, 2) + "-" + cuit.substring(2, 10) + "-" + cuit.substring(10);
        // orden: telefono, email, CUIT, razonSocial, direccion, categoria, limiteCredito, saldo
        Cliente nuevo = new Cliente(telefono, email, cuitFormateado, razon, direccion, categoria, limite, 0.0);
        repo.agregar(nuevo);

        vista.getTxtBuscar().setText("");
        cargarTabla(repo.listar());
        int fila = clientesMostrados.size() - 1;
        vista.getTablaClientes().setRowSelectionInterval(fila, fila);   // deja seleccionado el nuevo
        d.dispose();
    }

    // Validaciones - Devuelve null si esta todo bien.
    private String validar(String razon, String email, Double limite) {
        if (razon.isEmpty()) {
            return "La razón social es obligatoria.";
        }
        if (!email.isEmpty() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "El email no tiene un formato válido.";
        }
        if (limite == null) {
            return "Ingresá el límite de crédito.";
        }
        return null;
    }
}