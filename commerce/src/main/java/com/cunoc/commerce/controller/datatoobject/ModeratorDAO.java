package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.entity.Article;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModeratorDAO extends BaseDAO {

    // Agregar este método en la clase ModeratorDAO
    public Map<String, Object> getSancionesPaginadas(int pagina, int tamanoPagina) {
        Map<String, Object> resultado = new HashMap<>();
        List<Map<String, Object>> sanciones = new ArrayList<>();

        int offset = (pagina - 1) * tamanoPagina;

        String sql = "SELECT s.id_sancion, s.motivo, s.fecha_hora, s.id_usuario, " +
                "u.nombre || ' ' || u.apellido as nombre_usuario, " +
                "u.email as email_usuario " +
                "FROM Sancion s " +
                "INNER JOIN Usuario u ON s.id_usuario = u.id_usuario " +
                "ORDER BY s.fecha_hora DESC " +
                "LIMIT ? OFFSET ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, tamanoPagina);
            stmt.setInt(2, offset);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> sancion = new HashMap<>();
                sancion.put("id_sancion", rs.getInt("id_sancion"));
                sancion.put("motivo", rs.getString("motivo"));
                sancion.put("fecha_hora", rs.getTimestamp("fecha_hora").toLocalDateTime());
                sancion.put("id_usuario", rs.getInt("id_usuario"));
                sancion.put("nombre_usuario", rs.getString("nombre_usuario"));
                sancion.put("email_usuario", rs.getString("email_usuario"));

                sanciones.add(sancion);
            }

            // Obtener total de sanciones
            int totalSanciones = contarSanciones();
            int totalPaginas = (int) Math.ceil((double) totalSanciones / tamanoPagina);

            resultado.put("sanciones", sanciones);
            resultado.put("totalSanciones", totalSanciones);
            resultado.put("totalPaginas", totalPaginas);
            resultado.put("paginaActual", pagina);

        } catch (SQLException e) {
            System.err.println("Error al obtener sanciones: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return resultado;
    }

    // Método auxiliar para contar total de sanciones
    private int contarSanciones() {
        String sql = "SELECT COUNT(*) as total FROM Sancion";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al contar sanciones: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return 0;
    }

    /// Obtiene artículos pendientes de moderación con paginación
    public Map<String, Object> getArticulosPendientesPaginados(int pagina, int tamanoPagina) {
        Map<String, Object> resultado = new HashMap<>();
        List<Article> articulos = new ArrayList<>();

        int offset = (pagina - 1) * tamanoPagina;

        String sql = "SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, " +
                "a.imagen, a.stock, a.id_estado_articulo, a.id_accion, " +
                "ea.nombre as nombre_estado, " +
                "ma.nombre as nombre_accion " +
                "FROM Articulo a " +
                "INNER JOIN Estado_Articulo ea ON a.id_estado_articulo = ea.id_estado_articulo " +
                "INNER JOIN Moderador_Articulo ma ON a.id_accion = ma.id_estado " +
                "WHERE a.id_accion = 1 " +
                "ORDER BY a.id_articulo ASC " +
                "LIMIT ? OFFSET ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, tamanoPagina);
            stmt.setInt(2, offset);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Article article = mapResultSetToArticle(rs);

                // Cargar categorías del artículo
                ArticleDAO articleDAO = new ArticleDAO();
                List<String> categorias = articleDAO.getCategoriesByArticleId(article.getIdArticulo());
                article.setCategorias(categorias);

                articulos.add(article);
            }

            // Obtener total de artículos pendientes
            int totalArticulos = contarArticulosPendientes();
            int totalPaginas = (int) Math.ceil((double) totalArticulos / tamanoPagina);

            resultado.put("articulos", articulos);
            resultado.put("totalArticulos", totalArticulos);
            resultado.put("totalPaginas", totalPaginas);
            resultado.put("paginaActual", pagina);

        } catch (SQLException e) {
            System.err.println("Error al obtener artículos pendientes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return resultado;
    }

    // Cuenta total de artículos pendientes
    private int contarArticulosPendientes() {
        String sql = "SELECT COUNT(*) as total FROM Articulo WHERE id_accion = 1";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al contar artículos pendientes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return 0;
    }

    // Aprueba un artículo (2)
    public boolean aprobarArticulo(int idArticulo) {
        String sql = "UPDATE Articulo SET id_accion = 2 WHERE id_articulo = ?";
        int filasAfectadas = executeUpdate(sql, idArticulo);
        return filasAfectadas > 0;
    }

    // Rechaza un artículo (3)
    public boolean rechazarArticulo(int idArticulo) {
        String sql = "UPDATE Articulo SET id_accion = 3 WHERE id_articulo = ?";
        int filasAfectadas = executeUpdate(sql, idArticulo);
        return filasAfectadas > 0;
    }

    // Mapea ResultSet a objeto Article
    private Article mapResultSetToArticle(ResultSet rs) throws SQLException {
        Article article = new Article();
        article.setIdArticulo(rs.getInt("id_articulo"));
        article.setNombre(rs.getString("nombre"));
        article.setDescripcion(rs.getString("descripcion"));
        article.setPrecio(rs.getBigDecimal("precio"));
        article.setImagen(rs.getString("imagen"));
        article.setStock(rs.getInt("stock"));
        article.setIdEstadoArticulo(rs.getInt("id_estado_articulo"));
        article.setIdAccion(rs.getInt("id_accion"));
        return article;
    }
}