package controller;

import view.GestionProductos;
import view.NuevoProducto;
import model.CategoriaProducto;
import model.Producto;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ControllerProductos implements ActionListener, ListSelectionListener {

    private final GestionProductos vista;
    private final List<Producto> listaProductos; // Persistencia temporal en memoria
    private boolean modoEdicion; // true = creando/editando, false = navegando

    public ControllerProductos(GestionProductos vista) {
        this.vista = vista;
        this.listaProductos = new ArrayList<>();
        this.modoEdicion = false;

        cargarDatosEjemplo();
        actualizarTabla(listaProductos);
        registrarListeners();
    }

    // Carga inicial de datos de muestra para el prototipo
private void cargarDatosEjemplo() {
    CategoriaProducto catBebidas = new CategoriaProducto("Bebidas", "Bebidas analcohólicas y refrescos");
    CategoriaProducto catAlmacen = new CategoriaProducto("Almacén", "Productos comestibles secos");
    CategoriaProducto catLimpieza = new CategoriaProducto("Limpieza", "Artículos de higiene y limpieza");

    listaProductos.add(new Producto("PROD-001", "Agua Mineral 500ml", catBebidas, 450.00, 850.00, 120, 20));
    listaProductos.add(new Producto("PROD-002", "Arroz Integral 1kg", catAlmacen, 890.00, 1450.00, 45, 10));
    listaProductos.add(new Producto("PROD-003", "Detergente Multiuso 750ml", catLimpieza, 620.00, 1100.00, 8, 15));
    listaProductos.add(new Producto("PROD-004", "Aceite de Girasol 1.5L", catAlmacen, 1200.00, 1980.00, 60, 12));
}

    private void registrarListeners() {
        this.vista.getBtnBuscar().addActionListener(this);
        this.vista.getBtnNuevo().addActionListener(this);
        this.vista.getBtnEditar().addActionListener(this);
        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnCancelar().addActionListener(this);

        // Listener para detectar cuándo el usuario selecciona un producto en la JTable
        this.vista.getTablaProductos().getSelectionModel().addListSelectionListener(this);
    }

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

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && !modoEdicion) {
            int fila = vista.getTablaProductos().getSelectedRow();
            if (fila >= 0) {
                String codigo = (String) vista.getModeloTabla().getValueAt(fila, 0);
                Producto p = buscarPorCodigo(codigo);
                if (p != null) {
                    mostrarEnFormulario(p);
                    vista.habilitarEditar(true);
                }
            }
        }
    }

    // --- Lógica de Negocio y Métodos Auxiliares ---

    private void actualizarTabla(List<Producto> lista) {
        vista.getModeloTabla().setRowCount(0); // Limpiar tabla
        for (Producto p : lista) {

            String catNombre = (p.getCategoria() != null) ? p.getCategoria().getNombre() : "Sin Categoria";

            vista.getModeloTabla().addRow(new Object[]{
                p.getCodigo(),
                p.getDescripcion(),
                catNombre,
                String.format("$ %.2f", p.getPrecioBase()),
                p.getStockActual()
            });
        }
    }

    private void buscarProductos() {
        String criterio = vista.getTxtBuscar().getText().trim().toLowerCase();
        if (criterio.isEmpty()) {
            actualizarTabla(listaProductos);
            return;
        }

        List<Producto> filtrados = new ArrayList<>();
        for (Producto p : listaProductos) {
            if (p.getCodigo().toLowerCase().contains(criterio) || 
                p.getDescripcion().toLowerCase().contains(criterio)) {
                filtrados.add(p);
            }
        }
        actualizarTabla(filtrados);

        if (filtrados.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No se encontraron productos con el criterio ingresado.", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
        }
    }

