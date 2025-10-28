package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.entity.Article;
import com.cunoc.commerce.entity.Publicacion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository 
public class ArticleDAO extends BaseDAO {

    // Obtener todos los artículos disponibles
    public List<Article> findAllAvailable() {
        return findArticles("SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, a.imagen, a.stock, a.id_estado_articulo, ea.nombre as nombre_estado, a.id_accion " +
                "FROM Articulo a INNER JOIN Estado_Articulo ea ON a.id_estado_articulo = ea.id_estado_articulo " +
                "WHERE a.stock > 0 AND a.id_accion = 2 ORDER BY a.id_articulo DESC");
    }

    // Buscar artículos por ID de usuario
    public List<Article> findByUserId(int idUsuario) {
        return findArticles("SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, a.imagen, a.stock, a.id_estado_articulo, ea.nombre as nombre_estado, a.id_accion " +
                "FROM Articulo a INNER JOIN Estado_Articulo ea ON a.id_estado_articulo = ea.id_estado_articulo " +
                "INNER JOIN Publicacion p ON a.id_articulo = p.id_articulo WHERE p.id_usuario = ? ORDER BY p.fecha_hora_entrega DESC", idUsuario);
    }

    // Buscar artículo por ID
    public Article findById(int idArticulo) {
        List<Article> articles = findArticles("SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, a.imagen, a.stock, a.id_estado_articulo, ea.nombre as nombre_estado, a.id_accion " +
                "FROM Articulo a INNER JOIN Estado_Articulo ea ON a.id_estado_articulo = ea.id_estado_articulo WHERE a.id_articulo = ?", idArticulo);
        return articles.isEmpty() ? null : articles.get(0);
    }

    // Buscar artículo por ID y ID de usuario
    public Article findByIdAndUserId(int idArticulo, int idUsuario) {
        List<Article> articles = findArticles("SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, a.imagen, a.stock, a.id_estado_articulo, ea.nombre as nombre_estado, a.id_accion " +
                "FROM Articulo a INNER JOIN Estado_Articulo ea ON a.id_estado_articulo = ea.id_estado_articulo " +
                "INNER JOIN Publicacion p ON a.id_articulo = p.id_articulo WHERE a.id_articulo = ? AND p.id_usuario = ?", idArticulo, idUsuario);
        return articles.isEmpty() ? null : articles.get(0);
    }

    // Buscar artículos por nombre o descripción
    public List<Article> searchByNameOrDescription(String searchTerm) {
        String term = "%" + searchTerm + "%";
        return findArticles("SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, a.imagen, a.stock, a.id_estado_articulo, ea.nombre as nombre_estado, a.id_accion " +
                "FROM Articulo a INNER JOIN Estado_Articulo ea ON a.id_estado_articulo = ea.id_estado_articulo " +
                "WHERE (LOWER(a.nombre) LIKE LOWER(?) OR LOWER(a.descripcion) LIKE LOWER(?)) AND a.stock > 0 ORDER BY a.id_articulo DESC", term, term);
    }

