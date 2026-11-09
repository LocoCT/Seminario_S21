package dao;
import java.util.List;

import model.Cliente;
import model.Usuario;

public interface ClienteDAO {
    List<Cliente> listar();
    List<Cliente> buscar(String texto);
    boolean existeCuit(String cuit);
    void agregar(Cliente cliente, Usuario usuarioAlta);
    void actualizar(Cliente cliente);
    List<String> listarCategorias();
}