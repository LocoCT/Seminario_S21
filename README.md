# Sistema de Gestión Distribuidora Mayorista

Aplicación de escritorio en **Java (Swing)** para la gestión de una distribuidora mayorista. Permite administrar clientes, productos, órdenes de venta, facturación y cobros, y el control de stock en depósitos.

Proyecto desarrollado para la materia **Seminario de Práctica de Informática**.

> **Estado:** prototipo funcional (Tercera entrega). Los datos se guardan en memoria mediante DAO simulados (mock). La conexión a base de datos se incorpora en la Cuarta entrega.

## Funcionalidades

- **Login** con validación de credenciales y detección de usuarios inactivos.
- **Panel principal** con resumen, acciones rápidas y últimas órdenes de venta.
- **Clientes:** alta, búsqueda por CUIT o razón social y edición. Valida CUIT (11 dígitos, sin duplicados) y email.
- **Productos:** catálogo con búsqueda, alta con código autogenerado (`PROD-001`, ...) y edición.
- **Órdenes de venta:** armado del pedido con control de stock y de límite de crédito del cliente. Al confirmar descuenta stock y actualiza el saldo.
- **Facturación y cobros:** emisión de facturas con IVA 21 %, registro de pagos imputados a facturas e historial de cobros.
- **Stock:** movimientos de ingreso, egreso y ajuste, con validación de stock disponible.

## Arquitectura

Patrón **MVC** con capa **DAO** desacoplada mediante interfaces y `DAOFactory`:

```
Vista (Swing) → Controller → DAO (DAOFactory) → Datos en memoria (Mock)
                                              → Base de datos (JDBC, próxima entrega)
```

Para pasar a base de datos solo hay que cambiar en `DAOFactory` los `new ...Mock()` por las implementaciones JDBC.

## Estructura del repositorio

```
Seminario_S21/
├── Database/                        # Script de esquema de la base de datos
└── SistemaDistribucionMayorista/
    └── src/
        ├── app/                     # MainApp (punto de entrada)
        ├── controller/              # Controladores de cada módulo
        ├── dao/                     # Interfaces DAO, implementaciones Mock y DAOFactory
        ├── model/                   # Entidades del dominio
        ├── util/                    # Validator y Dialogs
        └── view/                    # Pantallas Swing (y view/estilos)
```

## Requisitos

- Java JDK 8 o superior.
- No requiere librerías externas ni base de datos.

## Cómo ejecutarlo

### Desde Visual Studio Code

1. Abrir la carpeta `SistemaDistribucionMayorista`.
2. Ejecutar la clase `app.MainApp`.

### Desde la terminal

Ubicarse en la carpeta `SistemaDistribucionMayorista` y ejecutar:

```
javac -d bin -sourcepath src src/app/MainApp.java
java -cp bin app.MainApp
```

## Credenciales de prueba

| Usuario  | Contraseña | Rol            | Estado                       |
| -------- | ---------- | -------------- | ---------------------------- |
| Admin    | admin123   | Administrativo | Activo                       |
| Vendedor | ventas123  | Vendedor       | Activo                       |
| inactivo | 1234       | Vendedor       | Inactivo (ingreso rechazado) |

La pantalla de login se abre con las credenciales de `Admin` precargadas.

## Limitaciones actuales

- Los datos se pierden al cerrar la aplicación (persistencia en memoria).
- Las contraseñas de prueba están en texto plano y los roles aún no restringen pantallas.
- Las tarjetas de resumen del panel principal muestran valores de ejemplo.
- No incluye facturación electrónica ni reportes.

## Autor

**Federico Catena**
Seminario de Práctica de Informática
