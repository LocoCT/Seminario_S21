package model;
import java.util.Date;

public class MovimientoStock {

  private int id;
  private Date fechaHora;
  private int cantidad;
  private String tipoMovimiento; // "Ingreso" "Egreso"
  private String motivo;
  private Producto producto;
  private Deposito deposito;
  private Usuario responsable;
  
public MovimientoStock(int id, int cantidad, String tipoMovimiento, String motivo, Producto producto, Deposito deposito, Usuario responsable) {
    this.id = id;
    this.fechaHora = new Date();
    this.cantidad = cantidad;
    this.tipoMovimiento = tipoMovimiento;
    this.motivo = motivo;
    this.producto = producto;
    this.deposito = deposito;
    this.responsable = responsable;
  }

public void RegistrarMovimientoStock(){
  this.producto.actualizarStock(this.cantidad, this.tipoMovimiento);
  System.out.println("Movimiento de stock registrado con exito"); 
}
  public int getId() {
    return id;
  }

  public Date getFechaHora() {
    return fechaHora;
  }

  public int getCantidad() {
    return cantidad;
  }

  public String getTipoMovimiento() {
    return tipoMovimiento;
  }

  public Producto getProducto() {
    return producto;
  }

  public Deposito getDeposito() {
    return deposito;
  }

  public Usuario getResponsable() {
    return responsable;
  }

  public String getMotivo() {
    return motivo;
  }
  
}
