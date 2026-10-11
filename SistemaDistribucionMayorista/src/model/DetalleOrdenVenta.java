package model;

public class DetalleOrdenVenta {

  private int cantidad;
  private double precioUnitario;
  private double porcentajeDescuento;
  private double subtotal;
  private Producto producto;

  public DetalleOrdenVenta(int cantidad, Producto producto, double porcentajeDescuento) {
    this.cantidad = cantidad;
    this.precioUnitario = producto.getPrecioBase();
    this.porcentajeDescuento = porcentajeDescuento;
    this.producto = producto;
    this.subtotal = calcularSubtotal();
  }

  public double calcularSubtotal() {
    double descuento = this.precioUnitario * (this.porcentajeDescuento / 100);
    return (this.precioUnitario - descuento) * this.cantidad;
  }

  public int getCantidad() {
    return cantidad;
  }

  public double getPrecioUnitario() {
    return precioUnitario;
  }

  public double getPorcentajeDescuento() {
    return porcentajeDescuento;
  }

  public double getSubtotal() {
    return subtotal;
  }

  public Producto getProducto() {
    return producto;
  }

  public void setCantidad(int cantidad) {
    this.cantidad = cantidad;
  }

  public void setPrecioUnitario(double precioUnitario) {
    this.precioUnitario = precioUnitario;
  }

  public void setPorcentajeDescuento(double porcentajeDescuento) {
    this.porcentajeDescuento = porcentajeDescuento;
  }

  public void setSubtotal(double subtotal) {
    this.subtotal = subtotal;
  }

  public void setProducto(Producto producto) {
    this.producto = producto;
  }
}