    private List<Article> findArticles(String sql, Object... params) {
        List<Article> articles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            rs = executeQuery(sql, params);
            conn = rs.getStatement().getConnection();
            stmt = (PreparedStatement) rs.getStatement();
            while (rs.next()) {
                Article article = mapResultSetToArticle(rs);
                article.setCategorias(getCategoriesByArticleId(article.getIdArticulo()));
                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar artículos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        return articles;
    }

    // Actualizar estado del artículo
    public boolean updateStatus(int idArticulo, int idEstadoArticulo) {
        return executeUpdate("UPDATE Articulo SET id_estado_articulo = ? WHERE id_articulo = ?", idEstadoArticulo, idArticulo) > 0;
    }

    // Crear nueva publicación
    public int create(Publicacion publicacion) {
        Connection conn = null;
        PreparedStatement stmtArticulo = null, stmtPublicacion = null, stmtCategoria = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            Article articulo = publicacion.getArticulo();

            stmtArticulo = conn.prepareStatement("INSERT INTO Articulo (nombre, descripcion, precio, imagen, stock, id_estado_articulo, id_accion) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmtArticulo.setString(1, articulo.getNombre());
            stmtArticulo.setString(2, articulo.getDescripcion());
            stmtArticulo.setBigDecimal(3, calculateCommission(articulo.getPrecio()));
            stmtArticulo.setString(4, articulo.getImagen());
            stmtArticulo.setInt(5, articulo.getStock());
            stmtArticulo.setInt(6, articulo.getIdEstadoArticulo());
            stmtArticulo.setInt(7, articulo.getIdAccion() != null ? articulo.getIdAccion() : 1);

            if (stmtArticulo.executeUpdate() > 0) {
                rs = stmtArticulo.getGeneratedKeys();
                if (rs.next()) {
                    int idArticulo = rs.getInt(1);
                    articulo.setIdArticulo(idArticulo);

                    stmtPublicacion = conn.prepareStatement("INSERT INTO Publicacion (id_articulo, id_usuario, fecha_hora_entrega) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    stmtPublicacion.setInt(1, idArticulo);
                    stmtPublicacion.setInt(2, publicacion.getIdUsuario());
                    stmtPublicacion.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

                    if (stmtPublicacion.executeUpdate() > 0) {
                        ResultSet rsPublicacion = stmtPublicacion.getGeneratedKeys();
                        if (rsPublicacion.next()) publicacion.setIdPublicacion(rsPublicacion.getInt(1));
                        rsPublicacion.close();

                        if (articulo.getCategorias() != null && !articulo.getCategorias().isEmpty()) {
                            stmtCategoria = conn.prepareStatement("INSERT INTO Categoria (id_articulo, id_categoria_tipo) VALUES (?, ?)");
                            for (String idCategoria : articulo.getCategorias()) {
                                stmtCategoria.setInt(1, idArticulo);
                                stmtCategoria.setInt(2, Integer.parseInt(idCategoria));
                                stmtCategoria.addBatch();
                            }
                            stmtCategoria.executeBatch();
                        }
                        conn.commit();
                        return idArticulo;
                    }
                }
            }
            conn.rollback();
            return 0;
        } catch (SQLException e) {
            System.err.println("Error al crear publicación: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { }
            return 0;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) { }
            closeResources(conn, stmtArticulo, rs);
            if (stmtPublicacion != null) try { stmtPublicacion.close(); } catch (SQLException e) { }
            if (stmtCategoria != null) try { stmtCategoria.close(); } catch (SQLException e) { }
        }
    }

    private BigDecimal calculateCommission(BigDecimal p) {
        return p.divide(new BigDecimal("0.95"), 2, RoundingMode.HALF_UP);
    }

    // Actualizar artículo
    public boolean update(Article article) {
        return executeUpdate("UPDATE Articulo SET nombre = ?, descripcion = ?, precio = ?, imagen = ?, stock = ?, id_estado_articulo = ?, id_accion = ? WHERE id_articulo = ?",
                article.getNombre(), article.getDescripcion(), article.getPrecio(), article.getImagen(), 
                article.getStock(), article.getIdEstadoArticulo(), article.getIdAccion(), article.getIdArticulo()) > 0;
    }

    // Eliminar artículo
    public boolean delete(int idArticulo) {
        return executeUpdate("DELETE FROM Articulo WHERE id_articulo = ?", idArticulo) > 0;
    }

    // Actualizar stock del artículo
    public boolean updateStock(int idArticulo, int newStock) {
        return executeUpdate("UPDATE Articulo SET stock = ? WHERE id_articulo = ?", newStock, idArticulo) > 0;
    }

    // Decrementar stock del artículo
    public boolean decrementStock(int idArticulo, int quantity) {
        return executeUpdate("UPDATE Articulo SET stock = stock - ? WHERE id_articulo = ? AND stock >= ?", quantity, idArticulo, quantity) > 0;
    }

    // Obtener categorías por ID de artículo
    public List<String> getCategoriesByArticleId(int idArticulo) {
        List<String> categories = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            rs = executeQuery("SELECT tc.nombre FROM Categoria c INNER JOIN Tipo_Categoria tc ON c.id_categoria_tipo = tc.id_categoria WHERE c.id_articulo = ?", idArticulo);
            conn = rs.getStatement().getConnection();
            stmt = (PreparedStatement) rs.getStatement();
            while (rs.next()) categories.add(rs.getString("nombre"));
        } catch (SQLException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        return categories;
    }

    private Article mapResultSetToArticle(ResultSet rs) throws SQLException {
        Article a = new Article();
        a.setIdArticulo(rs.getInt("id_articulo"));
        a.setNombre(rs.getString("nombre"));
        a.setDescripcion(rs.getString("descripcion"));
        a.setPrecio(rs.getBigDecimal("precio"));
        a.setImagen(rs.getString("imagen"));
        a.setStock(rs.getInt("stock"));
        a.setIdEstadoArticulo(rs.getInt("id_estado_articulo"));
        a.setNombreEstado(rs.getString("nombre_estado"));
        a.setIdAccion(rs.getInt("id_accion"));
        return a;
    }
}