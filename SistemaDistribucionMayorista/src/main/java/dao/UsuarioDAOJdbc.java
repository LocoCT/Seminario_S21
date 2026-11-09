package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import config.ConexionDB;
import model.Usuario;


public class UsuarioDAOJdbc implements UsuarioDAO {

    // El ? se completa con setString para nunca concatenar el texto del usuario en SQL (evita SQL Injection).
    private static final String SQL_BUSCAR =
            "SELECT username, contrasena, rol, estado FROM Usuario WHERE username = ?";

    @Override
    public Usuario buscarPorUsername(String username) {
        // try-with-resources - Connection, PreparedStatement y ResultSet se cierran solos
        try (Connection con = ConexionDB.obtener();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String user = rs.getString("username");
                
                return new Usuario(
                        user, "", user,
                        rs.getString("contrasena"),
                        rs.getString("rol"),
                        "A".equals(rs.getString("estado")));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el usuario '" + username + "'", e);
        }
    }
}