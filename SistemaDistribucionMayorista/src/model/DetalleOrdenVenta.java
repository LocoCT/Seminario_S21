package model;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class DetalleOrdenVenta {

  private int cantidad;
  private BigDecimal precioUnitario;
  private BigDecimal porcentajeDescuento;
  private BigDecimal subtotal;
  private Producto producto;

  public DetalleOrdenVenta(int cantidad, Producto producto, BigDecimal porcentajeDescuento) {
    this.cantidad = cantidad;
    this.precioUnitario = producto.getPrecioBase();
    this.porcentajeDescuento = porcentajeDescuento;
    this.producto = producto;
    this.subtotal = calcularSubtotal();
  }

  public BigDecimal calcularSubtotal() {
    BigDecimal descuento = this.precioUnitario
      .multiply(this.porcentajeDescuento)
      .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    this.subtotal = this.precioUnitario.subtract(descuento)
      .multiply(BigDecimal.valueOf(this.cantidad))
      .setScale(2, RoundingMode.HALF_UP);
    return this.subtotal;
  }

  public int getCantidad() {return cantidad;}
  public BigDecimal getPrecioUnitario() {return precioUnitario;}
  public BigDecimal getPorcentajeDescuento() {return porcentajeDescuento;}
  public BigDecimal getSubtotal() {return subtotal;}
  public Producto getProducto() {return producto;}

  public void setCantidad(int cantidad) {
    this.cantidad = cantidad;
    calcularSubtotal();
  }

  public void setPrecioUnitario(BigDecimal precioUnitario) {
    this.precioUnitario = precioUnitario;
    calcularSubtotal();
  }

  public void setPorcentajeDescuento(BigDecimal porcentajeDescuento) {
    this.porcentajeDescuento = porcentajeDescuento;
    calcularSubtotal();
  }

  public void setSubtotal(BigDecimal subtotal) {
    this.subtotal = subtotal;
  }

  public void setProducto(Producto producto) {
    this.producto = producto;
  }
}
