package model;

public class Deposito {

  private String nombre;
  private String ubicacion;

  public Deposito(String nombre, String ubicacion) {
    this.nombre = nombre;
    this.ubicacion = ubicacion;
  }

  public String getNombre() {
    return nombre;
  }

  public String getUbicacion() {
    return ubicacion;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public void setUbicacion(String ubicacion) {
    this.ubicacion = ubicacion;
  }
  
}
