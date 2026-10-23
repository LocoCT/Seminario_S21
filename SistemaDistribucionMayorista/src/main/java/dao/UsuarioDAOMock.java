package dao;
import java.util.ArrayList;
import java.util.List;
import model.Usuario;

public class UsuarioDAOMock implements UsuarioDAO {

    private final List<Usuario> usuarios = new ArrayList<>();

    public UsuarioDAOMock() {
        // Datos de prueba
        // Reemplazar por la consulta a la base de datos
        usuarios.add(new Usuario("Federico", "Catena", "1100000000", "admin@mayorista.com", "Admin", "admin123", "Administrativo", true));
        usuarios.add(new Usuario("Juan Carlos", "Fernandez", "1100000001", "ventas@mayorista.com", "Vendedor", "ventas123", "Vendedor", true));
        usuarios.add(new Usuario("Esteban", "Hernandez", "1100000002", "baja@mayorista.com", "inactivo", "1234", "Vendedor", false));
    }

    public Usuario buscarPorUsername(String username) {
        for (Usuario u : usuarios) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }
}