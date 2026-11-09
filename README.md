# Sistema de Distribución Mayorista

Aplicación de escritorio (Java + Swing) para la gestión de una distribuidora mayorista:
clientes, productos, stock en depósitos, órdenes de venta, facturación y cobros.
Los datos se guardan en una base MySQL, a la que se accede por JDBC.

## Requisitos

- JDK 21
- Maven 3.8 o superior
- MySQL 8 corriendo en `localhost:3306`

## Puesta en marcha

**1. Crear la base de datos con los datos de prueba**

Abrir una terminal en la carpeta raíz del repositorio, entrar al cliente de MySQL y ejecutar el script:

```
mysql -u root -p
```

```sql
source Database/schema.sql
```

El script borra y vuelve a crear la base `SistemaMayoristaDB`, así que se puede ejecutar todas las veces
que haga falta para volver a los datos iniciales. (Si se usa PowerShell, no sirve la forma
`mysql ... < schema.sql`; usar `source` como se indica arriba.)

**2. Revisar la conexión**

El archivo `SistemaDistribucionMayorista/src/main/resources/db.properties` usa por defecto el usuario
`root` con clave vacía. Si el MySQL local tiene otra clave, se puede editar ese archivo o pasar los
datos al ejecutar (con las comillas, para que funcione también en PowerShell):

```
mvn compile exec:java "-Ddb.user=root" "-Ddb.password=TU_CLAVE"
```

**3. Ejecutar la aplicación**

```
cd SistemaDistribucionMayorista
mvn compile exec:java
```

## Usuarios de prueba

| Usuario  | Contraseña | Rol           |
| -------- | ---------- | ------------- |
| Admin    | admin123   | Administrador |
| Vendedor | ventas123  | Vendedor      |

## Datos de ejemplo

- **Clientes:** Supermercado El Sol (categoría Mayorista Premium, tiene una orden pendiente),
  Autoservicio El Centro (con una factura ya pagada). _Despensa Los Amigos_ está dada de baja,
  por eso no aparece en los listados.
- **Productos:** PROD-001 a PROD-004. El PROD-003 (Detergente) tiene stock por debajo del mínimo.
- **Órdenes:** OV-000001 (pendiente de facturar) y OV-000002 (ya facturada y cobrada).

## Circuito sugerido para probar

1. Ingresar como `Admin`.
2. **Órdenes de Venta → Nueva Orden:** elegir un cliente, agregar productos y confirmar. Se valida el
   stock y el crédito disponible; al confirmar se descuenta el stock y se suma el total al saldo del cliente.
3. **Facturación y Cobros → Emitir Factura:** elegir una orden pendiente (por ejemplo OV-000001). La orden
   pasa a _Facturada_ y el IVA (21%) se suma al saldo del cliente.
4. **Facturación y Cobros → Registrar Cobro:** elegir el cliente, imputar el pago a la factura y confirmar.
   La factura pasa a _Pagada_ cuando lo pagado cubre su total (se aceptan pagos parciales) y el saldo del cliente baja.
5. **Depósito y Stock → Registrar Movimiento:** ingreso, egreso o ajuste manual; el stock del producto se actualiza.
6. Cerrar la aplicación y volver a abrirla: todo queda guardado en la base.

## Estructura del proyecto

```
Seminario_S21/
├── README.md
├── Database/
│   └── schema.sql                   # Esquema, datos de ejemplo y consultas de MySQL
└── SistemaDistribucionMayorista/
    ├── pom.xml                      # Configuración de Maven y dependencias
    └── src/main/
        ├── java/
        │   ├── app/                 # MainApp (punto de entrada)
        │   ├── config/              # ConexionDB (lee db.properties y abre las conexiones)
        │   ├── controller/          # Controladores de cada módulo
        │   ├── dao/                 # Interfaces DAO, implementaciones JDBC y Mock, DAOFactory y DAOException
        │   ├── model/               # Entidades del dominio
        │   ├── util/                # Validator y Dialogs
        │   └── view/                # Pantallas Swing (y view/estilos)
        └── resources/
            └── db.properties        # Configuración de conexión a MySQL
```

## Diseño

- **MVC:** las vistas (`view`) solo arman la interfaz, los controllers (`controller`) manejan los eventos y
  coordinan, y el modelo (`model`) contiene las reglas del dominio.
- **DAO + Factory:** cada entidad tiene una interfaz (`ClienteDAO`, `ProductoDAO`...) y su implementación
  contra MySQL (`ClienteDAOJdbc`, `ProductoDAOJdbc`...). Los controllers dependen solo de las interfaces y
  `DAOFactory` es el único lugar que decide qué implementación se entrega. Las clases `...Mock` (datos en
  memoria) implementan las mismas interfaces, pero ya no se usan.
- **Consultas parametrizadas:** todas las consultas usan `PreparedStatement`, nunca se concatena texto del
  usuario en el SQL (evita inyección SQL).
- **Transacciones:** las operaciones que modifican varias tablas se ejecutan en una sola transacción
  (`commit` si todo sale bien, `rollback` si algo falla): confirmar una orden de venta, emitir una factura,
  registrar un pago y registrar un movimiento de stock.
- **Excepciones:** los errores de base de datos se envuelven en `DAOException`. Los controllers la atrapan
  donde pueden dar un mensaje específico (por ejemplo, stock insuficiente) y `MainApp` muestra un mensaje
  al usuario para cualquier otro caso.

## Problemas frecuentes

| Mensaje                                                                  | Qué hacer                                                                     |
| ------------------------------------------------------------------------ | ----------------------------------------------------------------------------- |
| `Access denied for user 'root'`                                          | La clave de MySQL no coincide: editar `db.properties` o usar `-Ddb.password`. |
| `Unknown database 'sistemamayoristadb'`                                  | Falta ejecutar `Database/schema.sql` (paso 1).                                |
| `Communications link failure` / "No se pudo conectar a la base de datos" | MySQL no está en ejecución.                                                   |
| `no POM in this directory`                                               | Ejecutar Maven dentro de la carpeta `SistemaDistribucionMayorista`.           |
| `mvn` no se reconoce como comando                                        | Instalar Maven y agregarlo al `PATH`.                                         |
