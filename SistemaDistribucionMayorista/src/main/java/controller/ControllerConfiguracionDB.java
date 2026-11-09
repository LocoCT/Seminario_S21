package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.ConexionDB;
import util.Dialogs;
import view.ConfiguracionDBView;

public class ControllerConfiguracionDB implements ActionListener {

    private final ConfiguracionDBView vista;

    public ControllerConfiguracionDB(ConfiguracionDBView vista) {
        this.vista = vista;
    }

    public void iniciar() {
        this.vista.getBtnProbar().addActionListener(this);
        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnCancelar().addActionListener(this);
        cargarValoresActuales();
    }

    private void cargarValoresActuales() {
        String url = ConexionDB.getUrlActual();
        String host = "localhost";
        String puerto = "3306";
        String dbName = "SistemaMayoristaDB";

        // Regex para parsear jdbc:mysql://host:puerto/dbname?...
        Pattern pattern = Pattern.compile("jdbc:mysql://([^:/]+)(?::(\\d+))?/([^?]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            if (matcher.group(1) != null) host = matcher.group(1);
            if (matcher.group(2) != null) puerto = matcher.group(2);
            if (matcher.group(3) != null) dbName = matcher.group(3);
        }

        vista.setHost(host);
        vista.setPuerto(puerto);
        vista.setBaseDatos(dbName);
        vista.setUsuario(ConexionDB.getUserActual());
        vista.setClave(ConexionDB.getPasswordActual());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnProbar()) {
            probarConexion();
        } else if (e.getSource() == vista.getBtnGuardar()) {
            guardarConfiguracion();
        } else if (e.getSource() == vista.getBtnCancelar()) {
            vista.dispose();
        }
    }

    private void probarConexion() {
        String host = vista.getHost();
        String puerto = vista.getPuerto();
        String dbName = vista.getBaseDatos();
        String user = vista.getUsuario();
        String pass = new String(vista.getClave());

        if (host.isEmpty() || puerto.isEmpty() || dbName.isEmpty() || user.isEmpty()) {
            vista.mostrarMensaje("Completá todos los campos.", true);
            return;
        }

        vista.mostrarMensaje("Probando conexión...", false);
        try {
            ConexionDB.probarConexion(host, puerto, dbName, user, pass);
            vista.mostrarMensaje("¡Conexión exitosa a MySQL!", false);
            Dialogs.info(vista, "La conexión a la base de datos se estableció correctamente.", "Conexión Exitosa");
        } catch (SQLException ex) {
            vista.mostrarMensaje("Error al conectar: " + ex.getMessage(), true);
            Dialogs.error(vista, "No se pudo conectar a MySQL:\n" + ex.getMessage(), "Error de Conexión");
        }
    }

    private void guardarConfiguracion() {
        String host = vista.getHost();
        String puerto = vista.getPuerto();
        String dbName = vista.getBaseDatos();
        String user = vista.getUsuario();
        String pass = new String(vista.getClave());

        if (host.isEmpty() || puerto.isEmpty() || dbName.isEmpty() || user.isEmpty()) {
            vista.mostrarMensaje("Completá todos los campos requeridos.", true);
            return;
        }

        ConexionDB.configurar(host, puerto, dbName, user, pass);
        Dialogs.info(vista, "Configuración aplicada correctamente para la sesión actual.", "Éxito");
        vista.dispose();
    }
}
