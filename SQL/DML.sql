-- verificar si ya existe
-- CREATE DATABASE commerce;

-- \c commerce;

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

-- Primero insertar los estados de moderación si no existen
INSERT INTO Moderador_Articulo (nombre) VALUES 
('Pendiente'),
('Aprobado'),
('Rechazado');

-- Insertar 10 artículos para cada cliente (usuarios 2-11)
DO $$
DECLARE
    imagen_base64 TEXT := 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCABkAGQDAREAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlbaWmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwD/AD/6ACgAoAKACgAoAKACgAoAKACgD/9k=';
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