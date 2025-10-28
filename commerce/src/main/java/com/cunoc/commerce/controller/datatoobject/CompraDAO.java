package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.entity.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import org.springframework.stereotype.Repository;

@Repository 
public class CompraDAO extends BaseDAO {

    // Procesar compra completa
    public RespuestaCompra procesarCompra(CarritoCompra carrito) {
        if (carrito == null || carrito.isEmpty()) return new RespuestaCompra(false, "El carrito está vacío");

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            if (!verificarStock(conn, carrito)) {
                conn.rollback();
                return new RespuestaCompra(false, "Stock insuficiente para uno o más artículos");
            }

            int idVendedor = obtenerIdVendedor(conn, carrito.getItems());
            if (idVendedor == 0) {
                conn.rollback();
                return new RespuestaCompra(false, "No se pudo obtener el vendedor del artículo");
            }

            int idPago = crearPago(conn, carrito.calcularTotal());
            if (idPago == 0) {
                conn.rollback();
                return new RespuestaCompra(false, "Error al crear el pago");
            }

            int idCompra = crearCompra(conn, carrito.getIdUsuario(), idVendedor, idPago);
            if (idCompra == 0) {
                conn.rollback();
                return new RespuestaCompra(false, "Error al crear la compra");
            }

            if (!crearProductos(conn, idCompra, carrito.getItems())) {
                conn.rollback();
                return new RespuestaCompra(false, "Error al registrar los productos");
            }

            if (!actualizarStock(conn, carrito.getItems())) {
                conn.rollback();
                return new RespuestaCompra(false, "Error al actualizar el stock");
            }

            int idPedido = crearPedido(conn, carrito.getIdUsuario());
            if (idPedido == 0) {
                conn.rollback();
                return new RespuestaCompra(false, "Error al crear el pedido");
            }

            conn.commit();

            RespuestaCompra respuesta = new RespuestaCompra(true, "Compra realizada exitosamente");
            respuesta.setIdCompra(idCompra);
            respuesta.setIdPedido(idPedido);
            respuesta.setIdPago(idPago);
            respuesta.setTotal(carrito.calcularTotal());
            return respuesta;

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Error al procesar compra: " + e.getMessage());
            e.printStackTrace();
            return new RespuestaCompra(false, "Error al procesar la compra: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // Obtener ID del vendedor
    private int obtenerIdVendedor(Connection conn, java.util.List<ItemCarrito> items) throws SQLException {
        if (items == null || items.isEmpty()) return 0;
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id_usuario FROM Publicacion WHERE id_articulo = ?")) {
            stmt.setInt(1, items.get(0).getArticulo().getIdArticulo());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id_usuario");
                System.out.println("No se encontró publicación para el artículo ID: " + items.get(0).getArticulo().getIdArticulo());
                return 0;
            }
        }
    }

    // Verificar stock disponible
    private boolean verificarStock(Connection conn, CarritoCompra carrito) throws SQLException {
        for (ItemCarrito item : carrito.getItems()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT stock FROM Articulo WHERE id_articulo = ?")) {
                stmt.setInt(1, item.getArticulo().getIdArticulo());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getInt("stock") < item.getCantidad()) {
                            System.out.println("Stock insuficiente para artículo ID: " + item.getArticulo().getIdArticulo());
                            return false;
                        }
                    } else {
                        System.out.println("Artículo no encontrado ID: " + item.getArticulo().getIdArticulo());
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Crear registro de pago
    private int crearPago(Connection conn, BigDecimal monto) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Pago (monto) VALUES (?) RETURNING id_pago")) {
            stmt.setDouble(1, monto.doubleValue());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("id_pago") : 0;
            }
        }
    }

    // Crear registro de compra
    private int crearCompra(Connection conn, int idComprador, int idVendedor, int idPago) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Compra (fecha_hora, id_comprador, id_vendedor, id_pago) VALUES (?, ?, ?, ?) RETURNING id_compra")) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, idComprador);
            stmt.setInt(3, idVendedor);
            stmt.setInt(4, idPago);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("id_compra") : 0;
            }
        }
    }

    // Crear productos de la compra
    private boolean crearProductos(Connection conn, int idCompra, java.util.List<ItemCarrito> items) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Producto (id_compra, id_articulo, cantidad) VALUES (?, ?, ?)")) {
            for (ItemCarrito item : items) {
                stmt.setInt(1, idCompra);
                stmt.setInt(2, item.getArticulo().getIdArticulo());
                stmt.setInt(3, item.getCantidad());
                stmt.addBatch();
            }
            for (int resultado : stmt.executeBatch()) if (resultado == 0) return false;
            return true;
        }
    }

    // Actualizar stock de artículos
    private boolean actualizarStock(Connection conn, java.util.List<ItemCarrito> items) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("UPDATE Articulo SET stock = stock - ? WHERE id_articulo = ?")) {
            for (ItemCarrito item : items) {
                stmt.setInt(1, item.getCantidad());
                stmt.setInt(2, item.getArticulo().getIdArticulo());
                stmt.addBatch();
            }
            for (int resultado : stmt.executeBatch()) if (resultado == 0) return false;
            return true;
        }
    }

    // Crear pedido de entrega
    private int crearPedido(Connection conn, int idComprador) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Pedido (fecha_hora_entrega, id_comprador, id_estado_pedido) VALUES (?, ?, ?) RETURNING id_pedido")) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().plusDays(5)));
            stmt.setInt(2, idComprador);
            stmt.setInt(3, 1);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("id_pedido") : 0;
            }
        }
    }
}