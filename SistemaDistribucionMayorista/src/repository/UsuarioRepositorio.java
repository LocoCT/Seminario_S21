package repository;

import java.util.ArrayList;
import java.util.List;

import model.Usuario;

public class UsuarioRepositorio {

    private final List<Usuario> usuarios = new ArrayList<>();

    public UsuarioRepositorio() {
        // Datos de prueba. TODO: reemplazar por la consulta a la base de datos
        usuarios.add(new Usuario("1100000000", "admin@mayorista.com", "Admin", "admin123", "Administrativo", true));
        usuarios.add(new Usuario("1100000001", "ventas@mayorista.com", "Vendedor", "ventas123", "Vendedor", true));
        usuarios.add(new Usuario("1100000002", "baja@mayorista.com", "inactivo", "1234", "Vendedor", false));
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