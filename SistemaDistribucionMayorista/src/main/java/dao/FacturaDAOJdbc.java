package dao;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.ConexionDB;
import model.Cliente;
import model.Factura;
import model.OrdenVenta;
import model.Pago;

// Tablas: Factura, Pago, Pago_Factura y, al emitir una factura o registrar un pago,
// tambien OrdenVenta (estado) y Cliente (saldo).
public class FacturaDAOJdbc implements FacturaDAO {

    private static final String ESTADO_FACTURA_PENDIENTE = "Pendiente";
    private static final String ESTADO_ORDEN_FACTURADA = "Facturada";

    // Las ordenes (con cliente e items) las arma OrdenVentaDAOJdbc: no se repite esa logica
    private final OrdenVentaDAOJdbc daoOrdenes = new OrdenVentaDAOJdbc();

    private static final String SQL_FACTURAS =
            "SELECT nro_factura, nro_orden, fecha_emision, estado FROM Factura ORDER BY nro_factura";

    // Un pago con su cliente y, si esta imputado a una factura, el numero de esa factura
    private static final String SQL_PAGOS =
            "SELECT p.id_pago, p.fecha_pago, p.monto, p.medio_pago, p.comprobante_referencia, "
          + "c.cuit, c.razon_social, c.telefono, c.email, c.direccion, c.limite_credito, c.saldo, "
          + "cat.nombre AS categoria, pf.nro_factura "
          + "FROM Pago p "
          + "JOIN Cliente c ON p.id_cliente = c.id_cliente "
          + "LEFT JOIN CategoriaCliente cat ON c.id_categoria_cli = cat.id_categoria_cli "
          + "LEFT JOIN Pago_Factura pf ON pf.id_pago = p.id_pago "
          + "ORDER BY p.id_pago";

    private static final String SQL_INSERT_FACTURA =
            "INSERT INTO Factura (nro_orden, fecha_emision, subtotal, iva, total_final, estado) "
          + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_INSERT_PAGO =
            "INSERT INTO Pago (id_cliente, fecha_pago, monto, medio_pago, comprobante_referencia) "
          + "VALUES ((SELECT id_cliente FROM Cliente WHERE cuit = ?), ?, ?, ?, ?)";

    // Marca la factura como pagada cuando la suma de los pagos aplicados cubre su total.
    // Asi funcionan tambien los pagos parciales: se va acumulando hasta completar.
    private static final String SQL_MARCAR_PAGADA =
            "UPDATE Factura f SET f.estado = 'Pagada' "
          + "WHERE f.nro_factura = ? "
          + "AND (SELECT COALESCE(SUM(pf.monto_aplicado), 0) FROM Pago_Factura pf "
          + "     WHERE pf.nro_factura = f.nro_factura) >= f.total_final";

