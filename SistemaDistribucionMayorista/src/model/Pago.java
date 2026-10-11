package model;
import java.util.Date;

public class Pago {
  
  private int id;
  private Date fechaPago;
  private double monto;
  private String medioPago; // "Efectivo" "Tarjeta" "Transferencia"
  private String comprobanteReferencia;
  private Cliente cliente;
  private Factura factura;

  public Pago(int id, double monto, String medioPago, String comprobanteReferencia, Cliente cliente, Factura factura) {
    this.id = id;
    this.fechaPago = new Date();
    this.monto = monto;
    this.medioPago = medioPago;
    this.comprobanteReferencia = comprobanteReferencia;
    this.cliente = cliente;
    this.factura = factura;
  }

  public void registrarPago() {
    this.cliente.actualizarSaldo(-this.monto);
    System.out.println("Pago registrado con exito. Nuevo saldo del cliente: $" + this.cliente.getSaldo());
  }

  public int getId() {
    return id;
  }

  public Date getFechaPago() {
    return fechaPago;
  }

  public double getMonto() {
    return monto;
  }

  public String getMedioPago() {
    return medioPago;
  }

  public String getComprobanteReferencia() {
    return comprobanteReferencia;
  }

  public Cliente getCliente() {
    return cliente;
  }

  public Factura getFactura() {
    return factura;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setMonto(double monto) {
    this.monto = monto;
  }

  public void setMedioPago(String medioPago) {
    this.medioPago = medioPago;
  }

  public void setComprobanteReferencia(String comprobanteReferencia) {
    this.comprobanteReferencia = comprobanteReferencia;
  }

  public void setCliente(Cliente cliente) {
    this.cliente = cliente;
  }

  public void setFactura(Factura factura) {
    this.factura = factura;
  }

}
