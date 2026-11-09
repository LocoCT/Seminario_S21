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

```bash
mysql -u root -p
```

```sql
source Database/schema.sql
```

El script borra y vuelve a crear la base `SistemaMayoristaDB`, así que se puede ejecutar todas las veces
que haga falta para volver a los datos iniciales limpios. (Si se usa PowerShell, también podés ejecutar directamente `Get-Content Database/schema.sql | mysql -u root -p`).

**2. Ejecutar la aplicación**

```bash
cd SistemaDistribucionMayorista
mvn compile exec:java
```

**3. Configurar la conexión a MySQL (desde la aplicación)**

Al iniciar la aplicación, en la pantalla de inicio de sesión se encuentra el botón **"Configurar Base de Datos"**:
- Permite ingresar o ajustar el **Host**, **Puerto**, **Base de Datos**, **Usuario** y **Contraseña** de MySQL sin modificar archivos de texto ni código fuente.
- Incluye el botón **"Probar Conexión"** para validar en tiempo real el acceso al servidor antes de ingresar.
- Al presionar **"Guardar"**, la configuración se aplica de forma inmediata a la sesión en ejecución.

*(Alternativamente, también se puede predefinir en `SistemaDistribucionMayorista/src/main/resources/db.properties` o mediante parámetros de Maven: `mvn compile exec:java "-Ddb.user=root" "-Ddb.password=TU_CLAVE"`).*

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

1. En la pantalla de login, verificar o ajustar las credenciales de MySQL con el botón **"Configurar Base de Datos"** y presionar **"Probar Conexión"**.
2. Ingresar como `Admin` (o presionar directamente *Ingresar*, ya que las credenciales vienen precargadas para agilizar la prueba).
3. **Órdenes de Venta → Nueva Orden:** elegir un cliente, agregar productos y confirmar. Se valida el
   stock y el crédito disponible; al confirmar se descuenta el stock y se suma el total al saldo del cliente.
4. **Facturación y Cobros → Emitir Factura:** elegir una orden pendiente (por ejemplo OV-000001). La orden
   pasa a _Facturada_ y el IVA (21%) se suma al saldo del cliente.
5. **Facturación y Cobros → Registrar Cobro:** elegir el cliente, imputar el pago a la factura y confirmar.
   La factura pasa a _Pagada_ cuando lo pagado cubre su total (se aceptan pagos parciales) y el saldo del cliente baja.
6. **Depósito y Stock → Registrar Movimiento:** ingreso, egreso o ajuste manual; el stock del producto se actualiza.
7. Cerrar la aplicación y volver a abrirla: todo queda guardado en la base de datos MySQL.

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
        │   ├── config/              # ConexionDB (gestión y prueba de conexiones MySQL)
        │   ├── controller/          # Controladores (Login, Configuración DB, Clientes, etc.)
        │   ├── dao/                 # Interfaces DAO, implementaciones JDBC y Mock, DAOFactory y DAOException
        │   ├── model/               # Entidades del dominio
        │   ├── util/                # Validator y Dialogs
        │   └── view/                # Pantallas Swing y paquete view/estilos
        └── resources/
            └── db.properties        # Configuración por defecto de MySQL
```

## Diseño y Arquitectura

- **MVC (Modelo-Vista-Controlador):** las vistas (`view`) arman la interfaz gráfica Swing, los controladores (`controller`) gestionan los eventos y coordinan las acciones, y el modelo (`model`) encapsula las entidades y reglas de negocio.
- **DAO + Factory:** cada entidad cuenta con su interfaz DAO (`ClienteDAO`, `ProductoDAO`, etc.) implementada contra MySQL vía JDBC (`ClienteDAOJdbc`, `ProductoDAOJdbc`...). Los controladores interactúan exclusivamente a través de las interfaces provistas por `DAOFactory`.
- **Configuración Dinámica de Conexión:** pantalla dedicada para que evaluadores o usuarios configuren y prueben sus credenciales locales de MySQL en tiempo de ejecución.
- **Consultas parametrizadas:** todas las operaciones hacia la base de datos emplean `PreparedStatement`, previniendo vulnerabilidades de inyección SQL.
- **Manejo Transaccional:** las operaciones complejas que impactan múltiples tablas (confirmar orden, emitir factura, registrar cobranza, asentar movimientos de stock) se ejecutan bajo transacciones atómicas (`setAutoCommit(false)` con `commit` y `rollback`).
- **Control de Excepciones:** los errores de base de datos se encapsulan en `DAOException`. En `MainApp` se cuenta además con un manejador global de excepciones no controladas (`setDefaultUncaughtExceptionHandler`) que garantiza feedback visual continuo al usuario sin cierres abruptos.

## Problemas frecuentes

| Mensaje                                                                  | Qué hacer                                                                     |
| ------------------------------------------------------------------------ | ----------------------------------------------------------------------------- |
| `Access denied for user 'root'`                                          | Usar el botón **Configurar Base de Datos** en el Login para indicar tu usuario/clave. |
| `Unknown database 'sistemamayoristadb'`                                  | Falta ejecutar `Database/schema.sql` (paso 1).                                |
| `Communications link failure` / "No se pudo conectar a la base de datos" | Verificar que el servicio de MySQL esté iniciado en el equipo.               |
| `no POM in this directory`                                               | Ejecutar Maven dentro de la carpeta `SistemaDistribucionMayorista`.           |
| `mvn` no se reconoce como comando                                        | Instalar Maven y verificar que esté en las variables de entorno (`PATH`).    |
