package model;
import java.math.BigDecimal;
import java.util.Date;

public class Pago {
  
  private int id;
  private Date fechaPago;
  private BigDecimal monto;
  private String medioPago;
  private String comprobanteReferencia;
  private Cliente cliente;
  private Factura factura;

  public Pago(int id, BigDecimal monto, String medioPago, String comprobanteReferencia, Cliente cliente, Factura factura) {
    this.id = id;
    this.fechaPago = new Date();
    this.monto = monto;
    this.medioPago = medioPago;
    this.comprobanteReferencia = comprobanteReferencia;
    this.cliente = cliente;
    this.factura = factura;
  }

  public void registrarPago() {
    BigDecimal nuevoSaldo = this.cliente.getSaldo().subtract(this.monto).max(BigDecimal.ZERO);
    this.cliente.setSaldo(nuevoSaldo);
    System.out.println("Pago registrado con exito. Nuevo saldo del cliente: $" + nuevoSaldo);
  }

  public int getId() { return id; }
  public void setId(int id) {this.id = id;}

  public Date getFechaPago() { return fechaPago; }

  public BigDecimal getMonto() { return monto;}
  public void setMonto(BigDecimal monto) {this.monto = monto;}

  public String getMedioPago() {return medioPago; }
  public void setMedioPago(String medioPago) { this.medioPago = medioPago;}

  public String getComprobanteReferencia() {return comprobanteReferencia;}
  public void setComprobanteReferencia(String comprobanteReferencia) { this.comprobanteReferencia = comprobanteReferencia; }

  public Cliente getCliente() {return cliente; }
  public void setCliente(Cliente cliente) {this.cliente = cliente;}

  public Factura getFactura() {return factura;}
  public void setFactura(Factura factura) {this.factura = factura;}

}
