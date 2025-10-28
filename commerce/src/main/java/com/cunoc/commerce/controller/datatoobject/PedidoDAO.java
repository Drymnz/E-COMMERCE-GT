package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.entity.Pedido;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository 
public class PedidoDAO extends BaseDAO {

    // Buscar pedidos por usuario
    public List<Pedido> findByUsuario(int idUsuario) {
        return ejecutarConsultaPedidos(
            "SELECT id_pedido, fecha_hora_entrega, id_comprador, id_estado_pedido " +
            "FROM Pedido WHERE id_comprador = ? ORDER BY fecha_hora_entrega DESC",
            idUsuario);
    }

    // Buscar pedidos en curso
    public List<Pedido> findPedidosEnCurso() {
        return ejecutarConsultaPedidos(
            "SELECT id_pedido, fecha_hora_entrega, id_comprador, id_estado_pedido FROM Pedido");
    }

    // Buscar pedido por ID
    public Pedido findById(int idPedido) {
        String sql = "SELECT id_pedido, fecha_hora_entrega, id_comprador, id_estado_pedido " +
                "FROM Pedido WHERE id_pedido = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToPedido(rs) : null;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pedido: " + e.getMessage());
            return null;
        }
    }

    // Actualizar fecha de entrega
    public boolean updateFechaEntrega(int idPedido, LocalDateTime nuevaFecha) {
        return executeUpdate("UPDATE Pedido SET fecha_hora_entrega = ? WHERE id_pedido = ?",
            Timestamp.valueOf(nuevaFecha), idPedido) > 0;
    }

    // Actualizar estado del pedido
    public boolean actualizarEstado(int idPedido, int idEstado) {
        return executeUpdate("UPDATE Pedido SET id_estado_pedido = ? WHERE id_pedido = ?",
            idEstado, idPedido) > 0;
    }

    private List<Pedido> ejecutarConsultaPedidos(String sql, Object... params) {
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapResultSetToPedido(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pedidos: " + e.getMessage());
        }
        return pedidos;
    }

    private Pedido mapResultSetToPedido(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(rs.getInt("id_pedido"));
        Timestamp timestamp = rs.getTimestamp("fecha_hora_entrega");
        if (timestamp != null) pedido.setFechaHoraEntrega(timestamp.toLocalDateTime());
        pedido.setIdComprador(rs.getInt("id_comprador"));
        pedido.setIdEstadoPedido(rs.getInt("id_estado_pedido"));
        return pedido;
    }
}