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

-- Tarjetas de crédito de los usuarios
CREATE TABLE Tarjeta_de_Credito (
    cvv VARCHAR(4) NOT NULL,
    numero VARCHAR(16) PRIMARY KEY,
    fecha_vencimiento DATE NOT NULL,
    id_usuario INT NOT NULL,
    CONSTRAINT fk_tarjeta_usuario FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

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

-- Estados específicos de artículos
CREATE TABLE Estado_Articulo (
    id_estado_articulo SERIAL PRIMARY KEY,
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
    CONSTRAINT fk_articulo_estado FOREIGN KEY (id_estado_articulo) REFERENCES Estado_Articulo(id_estado_articulo)
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

-- Primero insertar los estados de moderación si no existen
INSERT INTO Moderador_Articulo (nombre) VALUES 
('Pendiente'),
('Aprobado'),
('Rechazado');

-- Insertar 10 artículos para cada cliente (usuarios 2-11)
DO $$
DECLARE
    imagen_base64 TEXT :=  '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1';
    v_id_articulo INT;
    v_id_usuario INT;
BEGIN
    -- Loop para cada cliente (usuarios 2 al 11)
    FOR v_id_usuario IN 2..11 LOOP
        -- Artículo 1
        INSERT INTO Articulo (nombre, descripcion, precio, imagen, stock, id_estado_articulo, id_accion)
        VALUES ('Laptop HP Core i5', 'Laptop para trabajo y estudio, 8GB RAM', 4736.84, imagen_base64, 3, 1, 2)
        RETURNING id_articulo INTO v_id_articulo;
        INSERT INTO Publicacion (id_articulo, id_usuario, fecha_hora_entrega) VALUES (v_id_articulo, v_id_usuario, CURRENT_TIMESTAMP);
        INSERT INTO Categoria (id_articulo, id_categoria_tipo) VALUES (v_id_articulo, 1);
        
        -- Artículo 2
        INSERT INTO Articulo (nombre, descripcion, precio, imagen, stock, id_estado_articulo, id_accion)
        VALUES ('Mouse Gaming RGB', 'Mouse con luces RGB y DPI ajustable', 189.47, imagen_base64, 10, 1, 2)
        RETURNING id_articulo INTO v_id_articulo;
        INSERT INTO Publicacion (id_articulo, id_usuario, fecha_hora_entrega) VALUES (v_id_articulo, v_id_usuario, CURRENT_TIMESTAMP);
        INSERT INTO Categoria (id_articulo, id_categoria_tipo) VALUES (v_id_articulo, 1);
        
        -- Artículo 3
        INSERT INTO Articulo (nombre, descripcion, precio, imagen, stock, id_estado_articulo, id_accion)
        VALUES ('Teclado Mecánico', 'Teclado con switches azules', 473.68, imagen_base64, 7, 1, 2)
        RETURNING id_articulo INTO v_id_articulo;
        INSERT INTO Publicacion (id_articulo, id_usuario, fecha_hora_entrega) VALUES (v_id_articulo, v_id_usuario, CURRENT_TIMESTAMP);
        INSERT INTO Categoria (id_articulo, id_categoria_tipo) VALUES (v_id_articulo, 1);
        
        -- Artículo 4
        INSERT INTO Articulo (nombre, descripcion, precio, imagen, stock, id_estado_articulo, id_accion)
        VALUES ('Monitor 24 pulgadas', 'Full HD 1080p IPS', 1263.16, imagen_base64, 6, 1, 2)
        RETURNING id_articulo INTO v_id_articulo;
        INSERT INTO Publicacion (id_articulo, id_usuario, fecha_hora_entrega) VALUES (v_id_articulo, v_id_usuario, CURRENT_TIMESTAMP);
        INSERT INTO Categoria (id_articulo, id_categoria_tipo) VALUES (v_id_articulo, 1);
        
        -- Artículo 5
        INSERT INTO Articulo (nombre, descripcion, precio, imagen, stock, id_estado_articulo, id_accion)
        VALUES ('Webcam HD 1080p', 'Cámara para videollamadas', 294.74, imagen_base64, 12, 1, 2)
        RETURNING id_articulo INTO v_id_articulo;
        INSERT INTO Publicacion (id_articulo, id_usuario, fecha_hora_entrega) VALUES (v_id_articulo, v_id_usuario, CURRENT_TIMESTAMP);
        INSERT INTO Categoria (id_articulo, id_categoria_tipo) VALUES (v_id_articulo, 1);
        
        -- Artículo 6
        INSERT INTO Articulo (nombre, descripcion, precio, imagen, stock, id_estado_articulo, id_accion)
        VALUES ('Audífonos Bluetooth', 'Sonido de alta calidad', 368.42, imagen_base64, 6, 1, 2)
        RETURNING id_articulo INTO v_id_articulo;
        INSERT INTO Publicacion (id_articulo, id_usuario, fecha_hora_entrega) VALUES (v_id_articulo, v_id_usuario, CURRENT_TIMESTAMP);
        INSERT INTO Categoria (id_articulo, id_categoria_tipo) VALUES (v_id_articulo, 1);
        
        -- Artículo 7
        INSERT INTO Articulo (nombre, descripcion, precio, imagen, stock, id_estado_articulo, id_accion)
        VALUES ('Silla Ergonómica', 'Silla de oficina con soporte lumbar', 936.84, imagen_base64, 4, 2, 2)
        RETURNING id_articulo INTO v_id_articulo;
        INSERT INTO Publicacion (id_articulo, id_usuario, fecha_hora_entrega) VALUES (v_id_articulo, v_id_usuario, CURRENT_TIMESTAMP);
        INSERT INTO Categoria (id_articulo, id_categoria_tipo) VALUES (v_id_articulo, 2);
        
        -- Artículo 8
        INSERT INTO Articulo (nombre, descripcion, precio, imagen, stock, id_estado_articulo, id_accion)
        VALUES ('Impresora HP WiFi', 'Multifuncional con scanner', 1031.58, imagen_base64, 4, 1, 2)
        RETURNING id_articulo INTO v_id_articulo;
        INSERT INTO Publicacion (id_articulo, id_usuario, fecha_hora_entrega) VALUES (v_id_articulo, v_id_usuario, CURRENT_TIMESTAMP);
        INSERT INTO Categoria (id_articulo, id_categoria_tipo) VALUES (v_id_articulo, 1);
        
        -- Artículo 9
        INSERT INTO Articulo (nombre, descripcion, precio, imagen, stock, id_estado_articulo, id_accion)
        VALUES ('Mochila Laptop 15.6"', 'Resistente al agua', 294.74, imagen_base64, 12, 1, 2)
        RETURNING id_articulo INTO v_id_articulo;
        INSERT INTO Publicacion (id_articulo, id_usuario, fecha_hora_entrega) VALUES (v_id_articulo, v_id_usuario, CURRENT_TIMESTAMP);
        INSERT INTO Categoria (id_articulo, id_categoria_tipo) VALUES (v_id_articulo, 4);
        
        -- Artículo 10
        INSERT INTO Articulo (nombre, descripcion, precio, imagen, stock, id_estado_articulo, id_accion)
        VALUES ('Cable HDMI 2m', 'Alta velocidad 4K', 47.37, imagen_base64, 30, 1, 2)
        RETURNING id_articulo INTO v_id_articulo;
        INSERT INTO Publicacion (id_articulo, id_usuario, fecha_hora_entrega) VALUES (v_id_articulo, v_id_usuario, CURRENT_TIMESTAMP);
        INSERT INTO Categoria (id_articulo, id_categoria_tipo) VALUES (v_id_articulo, 1);
        
    END LOOP;
    
END $$;