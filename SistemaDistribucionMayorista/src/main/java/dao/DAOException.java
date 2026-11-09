package dao;

// Excepcion propia de la capa de datos.
// Envuelve el SQLException para que los controllers no dependan de java.sql.
// Es "unchecked" (RuntimeException) para no tener que agregar "throws" a todas las interfaces DAO;
// los controllers la atrapan y muestran un mensaje con Dialogs.error().
public class DAOException extends RuntimeException {

    public DAOException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}