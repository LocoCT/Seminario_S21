package model;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class OrdenVenta {

  private int numeroOrden;
  private Date fechaEmision;
  private String estado;
  private double subtotal;
  private double descuentoAplicado;
  private double total;
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
    this.subtotal = 0.0;
    this.descuentoAplicado = 0.0;
    this.total = 0.0;
  }
  
  public void agregarItem(DetalleOrdenVenta item) {
    this.items.add(item);
    calcularTotal();
  }

  public void cambiarEstado(String nuevoEstado) {
    this.estado = nuevoEstado;
  }

  public double calcularTotal() {
    this.subtotal = 0;

    for (DetalleOrdenVenta item : items) {
      this.subtotal += item.calcularSubtotal();
    }
    this.total = this.subtotal;
    return this.total;
    
  }

  // Get y Set
  public int getNumeroOrden() {
    return numeroOrden;
  }
  
  public void setNumeroOrden(int numeroOrden) {
    this.numeroOrden = numeroOrden;
  }

  public Date getFechaEmision() {
    return fechaEmision;
  }

  public void setFechaEmision(Date fechaEmision) {
    this.fechaEmision = fechaEmision;
  }

  public String getEstado() {
    return estado;
  }

  public void setEstado(String estado) {
    this.estado = estado;
  }

  public double getSubtotal() {
    return subtotal;
  }

  public void setSubtotal(double subtotal) {
    this.subtotal = subtotal;
  }

  public double getDescuentoAplicado() {
    return descuentoAplicado;
  }

  public void setDescuentoAplicado(double descuentoAplicado) {
    this.descuentoAplicado = descuentoAplicado;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  public Cliente getCliente() {
    return cliente;
  }

  public void setCliente(Cliente cliente) {
    this.cliente = cliente;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public List<DetalleOrdenVenta> getItems() {
    return items;
  }

  public void setItems(List<DetalleOrdenVenta> items) {
    this.items = items;
  }
}
