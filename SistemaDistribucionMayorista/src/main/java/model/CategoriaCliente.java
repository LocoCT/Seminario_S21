package model;

public class CategoriaCliente {

  private String nombre;
  private double porcentajeDescuento;

  public CategoriaCliente(String nombre, double porcentajeDescuento) {
    this.nombre = nombre;
    this.porcentajeDescuento = porcentajeDescuento;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public double getPorcentajeDescuento() {
    return porcentajeDescuento;
  }

  public void setPorcentajeDescuento(double porcentajeDescuento) {
    this.porcentajeDescuento = porcentajeDescuento;
  }  
}
