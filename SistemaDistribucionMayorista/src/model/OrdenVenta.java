package model;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class OrdenVenta {

  private int numeroOrden;
  private Date fechaEmision;
  private String estado;
  private BigDecimal subtotal;
  private BigDecimal descuentoAplicado;
  private BigDecimal total;
  private Cliente cliente;
  private Usuario usuario;
  private List<DetalleOrdenVenta> items;

  public OrdenVenta(int numeroOrden, Cliente cliente, Usuario usuario) {
    this.numeroOrden = numeroOrden;
    this.fechaEmision = new Date();
    this.estado = "Pendiente";
    this.cliente = cliente;
    this.usuario = usuario;
    this.items = new ArrayList<>();
    this.subtotal = BigDecimal.ZERO;
    this.descuentoAplicado = BigDecimal.ZERO;
    this.total = BigDecimal.ZERO;
  }
  
  public void agregarItem(DetalleOrdenVenta item) {
    this.items.add(item);
    calcularTotal();
  }

  public void cambiarEstado(String nuevoEstado) {
    this.estado = nuevoEstado;
  }

  public BigDecimal calcularTotal() {
    this.subtotal = BigDecimal.ZERO;
    for (DetalleOrdenVenta item : items) {
      this.subtotal = this.subtotal.add(item.calcularSubtotal());
    }
    this.total = this.subtotal.subtract(this.descuentoAplicado).max(BigDecimal.ZERO);
    return this.total;
}

  public int getNumeroOrden() {return numeroOrden;}
  public void setNumeroOrden(int numeroOrden) {this.numeroOrden = numeroOrden;}

  public Date getFechaEmision() {return fechaEmision;}
  public void setFechaEmision(Date fechaEmision) {this.fechaEmision = fechaEmision;}

  public String getEstado() { return estado; }
  public void setEstado(String estado) {this.estado = estado;}

  public BigDecimal getSubtotal() { return subtotal; }
  public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

  public BigDecimal getDescuentoAplicado() {return descuentoAplicado; }
  public void setDescuentoAplicado(BigDecimal descuentoAplicado) {this.descuentoAplicado = descuentoAplicado;}

  public BigDecimal getTotal() {return total;}
  public void setTotal(BigDecimal total) {this.total = total;}

  public Cliente getCliente() { return cliente; }
  public void setCliente(Cliente cliente) { this.cliente = cliente; }

  public Usuario getUsuario() { return usuario; }
  public void setUsuario(Usuario usuario) { this.usuario = usuario; }
  
  public List<DetalleOrdenVenta> getItems() { return items;}
  public void setItems(List<DetalleOrdenVenta> items) { this.items = items; }
}
