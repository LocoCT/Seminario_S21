package dao;

// Para pasar a base de datos, se cambia el "new ...Mock()" por "new ...Jdbc()".
public final class DAOFactory {

    private DAOFactory() {}

    public static ClienteDAO clientes(){ return new ClienteDAOMock(); }
    public static ProductoDAO productos(){ return new ProductoDAOMock(); }
    public static StockDAO stock(){ return new StockDAOMock(); }
    public static OrdenVentaDAO ordenes(){ return new OrdenVentaDAOMock(); }
    public static FacturaDAO facturas(){ return new FacturaDAOMock(); }
    public static UsuarioDAO usuarios(){ return new UsuarioDAOMock(); }
}