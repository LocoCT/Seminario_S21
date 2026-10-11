package repository;
import model.Cliente;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepositorio {

    // Temporal: reemplazar por consultas a la base de datos
    private static final List<Cliente> CLIENTES = new ArrayList<>();

    static {
        // orden: telefono, email, CUIT, razonSocial, direccion, categoria, limiteCredito, saldo
        CLIENTES.add(new Cliente("1145550101", "compras@dnorte.com", "30-71234567-1",
                "Distribuidora Norte S.A.", "Av. Mitre 1200, Avellaneda", "Mayorista", 500000.00, 184500.00));
        CLIENTES.add(new Cliente("1145550102", "contacto@elsol.com", "30-70111222-5",
                "Almacén El Sol", "Calle 25 de Mayo 340, Quilmes", "Minorista", 150000.00, 96200.00));
        CLIENTES.add(new Cliente("1145550103", "ventas@cbelgrano.com", "30-68999000-3",
                "Comercial Belgrano", "Av. Cabildo 2550, CABA", "Corporativo", 750000.50, 241800.00));
    }

    public List<Cliente> listar() {
        return new ArrayList<>(CLIENTES);
    }

    public List<Cliente> buscar(String texto) {
        String t = texto.toLowerCase().trim();
        String digitos = t.replaceAll("\\D", "");
        List<Cliente> resultado = new ArrayList<>();
        for (Cliente c : CLIENTES) {
            boolean porNombre = c.getRazonSocial().toLowerCase().contains(t);
            boolean porCuit = !digitos.isEmpty() && c.getCUIT().replaceAll("\\D", "").contains(digitos);
            if (t.isEmpty() || porNombre || porCuit) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public boolean existeCuit(String cuit) {
        String digitos = cuit.replaceAll("\\D", "");
        for (Cliente c : CLIENTES) {
            if (c.getCUIT().replaceAll("\\D", "").equals(digitos)) {
                return true;
            }
        }
        return false;
    }

    public void agregar(Cliente cliente) {
        CLIENTES.add(cliente);
    }

    public void actualizar(Cliente cliente) {
        for (int i = 0; i < CLIENTES.size(); i++) {
            if (CLIENTES.get(i).getCUIT().equals(cliente.getCUIT())) {
                CLIENTES.set(i, cliente);
                return;
            }
        }
    }
}