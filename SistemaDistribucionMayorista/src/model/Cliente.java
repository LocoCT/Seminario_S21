package model;

public class Cliente extends Persona{
  private String CUIT;
  private String razonSocial;
  private String direccion;
  private String categoria;
  private double limiteCredito;
  private double saldo;


  //Constructor
  public Cliente(String telefono, String email, String CUIT, String razonSocial, String direccion, String categoria, double limiteCredito, double saldo){
    super(telefono, email);
    this.CUIT = CUIT;
    this.razonSocial = razonSocial;
    this.direccion = direccion;
    this.categoria = categoria;
    this.limiteCredito = limiteCredito;
    this.saldo = 0.0;
  }

  public void actualizarSaldo(double monto){
    this.saldo += monto;
  }

  public boolean verificarLimiteCredito(double monto){
    return (this.saldo + monto) <= this.limiteCredito;
  }

  @Override
  public void mostrarInformacion() {
    System.out.println("-- Detalle del Cliente --");
    System.out.println("Cliente: " + razonSocial);
    System.out.println("CUIT: " + CUIT);
    System.out.println("Dirección: " + direccion);
    System.out.println("Teléfono: " + getTelefono());
    System.out.println("Email: " + getEmail());
    System.out.println("Límite de Crédito: $" + limiteCredito);
    System.out.println("Saldo Actual: " + saldo);
  }

  public String getCUIT() {return CUIT;}
  public void setCUIT(String CUIT) {this.CUIT = CUIT;}
  public String getRazonSocial() {return razonSocial;}
  public void setRazonSocial(String razonSocial) {this.razonSocial = razonSocial;}
  public String getDireccion() {return direccion;}
  public void setDireccion(String direccion) {this.direccion = direccion;}
  public String getCategoria() {return categoria;}
  public void setCategoria(String categoria) {this.categoria = categoria;}
  public double getLimiteCredito() {return limiteCredito;}
  public void setLimiteCredito(double limiteCredito) {this.limiteCredito = limiteCredito;}
  public double getSaldo() {return saldo;}

}