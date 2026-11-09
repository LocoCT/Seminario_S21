package dao;
import java.util.List;

import model.Deposito;
import model.MovimientoStock;

public interface StockDAO {
    List<MovimientoStock> listarMovimientos();
    List<Deposito> listarDepositos();
    int siguienteId();
    void agregarMovimiento(MovimientoStock mov);
}