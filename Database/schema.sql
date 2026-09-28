-- Creacion de Base de Datos
CREATE DATABASE IF NOT EXISTS SistemaMayoristaDB;
USE SistemaMayoristaDB;

-- Create tabla Usuario
CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    estado CHAR(1) DEFAULT 'A'
);

-- Create tabla CategoriaCliente
CREATE TABLE CategoriaCliente (
    id_categoria_cli INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    porcentaje_descuento DECIMAL(5,2) DEFAULT 0.00
);

-- Create Categoria Producto
CREATE TABLE CategoriaProducto (
    id_categoria_prod INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(200)
);

-- Create Deposito
CREATE TABLE Deposito (
    id_deposito INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    ubicacion VARCHAR(150)
);

-- Create Cliente
CREATE TABLE Cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    cuit VARCHAR(20) UNIQUE NOT NULL,
    razon_social VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion VARCHAR(150),
    limite_credito DECIMAL(10,2) DEFAULT 0.00,
    saldo DECIMAL(10,2) DEFAULT 0.00,
    estado CHAR(1) DEFAULT 'A',
    id_categoria_cli INT,
    id_usuario_alta INT,
    FOREIGN KEY (id_categoria_cli) REFERENCES CategoriaCliente(id_categoria_cli),
    FOREIGN KEY (id_usuario_alta) REFERENCES Usuario(id_usuario)
);

-- Create Producto
CREATE TABLE Producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    descripcion VARCHAR(100) NOT NULL,
    precio_base DECIMAL(10,2) NOT NULL,
    costo_compra DECIMAL(10,2) NOT NULL,
    stock_actual INT DEFAULT 0,
    stock_minimo INT DEFAULT 0,
    estado CHAR(1) DEFAULT 'A',
    id_categoria_prod INT,
    FOREIGN KEY (id_categoria_prod) REFERENCES CategoriaProducto(id_categoria_prod)
);

-- Create OrdenVenta
CREATE TABLE OrdenVenta (
    nro_orden INT AUTO_INCREMENT PRIMARY KEY,
    fecha_emision DATETIME NOT NULL,
    estado VARCHAR(30) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    descuento_aplicado DECIMAL(10,2) DEFAULT 0.00,
    total DECIMAL(10,2) NOT NULL,
    id_cliente INT NOT NULL,
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente),
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
);

-- DetalleOrdenVenta
CREATE TABLE DetalleOrdenVenta (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    nro_orden INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    porcentaje_descuento DECIMAL(5,2) DEFAULT 0.00,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (nro_orden) REFERENCES OrdenVenta(nro_orden),
    FOREIGN KEY (id_producto) REFERENCES Producto(id_producto)
);

-- Create Factura
CREATE TABLE Factura (
    nro_factura INT AUTO_INCREMENT PRIMARY KEY,
    nro_orden INT NOT NULL UNIQUE,
    fecha_emision DATETIME NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    iva DECIMAL(10,2) NOT NULL,
    total_final DECIMAL(10,2) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    FOREIGN KEY (nro_orden) REFERENCES OrdenVenta(nro_orden)
);

-- Create Pago
CREATE TABLE Pago (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    fecha_pago DATETIME NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    medio_pago VARCHAR(50) NOT NULL,
    comprobante_referencia VARCHAR(50),
    FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente)
);

-- Create Pago_Factura
CREATE TABLE Pago_Factura (
    id_pago INT NOT NULL,
    nro_factura INT NOT NULL,
    monto_aplicado DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id_pago, nro_factura),
    FOREIGN KEY (id_pago) REFERENCES Pago(id_pago),
    FOREIGN KEY (nro_factura) REFERENCES Factura(nro_factura)
);

-- Create MovimientoStock
CREATE TABLE MovimientoStock (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    fecha_hora DATETIME NOT NULL,
    cantidad INT NOT NULL,
    tipo_movimiento VARCHAR(30) NOT NULL,
    motivo VARCHAR(100),
    id_producto INT NOT NULL,
    id_deposito INT NOT NULL,
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_producto) REFERENCES Producto(id_producto),
    FOREIGN KEY (id_deposito) REFERENCES Deposito(id_deposito),
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
);


------------------------------------------------------------
-- INSERTS
-- Insert tabla Usuarios
INSERT INTO Usuario (username, contrasena, rol, estado) 
VALUES 
('admin_federico', 'hash_1234', 'Administrador', 'A'),
('vend_juan', 'hash_5678', 'Vendedor', 'A');

-- Insert tabla CategoriaCliente
INSERT INTO CategoriaCliente (nombre, porcentaje_descuento) 
VALUES 
('Mayorista Estandar', 0.00),
('Mayorista Premium', 10.00);

-- Insert tabla Categoria Producto
INSERT INTO CategoriaProducto (nombre, descripcion) 
VALUES 
('Bebidas', 'Bebidas con y sin alcohol, jugos y aguas'),
('Almacen', 'Productos secos, conservas y aderezos');

