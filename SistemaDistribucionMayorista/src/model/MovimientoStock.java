package model;

import java.util.Date;

public class MovimientoStock {

    private int id;
    private Date fechaHora;
    private int cantidad;
    private String tipoMovimiento;
    private String motivo;
    private Producto producto;
    private Deposito deposito;
    private Usuario responsable;

    // Constructor 
    public MovimientoStock(int id, Date fechaHora, int cantidad, String tipoMovimiento, String motivo, Producto producto, Deposito deposito, Usuario responsable) {
        this.id = id;
        this.fechaHora = (fechaHora != null) ? fechaHora : new Date();
        this.cantidad = cantidad;
        this.tipoMovimiento = tipoMovimiento;
        this.motivo = motivo;
        this.producto = producto;
        this.deposito = deposito;
        this.responsable = responsable;
    }

    // Constructor con sobrecarga
    public MovimientoStock(int id, int cantidad, String tipoMovimiento, String motivo, Producto producto, Deposito deposito, Usuario responsable) {
        this(id, new Date(), cantidad, tipoMovimiento, motivo, producto, deposito, responsable);
    }

    public void registrarMovimientoStock() {
        if (this.producto != null) {
            String tipo = (this.tipoMovimiento != null) ? this.tipoMovimiento.toLowerCase() : "";

            if (tipo.contains("ingreso") || tipo.contains("compra")) {
                this.producto.setStockActual(this.producto.getStockActual() + this.cantidad);
            } else if (tipo.contains("egreso") || tipo.contains("venta")) {
                this.producto.setStockActual(Math.max(0, this.producto.getStockActual() - this.cantidad));
            } else if (tipo.contains("ajuste")) {
                this.producto.setStockActual(this.cantidad);
            }
        }
        System.out.println("Movimiento de stock N° " + id + " registrado con éxito.");
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getFechaHora() { return fechaHora; }
    public void setFechaHora(Date fechaHora) { this.fechaHora = fechaHora; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Deposito getDeposito() { return deposito; }
    public void setDeposito(Deposito deposito) { this.deposito = deposito; }

    public Usuario getResponsable() { return responsable; }
    public void setResponsable(Usuario responsable) { this.responsable = responsable; }
}