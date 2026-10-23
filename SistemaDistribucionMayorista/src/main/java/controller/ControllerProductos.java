package controller;
import view.GestionProductosView;
import view.NuevoProductoView;
import view.estilos.CampoMoneda;
import model.CategoriaProducto;
import model.Producto;
import util.Dialogs;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import dao.DAOFactory;
import dao.ProductoDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
 
public class ControllerProductos implements ActionListener, ListSelectionListener {
 
    private final GestionProductosView vista;
    private final ProductoDAO dao = DAOFactory.productos(); 
    private boolean modoEdicion;
 
    public ControllerProductos(GestionProductosView vista) {
        this.vista = vista;
        this.modoEdicion = false;
 
        cargarCategorias(vista.getCmbCategoria());
        actualizarTabla(dao.listar());
        registrarListeners();
    }
    
    // Registramos los listeners de eventos para cada boton de la vista
    private void registrarListeners() {
        this.vista.getBtnBuscar().addActionListener(this);
        this.vista.getBtnNuevo().addActionListener(this);
        this.vista.getBtnEditar().addActionListener(this);
        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnCancelar().addActionListener(this);
 
        
        this.vista.getTablaProductos().getSelectionModel().addListSelectionListener(this);
 
        // Actualizamos tabla principal si emitimos o volvemos a pantalla princiapl
        this.vista.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                refrescarTabla();
            }
        });
    }

    // precargamos las categorias
    private void cargarCategorias (JComboBox <String> combo){
        combo.removeAllItems();
        for (CategoriaProducto c : dao.listarCategorias()){
            combo.addItem(c.getNombre());
        }
    }
 
    // Botones pantalla productos
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
 
        if (src == vista.getBtnBuscar()) {
            buscarProductos();
        } else if (src == vista.getBtnNuevo()) {
            prepararNuevo();
        } else if (src == vista.getBtnEditar()) {
            prepararEdicion();
        } else if (src == vista.getBtnGuardar()) {
            guardarProducto();
        } else if (src == vista.getBtnCancelar()) {
            cancelarEdicion();
        }
    }
 
    // Si elegimos un producto y no estamos en modo Editar se carga tabla lateral
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !modoEdicion) {
            int fila = vista.getTablaProductos().getSelectedRow();
            if (fila >= 0) {
                String codigo = (String) vista.getModeloTabla().getValueAt(fila, 0);
                Producto p = dao.buscarPorCodigo(codigo);
                if (p != null) {
                    mostrarEnFormulario(p);
                    vista.habilitarEditar(true);
                }
            } else {
                vista.habilitarEditar(false);
            }
        }
    }
 
    // Vaciamos la tabla y la volvemos a poblar
    private void actualizarTabla(List<Producto> lista) {
        vista.getModeloTabla().setRowCount(0);
        for (Producto p : lista) {
 
            String catNombre = (p.getCategoria() != null) ? p.getCategoria().getNombre() : "Sin Categoria";
 
            vista.getModeloTabla().addRow(new Object[]{
                p.getCodigo(),
                p.getDescripcion(),
                catNombre,
                "$ " + CampoMoneda.formatear(p.getPrecioBase()),
                p.getStockActual()
            });
        }
    }
 
    // Recargamos la tabla desde el dao manteniendo la busqueda
    // Tambien se actualiza si emitimos una orden de venta
    private void refrescarTabla() {
        if (modoEdicion) {
            return;
        }
        int fila = vista.getTablaProductos().getSelectedRow();
        String codigoSel = (fila >= 0) ? (String) vista.getModeloTabla().getValueAt(fila, 0) : null;
 
        actualizarTabla(dao.buscar(vista.getTxtBuscar().getText()));
 
        if (codigoSel == null) {
            return;
        }
        for (int i = 0; i < vista.getModeloTabla().getRowCount(); i++) {
            if (codigoSel.equals(vista.getModeloTabla().getValueAt(i, 0))) {
                vista.getTablaProductos().setRowSelectionInterval(i, i); // vuelve a mostrar el detalle
                return;
            }
        }
        limpiarFormulario();
    }
 
    // Filtramos por codigo o descripcion de producto.
    private void buscarProductos() {
        List<Producto> filtrados = dao.buscar(vista.getTxtBuscar().getText());
        actualizarTabla(filtrados);
 
        if (filtrados.isEmpty()) {
            Dialogs.info(vista, "No se encontraron productos con el criterio ingresado.", "Búsqueda");
        }
    }
 
    // Abrimos vista para nuevo producto
    private void prepararNuevo() {
        NuevoProductoView vistaNuevo = new NuevoProductoView(this.vista);
        cargarCategorias(vistaNuevo.getCmbCategoria());
 
        // generamos nuevo codigo tomando el proximo disponible
        vistaNuevo.getTxtCodigo().setText(dao.siguienteCodigo());
 
        vistaNuevo.getBtnGuardar().addActionListener(e -> {
            String codigo = vistaNuevo.getTxtCodigo().getText().trim();
            String descripcion = vistaNuevo.getTxtDescripcion().getText().trim();
 
            if (descripcion.isEmpty()) {
                vistaNuevo.mostrarError("La descripción del producto es obligatoria.");
                return;
            }
            if (dao.existeCodigo(codigo)) {
                vistaNuevo.mostrarError("Ya existe un producto con ese código.");
                return;
            }
 
            try {
                BigDecimal costo = vistaNuevo.getTxtCostoCompra().getValorNumerico();
                BigDecimal precio = vistaNuevo.getTxtPrecioBase().getValorNumerico();
                int stockActual = Integer.parseInt(vistaNuevo.getTxtStockActual().getText().trim());
                int stockMin = Integer.parseInt(vistaNuevo.getTxtStockMinimo().getText().trim());
 
                Producto nuevo = new Producto(codigo, descripcion, categoriaSeleccionada(vistaNuevo.getCmbCategoria()), costo, precio, stockActual, stockMin);
                dao.agregar(nuevo);
 
                vista.getTxtBuscar().setText("");
                actualizarTabla(dao.listar());
 
                Dialogs.info(vista, "Producto registrado con éxito.", "Éxito");
                vistaNuevo.dispose();
 
            } catch (NumberFormatException ex) {
                vistaNuevo.mostrarError("Verifique los valores numéricos ingresados.");
            }
        });
 
        vistaNuevo.getBtnCancelar().addActionListener(e -> vistaNuevo.dispose());
        vistaNuevo.setVisible(true);
    }

    // Si hay un producto seleccionado de tabla principal habilitamos boton edicion
    private void prepararEdicion() {
        if (vista.getTablaProductos().getSelectedRow() < 0) {
            Dialogs.advertencia(vista, "Seleccione un producto de la tabla para editar.", "Advertencia");
            return;
        }
        modoEdicion = true;
        vista.habilitarEdicion(true);
    }
 
    // validamos y guardamos los cambios del producto editado y volvemos a modo lectura
    private void guardarProducto() {

        String codigo = vista.getTxtCodigo().getText().trim();
        String descripcion = vista.getTxtDescripcion().getText().trim();
 
        if (descripcion.isEmpty()) {
            Dialogs.error(vista, "La descripción del producto es obligatoria.", "Error de Validación");
            return;
        }
 
        try {
            BigDecimal costo = vista.getTxtCostoCompra().getValorNumerico();
            BigDecimal precio = vista.getTxtPrecioBase().getValorNumerico();
            int stockActual = Integer.parseInt(vista.getTxtStockActual().getText().trim());
            int stockMin = Integer.parseInt(vista.getTxtStockMinimo().getText().trim());
 
            CategoriaProducto categoria = categoriaSeleccionada(vista.getCmbCategoria());
 
            Producto existente = dao.buscarPorCodigo(codigo);
            if (existente == null) {
                Producto nuevo = new Producto(codigo, descripcion, categoria, costo, precio, stockActual, stockMin);
                dao.agregar(nuevo);
                Dialogs.info(vista, "Producto registrado correctamente.", "Éxito");
            } else {
                existente.setDescripcion(descripcion);
                existente.setCategoria(categoria);
                existente.setCostoCompra(costo);
                existente.setPrecioBase(precio);
                existente.setStockActual(stockActual);
                existente.setStockMinimo(stockMin);
                dao.actualizar(existente);
                Dialogs.info(vista, "Producto actualizado correctamente.", "Éxito");
            }
 
            modoEdicion = false;
            vista.habilitarEdicion(false);
            actualizarTabla(dao.buscar(vista.getTxtBuscar().getText()));
            limpiarFormulario();
 
        } catch (NumberFormatException ex) {
            Dialogs.error(vista, "Asegúrese de ingresar valores numéricos válidos.", "Error de Formato");
        }
    }
 
    // Descartamos cualquier tipo de cambio limpiamos y volvemos a modo lectura
    private void cancelarEdicion() {
        modoEdicion = false;
        limpiarFormulario();
        vista.habilitarEdicion(false);
        vista.getTablaProductos().clearSelection();
    }
 
    // Pasamos la info del producto a formulario de detalle
    private void mostrarEnFormulario(Producto p) {
        vista.getTxtCodigo().setText(p.getCodigo());
        vista.getTxtDescripcion().setText(p.getDescripcion());
        if (p.getCategoria() != null) {
            vista.getCmbCategoria().setSelectedItem(p.getCategoria().getNombre());
        } else if (vista.getCmbCategoria().getItemCount() > 0) {
            vista.getCmbCategoria().setSelectedIndex(0);
        }
        vista.getTxtCostoCompra().setMonto(p.getCostoCompra());
        vista.getTxtPrecioBase().setMonto(p.getPrecioBase());
        vista.getTxtStockActual().setText(String.valueOf(p.getStockActual()));
        vista.getTxtStockMinimo().setText(String.valueOf(p.getStockMinimo()));
    }

    // Limpiamos todos los campos del formulario
    private void limpiarFormulario() {
        vista.getTxtCodigo().setText("");
        vista.getTxtDescripcion().setText("");
        if (vista.getCmbCategoria().getItemCount() > 0) {
            vista.getCmbCategoria().setSelectedIndex(0);
        }
        vista.getTxtCostoCompra().setMonto(null);
        vista.getTxtPrecioBase().setMonto(null);
        vista.getTxtStockActual().setText("");
        vista.getTxtStockMinimo().setText("");
    }
 
    // Usa la categoría ya existente en el dao (no crea una nueva en cada guardado)
    private CategoriaProducto categoriaSeleccionada(JComboBox<String> combo) {
        return dao.buscarCategoria((String) combo.getSelectedItem());
    }
}