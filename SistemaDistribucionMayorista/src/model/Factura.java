package model;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class Factura {
  
  public static final BigDecimal ALICUOTA_IVA = new BigDecimal ("0.21");
  private int numeroFactura;
  private Date fechaEmision;
  private BigDecimal subtotal;
  private BigDecimal iva;
  private BigDecimal totalFinal;
  private String estado;
  private OrdenVenta orden;

  public Factura(int numeroFactura, OrdenVenta orden) {
    this.numeroFactura = numeroFactura;
    this.fechaEmision = new Date();
    this.orden = orden;
    this.subtotal = orden.calcularTotal();
    this.iva = calcularIva(this.subtotal);
    this.totalFinal = this.subtotal.add(this.iva);
    this.estado = "Emitida";
  }

  public static BigDecimal calcularIva (BigDecimal subtotal){
    return subtotal.multiply(ALICUOTA_IVA).setScale(2, RoundingMode.HALF_UP);
  }

  public int getNumeroFactura() { return numeroFactura;}
  public Date getFechaEmision() { return fechaEmision; }
  public BigDecimal getSubtotal() { return subtotal;}
  public BigDecimal getIva() { return iva;}
  public BigDecimal getTotalFinal() { return totalFinal; }
  public String getEstado() { return estado; }
  public OrdenVenta getOrden() { return orden; }
  public void setEstado(String estado) { this.estado = estado;}

}
