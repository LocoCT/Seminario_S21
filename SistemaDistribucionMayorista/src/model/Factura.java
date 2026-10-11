package model;
import java.util.Date;

public class Factura {
  
  private int numeroFactura;
  private Date fechaEmision;
  private double subtotal;
  private double iva;
  private double totalFinal;
  private String estado;
  private OrdenVenta orden;

  public Factura(int numeroFactura, OrdenVenta orden) {
    this.numeroFactura = numeroFactura;
    this.fechaEmision = new Date();
    this.orden = orden;
    this.subtotal = orden.calcularTotal();
    this.iva = this.subtotal * 0.21;
    this.totalFinal = this.subtotal + this.iva;
    this.estado = "Emitida";
  }

  public int getNumeroFactura() {
    return numeroFactura;
  }

  public Date getFechaEmision() {
    return fechaEmision;
  }

  public double getSubtotal() {
    return subtotal;
  }

  public double getIva() {
    return iva;
  }

  public double getTotalFinal() {
    return totalFinal;
  }

  public String getEstado() {
    return estado;
  }

  public OrdenVenta getOrden() {
    return orden;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

}
