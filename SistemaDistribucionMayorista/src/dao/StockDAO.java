package dao;
import java.util.List;
import model.MovimientoStock;

public interface StockDAO {
    List<MovimientoStock> listarMovimientos();
    int siguienteId();
    void agregarMovimiento(MovimientoStock mov);
}