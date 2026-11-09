package config;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Punto unico de acceso a la base de datos.
// Lee url, usuario y clave de src/main/resources/db.properties
// Devuelve una conexion nueva en cada llamada: asi cada DAO la abre con try-with-resources
// y la cierra sola, y una transaccion (setAutoCommit(false)) no afecta a otros DAO.
public final class ConexionDB {

    private static final Properties PROPS = new Properties();
    private static String urlPersonalizada = null;
    private static String userPersonalizado = null;
    private static String passwordPersonalizada = null;

    private ConexionDB() {}

    // Carga el archivo de configuracion una sola vez
    private static synchronized void cargarPropiedades() throws SQLException {
        if (!PROPS.isEmpty()) {
            return;
        }
        try (InputStream in = ConexionDB.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new SQLException("No se encontro db.properties en src/main/resources ");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new SQLException("No se pudo leer db.properties", e);
        }
    }

    public static synchronized String getUrlActual() {
        if (urlPersonalizada != null) return urlPersonalizada;
        try { cargarPropiedades(); } catch (SQLException ignored) {}
        return System.getProperty("db.url", PROPS.getProperty("db.url", "jdbc:mysql://localhost:3306/SistemaMayoristaDB"));
    }

    public static synchronized String getUserActual() {
        if (userPersonalizado != null) return userPersonalizado;
        try { cargarPropiedades(); } catch (SQLException ignored) {}
        return System.getProperty("db.user", PROPS.getProperty("db.user", "root"));
    }

    public static synchronized String getPasswordActual() {
        if (passwordPersonalizada != null) return passwordPersonalizada;
        try { cargarPropiedades(); } catch (SQLException ignored) {}
        return System.getProperty("db.password", PROPS.getProperty("db.password", ""));
    }

    // Configura nuevos valores en tiempo de ejecucion (para el profesor o tester)
    public static synchronized void configurar(String host, String puerto, String dbName, String user, String password) {
        String url = String.format("jdbc:mysql://%s:%s/%s?sslMode=DISABLED&allowPublicKeyRetrieval=true&serverTimezone=America/Argentina/Buenos_Aires",
                host.trim(), puerto.trim(), dbName.trim());
        urlPersonalizada = url;
        userPersonalizado = user.trim();
        passwordPersonalizada = password;
    }

    // Prueba la conexion con los parametros indicados sin alterar la configuracion actual
    public static void probarConexion(String host, String puerto, String dbName, String user, String password) throws SQLException {
        String url = String.format("jdbc:mysql://%s:%s/%s?sslMode=DISABLED&allowPublicKeyRetrieval=true&serverTimezone=America/Argentina/Buenos_Aires",
                host.trim(), puerto.trim(), dbName.trim());
        try (Connection conn = DriverManager.getConnection(url, user.trim(), password)) {
            if (!conn.isValid(3)) {
                throw new SQLException("No se pudo validar la conexión a MySQL.");
            }
        }
    }

    // Abre una conexion nueva. Quien la pida es responsable de cerrarla (try-with-resources).
    public static Connection obtener() throws SQLException {
        cargarPropiedades();
        String url = (urlPersonalizada != null) ? urlPersonalizada : System.getProperty("db.url", PROPS.getProperty("db.url"));
        String user = (userPersonalizado != null) ? userPersonalizado : System.getProperty("db.user", PROPS.getProperty("db.user"));
        String pass = (passwordPersonalizada != null) ? passwordPersonalizada : System.getProperty("db.password", PROPS.getProperty("db.password"));
        return DriverManager.getConnection(url, user, pass);
    }
}
