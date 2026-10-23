package dao;

import java.util.List;
import model.CategoriaProducto;
import model.Producto;

public interface ProductoDAO {
    List<Producto> listar();
    List<Producto> buscar(String texto);
    Producto buscarPorCodigo(String codigo);
    boolean existeCodigo(String codigo);
    String siguienteCodigo();
    void agregar(Producto producto);
    void actualizar(Producto producto);
    List<CategoriaProducto> listarCategorias();
    CategoriaProducto buscarCategoria(String nombre);
}