package model;

public abstract class Persona {
// atributos privados
  private String telefono;
  private String email;

  // Constructor 
  public Persona(String telefono, String email){
    this.telefono = telefono;
    this.email = email;
  }
  
  // Gets y Sets
  public String getTelefono(){
    return telefono;
  }

  public void setTelefono(String telefono){
    this.telefono = telefono;
  }

  public String getEmail(){
    return email;
  }

  public void setEmail(String email){
    this.email = email;
  }

  public abstract void mostrarInformacion();
}
