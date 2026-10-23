package dao;
import model.CategoriaProducto;
import model.Producto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
 
public class ProductoDAOMock implements ProductoDAO {
 
  // Temporal: reemplazar por consultas a la base de datos
  // Son static para que todas las pantallas compartan los mismos productos y su stock
  private static final List<CategoriaProducto> CATEGORIAS = new ArrayList<>();
  private static final List<Producto> PRODUCTOS = new ArrayList<>();
 
  static {
    CategoriaProducto bebidas = new CategoriaProducto("Bebidas", "Bebidas analcohólicas y refrescos");
    CategoriaProducto almacen = new CategoriaProducto("Almacén", "Productos comestibles secos");
    CategoriaProducto limpieza = new CategoriaProducto("Limpieza", "Artículos de higiene y limpieza");
    CATEGORIAS.add(bebidas);
    CATEGORIAS.add(almacen);
    CATEGORIAS.add(limpieza);
 
    // orden: codigo, descripcion, categoria, costoCompra, precioBase, stockActual, stockMinimo
    PRODUCTOS.add(new Producto("PROD-001", "Agua Mineral 500ml", bebidas, new BigDecimal("450.00"), new BigDecimal("850.00"), 120, 20));
    PRODUCTOS.add(new Producto("PROD-002", "Arroz Integral 1kg", almacen, new BigDecimal("890.00"), new BigDecimal("1450.00"), 45, 10));
    PRODUCTOS.add(new Producto("PROD-003", "Detergente Multiuso 750ml", limpieza, new BigDecimal("620.00"), new BigDecimal("1100.00"), 8, 15));
    PRODUCTOS.add(new Producto("PROD-004", "Aceite de Girasol 1.5L", almacen, new BigDecimal("1200.00"), new BigDecimal("1980.00"), 60, 12));
  }
 
  // Devuelve una copia de la lista, pero los Producto son los mismos objetos
  // (por eso un cambio de stock se ve en todas las pantallas).
  public List<Producto> listar() {
    return new ArrayList<>(PRODUCTOS);
  }
 
  public List<Producto> buscar(String texto) {
    String t = (texto == null) ? "" : texto.toLowerCase().trim();
    List<Producto> resultado = new ArrayList<>();
    for (Producto p : PRODUCTOS) {
      if (t.isEmpty()
          || p.getCodigo().toLowerCase().contains(t)
          || p.getDescripcion().toLowerCase().contains(t)) {
        resultado.add(p);
      }
    }
    return resultado;
  }
 
  public Producto buscarPorCodigo(String codigo) {
    for (Producto p : PRODUCTOS) {
      if (p.getCodigo().equalsIgnoreCase(codigo)) {
        return p;
      }
    }
    return null;
  }
 
  public boolean existeCodigo(String codigo) {
    return buscarPorCodigo(codigo) != null;
  }
 
  // Siguiente codigo libre: toma el ultimo numero usado + 1 
  public String siguienteCodigo() {
    int max = 0;
    for (Producto p : PRODUCTOS) {
      String cod = p.getCodigo();
      if (cod.toUpperCase().startsWith("PROD-")) {
        try {
          max = Math.max(max, Integer.parseInt(cod.substring(5)));
        } catch (NumberFormatException e) {
          // codigo con otro formato se ignora
        }
      }
    }
    return String.format("PROD-%03d", max + 1);
  }
 
  public void agregar(Producto producto) {
    PRODUCTOS.add(producto);
  }
 
  public void actualizar(Producto producto) {
    for (int i = 0; i < PRODUCTOS.size(); i++) {
      if (PRODUCTOS.get(i).getCodigo().equalsIgnoreCase(producto.getCodigo())) {
        PRODUCTOS.set(i, producto);
        return;
      }
    }
  }
 
  // Categorias
  public List<CategoriaProducto> listarCategorias() {
    return new ArrayList<>(CATEGORIAS);
  }
 
  // Devuelve la categoria existente con ese nombre (o null si no existe)
  public CategoriaProducto buscarCategoria(String nombre) {
    for (CategoriaProducto c : CATEGORIAS) {
      if (c.getNombre().equalsIgnoreCase(nombre)) {
        return c;
      }
    }
    return null;
  }
}