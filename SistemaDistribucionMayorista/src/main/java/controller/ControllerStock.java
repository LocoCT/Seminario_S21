package controller;
import model.Deposito;
import model.MovimientoStock;
import model.Producto;
import model.Usuario;
import view.GestionStockView;
import view.NuevoMovimientoStockView;
import util.Dialogs;
import dao.DAOFactory;
import dao.ProductoDAO;
import dao.StockDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ControllerStock implements ActionListener {

    private final GestionStockView vista;
    private final StockDAO daoStock;
    private final ProductoDAO daoProductos;
    private final Usuario usuarioLogueado;
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public ControllerStock(GestionStockView vista, Usuario usuarioLogueado) {
        this.vista = vista;
        this.usuarioLogueado = usuarioLogueado;
        this.daoStock = DAOFactory.stock();
        this.daoProductos = DAOFactory.productos();

        registrarListeners();
        cargarTablaMovimientos(daoStock.listarMovimientos());
    }

    private void registrarListeners() {
        vista.getBtnBuscar().addActionListener(this);
        vista.getTxtBuscar().addActionListener(this);
        vista.getBtnNuevoMovimiento().addActionListener(this);
        vista.getBtnCerrar().addActionListener(e -> vista.dispose());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == vista.getBtnBuscar() || src == vista.getTxtBuscar()) {
            buscarMovimientos();
        } else if (src == vista.getBtnNuevoMovimiento()) {
            abrirModalNuevoMovimiento();
        }
    }

    public void cargarTablaMovimientos(List<MovimientoStock> lista) {
        vista.getModeloMovimientos().setRowCount(0);
        for (MovimientoStock m : lista) {
            String id = String.format("MOV-%04d", m.getId());
            String fecha = (m.getFechaHora() != null) ? formatoFecha.format(m.getFechaHora()) : "-";
            String prodDesc = (m.getProducto() != null) ? m.getProducto().getCodigo() + " - " + m.getProducto().getDescripcion() : "-";
            String nomDeposito = (m.getDeposito() != null) ? m.getDeposito().getNombre() : "-";

            vista.getModeloMovimientos().addRow(new Object[]{
                id,
                fecha,
                prodDesc,
                m.getTipoMovimiento(),
                m.getCantidad(),
                nomDeposito,
                m.getMotivo()
            });
        }
    }

    private void buscarMovimientos() {
        String filtro = vista.getTxtBuscar().getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            cargarTablaMovimientos(daoStock.listarMovimientos());
            return;
        }

        List<MovimientoStock> lista = daoStock.listarMovimientos();
        lista.removeIf(m -> {
            String pDesc = (m.getProducto() != null) ? m.getProducto().getDescripcion().toLowerCase() : "";
            String dep = (m.getDeposito() != null) ? m.getDeposito().getNombre().toLowerCase() : "";
            return !pDesc.contains(filtro) && !dep.contains(filtro);
        });
        cargarTablaMovimientos(lista);
    }

    public void abrirNuevoMovimiento() {
        if (this.vista != null){
            this.vista.setVisible(true);
            this.vista.toFront();
        }
        abrirModalNuevoMovimiento();
    }

    public void abrirModalNuevoMovimiento() {
        List<Producto> productos = daoProductos.listar();
        if (productos.isEmpty()) {
            Dialogs.info(vista, "No hay productos registrados en el catálogo.", "Aviso");
            return;
        }

        NuevoMovimientoStockView modal = new NuevoMovimientoStockView(vista);

        modal.getCmbProductos().removeAllItems();
        for (Producto p : productos) {
            modal.getCmbProductos().addItem(p.getCodigo() + " - " + p.getDescripcion() + " (Stock actual: " + p.getStockActual() + ")");
        }

        modal.getBtnRegistrar().addActionListener(e -> {
            int idxProd = modal.getCmbProductos().getSelectedIndex();
            if (idxProd < 0) return;

            Producto prodSel = productos.get(idxProd);
            String tipoMov = (String) modal.getCmbTipoMovimiento().getSelectedItem();
            String nombreDeposito = (String) modal.getCmbDeposito().getSelectedItem();
            String motivo = modal.getTxtMotivo().getText().trim();

            int cantidad;
            try {
                cantidad = Integer.parseInt(modal.getTxtCantidad().getText().trim());
                if (cantidad <= 0) {
                    modal.mostrarError("La cantidad debe ser mayor a 0.");
                    return;
                }
            } catch (NumberFormatException ex) {
                modal.mostrarError("Ingrese un valor numérico para la cantidad.");
                return;
            }

            // Validamos que un egreso no exceda el stock existente
            if (tipoMov.toLowerCase().contains("egreso") && cantidad > prodSel.getStockActual()) {
                modal.mostrarError("Error: Egreso físico mayor al stock actual (" + prodSel.getStockActual() + ").");
                return;
            }

            if (motivo.isEmpty()) {
                modal.mostrarError("Debe especificar el motivo del movimiento.");
                return;
            }

            Deposito depositoSel = new Deposito(1, nombreDeposito, "Central");

            // Creamos y registramos el movimiento
            MovimientoStock nuevoMovimiento = new MovimientoStock(
                daoStock.siguienteId(),
                new Date(),
                cantidad,
                tipoMov,
                motivo,
                prodSel,
                depositoSel,
                this.usuarioLogueado
            );

            // Se agrega al dao y llamamos a registrarMovimiento
            daoStock.agregarMovimiento(nuevoMovimiento);

            Dialogs.info(modal, "Movimiento de stock registrado con éxito.", "Éxito");
            modal.dispose();
            cargarTablaMovimientos(daoStock.listarMovimientos());
        });

        modal.getBtnCancelar().addActionListener(e -> modal.dispose());
        modal.setVisible(true);
    }
}