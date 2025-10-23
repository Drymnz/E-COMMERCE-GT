package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.entity.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

public class CompraDAO extends BaseDAO {

    /**
     * @param carrito Carrito con items a comprar
     * @return RespuestaCompra con los IDs generados
     */
    public RespuestaCompra procesarCompra(CarritoCompra carrito) {
        if (carrito == null || carrito.isEmpty()) {
            return new RespuestaCompra(false, "El carrito está vacío");
        }

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Verificar stock de todos los artículos
            if (!verificarStock(conn, carrito)) {
                conn.rollback();
                return new RespuestaCompra(false, "Stock insuficiente para uno o más artículos");
            }

            // 2. Obtener el ID del vendedor desde la tabla Publicacion
            int idVendedor = obtenerIdVendedor(conn, carrito.getItems());
            if (idVendedor == 0) {
                conn.rollback();
                return new RespuestaCompra(false, "No se pudo obtener el vendedor del artículo");
            }

            // 3. Crear el Pago
            int idPago = crearPago(conn, carrito.calcularTotal());
            if (idPago == 0) {
                conn.rollback();
                return new RespuestaCompra(false, "Error al crear el pago");
            }

            // 4. Crear la Compra
            int idCompra = crearCompra(conn, carrito.getIdUsuario(), idVendedor, idPago);
            if (idCompra == 0) {
                conn.rollback();
                return new RespuestaCompra(false, "Error al crear la compra");
            }

            // 5. Crear los Productos (items de la compra)
            if (!crearProductos(conn, idCompra, carrito.getItems())) {
                conn.rollback();
                return new RespuestaCompra(false, "Error al registrar los productos");
            }

            // 6. Actualizar stock de artículos
            if (!actualizarStock(conn, carrito.getItems())) {
                conn.rollback();
                return new RespuestaCompra(false, "Error al actualizar el stock");
            }

            // 7. Crear el Pedido
            int idPedido = crearPedido(conn, carrito.getIdUsuario());
            if (idPedido == 0) {
                conn.rollback();
                return new RespuestaCompra(false, "Error al crear el pedido");
            }

            conn.commit(); // Confirmar transacción

            // Crear respuesta exitosa
            RespuestaCompra respuesta = new RespuestaCompra(true, "Compra realizada exitosamente");
            respuesta.setIdCompra(idCompra);
            respuesta.setIdPedido(idPedido);
            respuesta.setIdPago(idPago);
            respuesta.setTotal(carrito.calcularTotal());

            return respuesta;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Error al procesar compra: " + e.getMessage());
            e.printStackTrace();
            return new RespuestaCompra(false, "Error al procesar la compra: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Obtiene el ID del vendedor desde la tabla Publicacion usando el primer artículo
    private int obtenerIdVendedor(Connection conn, java.util.List<ItemCarrito> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            return 0;
        }
        
        String sql = "SELECT id_usuario FROM Publicacion WHERE id_articulo = ?";
        
        // Usamos el primer artículo del carrito para obtener el vendedor
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, items.get(0).getArticulo().getIdArticulo());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_usuario");
                } else {
                    System.out.println("No se encontró publicación para el artículo ID: " + 
                                     items.get(0).getArticulo().getIdArticulo());
                    return 0;
                }
            }
        }
    }

    //Verificar stock
    private boolean verificarStock(Connection conn, CarritoCompra carrito) throws SQLException {
        String sql = "SELECT stock FROM Articulo WHERE id_articulo = ?";
        
        for (ItemCarrito item : carrito.getItems()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, item.getArticulo().getIdArticulo());
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int stockDisponible = rs.getInt("stock");
                        if (stockDisponible < item.getCantidad()) {
                            System.out.println("Stock insuficiente para artículo ID: " + 
                                             item.getArticulo().getIdArticulo());
                            return false;
                        }
                    } else {
                        System.out.println("Artículo no encontrado ID: " + 
                                         item.getArticulo().getIdArticulo());
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //Crea Pago
    private int crearPago(Connection conn, BigDecimal monto) throws SQLException {
        String sql = "INSERT INTO Pago (monto) VALUES (?) RETURNING id_pago";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, monto.doubleValue());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_pago");
                }
            }
        }
        return 0;
    }

    //Crea  Compra
    private int crearCompra(Connection conn, int idComprador, int idVendedor, int idPago) 
            throws SQLException {
        String sql = """
            INSERT INTO Compra (fecha_hora, id_comprador, id_vendedor, id_pago)
            VALUES (?, ?, ?, ?)
            RETURNING id_compra
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, idComprador);
            stmt.setInt(3, idVendedor);
            stmt.setInt(4, idPago);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_compra");
                }
            }
        }
        return 0;
    }

    //Crea  Producto 
    private boolean crearProductos(Connection conn, int idCompra, java.util.List<ItemCarrito> items) 
            throws SQLException {
        String sql = """
            INSERT INTO Producto (id_compra, id_articulo, cantidad)
            VALUES (?, ?, ?)
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (ItemCarrito item : items) {
                stmt.setInt(1, idCompra);
                stmt.setInt(2, item.getArticulo().getIdArticulo());
                stmt.setInt(3, item.getCantidad());
                stmt.addBatch();
            }
            
            int[] resultados = stmt.executeBatch();
            // Verificar que todos se insertaron correctamente
            for (int resultado : resultados) {
                if (resultado == 0) {
                    return false;
                }
            }
            return true;
        }
    }

    //Actualiza el stock 
    private boolean actualizarStock(Connection conn, java.util.List<ItemCarrito> items) 
            throws SQLException {
        String sql = """
            UPDATE Articulo 
            SET stock = stock - ?
            WHERE id_articulo = ?
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (ItemCarrito item : items) {
                stmt.setInt(1, item.getCantidad());
                stmt.setInt(2, item.getArticulo().getIdArticulo());
                stmt.addBatch();
            }
            
            int[] resultados = stmt.executeBatch();
            for (int resultado : resultados) {
                if (resultado == 0) {
                    return false;
                }
            }
            return true;
        }
    }

    //Crea un registro de Pedido (fecha de entrega a 10 días)
    private int crearPedido(Connection conn, int idComprador) throws SQLException {
        String sql = """
            INSERT INTO Pedido (fecha_hora_entrega, id_comprador, id_estado_pedido)
            VALUES (?, ?, ?)
            RETURNING id_pedido
            """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Fecha de entrega: 10 días desde ahora
            LocalDateTime fechaEntrega = LocalDateTime.now().plusDays(10);
            stmt.setTimestamp(1, Timestamp.valueOf(fechaEntrega));
            stmt.setInt(2, idComprador);
            stmt.setInt(3, 1); // Estado "En Curso" (id = 1)
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_pedido");
                }
            }
        }
        return 0;
    }
}