private void prepararNuevo() {
    NuevoProducto vistaNuevo = new NuevoProducto(this.vista);

    // Código autogenerado para el prototipo
    String nuevoCodigo = String.format("PROD-%03d", listaProductos.size() + 1);
    vistaNuevo.getTxtCodigo().setText(nuevoCodigo);

    vistaNuevo.getBtnGuardar().addActionListener(e -> {
        String codigo = vistaNuevo.getTxtCodigo().getText().trim();
        String descripcion = vistaNuevo.getTxtDescripcion().getText().trim();
        String nombreCat = (String) vistaNuevo.getCmbCategoria().getSelectedItem();

        if (descripcion.isEmpty()) {
            vistaNuevo.mostrarError("La descripción del producto es obligatoria.");
            return;
        }

        try {
            double costo = vistaNuevo.getTxtCostoCompra().getValorNumerico();
            double precio = vistaNuevo.getTxtPrecioBase().getValorNumerico();
            int stockActual = Integer.parseInt(vistaNuevo.getTxtStockActual().getText().trim());
            int stockMin = Integer.parseInt(vistaNuevo.getTxtStockMinimo().getText().trim());

            CategoriaProducto cat = new CategoriaProducto(nombreCat, "Categoría " + nombreCat);
            Producto nuevo = new Producto(codigo, descripcion, cat, costo, precio, stockActual, stockMin);

            listaProductos.add(nuevo);
            actualizarTabla(listaProductos);

            JOptionPane.showMessageDialog(vista, "Producto registrado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            vistaNuevo.dispose();

        } catch (NumberFormatException ex) {
            vistaNuevo.mostrarError("Verifique los valores numéricos ingresados.");
        }
    });

    vistaNuevo.getBtnCancelar().addActionListener(e -> vistaNuevo.dispose());

    vistaNuevo.setVisible(true);
}

    private void prepararEdicion() {
        if (vista.getTablaProductos().getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(vista, "Seleccione un producto de la tabla para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modoEdicion = true;
        vista.habilitarEdicion(true);
    }

    private void guardarProducto() {
    String codigo = vista.getTxtCodigo().getText().trim();
    String descripcion = vista.getTxtDescripcion().getText().trim();
    String nombreCategoria = (String) vista.getCmbCategoria().getSelectedItem();

    if (descripcion.isEmpty()) {
        JOptionPane.showMessageDialog(vista, "La descripción del producto es obligatoria.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        double costo = vista.getTxtCostoCompra().getValorNumerico();
        double precio = vista.getTxtPrecioBase().getValorNumerico();
        int stockActual = Integer.parseInt(vista.getTxtStockActual().getText().trim());
        int stockMin = Integer.parseInt(vista.getTxtStockMinimo().getText().trim());

        // 1. Instanciamos el objeto CategoriaProducto con el nombre seleccionado de la lista
        CategoriaProducto objetoCategoria = new CategoriaProducto(nombreCategoria, "Categoría " + nombreCategoria);

        Producto existente = buscarPorCodigo(codigo);
        if (existente == null) {
            // 2. Pasamos 'objetoCategoria' al constructor de Producto
            Producto nuevo = new Producto(codigo, descripcion, objetoCategoria, costo, precio, stockActual, stockMin);
            listaProductos.add(nuevo);
            JOptionPane.showMessageDialog(vista, "Producto registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // 3. Pasamos 'objetoCategoria' al método setCategoria()
            existente.setDescripcion(descripcion);
            existente.setCategoria(objetoCategoria);
            existente.setCostoCompra(costo);
            existente.setPrecioBase(precio);
            existente.setStockActual(stockActual);
            existente.setStockMinimo(stockMin);
            JOptionPane.showMessageDialog(vista, "Producto actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }

        modoEdicion = false;
        vista.habilitarEdicion(false);
        actualizarTabla(listaProductos);
        limpiarFormulario();

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(vista, "Asegúrese de ingresar valores numéricos válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
    }
}

    private void cancelarEdicion() {
        modoEdicion = false;
        limpiarFormulario();
        vista.habilitarEdicion(false);
        vista.getTablaProductos().clearSelection();
    }

    private void mostrarEnFormulario(Producto p) {
        vista.getTxtCodigo().setText(p.getCodigo());
        vista.getTxtDescripcion().setText(p.getDescripcion());
        vista.getCmbCategoria().setSelectedItem(p.getCategoria());
        vista.getTxtCostoCompra().setMonto(p.getCostoCompra());
        vista.getTxtPrecioBase().setMonto(p.getPrecioBase());
        vista.getTxtStockActual().setText(String.valueOf(p.getStockActual()));
        vista.getTxtStockMinimo().setText(String.valueOf(p.getStockMinimo()));
    }

    private void limpiarFormulario() {
        vista.getTxtCodigo().setText("");
        vista.getTxtDescripcion().setText("");
        if (vista.getCmbCategoria().getItemCount() > 0) {
            vista.getCmbCategoria().setSelectedIndex(0);
        }
        vista.getTxtCostoCompra().setText("");
        vista.getTxtPrecioBase().setText("");
        vista.getTxtStockActual().setText("");
        vista.getTxtStockMinimo().setText("");
    }

    private Producto buscarPorCodigo(String codigo) {
        for (Producto p : listaProductos) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                return p;
            }
        }
        return null;
    }

}