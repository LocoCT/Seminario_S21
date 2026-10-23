package dao;
import java.util.List;
import model.OrdenVenta;

public interface OrdenVentaDAO {
    List<OrdenVenta> listar();
    int siguienteNumero();
    void agregar(OrdenVenta orden);

    static String formatearNumero(int numero) {
        return String.format("OV-%06d", numero);
    }
}