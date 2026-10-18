package model;
import java.math.BigDecimal;

public class Cliente extends Persona{
  private String CUIT;
  private String razonSocial;
  private String direccion;
  private String categoria;
  private BigDecimal limiteCredito;
  private BigDecimal saldo;


  //Constructor
  public Cliente(String telefono, String email, String CUIT, String razonSocial, String direccion, String categoria, BigDecimal limiteCredito, BigDecimal saldo){
    super(razonSocial, "", telefono, email);
    this.CUIT = CUIT;
    this.razonSocial = razonSocial;
    this.direccion = direccion;
    this.categoria = categoria;
    this.limiteCredito = limiteCredito;
    this.saldo = saldo;
  }

  public void actualizarSaldo(BigDecimal monto){
    this.saldo = this.saldo.add(monto);
  }

  public boolean verificarLimiteCredito(BigDecimal monto){
    return this.saldo.add(monto).compareTo(this.limiteCredito) <= 0;
  }

  // Credito y limite disponible 
  public BigDecimal getCreditoDisponible(){
    return this.limiteCredito.subtract(this.saldo);
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

  public BigDecimal getLimiteCredito() {return limiteCredito;}
  public void setLimiteCredito(BigDecimal limiteCredito) {this.limiteCredito = limiteCredito;}
  
  public BigDecimal getSaldo() {return saldo;}
  public void setSaldo(BigDecimal saldo) {this.saldo = saldo;}
}