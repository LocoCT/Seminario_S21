package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.ConexionDB;
import model.CategoriaProducto;
import model.Producto;

public class ProductoDAOJdbc implements ProductoDAO {

    // Consulta base solo productos activos ('A').
    // LEFT JOIN para traer nombre y descripcion de la categoria en la misma consulta.
    private static final String SQL_BASE =
            "SELECT p.codigo, p.descripcion, p.precio_base, p.costo_compra, "
          + "p.stock_actual, p.stock_minimo, cat.nombre AS cat_nombre, cat.descripcion AS cat_descripcion "
          + "FROM Producto p "
          + "LEFT JOIN CategoriaProducto cat ON p.id_categoria_prod = cat.id_categoria_prod "
          + "WHERE p.estado = 'A' ";

    private static final String ORDEN = " ORDER BY p.id_producto";

    // El id de la categoria se busca por nombre dentro del propio INSERT / UPDATE
    private static final String SQL_INSERT =
            "INSERT INTO Producto (codigo, descripcion, precio_base, costo_compra, "
          + "stock_actual, stock_minimo, estado, id_categoria_prod) "
          + "VALUES (?, ?, ?, ?, ?, ?, 'A', "
          + "(SELECT id_categoria_prod FROM CategoriaProducto WHERE nombre = ?))";

    private static final String SQL_UPDATE =
            "UPDATE Producto SET descripcion = ?, precio_base = ?, costo_compra = ?, "
          + "stock_actual = ?, stock_minimo = ?, "
          + "id_categoria_prod = (SELECT id_categoria_prod FROM CategoriaProducto WHERE nombre = ?) "
          + "WHERE codigo = ?";

    @Override
    public List<Producto> listar() {
        return consultar(SQL_BASE + ORDEN);
    }

    // Busca por codigo o descripcion (contiene el texto)
    @Override
    public List<Producto> buscar(String texto) {
        String t = (texto == null) ? "" : texto.trim();
        if (t.isEmpty()) {
            return listar();
        }
        String patron = "%" + t + "%";
        return consultar(SQL_BASE + "AND (p.codigo LIKE ? OR p.descripcion LIKE ?)" + ORDEN,
                patron, patron);
    }

    // Devuelve null si no existe (el controller usa esto para decidir entre alta y modificacion)
    @Override
    public Producto buscarPorCodigo(String codigo) {
        String sql = "SELECT p.codigo, p.descripcion, p.precio_base, p.costo_compra, "
                   + "p.stock_actual, p.stock_minimo, cat.nombre AS cat_nombre, cat.descripcion AS cat_descripcion "
                   + "FROM Producto p "
                   + "LEFT JOIN CategoriaProducto cat ON p.id_categoria_prod = cat.id_categoria_prod "
                   + "WHERE p.codigo = ?";
        List<Producto> resultado = consultar(sql, codigo);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    @Override
    public boolean existeCodigo(String codigo) {
        return buscarPorCodigo(codigo) != null;
    }

    // Proximo codigo libre con formato PROD-001: toma el mayor numero usado + 1
    // (los codigos con otro formato se ignoran)
    @Override
    public String siguienteCodigo() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(codigo, 6) AS UNSIGNED)), 0) + 1 "
                   + "FROM Producto WHERE codigo LIKE 'PROD-%'";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int siguiente = rs.next() ? rs.getInt(1) : 1;
            return String.format("PROD-%03d", siguiente);
        } catch (SQLException e) {
            throw new DAOException("Error al calcular el siguiente codigo de producto", e);
        }
    }

    @Override
    public void agregar(Producto p) {
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getDescripcion());
            ps.setBigDecimal(3, p.getPrecioBase());
            ps.setBigDecimal(4, p.getCostoCompra());
            ps.setInt(5, p.getStockActual());
            ps.setInt(6, p.getStockMinimo());
            ps.setString(7, nombreCategoria(p));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al guardar el producto " + p.getCodigo(), e);
        }
    }

    @Override
    public void actualizar(Producto p) {
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, p.getDescripcion());
            ps.setBigDecimal(2, p.getPrecioBase());
            ps.setBigDecimal(3, p.getCostoCompra());
            ps.setInt(4, p.getStockActual());
            ps.setInt(5, p.getStockMinimo());
            ps.setString(6, nombreCategoria(p));
            ps.setString(7, p.getCodigo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar el producto " + p.getCodigo(), e);
        }
    }

    @Override
    public List<CategoriaProducto> listarCategorias() {
        List<CategoriaProducto> categorias = new ArrayList<>();
        String sql = "SELECT nombre, descripcion FROM CategoriaProducto ORDER BY id_categoria_prod";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categorias.add(new CategoriaProducto(rs.getString("nombre"), rs.getString("descripcion")));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar las categorias de producto", e);
        }
        return categorias;
    }

    // Devuelve la categoria con ese nombre o null si no existe
    @Override
    public CategoriaProducto buscarCategoria(String nombre) {
        String sql = "SELECT nombre, descripcion FROM CategoriaProducto WHERE nombre = ?";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CategoriaProducto(rs.getString("nombre"), rs.getString("descripcion"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar la categoria " + nombre, e);
        }
    }

    // Nombre de la categoria del producto
    private String nombreCategoria(Producto p) {
        return (p.getCategoria() != null) ? p.getCategoria().getNombre() : null;
    }

    // Ejecuta un SELECT con parametros y convierte cada fila en un Producto
    private List<Producto> consultar(String sql, String... parametros) {
        List<Producto> lista = new ArrayList<>();
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setString(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al consultar productos", e);
        }
        return lista;
    }

    // Fila de la base -> objeto Producto (mismo orden de parametros que el constructor)
    private Producto mapear(ResultSet rs) throws SQLException {
        String catNombre = rs.getString("cat_nombre");
        CategoriaProducto categoria = (catNombre != null)
                ? new CategoriaProducto(catNombre, rs.getString("cat_descripcion"))
                : null;
        return new Producto(
                rs.getString("codigo"),
                rs.getString("descripcion"),
                categoria,
                rs.getBigDecimal("costo_compra"),
                rs.getBigDecimal("precio_base"),
                rs.getInt("stock_actual"),
                rs.getInt("stock_minimo"));
    }
}