package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.ConexionDB;
import model.Cliente;
import model.Usuario;

public class ClienteDAOJdbc implements ClienteDAO {

    // Consulta clientes activos ('A')
    // LEFT JOIN para traer el nombre de la categoria en vez de su id.
    private static final String SQL_BASE =
            "SELECT c.cuit, c.razon_social, c.telefono, c.email, c.direccion, "
          + "c.limite_credito, c.saldo, cat.nombre AS categoria "
          + "FROM Cliente c "
          + "LEFT JOIN CategoriaCliente cat ON c.id_categoria_cli = cat.id_categoria_cli "
          + "WHERE c.estado = 'A' ";

    // Orden por id: el cliente recien agregado queda al final de la tabla
    private static final String ORDEN = " ORDER BY c.id_cliente";

    // Los ids de la categoria y del usuario que da el alta se buscan por nombre / username
    // dentro del propio INSERT
    private static final String SQL_INSERT =
            "INSERT INTO Cliente (cuit, razon_social, telefono, email, direccion, "
          + "limite_credito, saldo, estado, id_categoria_cli, id_usuario_alta) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, 'A', "
          + "(SELECT id_categoria_cli FROM CategoriaCliente WHERE nombre = ?), "
          + "(SELECT id_usuario FROM Usuario WHERE username = ?))";

    // No se actualiza el saldo - se modifica desde las ordenes y los pagos
    private static final String SQL_UPDATE =
            "UPDATE Cliente SET razon_social = ?, telefono = ?, email = ?, direccion = ?, "
          + "limite_credito = ?, "
          + "id_categoria_cli = (SELECT id_categoria_cli FROM CategoriaCliente WHERE nombre = ?) "
          + "WHERE cuit = ?";

    @Override
    public List<Cliente> listar() {
        return consultar(SQL_BASE + ORDEN);
    }

    // Busca por razon social o por CUIT (con o sin guiones)
    @Override
    public List<Cliente> buscar(String texto) {
        String t = (texto == null) ? "" : texto.trim();
        if (t.isEmpty()) {
            return listar();
        }
        String digitos = t.replaceAll("\\D", "");

        // Si el texto no tiene numeros no tiene sentido buscar por CUIT
        if (digitos.isEmpty()) {
            return consultar(SQL_BASE + "AND c.razon_social LIKE ?" + ORDEN, "%" + t + "%");
        }
        return consultar(
                SQL_BASE + "AND (c.razon_social LIKE ? OR REPLACE(c.cuit, '-', '') LIKE ?)" + ORDEN,
                "%" + t + "%", "%" + digitos + "%");
    }

    @Override
    public boolean existeCuit(String cuit) {
        String digitos = cuit.replaceAll("\\D", "");
        String sql = "SELECT 1 FROM Cliente WHERE REPLACE(cuit, '-', '') = ? LIMIT 1";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, digitos);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DAOException("Error al verificar el CUIT " + cuit, e);
        }
    }

    @Override
    public void agregar(Cliente c, Usuario usuarioAlta) {
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {
            ps.setString(1, c.getCUIT());
            ps.setString(2, c.getRazonSocial());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setString(5, c.getDireccion());
            ps.setBigDecimal(6, c.getLimiteCredito());
            ps.setBigDecimal(7, c.getSaldo());
            ps.setString(8, c.getCategoria());
            ps.setString(9, usuarioAlta.getUsername());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al guardar el cliente " + c.getRazonSocial(), e);
        }
    }

    @Override
    public void actualizar(Cliente c) {
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, c.getRazonSocial());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getDireccion());
            ps.setBigDecimal(5, c.getLimiteCredito());
            ps.setString(6, c.getCategoria());
            ps.setString(7, c.getCUIT());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar el cliente " + c.getRazonSocial(), e);
        }
    }

    @Override
    public List<String> listarCategorias() {
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT nombre FROM CategoriaCliente ORDER BY id_categoria_cli";
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categorias.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar las categorias de cliente", e);
        }
        return categorias;
    }

    // Ejecuta un SELECT con parametros y convierte cada fila en un Cliente
    private List<Cliente> consultar(String sql, String... parametros) {
        List<Cliente> lista = new ArrayList<>();
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
            throw new DAOException("Error al consultar clientes", e);
        }
        return lista;
    }

    // Fila de la base -> objeto Cliente (mismo orden de parametros que el constructor)
    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("cuit"),
                rs.getString("razon_social"),
                rs.getString("direccion"),
                rs.getString("categoria"),
                rs.getBigDecimal("limite_credito"),
                rs.getBigDecimal("saldo"));
    }
}