-- verificar si ya existe
CREATE DATABASE commerce;

\c commerce;

-- Tabla de estados generales del sistema
CREATE TABLE Estado_Usuario (
    id_estado SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL
);
 
-- Tabla de roles de usuario
CREATE TABLE Rol (
    id_rol SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL
);

-- Tabla principal de usuarios
CREATE TABLE Usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    id_estado INT NOT NULL,
    id_rol INT NOT NULL,
    CONSTRAINT fk_usuario_estado FOREIGN KEY (id_estado) REFERENCES Estado_Usuario(id_estado),
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES Rol(id_rol)
);

confirmación de elimi

-- Notificaciones enviadas a usuarios
CREATE TABLE Notificacion (
    id_notificacion SERIAL PRIMARY KEY,
    mensaje TEXT NOT NULL,
    fecha_hora TIMESTAMP NOT NULL,
    id_usuario INT NOT NULL,
    CONSTRAINT fk_notificacion_usuario FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- Sanciones aplicadas a usuarios
CREATE TABLE Sancion (
    id_sancion SERIAL PRIMARY KEY,
    motivo TEXT NOT NULL,
    fecha_hora TIMESTAMP NOT NULL,
    id_usuario INT NOT NULL,
    CONSTRAINT fk_sancion_usuario FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- Categorías de artículos
CREATE TABLE Tipo_Categoria (
    id_categoria SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL
);

-- Estados del artículo
CREATE TABLE Estado_Articulo (
    id_estado_articulo SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL
);

-- Estados del artículo
CREATE TABLE Moderador_Articulo (
    id_estado SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL
);

-- Artículos del sistema
CREATE TABLE Articulo (
    id_articulo SERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL CHECK (precio >= 0),
    imagen TEXT, --sera base64
    stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
    id_estado_articulo INT NOT NULL,
    id_accion INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_articulo_estado FOREIGN KEY (id_estado_articulo) REFERENCES Estado_Articulo(id_estado_articulo),
    CONSTRAINT fk_id_accion FOREIGN KEY (id_accion) REFERENCES Moderador_Articulo(id_estado)
);

-- Estados específicos de artículos
CREATE TABLE Categoria (
    id_categoria SERIAL PRIMARY KEY,
    id_articulo INT NOT NULL,
    id_categoria_tipo INT NOT NULL,
    CONSTRAINT fk_articulo FOREIGN KEY (id_articulo) REFERENCES Articulo(id_articulo)ON DELETE CASCADE,
    CONSTRAINT fk_categoria FOREIGN KEY (id_categoria_tipo) REFERENCES Tipo_Categoria(id_categoria) ON DELETE CASCADE
);

-- Publicaciones de artículos por usuarios
CREATE TABLE Publicacion (
    id_publicacion SERIAL PRIMARY KEY,
    id_articulo INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_hora_entrega TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    CONSTRAINT fk_publicacion_articulo FOREIGN KEY (id_articulo) REFERENCES Articulo(id_articulo) ON DELETE CASCADE,
    CONSTRAINT fk_publicacion_usuario FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- Comentarios y valoraciones de artículos
CREATE TABLE Comentario (
    id_comentario SERIAL PRIMARY KEY,
    descripcion TEXT NOT NULL,
    puntuacion INT CHECK (puntuacion >= 1 AND puntuacion <= 5),
    id_usuario INT NOT NULL,
    id_articulo INT NOT NULL,
    CONSTRAINT fk_comentario_usuario FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_comentario_articulo FOREIGN KEY (id_articulo) REFERENCES Articulo(id_articulo) ON DELETE CASCADE
);

-- Estados de pedidos
CREATE TABLE Estado_Pedido (
    id_estado_pedido SERIAL PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL
);

-- Pedidos realizados por compradores
CREATE TABLE Pedido (
    id_pedido SERIAL PRIMARY KEY,
    fecha_hora_entrega TIMESTAMP,
    id_comprador INT NOT NULL,
    id_estado_pedido INT NOT NULL,
    CONSTRAINT fk_pedido_comprador FOREIGN KEY (id_comprador) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_pedido_estado FOREIGN KEY (id_estado_pedido) REFERENCES Estado_Pedido(id_estado_pedido)
);

-- Pagos realizados
CREATE TABLE Pago (
    id_pago SERIAL PRIMARY KEY,
    monto DECIMAL(10, 2) NOT NULL CHECK (monto >= 0)
);

-- Transacciones de compra
CREATE TABLE Compra (
    id_compra SERIAL PRIMARY KEY,
    fecha_hora TIMESTAMP NOT NULL,
    id_comprador INT NOT NULL,
    id_vendedor INT NOT NULL,
    id_pago INT NOT NULL,
    CONSTRAINT fk_compra_comprador FOREIGN KEY (id_comprador) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
    CONSTRAINT fk_pago  FOREIGN KEY (id_pago) REFERENCES Pago(id_pago) ON DELETE CASCADE,
    CONSTRAINT fk_compra_vendedor FOREIGN KEY (id_vendedor) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

-- Relación entre compras y pedidos
CREATE TABLE Producto (
    id_producto SERIAL PRIMARY KEY,
    id_compra INT NOT NULL,
    id_articulo INT NOT NULL,
    cantidad INT NOT NULL,
    CONSTRAINT fk_compra FOREIGN KEY (id_compra) REFERENCES Compra(id_compra) ON DELETE CASCADE,
    CONSTRAINT fk__articulo FOREIGN KEY (id_articulo) REFERENCES Articulo(id_articulo) ON DELETE CASCADE
);

-- Inserts para datos básicos

-- Estados de Pedido
INSERT INTO Estado_Pedido (nombre) VALUES 
('Curso'),
('Entregado');

-- Estados de Usuario
INSERT INTO Estado_Usuario (nombre) VALUES 
('Fraudes'),
('Activo'),
('Incumplimientos'),
('Desactivado');

-- Roles de Usuario
INSERT INTO Rol (nombre) VALUES 
('Común'),
('Moderador'),
('Logística'),
('Administrador');

-- Tipos de Categoría
INSERT INTO Tipo_Categoria (nombre) VALUES 
('Tecnología'),
('Hogar'),
('Académico'),
('Personal'),
('Decoración');

-- Estados de Artículo
INSERT INTO Estado_Articulo (nombre) VALUES 
('Nuevo'),
('Usado');

-- Usuario Administrador
-- Email: admin@commerce.com
-- Password: admin
INSERT INTO Usuario (nombre, apellido, email, password, id_estado, id_rol) VALUES 
-- Administrador
('Benjamin de Jesus', 'Perez Aguilar', 'admin@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 4),

-- 10 Clientes 
('Benjamin de Jesus', 'Perez Aguilar', 'mcdreck276@gmail.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 1),
('Benjamin de Jesus', 'Luna Robles', 'bj97perezaguilar@gmail.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 1),
('Laura Sofía', 'Ramírez Gómez', 'cliente3@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 1),
('Diego Alejandro', 'Torres Sánchez', 'cliente4@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 1),
('Andrea Paola', 'Morales Ruiz', 'cliente5@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 1),
('Fernando José', 'Vásquez Ortiz', 'cliente6@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 1),
('Gabriela Isabel', 'Castillo Núñez', 'cliente7@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 1),
('Miguel Ángel', 'Reyes Domínguez', 'cliente8@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 1),
('Valeria Fernanda', 'Cruz Medina', 'cliente9@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 1),
('Javier Antonio', 'Jiménez Vargas', 'cliente10@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 1),

-- 5 Moderadores 
('Carlos Alberto', 'Rodríguez Méndez', 'moderador1@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 2),
('Patricia Elena', 'Silva Contreras', 'moderador2@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 2),
('Eduardo Ramón', 'Gutiérrez Parra', 'moderador3@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 2),
('Mónica Lucía', 'Chávez Herrera', 'moderador4@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 2),
('Ricardo Daniel', 'Mendoza Rivas', 'moderador5@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 2),

-- 3 Logística
('Ana Patricia', 'Hernández Castro', 'logistica1@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 3),
('Luis Fernando', 'Campos Delgado', 'logistica2@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 3),
('Carmen Rosa', 'Navarro Peña', 'logistica3@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 3);

-- Script para insertar 10 artículos por usuario (usuarios 2-11)
DO $$
DECLARE
    imagen_base64 TEXT := 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCABkAGQDAREAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlbaWmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD/AD/6ACgAoAKACgAoAKACgAoAKACgD/9k=';
    v_id_usuario INT;
    v_id_articulo INT;
    v_articulos_data TEXT[][];
    idx INT;
    titulo TEXT;
    descripcion TEXT;
    precio DECIMAL;
    stock_qty INT;
    estado INT;
    categorias TEXT;
    precio_con_comision DECIMAL;
    categoria_id INT;
BEGIN
    -- Definir artículos para cada usuario
    v_articulos_data := ARRAY[
        -- Usuario 2 
        ARRAY['Laptop HP Core i5', 'Laptop para trabajo y estudio, 8GB RAM', '4500', '3', '1', '{1,4}'],
        ARRAY['Mouse Gaming RGB', 'Mouse con luces RGB y DPI ajustable', '180', '10', '1', '{1}'],
        ARRAY['Teclado Mecánico', 'Teclado con switches azules', '450', '7', '1', '{1}'],
        ARRAY['Mouse Gaming RGB', 'Mouse con luces RGB y DPI ajustable', '180', '8', '1', '{1}'], 
        ARRAY['Webcam HD 1080p', 'Cámara para videollamadas', '280', '12', '1', '{1}'],
        ARRAY['Audífonos Bluetooth', 'Sonido de alta calidad', '350', '6', '1', '{1,4}'],
        ARRAY['Teclado Mecánico', 'Teclado con switches azules', '450', '5', '1', '{1}'], 
        ARRAY['Hub USB 4 puertos', 'Expandir conexiones USB', '95', '15', '1', '{1}'],
        ARRAY['Mousepad Gaming XL', 'Grande y antideslizante', '120', '20', '1', '{1,4}'],
        ARRAY['Cable HDMI 2m', 'Alta velocidad 4K', '45', '30', '1', '{1}'],
        
        -- Usuario 3 
        ARRAY['Monitor 24 pulgadas', 'Full HD 1080p IPS', '1200', '6', '1', '{1}'],
        ARRAY['Silla Ergonómica', 'Silla de oficina con soporte lumbar', '890', '4', '2', '{2}'],
        ARRAY['Escritorio L Grande', 'Escritorio esquinero de madera', '1200', '3', '2', '{2}'],
        ARRAY['Lámpara LED Escritorio', 'Luz ajustable con USB', '180', '12', '1', '{2,5}'],
        ARRAY['Organizador Escritorio', 'Bambú con 5 compartimentos', '120', '15', '1', '{2,5}'],
        ARRAY['Silla Ergonómica', 'Silla de oficina con soporte lumbar', '890', '3', '2', '{2}'], 
        ARRAY['Cajonera Móvil', 'Con ruedas y 3 cajones', '580', '5', '1', '{2}'],
        ARRAY['Estante Flotante', 'Set de 3, madera blanca', '280', '10', '1', '{2,5}'],
        ARRAY['Pizarra Magnética', 'Con marcadores incluidos', '220', '8', '1', '{3}'],
        ARRAY['Reloj de Pared', 'Digital con temperatura', '95', '12', '1', '{5}'],
        
        -- Usuario 4 
        ARRAY['Mochila Laptop 15.6"', 'Resistente al agua', '280', '12', '1', '{4}'],
        ARRAY['Cuadernos Universitarios', 'Pack de 5 cuadriculados', '85', '25', '1', '{3}'],
        ARRAY['Set Lápices Colores', 'Caja de 36 profesionales', '120', '18', '1', '{3}'],
        ARRAY['Cuadernos Universitarios', 'Pack de 5 cuadriculados', '85', '30', '1', '{3}'], 
        ARRAY['Calculadora Científica', 'Casio FX-991', '180', '15', '1', '{3}'],
        ARRAY['Archivador Plástico', 'Pack de 10 colores', '65', '40', '1', '{3}'],
        ARRAY['Mochila Laptop 15.6"', 'Resistente al agua', '280', '10', '1', '{4}'], 
        ARRAY['Post-it Variados', 'Set de 12 blocks', '45', '50', '1', '{3}'],
        ARRAY['Grapadora Industrial', 'Para 50 hojas', '95', '20', '1', '{3}'],
        ARRAY['Tijeras Profesionales', 'Acero inoxidable 8"', '35', '35', '1', '{3}'],
        
        -- Usuario 5 
        ARRAY['Impresora HP WiFi', 'Multifuncional con scanner', '980', '4', '1', '{1}'],
        ARRAY['Papel Bond A4', 'Paquete de 500 hojas', '48', '35', '1', '{3}'],
        ARRAY['Cartuchos HP Color', 'Pack original 4 colores', '420', '12', '1', '{1}'],
        ARRAY['Papel Bond A4', 'Paquete de 500 hojas', '48', '40', '1', '{3}'], 
        ARRAY['Scanner Portátil', 'USB con OCR', '650', '7', '1', '{1,3}'],
        ARRAY['Tinta Epson Original', 'Set completo CMYK', '380', '10', '1', '{1}'],
        ARRAY['Impresora HP WiFi', 'Multifuncional con scanner', '980', '3', '1', '{1}'], 
        ARRAY['Papel Fotográfico', 'Glossy A4 pack 100', '95', '25', '1', '{3}'],
        ARRAY['Etiquetas Adhesivas', 'Rollo 1000 unidades', '55', '30', '1', '{3}'],
        ARRAY['Plastificadora A4', 'Con fundas incluidas', '280', '8', '1', '{1,3}'],
        
        -- Usuario 6 
        ARRAY['Laptop Dell i3', 'Core i3, 8GB RAM, 256GB SSD', '3800', '4', '1', '{1,4}'],
        ARRAY['Mouse Inalámbrico', 'Ergonómico 2.4GHz', '85', '18', '1', '{1}'],
        ARRAY['Laptop Dell i3', 'Core i3, 8GB RAM, 256GB SSD', '3800', '3', '1', '{1,4}'], 
        ARRAY['Bocinas Bluetooth', 'Portátiles 20W', '280', '12', '1', '{1}'],
        ARRAY['Cargador Universal', 'Multiple tips', '75', '25', '1', '{1}'],
        ARRAY['Mouse Inalámbrico', 'Ergonómico 2.4GHz', '85', '20', '1', '{1}'], 
        ARRAY['Disco Duro 1TB', 'Externo USB 3.0', '450', '8', '1', '{1}'],
        ARRAY['Memoria RAM 8GB', 'DDR4 2666MHz', '280', '15', '1', '{1}'],
        ARRAY['Ventilador USB', 'Portátil silencioso', '65', '22', '1', '{1,2}'],
        ARRAY['Bocinas Bluetooth', 'Portátiles 20W', '280', '10', '1', '{1}'], 
        
        -- Usuario 7 
        ARRAY['Monitor Curvo 27"', 'Gaming 144Hz 1ms', '2100', '3', '1', '{1}'],
        ARRAY['Teclado RGB Mecánico', 'Switches rojos gaming', '650', '8', '1', '{1}'],
        ARRAY['Mouse Gamer RGB', 'DPI 16000 programable', '320', '12', '1', '{1}'],
        ARRAY['Audífonos Gamer 7.1', 'Surround con micrófono', '480', '9', '1', '{1,4}'],
        ARRAY['Silla Gamer Pro', 'Reclinable con reposabrazos 4D', '1500', '3', '2', '{2}'],
        ARRAY['Teclado RGB Mecánico', 'Switches rojos gaming', '650', '6', '1', '{1}'], 
        ARRAY['Alfombrilla XXL RGB', 'Con iluminación LED', '180', '15', '1', '{1}'],
        ARRAY['Soporte Monitor Dual', 'Para 2 pantallas 27"', '350', '7', '1', '{1,2}'],
        ARRAY['Cable Ethernet 10m', 'Cat 7 para gaming', '85', '20', '1', '{1}'],
        ARRAY['Mouse Gamer RGB', 'DPI 16000 programable', '320', '10', '1', '{1}'], 
        
        -- Usuario 8
        ARRAY['PC Gamer RTX 3060', 'i5-12400F, 16GB, SSD 512GB', '8500', '2', '1', '{1}'],
        ARRAY['Laptop Lenovo Legion', 'RTX 3050, i7, 16GB', '7200', '3', '1', '{1,4}'],
        ARRAY['Monitor 4K 32"', 'IPS HDR profesional', '3200', '2', '1', '{1}'],
        ARRAY['Teclado Logitech MX', 'Mecánico profesional', '980', '6', '1', '{1}'],
        ARRAY['PC Gamer RTX 3060', 'i5-12400F, 16GB, SSD 512GB', '8500', '1', '1', '{1}'], 
        ARRAY['Webcam 4K Logitech', 'Con HDR y autofocus', '850', '5', '1', '{1}'],
        ARRAY['SSD NVMe 1TB', 'Samsung 980 PRO', '780', '8', '1', '{1}'],
        ARRAY['Fuente Poder 750W', 'Modular 80+ Gold', '650', '7', '1', '{1}'],
        ARRAY['Tarjeta Captura HD', 'Para streaming 1080p', '420', '10', '1', '{1}'],
        ARRAY['Laptop Lenovo Legion', 'RTX 3050, i7, 16GB', '7200', '2', '1', '{1,4}'], 
        
        -- Usuario 9 
        ARRAY['MacBook Air M1', '8GB RAM, 256GB SSD', '12000', '2', '1', '{1,4}'],
        ARRAY['iPad Pro 11"', 'M2, 128GB WiFi', '8500', '3', '1', '{1,4}'],
        ARRAY['AirPods Pro 2', 'Con cancelación de ruido', '2400', '5', '1', '{1,4}'],
        ARRAY['MacBook Air M1', '8GB RAM, 256GB SSD', '12000', '1', '1', '{1,4}'], 
        ARRAY['Apple Pencil 2', 'Para iPad Pro', '1200', '6', '1', '{1,3}'],
        ARRAY['Magic Keyboard', 'Para iPad Pro 11"', '980', '4', '1', '{1}'],
        ARRAY['AirPods Pro 2', 'Con cancelación de ruido', '2400', '4', '1', '{1,4}'], 
        ARRAY['Adaptador USB-C Hub', 'Multipuertos 7 en 1', '350', '10', '1', '{1}'],
        ARRAY['Funda MacBook Air', 'Cuero premium 13"', '280', '12', '1', '{4}'],
        ARRAY['Magic Mouse', 'Inalámbrico recargable', '750', '6', '1', '{1}'],
        
        -- Usuario 10 
        ARRAY['Soporte Laptop Aluminio', 'Ajustable ventilado', '180', '15', '1', '{1,5}'],
        ARRAY['Organizador Cables', 'Kit 20 piezas velcro', '65', '30', '1', '{1,2}'],
        ARRAY['Lámpara LED Inteligente', 'RGB WiFi Alexa', '320', '10', '1', '{1,2,5}'],
        ARRAY['Soporte Laptop Aluminio', 'Ajustable ventilado', '180', '12', '1', '{1,5}'], 
        ARRAY['Regleta Inteligente', '6 tomas USB WiFi', '280', '8', '1', '{1,2}'],
        ARRAY['Termo Inteligente', 'Mantiene 8hrs temperatura', '250', '10', '1', '{2}'],
        ARRAY['Organizador Cables', 'Kit 20 piezas velcro', '65', '35', '1', '{1,2}'], 
        ARRAY['Humidificador USB', 'Portátil silencioso', '150', '15', '1', '{2}'],
        ARRAY['Cargador Portátil', 'Power Bank 30000mAh', '380', '12', '1', '{1,4}'],
        ARRAY['Base Enfriadora Laptop', 'Con 5 ventiladores RGB', '220', '14', '1', '{1}'],
        
        -- Usuario 11 
        ARRAY['Tablet Samsung Tab S8', '128GB WiFi', '4500', '4', '1', '{1,4}'],
        ARRAY['Teclado Logitech K380', 'Bluetooth multidispositivo', '280', '15', '1', '{1}'],
        ARRAY['Tablet Samsung Tab S8', '128GB WiFi', '4500', '3', '1', '{1,4}'], 
        ARRAY['Mouse Vertical', 'Ergonómico inalámbrico', '150', '18', '1', '{1}'],
        ARRAY['Lámpara Escritorio LED', 'Con cargador inalámbrico', '280', '10', '1', '{2,5}'],
        ARRAY['Teclado Logitech K380', 'Bluetooth multidispositivo', '280', '12', '1', '{1}'], 
        ARRAY['Soporte Tablet Ajustable', 'Para mesa o cama', '95', '20', '1', '{1,5}'],
        ARRAY['Funda Universal 10"', 'Con teclado Bluetooth', '180', '14', '1', '{4}'],
        ARRAY['Mouse Vertical', 'Ergonómico inalámbrico', '150', '16', '1', '{1}'], 
        ARRAY['Limpiador Pantallas', 'Kit spray + paño microfibra', '85', '25', '1', '{1}']
    ];

    -- Loop por cada usuario (2-11)
    FOR v_id_usuario IN 2..11 LOOP
        -- Loop por cada artículo (10 por usuario)
        FOR i IN 1..10 LOOP
            -- Calcular índice en el array
            idx := (v_id_usuario - 2) * 10 + i;
            
            -- Extraer datos del artículo
            titulo := v_articulos_data[idx][1];
            descripcion := v_articulos_data[idx][2];
            precio := v_articulos_data[idx][3]::DECIMAL;
            stock_qty := v_articulos_data[idx][4]::INT;
            estado := v_articulos_data[idx][5]::INT;
            categorias := v_articulos_data[idx][6];
            
            -- Calcular precio con comisión del 5%
            precio_con_comision := precio / 0.95;
            
            -- Insertar artículo con estado aprobado (id_accion = 2)
            INSERT INTO Articulo (nombre, descripcion, precio, imagen, stock, id_estado_articulo, id_accion)
            VALUES (titulo, descripcion, precio_con_comision, imagen_base64, stock_qty, estado, 2)
            RETURNING id_articulo INTO v_id_articulo;
            
            -- Insertar publicación
            INSERT INTO Publicacion (id_articulo, id_usuario, fecha_hora_entrega)
            VALUES (v_id_articulo, v_id_usuario, CURRENT_TIMESTAMP);
            
            -- Insertar categorías
            categorias := REPLACE(REPLACE(categorias, '{', ''), '}', '');
            
            -- Iterar sobre cada categoría
            FOREACH categoria_id IN ARRAY string_to_array(categorias, ',')::INT[]
            LOOP
                INSERT INTO Categoria (id_articulo, id_categoria_tipo)
                VALUES (v_id_articulo, categoria_id);
            END LOOP;
        END LOOP;
    END LOOP;
    
    RAISE NOTICE 'Se insertaron 100 artículos exitosamente (10 por cada usuario del 2 al 11)';
    
END $$;