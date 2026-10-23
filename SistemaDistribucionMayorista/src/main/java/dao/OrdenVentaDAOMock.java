package dao;
import model.Cliente;
import model.DetalleOrdenVenta;
import model.OrdenVenta;
import model.Producto;
import model.Usuario;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OrdenVentaDAOMock implements OrdenVentaDAO {

    // Temporal: reemplazar por consultas a la base de datos.
    // Es static para que todas las pantallas ven las mismas órdenes.
    private static final List<OrdenVenta> ORDENES = new ArrayList<>();

    static {
        inicializarMock();
    }

    private static void inicializarMock() {
        ClienteDAO repoClientes = new ClienteDAOMock();
        ProductoDAO repoProductos = new ProductoDAOMock();
        List<Cliente> clientes = repoClientes.listar();
        List<Producto> productos = repoProductos.listar();

        Usuario vendedorMock = new Usuario("Sebastian", "Puccio", "1144445555", "vendedor@distribuidora.com", "Vendedor", "vendedor123", "Vendedor", true);

        // Helper para crear fechas anteriores
        Calendar cal = Calendar.getInstance();

        // 1. OV-000180: Autoservicio / Cliente 0 (hace 3 días) - Facturada
        if (clientes.size() > 0 && productos.size() >= 2) {
            cal.add(Calendar.DAY_OF_YEAR, -3);
            OrdenVenta ov1 = new OrdenVenta(180, clientes.get(0), vendedorMock);
            ov1.setFechaEmision(cal.getTime());
            ov1.setEstado("Facturada");
            ov1.agregarItem(new DetalleOrdenVenta(40, productos.get(0), BigDecimal.ZERO)); // Agua Mineral
            ov1.agregarItem(new DetalleOrdenVenta(20, productos.get(1), BigDecimal.ZERO)); // Arroz Integral
            ORDENES.add(ov1);
        }

        // 2. OV-000181: Cliente 1 (hace 2 días) - Entregada
        if (clientes.size() > 1 && productos.size() >= 3) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            OrdenVenta ov2 = new OrdenVenta(181, clientes.get(1), vendedorMock);
            ov2.setFechaEmision(cal.getTime());
            ov2.setEstado("Entregada");
            ov2.agregarItem(new DetalleOrdenVenta(15, productos.get(1), BigDecimal.ZERO)); // Arroz Integral
            ov2.agregarItem(new DetalleOrdenVenta(10, productos.get(2), BigDecimal.ZERO)); // Detergente
            ORDENES.add(ov2);
        }

        // 3. OV-000182: Comercial Belgrano / Cliente 2 (hace 1 día) - Pendiente
        if (clientes.size() > 2 && productos.size() >= 4) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            OrdenVenta ov3 = new OrdenVenta(182, clientes.get(2), vendedorMock);
            ov3.setFechaEmision(cal.getTime());
            ov3.setEstado("Pendiente");
            ov3.agregarItem(new DetalleOrdenVenta(50, productos.get(0), BigDecimal.ZERO)); // Agua
            ov3.agregarItem(new DetalleOrdenVenta(30, productos.get(3), BigDecimal.ZERO)); // Aceite
            ORDENES.add(ov3);
        }

        // 4. OV-000183: Almacén El Sol / Cliente 1 (ayer) - Facturada
        if (clientes.size() > 1 && productos.size() >= 2) {
            OrdenVenta ov4 = new OrdenVenta(183, clientes.get(1), vendedorMock);
            ov4.setFechaEmision(new Date());
            ov4.setEstado("Facturada");
            ov4.agregarItem(new DetalleOrdenVenta(25, productos.get(0), BigDecimal.ZERO)); // Agua
            ov4.agregarItem(new DetalleOrdenVenta(15, productos.get(1), BigDecimal.ZERO)); // Arroz
            ORDENES.add(ov4);
        }

        // 5. OV-000184: Distribuidora Norte / Cliente 0 (hoy) - Pendiente
        if (clientes.size() > 0 && productos.size() >= 3) {
            OrdenVenta ov5 = new OrdenVenta(184, clientes.get(0), vendedorMock);
            ov5.setFechaEmision(new Date());
            ov5.setEstado("Pendiente");
            ov5.agregarItem(new DetalleOrdenVenta(60, productos.get(0), BigDecimal.ZERO)); // Agua
            ov5.agregarItem(new DetalleOrdenVenta(20, productos.get(2), BigDecimal.ZERO)); // Detergente
            ORDENES.add(ov5);
        }
    }

    public List<OrdenVenta> listar() {
        return new ArrayList<>(ORDENES);
    }

    // Siguiente numero libre (ultimo numero usado + 1).
    // solo se ocupa cuando la orden se guarda con agregar().
    public int siguienteNumero() {
        int max = 0;
        for (OrdenVenta o : ORDENES) {
            max = Math.max(max, o.getNumeroOrden());
        }
        return max + 1;
    }

    public void agregar(OrdenVenta orden) {
        ORDENES.add(orden);
    }
}