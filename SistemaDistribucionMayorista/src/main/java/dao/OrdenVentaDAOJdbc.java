package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import config.ConexionDB;
import model.CategoriaProducto;
import model.Cliente;
import model.DetalleOrdenVenta;
import model.OrdenVenta;
import model.Producto;
import model.Usuario;

public class OrdenVentaDAOJdbc implements OrdenVentaDAO {

    // Deposito del que sale la mercaderia de las ventas (1 = Deposito Central)
    private static final int ID_DEPOSITO_VENTAS = 1;
    private static final String TIPO_MOVIMIENTO_VENTA = "Egreso por venta";

    // cada orden con su cliente y su vendedor.
    // Se une con Cliente sin filtrar por estado: el historial incluye clientes dados de baja.
    private static final String SQL_CABECERAS =
            "SELECT o.nro_orden, o.fecha_emision, o.estado, o.descuento_aplicado, "
          + "c.cuit, c.razon_social, c.telefono, c.email, c.direccion, c.limite_credito, c.saldo, "
          + "cat.nombre AS categoria, u.username, u.rol, u.estado AS usuario_estado "
          + "FROM OrdenVenta o "
          + "JOIN Cliente c ON o.id_cliente = c.id_cliente "
          + "LEFT JOIN CategoriaCliente cat ON c.id_categoria_cli = cat.id_categoria_cli "
          + "JOIN Usuario u ON o.id_usuario = u.id_usuario "
          + "ORDER BY o.nro_orden";

    // Items de TODAS las ordenes en una sola consulta (evita una consulta por cada orden)
    private static final String SQL_ITEMS =
            "SELECT d.nro_orden, d.cantidad, d.precio_unitario, d.porcentaje_descuento, "
          + "p.codigo, p.descripcion, p.precio_base, p.costo_compra, p.stock_actual, p.stock_minimo, "
          + "cat.nombre AS cat_nombre, cat.descripcion AS cat_descripcion "
          + "FROM DetalleOrdenVenta d "
          + "JOIN Producto p ON d.id_producto = p.id_producto "
          + "LEFT JOIN CategoriaProducto cat ON p.id_categoria_prod = cat.id_categoria_prod "
          + "ORDER BY d.id_detalle";

    private static final String SQL_INSERT_ORDEN =
            "INSERT INTO OrdenVenta (fecha_emision, estado, subtotal, descuento_aplicado, total, "
          + "id_cliente, id_usuario) VALUES (?, ?, ?, ?, ?, "
          + "(SELECT id_cliente FROM Cliente WHERE cuit = ?), "
          + "(SELECT id_usuario FROM Usuario WHERE username = ?))";

    private static final String SQL_INSERT_DETALLE =
            "INSERT INTO DetalleOrdenVenta (nro_orden, id_producto, cantidad, precio_unitario, "
          + "porcentaje_descuento, subtotal) VALUES (?, "
          + "(SELECT id_producto FROM Producto WHERE codigo = ?), ?, ?, ?, ?)";

    // Descuenta el stock solo si alcanza: la condicion "stock_actual >= ?" se evalua en la
    // misma sentencia, asi dos ventas simultaneas no pueden dejar el stock en negativo.
    private static final String SQL_DESCONTAR_STOCK =
            "UPDATE Producto SET stock_actual = stock_actual - ? "
          + "WHERE codigo = ? AND stock_actual >= ?";

    private static final String SQL_INSERT_MOVIMIENTO =
            "INSERT INTO MovimientoStock (fecha_hora, cantidad, tipo_movimiento, motivo, "
          + "id_producto, id_deposito, id_usuario) VALUES (?, ?, ?, ?, "
          + "(SELECT id_producto FROM Producto WHERE codigo = ?), ?, "
          + "(SELECT id_usuario FROM Usuario WHERE username = ?))";

