package dao;

public final class DAOFactory {

    private DAOFactory() {}

    public static ClienteDAO clientes(){ return new ClienteDAOJdbc(); }
    public static ProductoDAO productos(){ return new ProductoDAOJdbc(); }
    public static StockDAO stock(){ return new StockDAOJdbc(); }
    public static OrdenVentaDAO ordenes(){ return new OrdenVentaDAOJdbc(); }
    public static FacturaDAO facturas(){ return new FacturaDAOJdbc(); }
    public static UsuarioDAO usuarios(){ return new UsuarioDAOJdbc(); }
}