package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.entity.Article;
import com.cunoc.commerce.entity.Usuario;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository 
public class ReportDAO extends BaseDAO {

    // Obtener top productos más vendidos
    public List<Article> getTopProductosMasVendidos(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        String sql = "SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, a.imagen, a.stock, " +
                "a.id_estado_articulo, ea.nombre AS nombre_estado, a.id_accion " +
                "FROM Producto p " +
                "INNER JOIN Articulo a ON p.id_articulo = a.id_articulo " +
                "INNER JOIN Compra c ON p.id_compra = c.id_compra " +
                "INNER JOIN Estado_Articulo ea ON a.id_estado_articulo = ea.id_estado_articulo " +
                "WHERE c.fecha_hora BETWEEN ? AND ? " +
                "GROUP BY a.id_articulo, a.nombre, a.descripcion, a.precio, a.imagen, " +
                "a.stock, a.id_estado_articulo, ea.nombre, a.id_accion " +
                "ORDER BY SUM(p.cantidad) DESC LIMIT 10";

        List<Article> productos = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepararStatement(conn, sql, fechaInicio, fechaFin);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Article article = new Article(rs.getString("nombre"), rs.getString("descripcion"),
                    rs.getBigDecimal("precio"), rs.getString("imagen"), rs.getInt("stock"),
                    rs.getInt("id_estado_articulo"), rs.getInt("id_accion"));
                article.setIdArticulo(rs.getInt("id_articulo"));
                article.setNombreEstado(rs.getString("nombre_estado"));
                productos.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener top productos vendidos: " + e.getMessage());
        }
        return productos;
    }

    // Obtener top clientes por ganancias
    public List<Usuario> getTopClientesPorGanancias(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        String sql = "SELECT u.id_usuario, u.nombre, u.apellido, u.email, u.id_estado, u.id_rol " +
                "FROM Usuario u " +
                "INNER JOIN Compra c ON u.id_usuario = c.id_comprador " +
                "INNER JOIN Pago pg ON c.id_pago = pg.id_pago " +
                "WHERE c.fecha_hora BETWEEN ? AND ? " +
                "GROUP BY u.id_usuario, u.nombre, u.apellido, u.email, u.id_estado, u.id_rol " +
                "ORDER BY SUM(pg.monto) DESC LIMIT 5";
        
        return consultarTopUsuarios(sql, fechaInicio, fechaFin);
    }

    // Obtener top clientes por ventas
    public List<Usuario> getTopClientesPorVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        String sql = "SELECT u.id_usuario, u.nombre, u.apellido, u.email, u.id_estado, u.id_rol " +
                "FROM Usuario u " +
                "INNER JOIN Compra c ON u.id_usuario = c.id_vendedor " +
                "INNER JOIN Producto p ON c.id_compra = p.id_compra " +
                "WHERE c.fecha_hora BETWEEN ? AND ? " +
                "GROUP BY u.id_usuario, u.nombre, u.apellido, u.email, u.id_estado, u.id_rol " +
                "ORDER BY SUM(p.cantidad) DESC LIMIT 5";
        
        return consultarTopUsuarios(sql, fechaInicio, fechaFin);
    }

    // Obtener top clientes por pedidos
    public List<Usuario> getTopClientesPorPedidos(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        String sql = "SELECT u.id_usuario, u.nombre, u.apellido, u.email, u.id_estado, u.id_rol " +
                "FROM (SELECT id_comprador, COUNT(*) AS total_pedidos FROM Pedido " +
                "WHERE fecha_hora_entrega BETWEEN ? AND ? " +
                "GROUP BY id_comprador ORDER BY total_pedidos DESC LIMIT 10) AS top_compradores " +
                "INNER JOIN Usuario u ON top_compradores.id_comprador = u.id_usuario " +
                "ORDER BY top_compradores.total_pedidos DESC";
        
        return consultarTopUsuarios(sql, fechaInicio, fechaFin);
    }

    // Obtener top clientes con más productos en venta
    public List<Usuario> getTopClientesConMasProductosEnVenta() {
        String sql = "SELECT u.id_usuario, u.nombre, u.apellido, u.email, u.id_estado, u.id_rol " +
                "FROM Usuario u " +
                "INNER JOIN Publicacion pub ON u.id_usuario = pub.id_usuario " +
                "INNER JOIN Articulo a ON pub.id_articulo = a.id_articulo " +
                "WHERE a.stock > 0 AND a.id_accion = 2 " +
                "GROUP BY u.id_usuario, u.nombre, u.apellido, u.email, u.id_estado, u.id_rol " +
                "ORDER BY COUNT(DISTINCT pub.id_articulo) DESC LIMIT 10";
        
        return consultarTopUsuarios(sql);
    }

    private List<Usuario> consultarTopUsuarios(String sql, LocalDateTime... fechas) {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = fechas.length > 0 ? prepararStatement(conn, sql, fechas) : conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(new Usuario(rs.getInt("id_usuario"), rs.getString("nombre"),
                    rs.getString("apellido"), rs.getString("email"),
                    rs.getInt("id_estado"), rs.getInt("id_rol")));
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    private PreparedStatement prepararStatement(Connection conn, String sql, LocalDateTime... fechas) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 0; i < fechas.length; i++) {
            stmt.setTimestamp(i + 1, Timestamp.valueOf(fechas[i]));
        }
        return stmt;
    }
}