package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.entity.Pedido;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO extends BaseDAO {

    // Obtener pedidos por usuario
    public List<Pedido> findByUsuario(int idUsuario) {
        String sql = "SELECT id_pedido, fecha_hora_entrega, id_comprador, id_estado_pedido " +
                "FROM Pedido WHERE id_comprador = ? ORDER BY fecha_hora_entrega DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Pedido> pedidos = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idUsuario);
            rs = stmt.executeQuery();

            while (rs.next()) {
                pedidos.add(mapResultSetToPedido(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pedidos: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, rs);
        }

        return pedidos;
    }

    // Obtener todos los pedidos en curso
    public List<Pedido> findPedidosEnCurso() {
        String sql = "SELECT id_pedido, fecha_hora_entrega, id_comprador, id_estado_pedido " +
                "FROM Pedido";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Pedido> pedidos = new ArrayList<>();

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                pedidos.add(mapResultSetToPedido(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pedidos en curso: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, rs);
        }

        return pedidos;
    }

    // Obtener pedido por ID
    public Pedido findById(int idPedido) {
        String sql = "SELECT id_pedido, fecha_hora_entrega, id_comprador, id_estado_pedido " +
                "FROM Pedido WHERE id_pedido = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idPedido);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPedido(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pedido: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, rs);
        }

        return null;
    }

    // Actualizar fecha de entrega
    public boolean updateFechaEntrega(int idPedido, LocalDateTime nuevaFecha) {
        String sql = "UPDATE Pedido SET fecha_hora_entrega = ? WHERE id_pedido = ?";
        return executeUpdate(sql, Timestamp.valueOf(nuevaFecha), idPedido) > 0;
    }

    // Actualizar estado del pedido
    public boolean actualizarEstado(int idPedido, int idEstado) {
        String sql = "UPDATE Pedido SET id_estado_pedido = ? WHERE id_pedido = ?";
        return executeUpdate(sql, idEstado, idPedido) > 0;
    }

    // Mapeo de ResultSet a objeto Pedido
    private Pedido mapResultSetToPedido(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(rs.getInt("id_pedido"));

        Timestamp timestamp = rs.getTimestamp("fecha_hora_entrega");
        if (timestamp != null) {
            pedido.setFechaHoraEntrega(timestamp.toLocalDateTime());
        }

        pedido.setIdComprador(rs.getInt("id_comprador"));
        pedido.setIdEstadoPedido(rs.getInt("id_estado_pedido"));
        return pedido;
    }
}