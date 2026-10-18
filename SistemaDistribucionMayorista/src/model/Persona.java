package model;

public abstract class Persona {
    protected String nombre;
    protected String apellido;
    protected String telefono;
    protected String email;

    // Constructor
    public Persona(String nombre, String apellido, String telefono, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
    }

    // Constructor con sobrecarga
    public Persona(String nombre, String apellido) {
        this(nombre, apellido, "", "");
    }

    public abstract void mostrarInformacion();

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}