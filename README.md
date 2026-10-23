# Sistema de Gestión Mayorista

Aplicación de escritorio en **Java (Swing)** para la gestión de una distribuidora mayorista. Permite administrar clientes, productos, órdenes de venta, facturación y cobros, y el control de stock en depósitos.

Proyecto desarrollado para la materia **Seminario de Práctica de Informática**.

## Estado del proyecto

| Entrega         | Estado                                                                                                             |
| --------------- | ------------------------------------------------------------------------------------------------------------------ |
| Tercera entrega | Prototipo funcional con datos en memoria (DAO simulados)                                                           |
| Cuarta entrega  | **En desarrollo:** migración a Maven completada y esquema MySQL disponible. Los DAO con JDBC se están incorporando |

Hasta que todos los DAO JDBC estén implementados, la aplicación sigue funcionando con los DAO simulados (mock) definidos en `DAOFactory`.

## Funcionalidades

- **Login** con validación de credenciales y detección de usuarios inactivos.
- **Panel principal** con resumen, acciones rápidas y últimas órdenes de venta.
- **Clientes:** alta, búsqueda por CUIT o razón social y edición. Valida CUIT (11 dígitos, sin duplicados) y email.
- **Productos:** catálogo con búsqueda, alta con código autogenerado (`PROD-001`, ...) y edición.
- **Órdenes de venta:** armado del pedido con control de stock y de límite de crédito del cliente. Al confirmar descuenta stock y actualiza el saldo.
- **Facturación y cobros:** emisión de facturas con IVA 21 %, registro de pagos imputados a facturas e historial de cobros.
- **Stock:** movimientos de ingreso, egreso y ajuste, con validación de stock disponible.

## Arquitectura

Patrón **MVC** con capa **DAO** desacoplada mediante interfaces y `DAOFactory` (Factory):

```
Vista (Swing) → Controller → DAO (DAOFactory) → Datos en memoria (Mock)
                                              → MySQL (JDBC)
```

Para pasar de datos simulados a MySQL solo se cambia en `DAOFactory` cada `new ...Mock()` por su implementación JDBC. Los controladores y las vistas no se modifican.

## Estructura del repositorio

```
Seminario_S21/
├── Database/
│   └── schema.sql                   # Esquema, datos de ejemplo y consultas de MySQL
└── SistemaDistribucionMayorista/
    ├── pom.xml                      # Configuración de Maven y dependencias
    └── src/main/java/
        ├── app/                     # MainApp (punto de entrada)
        ├── controller/              # Controladores de cada módulo
        ├── dao/                     # Interfaces DAO, implementaciones Mock y DAOFactory
        ├── model/                   # Entidades del dominio
        ├── util/                    # Validator y Dialogs
        └── view/                    # Pantallas Swing (y view/estilos)
```

## Base de datos

El script `Database/schema.sql` crea la base **`SistemaMayoristaDB`** en MySQL con 12 tablas:

| Grupo                 | Tablas                                                         |
| --------------------- | -------------------------------------------------------------- |
| Usuarios y catálogos  | `Usuario`, `CategoriaCliente`, `CategoriaProducto`, `Deposito` |
| Entidades principales | `Cliente`, `Producto`                                          |
| Ventas                | `OrdenVenta`, `DetalleOrdenVenta`                              |
| Facturación y cobros  | `Factura`, `Pago`, `Pago_Factura`                              |
| Inventario            | `MovimientoStock`                                              |

Las relaciones se resuelven con claves foráneas. `Pago_Factura` es la tabla intermedia que permite imputar un pago a una o varias facturas, con el monto aplicado a cada una. El script incluye además datos de ejemplo y consultas de muestra (movimientos de stock, clientes con su categoría y seguimiento de un pedido hasta su cobro).

## Requisitos

- **JDK 17 o superior**
- **Maven 3.9 o superior**
- **MySQL 8** (solo para la parte con base de datos)

El driver de MySQL (`mysql-connector-j`) se descarga automáticamente con Maven a través del `pom.xml`.

## Cómo ejecutarlo

### 1. Crear la base de datos (opcional hasta completar los DAO JDBC)

Desde la raíz del repositorio:

```
mysql -u root -p < Database/schema.sql
```

El script no borra tablas existentes. Si necesitás ejecutarlo de nuevo, primero eliminá la base con `DROP DATABASE SistemaMayoristaDB;`.

### 2. Compilar y ejecutar la aplicación

Desde la carpeta `SistemaDistribucionMayorista`:

```
mvn compile
mvn exec:java
```

También se puede abrir esa carpeta en Visual Studio Code y ejecutar la clase `app.MainApp`.

## Credenciales de prueba

Mientras la aplicación use los DAO simulados:

| Usuario  | Contraseña | Rol            | Estado                       |
| -------- | ---------- | -------------- | ---------------------------- |
| Admin    | admin123   | Administrativo | Activo                       |
| Vendedor | ventas123  | Vendedor       | Activo                       |
| inactivo | 1234       | Vendedor       | Inactivo (ingreso rechazado) |

La pantalla de login se abre con las credenciales de `Admin` precargadas. Cuando el login pase a leer de MySQL, los usuarios serán los cargados en la tabla `Usuario` del script.

## Limitaciones actuales

- Con los DAO simulados, los datos se pierden al cerrar la aplicación.
- Las contraseñas están sin cifrar y los roles aún no restringen pantallas.
- Las tarjetas de resumen del panel principal muestran valores de ejemplo.
- No incluye facturación electrónica ni reportes.

## Autor

**[Nombre y apellido]** – Legajo [Legajo]
Seminario de Práctica de Informática
