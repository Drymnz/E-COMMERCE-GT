package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.entity.Article;
import com.cunoc.commerce.entity.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO extends BaseDAO {

    // Top 10 productos más vendidos en un intervalo de tiempo
    public List<Article> getTopProductosMasVendidos(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        String sql = """
                SELECT
                    a.id_articulo,
                    a.nombre,
                    a.descripcion,
                    a.precio,
                    a.imagen,
                    a.stock,
                    a.id_estado_articulo,
                    ea.nombre AS nombre_estado,
                    a.id_accion
                FROM Producto p
                INNER JOIN Articulo a ON p.id_articulo = a.id_articulo
                INNER JOIN Compra c ON p.id_compra = c.id_compra
                INNER JOIN Estado_Articulo ea ON a.id_estado_articulo = ea.id_estado_articulo
                WHERE c.fecha_hora BETWEEN ? AND ?
                GROUP BY a.id_articulo, a.nombre, a.descripcion, a.precio, a.imagen,
                         a.stock, a.id_estado_articulo, ea.nombre, a.id_accion
                ORDER BY SUM(p.cantidad) DESC
                LIMIT 10
                """;

        List<Article> productos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(fechaInicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fechaFin));

            rs = stmt.executeQuery();

            while (rs.next()) {
                Article article = new Article(
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getBigDecimal("precio"),
                        rs.getString("imagen"),
                        rs.getInt("stock"),
                        rs.getInt("id_estado_articulo"),
                        rs.getInt("id_accion"));
                article.setIdArticulo(rs.getInt("id_articulo"));
                article.setNombreEstado(rs.getString("nombre_estado"));
                productos.add(article);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener top productos vendidos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return productos;
    }

    // Top 5 clientes que más ganancias por compras han generado
    public List<Usuario> getTopClientesPorGanancias(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        String sql = """
                SELECT
                    u.id_usuario,
                    u.nombre,
                    u.apellido,
                    u.email,
                    u.id_estado,
                    u.id_rol
                FROM Usuario u
                INNER JOIN Compra c ON u.id_usuario = c.id_comprador
                INNER JOIN Pago pg ON c.id_pago = pg.id_pago
                WHERE c.fecha_hora BETWEEN ? AND ?
                GROUP BY u.id_usuario, u.nombre, u.apellido, u.email, u.id_estado, u.id_rol
                ORDER BY SUM(pg.monto) DESC
                LIMIT 5
                """;

        List<Usuario> clientes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(fechaInicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fechaFin));

            rs = stmt.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getInt("id_estado"),
                        rs.getInt("id_rol"));
                clientes.add(usuario);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener top clientes por ganancias: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return clientes;
    }

    // Top 5 clientes que más productos han vendido
    public List<Usuario> getTopClientesPorVentas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        String sql = """
                SELECT
                    u.id_usuario,
                    u.nombre,
                    u.apellido,
                    u.email,
                    u.id_estado,
                    u.id_rol
                FROM Usuario u
                INNER JOIN Compra c ON u.id_usuario = c.id_vendedor
                INNER JOIN Producto p ON c.id_compra = p.id_compra
                WHERE c.fecha_hora BETWEEN ? AND ?
                GROUP BY u.id_usuario, u.nombre, u.apellido, u.email, u.id_estado, u.id_rol
                ORDER BY SUM(p.cantidad) DESC
                LIMIT 5
                """;

        List<Usuario> clientes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(fechaInicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fechaFin));

            rs = stmt.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getInt("id_estado"),
                        rs.getInt("id_rol"));
                clientes.add(usuario);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener top clientes por ventas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return clientes;
    }

    // Top 10 clientes que más pedidos han realizado
    /**
     * Top 10 clientes que más pedidos han realizado
     */
    public List<Usuario> getTopClientesPorPedidos(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        String sql = """
                SELECT
                    u.id_usuario,
                    u.nombre,
                    u.apellido,
                    u.email,
                    u.id_estado,
                    u.id_rol
                FROM (
                    SELECT
                        id_comprador,
                        COUNT(*) AS total_pedidos
                    FROM Pedido
                    WHERE fecha_hora_entrega BETWEEN ? AND ?
                    GROUP BY id_comprador
                    ORDER BY total_pedidos DESC
                    LIMIT 10
                ) AS top_compradores
                INNER JOIN Usuario u ON top_compradores.id_comprador = u.id_usuario
                ORDER BY top_compradores.total_pedidos DESC
                """;

        List<Usuario> clientes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(fechaInicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fechaFin));

            rs = stmt.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getInt("id_estado"),
                        rs.getInt("id_rol"));
                clientes.add(usuario);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener top clientes por pedidos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return clientes;
    }

    // Top 10 clientes que más productos tienen a la venta
    public List<Usuario> getTopClientesConMasProductosEnVenta() {
        String sql = """
                SELECT
                    u.id_usuario,
                    u.nombre,
                    u.apellido,
                    u.email,
                    u.id_estado,
                    u.id_rol
                FROM Usuario u
                INNER JOIN Publicacion pub ON u.id_usuario = pub.id_usuario
                INNER JOIN Articulo a ON pub.id_articulo = a.id_articulo
                WHERE a.stock > 0
                    AND a.id_accion = 1
                GROUP BY u.id_usuario, u.nombre, u.apellido, u.email, u.id_estado, u.id_rol
                ORDER BY COUNT(DISTINCT pub.id_articulo) DESC
                LIMIT 10
                """;

        List<Usuario> clientes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getInt("id_estado"),
                        rs.getInt("id_rol"));
                clientes.add(usuario);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener top clientes con productos en venta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return clientes;
    }
}