package dao;
import java.util.List;
import model.Factura;
import model.Pago;

public interface FacturaDAO {
    List<Factura> listarFacturas();
    List<Pago> listarPagos();
    int siguienteNumeroFactura();
    int siguienteIdPago();
    void agregarFactura(Factura factura);
    void agregarPago(Pago pago);

    static String formatearNumeroFactura(int numero) {
        return String.format("FC-%06d", numero);
    }
}