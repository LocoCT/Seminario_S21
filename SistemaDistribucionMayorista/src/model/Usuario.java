package model;

public class Usuario extends Persona {
    private String username;
    private String contrasena;
    private String rol;
    private boolean estado;

    // Constructor
    public Usuario(String telefono, String email, String username, String contrasena, String rol, boolean estado) {
        super(telefono, email); 
        this.username = username;
        this.contrasena = contrasena;
        this.rol = rol;
        this.estado = estado;
    }
    
    public boolean iniciarSesion(String username, String contrasena) {
        return this.username.equals(username) && this.contrasena.equals(contrasena) && this.estado;
    }

    @Override
    public void mostrarInformacion() {
        System.out.println("Usuario: " + username + ", Rol: " + rol + ", Estado: " + (estado ? "Activo" : "Inactivo"));
    }

    // Get y Set
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
}