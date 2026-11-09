package dao;
import java.util.ArrayList;
import java.util.List;

import model.Factura;
import model.OrdenVenta;
import model.Pago;

 public class FacturaDAOMock implements FacturaDAO {

    private static final List<Factura> FACTURAS = new ArrayList<>();
    private static final List<Pago> PAGOS = new ArrayList<>();

    static {
        inicializarMock();
    }

    private static void inicializarMock() {
        OrdenVentaDAO repoOrdenes = new OrdenVentaDAOMock();
        List<OrdenVenta> ordenes = repoOrdenes.listar();

        // Enlazamos las ordenes que ya estaban marcadas como "Facturada" en el mock
        int numFactura = 101;
        for (OrdenVenta ov : ordenes) {
            if ("Facturada".equalsIgnoreCase(ov.getEstado())) {
                Factura f = new Factura(numFactura++, ov);
                FACTURAS.add(f);
            }
        }

        // Mock de un pago registrado sobre la primera factura
        // Si hay facturas generadas a partir del mock de ordenes:
        if (!FACTURAS.isEmpty()) {
            // Factura 1: Se simula como pagada
            Factura f1 = FACTURAS.get(0);
            f1.setEstado("Pagada");
            Pago p1 = new Pago(1, f1.getTotalFinal(), "Transferencia", "TRANSF-884912", f1.getOrden().getCliente(), f1);
            PAGOS.add(p1);

            // Factura 2 y siguientes: Se fuerzan en estado "PENDIENTE" para que aparezcan en el combo de cobros
            if (FACTURAS.size() > 1) {
                Factura f2 = FACTURAS.get(1);
                f2.setEstado("PENDIENTE");
            }
        }
    }

    @Override
    public List<Factura> listarFacturas() {
        return new ArrayList<>(FACTURAS);
    }

    @Override
    public List<Pago> listarPagos() {
        return new ArrayList<>(PAGOS);
    }

    @Override 
    public int siguienteNumeroFactura() {
        int max = 0;
        for (Factura f : FACTURAS) {
            max = Math.max(max, f.getNumeroFactura());
        }
        return max + 1;
    }

    @Override
    public int siguienteIdPago() {
        int max = 0;
        for (Pago p : PAGOS) {
            max = Math.max(max, p.getId());
        }
        return max + 1;
    }

    @Override
    public void agregarFactura(Factura factura) {
        factura.setEstado("PENDIENTE");
        FACTURAS.add(factura);
        if (factura.getOrden() != null) {
            factura.getOrden().setEstado("Facturada");
            factura.getOrden().getCliente().actualizarSaldo(factura.getIva());
        }
    }

    @Override
    public void agregarPago(Pago pago) {
        PAGOS.add(pago);
        // Reduce el saldo del cliente
        pago.registrarPago();
    }
}