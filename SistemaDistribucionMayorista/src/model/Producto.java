package model;
import java.math.BigDecimal;

public class Producto {

  private String codigo;
  private String descripcion;
  private BigDecimal precioBase;
  private BigDecimal costoCompra;
  private int stockActual;
  private int stockMinimo;
  private CategoriaProducto categoria;
  
    // Constructor
    public Producto(String codigo, String descripcion, CategoriaProducto categoria, BigDecimal costoCompra, BigDecimal precioBase, int stockActual, int stockMinimo) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.costoCompra = costoCompra;
        this.precioBase = precioBase;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
    }

  public void actualizarStock(int cantidad, String tipoMovimiento){
    if (tipoMovimiento.equalsIgnoreCase("Ingreso")){
      this.stockActual += cantidad;
    } else if (tipoMovimiento.equalsIgnoreCase("Egreso")){
      this.stockActual -= cantidad;
    } else {
      System.out.println("Tipo de movimiento inválido. Use 'Ingreso' o 'Egreso'.");
    }
  }

  public boolean verificarDisponibilidad(int cantidad) {
    return this.stockActual >= cantidad;
  }

  public String getCodigo() {return codigo;}
  public void setCodigo(String codigo) {this.codigo = codigo;}

  public String getDescripcion() {return descripcion;}
  public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

  public BigDecimal getPrecioBase() {return precioBase;}
  public void setPrecioBase(BigDecimal precioBase) {this.precioBase = precioBase;}

  public BigDecimal getCostoCompra() {return costoCompra;}
  public void setCostoCompra(BigDecimal costoCompra) {this.costoCompra = costoCompra;}

  public int getStockActual() {return stockActual;}
  public void setStockActual(int stockActual) {this.stockActual = stockActual;}

  public int getStockMinimo() {return stockMinimo;}
  public void setStockMinimo(int stockMinimo) {this.stockMinimo = stockMinimo;}

  public CategoriaProducto getCategoria() {return categoria;}
  public void setCategoria(CategoriaProducto categoria) {this.categoria = categoria;}
}