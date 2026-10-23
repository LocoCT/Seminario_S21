package dao;
import model.Deposito;
import model.MovimientoStock;
import model.Producto;
import model.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockDAOMock implements StockDAO {

    private static final List<MovimientoStock> MOVIMIENTOS = new ArrayList<>();

    static {
        // Datos de prueba iniciales
        ProductoDAO repoProductos = new ProductoDAOMock();
        List<Producto> productos = repoProductos.listar();

        if (!productos.isEmpty()) {
            Producto p1 = productos.get(0);
            Deposito depositoCentral = new Deposito(1, "Depósito Central", "Zona Sur");
            Usuario adminMock = new Usuario("Federico", "González", "admin_fede", "1234", "Administrador", true);

            MOVIMIENTOS.add(new MovimientoStock(
                1,
                new Date(),
                50,
                "Ingreso",
                "Reposición semanal de stock",
                p1,
                depositoCentral,
                adminMock
            ));
        }
    }

    public List<MovimientoStock> listarMovimientos() {
        return new ArrayList<>(MOVIMIENTOS);
    }

    public int siguienteId() {
        int max = 0;
        for (MovimientoStock m : MOVIMIENTOS) {
            max = Math.max(max, m.getId());
        }
        return max + 1;
    }

    public void agregarMovimiento(MovimientoStock mov) {
        MOVIMIENTOS.add(mov);
        mov.registrarMovimientoStock();
    }
}