    @Override
    public List<Factura> listarFacturas() {
        // Ordenes indexadas por numero para enlazar cada factura con la suya
        Map<Integer, OrdenVenta> ordenes = new HashMap<>();
        for (OrdenVenta o : daoOrdenes.listar()) {
            ordenes.put(o.getNumeroOrden(), o);
        }

        List<Factura> facturas = new ArrayList<>();
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_FACTURAS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                OrdenVenta orden = ordenes.get(rs.getInt("nro_orden"));
                if (orden == null) {
                    continue;
                }
                // El constructor calcula subtotal, IVA y total a partir de la orden
                // y pone la fecha de hoy: aca se restauran la fecha y el estado guardados.
                Factura f = new Factura(rs.getInt("nro_factura"), orden);
                f.setFechaEmision(new Date(rs.getTimestamp("fecha_emision").getTime()));
                f.setEstado(rs.getString("estado"));
                facturas.add(f);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar las facturas", e);
        }
        return facturas;
    }

    @Override
    public List<Pago> listarPagos() {
        Map<Integer, Factura> facturas = new HashMap<>();
        for (Factura f : listarFacturas()) {
            facturas.put(f.getNumeroFactura(), f);
        }

        List<Pago> pagos = new ArrayList<>();
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_PAGOS);
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

                // Un pago a cuenta corriente general no tiene factura (nro_factura es NULL)
                int nroFactura = rs.getInt("nro_factura");
                Factura factura = rs.wasNull() ? null : facturas.get(nroFactura);

                Pago pago = new Pago(
                        rs.getInt("id_pago"),
                        rs.getBigDecimal("monto"),
                        rs.getString("medio_pago"),
                        rs.getString("comprobante_referencia"),
                        cliente, factura);
                pago.setFechaPago(new Date(rs.getTimestamp("fecha_pago").getTime()));
                pagos.add(pago);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar los pagos", e);
        }
        return pagos;
    }

    // Los numeros reales los genera MySQL (AUTO_INCREMENT) al insertar.
    // Estos metodos solo dan una referencia del proximo numero.
    @Override
    public int siguienteNumeroFactura() {
        return siguiente("SELECT COALESCE(MAX(nro_factura), 0) + 1 FROM Factura");
    }

    @Override
    public int siguienteIdPago() {
        return siguiente("SELECT COALESCE(MAX(id_pago), 0) + 1 FROM Pago");
    }

    // TRANSACCION: emitir una factura implica 1) guardarla, 2) marcar la orden como facturada
    // y 3) sumar el IVA al saldo del cliente. Se guardan los tres cambios o ninguno.
    @Override
    public void agregarFactura(Factura factura) {
        factura.setEstado(ESTADO_FACTURA_PENDIENTE);
        try (Connection con = ConexionDB.obtener()) {
            con.setAutoCommit(false);
            try {
                insertarFactura(con, factura);
                marcarOrdenFacturada(con, factura);
                sumarIvaAlSaldo(con, factura);
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al emitir la factura", e);
        }

        // Solo si hubo commit: reflejamos los cambios en los objetos en memoria
        factura.getOrden().setEstado(ESTADO_ORDEN_FACTURADA);
        factura.getOrden().getCliente().actualizarSaldo(factura.getIva());
    }

    // TRANSACCION: registrar un pago implica 1) guardarlo, 2) imputarlo a la factura (si tiene),
    // 3) marcar la factura como pagada si ya quedo cubierta y 4) descontar el monto del saldo.
    @Override
    public void agregarPago(Pago pago) {
        try (Connection con = ConexionDB.obtener()) {
            con.setAutoCommit(false);
            try {
                insertarPago(con, pago);
                if (pago.getFactura() != null) {
                    imputarAFactura(con, pago);
                    marcarFacturaPagadaSiCorresponde(con, pago.getFactura());
                }
                descontarSaldo(con, pago);
                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al registrar el pago", e);
        }

        // Solo si hubo commit: descuenta el saldo en el objeto cliente (misma regla que el modelo)
        pago.registrarPago();
    }

    // Paso 1 de la factura: guarda y recupera el numero generado por MySQL
    private void insertarFactura(Connection con, Factura factura) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_FACTURA, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, factura.getOrden().getNumeroOrden());
            ps.setTimestamp(2, new Timestamp(factura.getFechaEmision().getTime()));
            ps.setBigDecimal(3, factura.getSubtotal());
            ps.setBigDecimal(4, factura.getIva());
            ps.setBigDecimal(5, factura.getTotalFinal());
            ps.setString(6, factura.getEstado());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    factura.setNumeroFactura(keys.getInt(1));
                }
            }
        }
    }

    // Paso 2 de la factura
    private void marcarOrdenFacturada(Connection con, Factura factura) throws SQLException {
        String sql = "UPDATE OrdenVenta SET estado = ? WHERE nro_orden = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, ESTADO_ORDEN_FACTURADA);
            ps.setInt(2, factura.getOrden().getNumeroOrden());
            if (ps.executeUpdate() == 0) {
                throw new DAOException("No existe la orden de venta de la factura.", null);
            }
        }
    }

    // Paso 3 de la factura: la orden ya sumo su total sin IVA al saldo; al facturar se suma el IVA,
    // asi el saldo del cliente refleja lo que realmente debe (orden + IVA - pagos).
    private void sumarIvaAlSaldo(Connection con, Factura factura) throws SQLException {
        String sql = "UPDATE Cliente SET saldo = saldo + ? WHERE cuit = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, factura.getIva());
            ps.setString(2, factura.getOrden().getCliente().getCUIT());
            ps.executeUpdate();
        }
    }

    // Paso 1 del pago: guarda y recupera el id generado por MySQL
    private void insertarPago(Connection con, Pago pago) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_PAGO, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pago.getCliente().getCUIT());
            ps.setTimestamp(2, new Timestamp(pago.getFechaPago().getTime()));
            ps.setBigDecimal(3, pago.getMonto());
            ps.setString(4, pago.getMedioPago());
            ps.setString(5, pago.getComprobanteReferencia());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    pago.setId(keys.getInt(1));
                }
            }
        }
    }

    // Paso 2 del pago: lo que se aplica a la factura nunca supera el total de esa factura
    private void imputarAFactura(Connection con, Pago pago) throws SQLException {
        String sql = "INSERT INTO Pago_Factura (id_pago, nro_factura, monto_aplicado) VALUES (?, ?, ?)";
        BigDecimal aplicado = pago.getMonto().min(pago.getFactura().getTotalFinal());
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pago.getId());
            ps.setInt(2, pago.getFactura().getNumeroFactura());
            ps.setBigDecimal(3, aplicado);
            ps.executeUpdate();
        }
    }

    // Paso 3 del pago
    private void marcarFacturaPagadaSiCorresponde(Connection con, Factura factura) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_MARCAR_PAGADA)) {
            ps.setInt(1, factura.getNumeroFactura());
            ps.executeUpdate();
        }
    }

    // Paso 4 del pago: el saldo nunca baja de cero (igual que Pago.registrarPago)
    private void descontarSaldo(Connection con, Pago pago) throws SQLException {
        String sql = "UPDATE Cliente SET saldo = GREATEST(saldo - ?, 0) WHERE cuit = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, pago.getMonto());
            ps.setString(2, pago.getCliente().getCUIT());
            ps.executeUpdate();
        }
    }

    // Ejecuta un SELECT que devuelve un solo numero
    private int siguiente(String sql) {
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 1;
        } catch (SQLException e) {
            throw new DAOException("Error al calcular el siguiente numero", e);
        }
    }
}