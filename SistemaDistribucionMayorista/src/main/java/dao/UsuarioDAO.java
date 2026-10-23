package dao;
import model.Usuario;

public interface UsuarioDAO {
    Usuario buscarPorUsername(String username);
}