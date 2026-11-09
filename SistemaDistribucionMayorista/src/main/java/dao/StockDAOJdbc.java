package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import config.ConexionDB;
import model.Deposito;
import model.MovimientoStock;
import model.Producto;
import model.Usuario;

public class StockDAOJdbc implements StockDAO {

    // Historial completo - un movimiento con su producto, deposito y responsable.
    // Los mas recientes primero.
    private static final String SQL_LISTAR =
            "SELECT m.id_movimiento, m.fecha_hora, m.cantidad, m.tipo_movimiento, m.motivo, "
          + "p.codigo, p.descripcion, p.precio_base, p.costo_compra, p.stock_actual, p.stock_minimo, "
          + "d.id_deposito, d.nombre AS dep_nombre, d.ubicacion, "
          + "u.username, u.rol, u.estado "
          + "FROM MovimientoStock m "
          + "JOIN Producto p ON m.id_producto = p.id_producto "
          + "JOIN Deposito d ON m.id_deposito = d.id_deposito "
          + "JOIN Usuario u ON m.id_usuario = u.id_usuario "
          + "ORDER BY m.fecha_hora DESC, m.id_movimiento DESC";

    // El id del producto y del usuario se buscan por codigo / username dentro del propio INSERT
    private static final String SQL_INSERT =
            "INSERT INTO MovimientoStock "
          + "(fecha_hora, cantidad, tipo_movimiento, motivo, id_producto, id_deposito, id_usuario) "
          + "VALUES (?, ?, ?, ?, "
          + "(SELECT id_producto FROM Producto WHERE codigo = ?), ?, "
          + "(SELECT id_usuario FROM Usuario WHERE username = ?))";

    @Override
    public List<MovimientoStock> listarMovimientos() {
        List<MovimientoStock> lista = new ArrayList<>();
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar los movimientos de stock", e);
        }
        return lista;
    }

    // El id real lo genera MySQL (AUTO_INCREMENT) al insertar.
    // Este metodo solo da una referencia del proximo numero.
    @Override
    public int siguienteId() {
        String sql = "SELECT COALESCE(MAX(id_movimiento), 0) + 1 FROM MovimientoStock";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 1;
        } catch (SQLException e) {
            throw new DAOException("Error al calcular el siguiente id de movimiento", e);
        }
    }

    // registrar el movimiento y actualizar el stock del producto son dos pasos
    // que tienen que ocurrir juntos. Si falla alguno, rollback y no se guarda nada.
    @Override
    public void agregarMovimiento(MovimientoStock mov) {
        try (Connection con = ConexionDB.obtener()) {
            con.setAutoCommit(false);   
            try {
                insertarMovimiento(con, mov);
                actualizarStockProducto(con, mov);
                con.commit();
            } catch (SQLException | RuntimeException e) {
                // deshace el INSERT si fallo el UPDATE
                con.rollback();         
                throw e;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al registrar el movimiento de stock", e);
        }
    }

    @Override
    public List<Deposito> listarDepositos() {
        List<Deposito> depositos = new ArrayList<>();
        String sql = "SELECT id_deposito, nombre, ubicacion FROM Deposito ORDER BY id_deposito";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                depositos.add(new Deposito(
                        rs.getInt("id_deposito"), rs.getString("nombre"), rs.getString("ubicacion")));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar los depositos", e);
        }
        return depositos;
    }

    // guarda el movimiento y le asigna el id generado por MySQL
    private void insertarMovimiento(Connection con, MovimientoStock mov) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, new Timestamp(mov.getFechaHora().getTime()));
            ps.setInt(2, mov.getCantidad());
            ps.setString(3, mov.getTipoMovimiento());
            ps.setString(4, mov.getMotivo());
            ps.setString(5, mov.getProducto().getCodigo());
            ps.setInt(6, mov.getDeposito().getId());
            ps.setString(7, mov.getResponsable().getUsername());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    mov.setId(keys.getInt(1));
                }
            }
        }
    }

    // Actualiza el stock del producto.
    // ingreso suma, egreso resta, ajuste reemplaza  vive en MovimientoStock;
    private void actualizarStockProducto(Connection con, MovimientoStock mov) throws SQLException {
        Producto producto = mov.getProducto();

        // FOR UPDATE bloquea la fila hasta el commit
        String sqlLeer = "SELECT stock_actual FROM Producto WHERE codigo = ? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sqlLeer)) {
            ps.setString(1, producto.getCodigo());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("No existe el producto " + producto.getCodigo());
                }
                producto.setStockActual(rs.getInt("stock_actual"));
            }
        }
        // calcula el nuevo stock en el objeto
        mov.registrarMovimientoStock();  

        String sqlGuardar = "UPDATE Producto SET stock_actual = ? WHERE codigo = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlGuardar)) {
            ps.setInt(1, producto.getStockActual());
            ps.setString(2, producto.getCodigo());
            ps.executeUpdate();
        }
    }

    // Fila de la base -> objeto MovimientoStock
    private MovimientoStock mapear(ResultSet rs) throws SQLException {
        Producto producto = new Producto(
                rs.getString("codigo"),
                rs.getString("descripcion"),
                null,
                rs.getBigDecimal("costo_compra"),
                rs.getBigDecimal("precio_base"),
                rs.getInt("stock_actual"),
                rs.getInt("stock_minimo"));

        Deposito deposito = new Deposito(
                rs.getInt("id_deposito"), rs.getString("dep_nombre"), rs.getString("ubicacion"));

        String username = rs.getString("username");
        Usuario responsable = new Usuario(
                username, "", username, "", rs.getString("rol"), "A".equals(rs.getString("estado")));

        return new MovimientoStock(
                rs.getInt("id_movimiento"),
                new Date(rs.getTimestamp("fecha_hora").getTime()),
                rs.getInt("cantidad"),
                rs.getString("tipo_movimiento"),
                rs.getString("motivo"),
                producto, deposito, responsable);
    }
}