-- Insert tabla Deposito
INSERT INTO Deposito (nombre, ubicacion) 
VALUES 
('Deposito Central', 'Av. San Martin 1500, Zona Sur'),
('Deposito Belgrano', 'Av. Congreso 3803, Zona Norte');

-- Insert tabla Cliente
INSERT INTO Cliente (cuit, razon_social, telefono, email, direccion, limite_credito, saldo, estado, id_categoria_cli, id_usuario_alta) 
VALUES 
('30711111119', 'Supermercado El Sol', '11-14445555', 'super@elsol.com', 'Av. Colon 2000', 1500000.00, 0.00, 'A', 2, 1),
('33622222229', 'Despensa Los Amigos', '11-15556666', 'despensa@losamigos.com', 'Av. Belgrano 150', 500000.00, 0.00, 'A', 1, 1),
('30999999995', 'Autoservicio El Centro', '11-12223333', 'autoservicio@elcentro.com', 'San Martin 400', 2000000.00, 0.00, 'A', 1, 1);


-- Insert Producto
INSERT INTO Producto (codigo, descripcion, precio_base, costo_compra, stock_actual, stock_minimo, estado, id_categoria_prod) 
VALUES 
('BEB-001', 'Gaseosa Cola 2.25L', 1500.00, 900.00, 500, 50, 'A', 1),
('ALM-001', 'Pure de Tomate 520g', 850.00, 500.00, 1200, 100, 'A', 2);

-- Insert de una Orden de Venta
INSERT INTO OrdenVenta (fecha_emision, estado, subtotal, descuento_aplicado, total, id_cliente, id_usuario) 
VALUES 
('2026-05-10 10:30:00', 'CONFIRMADA', 15000.00, 1500.00, 13500.00, 1, 2),
('2026-10-20 09:15:00', 'FACTURADA', 85000.00, 0.00, 85000.00, 3, 2);

-- Detalle de la Orden de Venta (10 Gaseosas)
INSERT INTO DetalleOrdenVenta (nro_orden, id_producto, cantidad, precio_unitario, porcentaje_descuento, subtotal) 
VALUES 
(1, 1, 10, 1500.00, 10.00, 13500.00),
(2, 2, 100, 850.00, 0.00, 85000.00);

-- Insert del movimiento de stock por la venta
INSERT INTO MovimientoStock (fecha_hora, cantidad, tipo_movimiento, motivo, id_producto, id_deposito, id_usuario) 
VALUES 
('2026-05-10 10:35:00', -10, 'EGRESO', 'Venta por Orden Nro 1', 1, 1, 2),
('2026-10-20 09:16:00', -100, 'EGRESO', 'Venta por Orden Nro 2', 2, 1, 2);

-- Insert Tabla Factura
INSERT INTO Factura (nro_orden, fecha_emision, subtotal, iva, total_final, estado) 
VALUES (2, '2026-10-20 09:30:00', 85000.00, 17850.00, 102850.00, 'PAGADA');

-- Insert Tabla Pago
INSERT INTO Pago (id_cliente, fecha_pago, monto, medio_pago, comprobante_referencia) 
VALUES (3, '2026-10-20 10:00:00', 102850.00, 'Transferencia', 'TRX-00991122');

-- Insert Pago_Factura
INSERT INTO Pago_Factura (id_pago, nro_factura, monto_aplicado) 
VALUES (1, 1, 102850.00);

-- Consulta de Movimientos de stock de un producto
SELECT 
    m.fecha_hora, 
    p.descripcion AS producto, 
    m.cantidad, 
    m.tipo_movimiento, 
    d.nombre AS deposito
FROM MovimientoStock m
INNER JOIN Producto p ON m.id_producto = p.id_producto
INNER JOIN Deposito d ON m.id_deposito = d.id_deposito;

-- Consulta de Clientes registrados con su categoria
SELECT 
    c.id_cliente,
    c.cuit, 
    c.razon_social, 
    cat.nombre AS categoria, 
    c.limite_credito 
FROM Cliente c
INNER JOIN CategoriaCliente cat ON c.id_categoria_cli = cat.id_categoria_cli;

-- Consulta de Punta a Punta (Fecha Pedido hasta Fecha Pago)
SELECT 
    c.razon_social AS Cliente,
    o.nro_orden AS Orden,
    o.fecha_emision AS Fecha_Pedido,
    f.nro_factura AS Factura,
    f.total_final AS Total_Facturado,
    p.fecha_pago AS Fecha_Pago,
    p.medio_pago AS Medio_Pago,
    pf.monto_aplicado AS Monto_Pagado
FROM Cliente c
JOIN OrdenVenta o ON c.id_cliente = o.id_cliente
JOIN Factura f ON o.nro_orden = f.nro_orden
JOIN Pago_Factura pf ON f.nro_factura = pf.nro_factura
JOIN Pago p ON pf.id_pago = p.id_pago
WHERE c.cuit = '30999999995';

-- Update Baja de Cliente
UPDATE Cliente 
SET estado = 'B' 
WHERE cuit = '33622222229';