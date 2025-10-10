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
    imagen VARCHAR(255),
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
-- Estado: activo (id_estado = 2)
-- Rol: Administrador (id_rol = 4)
INSERT INTO Usuario (nombre, apellido, email, password, id_estado, id_rol) VALUES 
('María Elena', 'García López', 'cliente@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 1),
('Carlos Alberto', 'Rodríguez Méndez', 'moderador@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 2),
('Ana Patricia', 'Hernández Castro', 'logística@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 3),
('Benjamin de Jesus', 'Perez Aguilar', 'admin@commerce.com', '$2a$12$MGXSpXuY2OWJlFwWZp8zdOhjGoz7YuHcF.Cy3rhUTCXbzVsCTHAYe', 2, 4);