    // Suma al saldo solo si no supera el limite de credito (misma idea que el stock)
    private static final String SQL_SUMAR_SALDO =
            "UPDATE Cliente SET saldo = saldo + ? "
          + "WHERE cuit = ? AND saldo + ? <= limite_credito";

    @Override
    public List<OrdenVenta> listar() {
        // LinkedHashMap: mantiene el orden y permite ubicar cada orden por su numero
        Map<Integer, OrdenVenta> ordenes = new LinkedHashMap<>();
        try (Connection con = ConexionDB.obtener()) {
            cargarCabeceras(con, ordenes);
            cargarItems(con, ordenes);
        } catch (SQLException e) {
            throw new DAOException("Error al listar las ordenes de venta", e);
        }
        return new ArrayList<>(ordenes.values());
    }

    // El numero real lo genera MySQL (AUTO_INCREMENT) al insertar y se le asigna a la orden.
    // Este metodo solo da una referencia del proximo numero.
    @Override
    public int siguienteNumero() {
        String sql = "SELECT COALESCE(MAX(nro_orden), 0) + 1 FROM OrdenVenta";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 1;
        } catch (SQLException e) {
            throw new DAOException("Error al calcular el siguiente numero de orden", e);
        }
    }

    // TRANSACCION: confirmar una orden implica varios pasos que tienen que ocurrir TODOS o NINGUNO:
    // 1) guardar la orden, 2) guardar cada item, 3) descontar el stock, 4) registrar el
    // movimiento de stock, 5) sumar el total al saldo del cliente.
    // Si cualquier paso falla (por ejemplo, no hay stock) se hace rollback y no queda nada a medias.
    @Override
    public void agregar(OrdenVenta orden) {
        try (Connection con = ConexionDB.obtener()) {
            con.setAutoCommit(false);
            try {
                insertarCabecera(con, orden);
                for (DetalleOrdenVenta item : orden.getItems()) {
                    insertarDetalle(con, orden, item);
                    descontarStock(con, item);
                    registrarMovimiento(con, orden, item);
                }
                sumarSaldoCliente(con, orden);
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al registrar la orden de venta", e);
        }

        // Solo si hubo commit: reflejamos los cambios en los objetos en memoria,
        // para que el controller muestre el stock y el saldo actualizados.
        for (DetalleOrdenVenta item : orden.getItems()) {
            item.getProducto().actualizarStock(item.getCantidad(), "Egreso");
        }
        orden.getCliente().actualizarSaldo(orden.getTotal());
    }

    // Paso 1: la orden. Recupera el numero generado por MySQL y se lo asigna al objeto.
    private void insertarCabecera(Connection con, OrdenVenta orden) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_ORDEN, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, new Timestamp(orden.getFechaEmision().getTime()));
            ps.setString(2, orden.getEstado());
            ps.setBigDecimal(3, orden.getSubtotal());
            ps.setBigDecimal(4, orden.getDescuentoAplicado());
            ps.setBigDecimal(5, orden.getTotal());
            ps.setString(6, orden.getCliente().getCUIT());
            ps.setString(7, orden.getUsuario().getUsername());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    orden.setNumeroOrden(keys.getInt(1));
                }
            }
        }
    }

    // Paso 2: un item de la orden
    private void insertarDetalle(Connection con, OrdenVenta orden, DetalleOrdenVenta item) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_DETALLE)) {
            ps.setInt(1, orden.getNumeroOrden());
            ps.setString(2, item.getProducto().getCodigo());
            ps.setInt(3, item.getCantidad());
            ps.setBigDecimal(4, item.getPrecioUnitario());
            ps.setBigDecimal(5, item.getPorcentajeDescuento());
            ps.setBigDecimal(6, item.getSubtotal());
            ps.executeUpdate();
        }
    }

    // Paso 3: si no se actualizo ninguna fila es porque el stock no alcanza.
    // La excepcion dispara el rollback de toda la orden.
    private void descontarStock(Connection con, DetalleOrdenVenta item) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_DESCONTAR_STOCK)) {
            ps.setInt(1, item.getCantidad());
            ps.setString(2, item.getProducto().getCodigo());
            ps.setInt(3, item.getCantidad());
            if (ps.executeUpdate() == 0) {
                throw new DAOException("Stock insuficiente para el producto "
                        + item.getProducto().getDescripcion() + ".", null);
            }
        }
    }

    // Paso 4: deja registrado en el historial de stock que ese producto salio por una venta
    private void registrarMovimiento(Connection con, OrdenVenta orden, DetalleOrdenVenta item) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_MOVIMIENTO)) {
            ps.setTimestamp(1, new Timestamp(orden.getFechaEmision().getTime()));
            ps.setInt(2, item.getCantidad());
            ps.setString(3, TIPO_MOVIMIENTO_VENTA);
            ps.setString(4, "Venta por Orden Nro " + orden.getNumeroOrden());
            ps.setString(5, item.getProducto().getCodigo());
            ps.setInt(6, ID_DEPOSITO_VENTAS);
            ps.setString(7, orden.getUsuario().getUsername());
            ps.executeUpdate();
        }
    }

    // Paso 5: si no se actualizo ninguna fila es porque la orden supera el credito disponible
    private void sumarSaldoCliente(Connection con, OrdenVenta orden) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_SUMAR_SALDO)) {
            ps.setBigDecimal(1, orden.getTotal());
            ps.setString(2, orden.getCliente().getCUIT());
            ps.setBigDecimal(3, orden.getTotal());
            if (ps.executeUpdate() == 0) {
                throw new DAOException("La orden supera el credito disponible del cliente.", null);
            }
        }
    }

    // Lectura, parte 1: crea las ordenes con su cliente y vendedor (todavia sin items)
    private void cargarCabeceras(Connection con, Map<Integer, OrdenVenta> ordenes) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_CABECERAS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("cuit"),
                        rs.getString("razon_social"),
                        rs.getString("direccion"),
                        rs.getString("categoria"),
                        rs.getBigDecimal("limite_credito"),
                        rs.getBigDecimal("saldo"));

                // No se trae la contrasena del vendedor: no hace falta para mostrar la orden
                String username = rs.getString("username");
                Usuario vendedor = new Usuario(username, "", username, "",
                        rs.getString("rol"), "A".equals(rs.getString("usuario_estado")));

                OrdenVenta orden = new OrdenVenta(rs.getInt("nro_orden"), cliente, vendedor);
                orden.setFechaEmision(new Date(rs.getTimestamp("fecha_emision").getTime()));
                orden.setEstado(rs.getString("estado"));
                orden.setDescuentoAplicado(rs.getBigDecimal("descuento_aplicado"));
                ordenes.put(orden.getNumeroOrden(), orden);
            }
        }
    }

    // Lectura, parte 2: agrega los items a cada orden (el total se recalcula solo en agregarItem)
    private void cargarItems(Connection con, Map<Integer, OrdenVenta> ordenes) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_ITEMS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                OrdenVenta orden = ordenes.get(rs.getInt("nro_orden"));
                if (orden == null) {
                    continue;
                }

                String catNombre = rs.getString("cat_nombre");
                CategoriaProducto categoria = (catNombre != null)
                        ? new CategoriaProducto(catNombre, rs.getString("cat_descripcion"))
                        : null;
                Producto producto = new Producto(
                        rs.getString("codigo"),
                        rs.getString("descripcion"),
                        categoria,
                        rs.getBigDecimal("costo_compra"),
                        rs.getBigDecimal("precio_base"),
                        rs.getInt("stock_actual"),
                        rs.getInt("stock_minimo"));

                DetalleOrdenVenta item = new DetalleOrdenVenta(
                        rs.getInt("cantidad"), producto, rs.getBigDecimal("porcentaje_descuento"));
                // El constructor toma el precio actual del producto; aca se restaura el precio
                // al que se vendio (puede haber cambiado desde entonces)
                item.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                orden.agregarItem(item);
            }
        }
    }
}