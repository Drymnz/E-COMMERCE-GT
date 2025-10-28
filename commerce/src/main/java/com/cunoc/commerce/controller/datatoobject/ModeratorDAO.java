package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.entity.Article;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.*;

@Repository 
public class ModeratorDAO extends BaseDAO {

    // Obtener sanciones paginadas
    public Map<String, Object> getSancionesPaginadas(int pagina, int tamanoPagina) {
        Map<String, Object> resultado = new HashMap<>();
        List<Map<String, Object>> sanciones = new ArrayList<>();
        
        String sql = "SELECT s.id_sancion, s.motivo, s.fecha_hora, s.id_usuario, " +
                "u.nombre || ' ' || u.apellido as nombre_usuario, u.email as email_usuario " +
                "FROM Sancion s INNER JOIN Usuario u ON s.id_usuario = u.id_usuario " +
                "ORDER BY s.fecha_hora DESC LIMIT ? OFFSET ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tamanoPagina);
            stmt.setInt(2, (pagina - 1) * tamanoPagina);
            
            try (ResultSet rs = stmt.executeQuery()) {
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
            }
            
            int totalSanciones = contarRegistros("SELECT COUNT(*) as total FROM Sancion");
            resultado.put("sanciones", sanciones);
            resultado.put("totalSanciones", totalSanciones);
            resultado.put("totalPaginas", (int) Math.ceil((double) totalSanciones / tamanoPagina));
            resultado.put("paginaActual", pagina);
            
        } catch (SQLException e) {
            System.err.println("Error al obtener sanciones: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultado;
    }

    // Obtener artículos pendientes paginados
    public Map<String, Object> getArticulosPendientesPaginados(int pagina, int tamanoPagina) {
        Map<String, Object> resultado = new HashMap<>();
        List<Article> articulos = new ArrayList<>();
        
        String sql = "SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, " +
                "a.imagen, a.stock, a.id_estado_articulo, a.id_accion " +
                "FROM Articulo a WHERE a.id_accion = 1 " +
                "ORDER BY a.id_articulo ASC LIMIT ? OFFSET ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tamanoPagina);
            stmt.setInt(2, (pagina - 1) * tamanoPagina);
            
            try (ResultSet rs = stmt.executeQuery()) {
                ArticleDAO articleDAO = new ArticleDAO();
                while (rs.next()) {
                    Article article = mapResultSetToArticle(rs);
                    article.setCategorias(articleDAO.getCategoriesByArticleId(article.getIdArticulo()));
                    articulos.add(article);
                }
            }
            
            int totalArticulos = contarRegistros("SELECT COUNT(*) as total FROM Articulo WHERE id_accion = 1");
            resultado.put("articulos", articulos);
            resultado.put("totalArticulos", totalArticulos);
            resultado.put("totalPaginas", (int) Math.ceil((double) totalArticulos / tamanoPagina));
            resultado.put("paginaActual", pagina);
            
        } catch (SQLException e) {
            System.err.println("Error al obtener artículos pendientes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultado;
    }

    private int contarRegistros(String sql) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            System.err.println("Error al contar registros: " + e.getMessage());
            return 0;
        }
    }

    // Aprobar artículo
    public boolean aprobarArticulo(int idArticulo) {
        return executeUpdate("UPDATE Articulo SET id_accion = 2 WHERE id_articulo = ?", idArticulo) > 0;
    }

    // Rechazar artículo
    public boolean rechazarArticulo(int idArticulo) {
        return executeUpdate("UPDATE Articulo SET id_accion = 3 WHERE id_articulo = ?", idArticulo) > 0;
    }

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