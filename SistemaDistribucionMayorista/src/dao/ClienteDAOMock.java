package dao;
import model.Cliente;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOMock implements ClienteDAO {

    // Temporal: reemplaza por consultas a la base de datos
    private static final List<Cliente> CLIENTES = new ArrayList<>();
    public List<String> listarCategorias(){
        return new ArrayList<>(java.util.Arrays.asList("Minorista", "Mayorista", "Coporativo"));
    }

    static {
        // orden: telefono, email, CUIT, razonSocial, direccion, categoria, limiteCredito, saldo
        CLIENTES.add(new Cliente("1145550101", "compras@dnorte.com", "30-71234567-1",
        "Distribuidora Norte S.A.", "Av. Mitre 1200, Avellaneda", "Mayorista",
                new BigDecimal("500000.00"), new BigDecimal("184500.00")));
        CLIENTES.add(new Cliente("1145550102", "contacto@elsol.com", "30-70111222-5",
        "Almacén El Sol", "Calle 25 de Mayo 340, Quilmes", "Minorista",
                new BigDecimal("150000.00"), new BigDecimal("96200.00")));
        CLIENTES.add(new Cliente("1145550103", "ventas@cbelgrano.com", "30-68999000-3",
        "Comercial Belgrano", "Av. Cabildo 2550, CABA", "Corporativo",
                new BigDecimal("750000.50"), new BigDecimal("241800.00")));
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