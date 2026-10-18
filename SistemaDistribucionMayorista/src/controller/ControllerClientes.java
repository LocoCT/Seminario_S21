package controller;
import model.Cliente;
import view.GestionClientesView;
import view.NuevoClienteView;
import view.estilos.CampoMoneda;
import java.math.BigDecimal;
import javax.swing.*;
import util.Dialogs;
import util.Validator;
import dao.ClienteDAO;
import dao.DAOFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ControllerClientes implements ActionListener {

    private final GestionClientesView vista;
    private final ClienteDAO dao = DAOFactory.clientes();
    private List<Cliente> clientesMostrados = new ArrayList<>();
    private Cliente seleccionado;

    public ControllerClientes(GestionClientesView vista) {
        this.vista = vista;
        cargarCategorias(vista.getCmbCategoria());
        registrarListeners();
        cargarTabla(dao.listar());
    }

    // Registramos los listeners de eventos para cada boton de la vista
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

    private void cargarCategorias(JComboBox<String> combo){
        combo.removeAllItems();
        for (String cat : dao.listarCategorias()){
            combo.addItem(cat);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object origen = e.getSource();

        if (origen == vista.getBtnBuscar() || origen == vista.getTxtBuscar()) {
            cargarTabla(dao.buscar(vista.getTxtBuscar().getText()));

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

    // Cargamos la tabla y detalle 
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

    // Si elegimos una cliente mostramos su detalle en formulario
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

    // Buscamos y pasamos detalle de cliente para formulario
    private void mostrarDetalle(Cliente c) {
        vista.getTxtCuit().setText(c.getCUIT());
        vista.getTxtRazonSocial().setText(c.getRazonSocial());
        vista.getTxtTelefono().setText(c.getTelefono());
        vista.getTxtEmail().setText(c.getEmail());
        vista.getTxtDireccion().setText(c.getDireccion());
        vista.getTxtLimiteCredito().setMonto(c.getLimiteCredito());
        vista.getCmbCategoria().setSelectedItem(c.getCategoria());
    }

    //Limpiamos campos de detalle
    private void limpiarDetalle() {
        vista.getTxtCuit().setText("");
        vista.getTxtRazonSocial().setText("");
        vista.getTxtTelefono().setText("");
        vista.getTxtEmail().setText("");
        vista.getTxtDireccion().setText("");
        vista.getTxtLimiteCredito().setMonto(null);
        vista.getCmbCategoria().setSelectedIndex(0);
    }

    // Bloqueamos los campos y habilita boton editar si tenemos un cliente seleccionado
    private void volverAModoLectura() {
        vista.habilitarEdicion(false);
        vista.habilitarEditar(seleccionado != null);
    }

    // Validamos los datos y guardamos los cambios - el CUIT no se puede modificar
    private void guardarEdicion() {
        String razon = vista.getTxtRazonSocial().getText().trim();
        String telefono = vista.getTxtTelefono().getText().trim();
        String email = vista.getTxtEmail().getText().trim();
        String direccion = vista.getTxtDireccion().getText().trim();
        BigDecimal limite = vista.getTxtLimiteCredito().getMonto();
        String categoria = (String) vista.getCmbCategoria().getSelectedItem();
        
        vista.marcarEmailInvalido(!Validator.esEmailValido(email));

        String error = validar(razon, email, limite);
        if (error != null) {
            Dialogs.advertencia(vista, error, "Datos inválidos");
            return;
        }

        seleccionado.setRazonSocial(razon);
        seleccionado.setTelefono(telefono); 
        seleccionado.setEmail(email);       
        seleccionado.setDireccion(direccion);
        seleccionado.setCategoria(categoria);
        seleccionado.setLimiteCredito(limite);
        dao.actualizar(seleccionado);

        int fila = vista.getTablaClientes().getSelectedRow();
        vista.getModeloTabla().setValueAt(razon, fila, 1);
        volverAModoLectura();
    }

    // Descartamos los cambios y volvemos a modo lectura
    private void cancelarEdicion() {
        if (seleccionado != null) {
            mostrarDetalle(seleccionado); 
        }
        volverAModoLectura();
    }

    // Abrimos vista de nuevo cliente  
    public void abrirNuevoCliente() {
        NuevoClienteView dialogo = new NuevoClienteView(vista);
        cargarCategorias(dialogo.getCmbCategoria());
        dialogo.getBtnCancelar().addActionListener(ev -> dialogo.dispose());
        dialogo.getBtnGuardar().addActionListener(ev -> guardarNuevo(dialogo));
        dialogo.setVisible(true);
    }

    //Validamos datos de nuevo cliente y si esta OK guardamos.
    private void guardarNuevo(NuevoClienteView d) {

        String cuit = d.getTxtCuit().getText().replaceAll("\\D", "");
        String razon = d.getTxtRazonSocial().getText().trim();
        String telefono = d.getTxtTelefono().getText().trim();
        String email = d.getTxtEmail().getText().trim();
        String direccion = d.getTxtDireccion().getText().trim();
        String categoria = (String) d.getCmbCategoria().getSelectedItem();
        BigDecimal limite = d.getTxtLimiteCredito().getMonto();

        d.marcarEmailInvalido(!Validator.esEmailValido(email));

        String error;
        if (!Validator.esCuitValido(cuit)) {
            error = "El CUIT debe tener 11 dígitos.";
        } else if (dao.existeCuit(cuit)) {
            error = "Ya existe un cliente con ese CUIT.";
        } else {
            error = validar(razon, email, limite);
        }

        if (error != null) {
            Dialogs.advertencia(vista, error, "Datos inválidos");
            return;
        }

        String cuitFormateado = cuit.substring(0, 2) + "-" + cuit.substring(2, 10) + "-" + cuit.substring(10);
        // orden: telefono, email, CUIT, razonSocial, direccion, categoria, limiteCredito, saldo
        Cliente nuevo = new Cliente(telefono, email, cuitFormateado, razon, direccion, categoria, limite, BigDecimal.ZERO);
        dao.agregar(nuevo);

        vista.getTxtBuscar().setText("");
        cargarTabla(dao.listar());
        int fila = clientesMostrados.size() - 1;
        vista.getTablaClientes().setRowSelectionInterval(fila, fila);   // deja seleccionado el nuevo
        d.dispose();
    }

    // Validaciones - Devuelve null si esta todo bien.
    private String validar(String razon, String email, BigDecimal limite) {
        if (Validator.esVacio(razon)) {
            return "La razón social es obligatoria.";
        }
        if (!Validator.esEmailValido(email)) {
            return "Ingresá un email válido.";
        }
        if (limite == null) {
            return "Ingresá el límite de crédito.";
        }
        return null;
    }
}