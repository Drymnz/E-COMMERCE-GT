package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.entity.Article;
import com.cunoc.commerce.entity.Publicacion;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAO extends BaseDAO {

    // Lista todos los artículos disponibles (con stock > 0 Y aprobados por moderador)
    public List<Article> findAllAvailable() {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, " +
                "a.imagen, a.stock, a.id_estado_articulo, ea.nombre as nombre_estado, a.id_accion " +
                "FROM Articulo a " +
                "INNER JOIN Estado_Articulo ea ON a.id_estado_articulo = ea.id_estado_articulo " +
                "WHERE a.stock > 0 " +
                "AND a.id_accion = 2 " + 
                "ORDER BY a.id_articulo DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            rs = executeQuery(sql);
            conn = rs.getStatement().getConnection();
            stmt = (PreparedStatement) rs.getStatement();

            while (rs.next()) {
                Article article = mapResultSetToArticle(rs);

                // Cargar las categorías del artículo
                List<String> categorias = getCategoriesByArticleId(article.getIdArticulo());
                article.setCategorias(categorias);

                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar artículos disponibles: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return articles;
    }

    // Lista todos los artículos de un usuario específico
    public List<Article> findByUserId(int idUsuario) {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, " +
                "a.imagen, a.stock, a.id_estado_articulo, ea.nombre as nombre_estado, a.id_accion " +
                "FROM Articulo a " +
                "INNER JOIN Estado_Articulo ea ON a.id_estado_articulo = ea.id_estado_articulo " +
                "INNER JOIN Publicacion p ON a.id_articulo = p.id_articulo " +
                "WHERE p.id_usuario = ? " +
                "ORDER BY p.fecha_hora_entrega DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            rs = executeQuery(sql, idUsuario);
            conn = rs.getStatement().getConnection();
            stmt = (PreparedStatement) rs.getStatement();

            while (rs.next()) {
                Article article = mapResultSetToArticle(rs);

                // Cargar las categorías del artículo
                List<String> categorias = getCategoriesByArticleId(article.getIdArticulo());
                article.setCategorias(categorias);

                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar artículos del usuario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return articles;
    }

    // Busca un artículo por ID
    public Article findById(int idArticulo) {
        String sql = "SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, " +
                "a.imagen, a.stock, a.id_estado_articulo, ea.nombre as nombre_estado, a.id_accion " +
                "FROM Articulo a " +
                "INNER JOIN Estado_Articulo ea ON a.id_estado_articulo = ea.id_estado_articulo " +
                "WHERE a.id_articulo = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            rs = executeQuery(sql, idArticulo);
            conn = rs.getStatement().getConnection();
            stmt = (PreparedStatement) rs.getStatement();

            if (rs.next()) {
                Article article = mapResultSetToArticle(rs);
                // Cargar las categorías del artículo
                List<String> categorias = getCategoriesByArticleId(article.getIdArticulo());
                article.setCategorias(categorias);

                return article;
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar artículo por ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return null;
    }

    // Veriricar si se actuliza el nuevo estado
    public boolean updateStatus(int idArticulo, int idEstadoArticulo) {
        String sql = "UPDATE Articulo SET id_estado_articulo = ? WHERE id_articulo = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idEstadoArticulo);
            stmt.setInt(2, idArticulo);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del artículo: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    public Article findByIdAndUserId(int idArticulo, int idUsuario) {
        String sql = "SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, " +
                "a.imagen, a.stock, a.id_estado_articulo, ea.nombre as nombre_estado, a.id_accion " +
                "FROM Articulo a " +
                "INNER JOIN Estado_Articulo ea ON a.id_estado_articulo = ea.id_estado_articulo " +
                "INNER JOIN Publicacion p ON a.id_articulo = p.id_articulo " +
                "WHERE a.id_articulo = ? AND p.id_usuario = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            rs = executeQuery(sql, idArticulo, idUsuario);
            conn = rs.getStatement().getConnection();
            stmt = (PreparedStatement) rs.getStatement();

            if (rs.next()) {
                Article article = mapResultSetToArticle(rs);

                // Cargar las categorías del artículo
                List<String> categorias = getCategoriesByArticleId(article.getIdArticulo());
                article.setCategorias(categorias);

                return article;
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar artículo por ID y usuario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return null;
    }

    // Crea un artículo y su publicación en una transacción
    public int create(Publicacion publicacion) {
        String sqlArticulo = "INSERT INTO Articulo (nombre, descripcion, precio, imagen, stock, id_estado_articulo, id_accion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlPublicacion = "INSERT INTO Publicacion (id_articulo, id_usuario, fecha_hora_entrega) " +
                "VALUES (?, ?, ?)";
        String sqlCategoria = "INSERT INTO Categoria (id_articulo, id_categoria_tipo) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement stmtArticulo = null;
        PreparedStatement stmtPublicacion = null;
        PreparedStatement stmtCategoria = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            Article articulo = publicacion.getArticulo();

            // 1. Insertar el artículo
            stmtArticulo = conn.prepareStatement(sqlArticulo, Statement.RETURN_GENERATED_KEYS);
            stmtArticulo.setString(1, articulo.getNombre());
            stmtArticulo.setString(2, articulo.getDescripcion());
            stmtArticulo.setBigDecimal(3, articulo.getPrecio());
            stmtArticulo.setString(4, articulo.getImagen());
            stmtArticulo.setInt(5, articulo.getStock());
            stmtArticulo.setInt(6, articulo.getIdEstadoArticulo());
            stmtArticulo.setInt(7, articulo.getIdAccion() != null ? articulo.getIdAccion() : 1);

            int affectedRows = stmtArticulo.executeUpdate();

            if (affectedRows > 0) {
                rs = stmtArticulo.getGeneratedKeys();
                if (rs.next()) {
                    int idArticulo = rs.getInt(1);
                    articulo.setIdArticulo(idArticulo);

                    // 2. Insertar la publicación
                    stmtPublicacion = conn.prepareStatement(sqlPublicacion, Statement.RETURN_GENERATED_KEYS);
                    stmtPublicacion.setInt(1, idArticulo);
                    stmtPublicacion.setInt(2, publicacion.getIdUsuario());
                    stmtPublicacion.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

                    int publicacionInserted = stmtPublicacion.executeUpdate();

                    if (publicacionInserted > 0) {
                        ResultSet rsPublicacion = stmtPublicacion.getGeneratedKeys();
                        if (rsPublicacion.next()) {
                            int idPublicacion = rsPublicacion.getInt(1);
                            publicacion.setIdPublicacion(idPublicacion);
                            rsPublicacion.close();
                        }

                        // 3. Insertar las categorías
                        if (articulo.getCategorias() != null && !articulo.getCategorias().isEmpty()) {
                            stmtCategoria = conn.prepareStatement(sqlCategoria);
                            for (String idCategoria : articulo.getCategorias()) {
                                stmtCategoria.setInt(1, idArticulo);
                                stmtCategoria.setInt(2, Integer.parseInt(idCategoria));
                                stmtCategoria.addBatch();
                            }
                            stmtCategoria.executeBatch();
                        }

                        conn.commit();
                        return idArticulo;
                    } else {
                        conn.rollback();
                        System.err.println("Error: No se pudo crear la publicación");
                        return 0;
                    }
                }
            }

            conn.rollback();
            return 0;

        } catch (SQLException e) {
            System.err.println("Error al crear publicación: " + e.getMessage());
            e.printStackTrace();

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            return 0;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error al restaurar autocommit: " + e.getMessage());
                }
            }
            closeResources(conn, stmtArticulo, rs);
            if (stmtPublicacion != null) {
                try {
                    stmtPublicacion.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar statement de publicación: " + e.getMessage());
                }
            }
            if (stmtCategoria != null) {
                try {
                    stmtCategoria.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar statement de categoría: " + e.getMessage());
                }
            }
        }
    }

    // Actualiza un artículo existente
    public boolean update(Article article) {
        String sql = "UPDATE Articulo SET nombre = ?, descripcion = ?, precio = ?, " +
                "imagen = ?, stock = ?, id_estado_articulo = ?, id_accion = ? " +
                "WHERE id_articulo = ?";

        int rowsAffected = executeUpdate(sql,
                article.getNombre(),
                article.getDescripcion(),
                article.getPrecio(),
                article.getImagen(),
                article.getStock(),
                article.getIdEstadoArticulo(),
                article.getIdAccion(),
                article.getIdArticulo());

        return rowsAffected > 0;
    }

    // Elimina un artículo (eliminación física)
    public boolean delete(int idArticulo) {
        String sql = "DELETE FROM Articulo WHERE id_articulo = ?";
        return executeUpdate(sql, idArticulo) > 0;
    }

    // Busca artículos por nombre O descripción (búsqueda más flexible)
    public List<Article> searchByNameOrDescription(String searchTerm) {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT a.id_articulo, a.nombre, a.descripcion, a.precio, " +
                "a.imagen, a.stock, a.id_estado_articulo, ea.nombre as nombre_estado, a.id_accion " +
                "FROM Articulo a " +
                "INNER JOIN Estado_Articulo ea ON a.id_estado_articulo = ea.id_estado_articulo " +
                "WHERE (LOWER(a.nombre) LIKE LOWER(?) OR LOWER(a.descripcion) LIKE LOWER(?)) " +
                "AND a.stock > 0 " +
                "ORDER BY a.id_articulo DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String term = "%" + searchTerm + "%";
            rs = executeQuery(sql, term, term);
            conn = rs.getStatement().getConnection();
            stmt = (PreparedStatement) rs.getStatement();

            while (rs.next()) {
                Article article = mapResultSetToArticle(rs);
                List<String> categorias = getCategoriesByArticleId(article.getIdArticulo());
                article.setCategorias(categorias);
                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar por nombre o descripción: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return articles;
    }

    // Filtra artículos por categoría
    public List<Article> filterByCategory(String categoryName) {
        List<Article> articles = new ArrayList<>();
        String sql = "SELECT DISTINCT a.id_articulo, a.nombre, a.descripcion, a.precio, " +
                "a.imagen, a.stock, a.id_estado_articulo, ea.nombre as nombre_estado, a.id_accion " +
                "FROM Articulo a " +
                "INNER JOIN Estado_Articulo ea ON a.id_estado_articulo = ea.id_estado_articulo " +
                "INNER JOIN Categoria c ON a.id_articulo = c.id_articulo " +
                "INNER JOIN Tipo_Categoria tc ON c.id_categoria_tipo = tc.id_categoria " +
                "WHERE LOWER(tc.nombre) = LOWER(?) " +
                "AND a.stock > 0 " +
                "ORDER BY a.id_articulo DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            rs = executeQuery(sql, categoryName);
            conn = rs.getStatement().getConnection();
            stmt = (PreparedStatement) rs.getStatement();

            while (rs.next()) {
                Article article = mapResultSetToArticle(rs);
                List<String> categorias = getCategoriesByArticleId(article.getIdArticulo());
                article.setCategorias(categorias);
                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Error al filtrar por categoría: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return articles;
    }

    // Filtra artículos por múltiples criterios
    public List<Article> filterByMultipleCriteria(String categoryName, BigDecimal minPrice,
            BigDecimal maxPrice, Integer stateId) {
        List<Article> articles = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT DISTINCT a.id_articulo, a.nombre, a.descripcion, a.precio, ")
                .append("a.imagen, a.stock, a.id_estado_articulo, ea.nombre as nombre_estado, a.id_accion ")
                .append("FROM Articulo a ")
                .append("INNER JOIN Estado_Articulo ea ON a.id_estado_articulo = ea.id_estado_articulo ");

        if (categoryName != null && !categoryName.isEmpty()) {
            sql.append("INNER JOIN Categoria c ON a.id_articulo = c.id_articulo ")
                    .append("INNER JOIN Tipo_Categoria tc ON c.id_categoria_tipo = tc.id_categoria ");
        }

        sql.append("WHERE a.stock > 0 ");

        if (categoryName != null && !categoryName.isEmpty()) {
            sql.append("AND LOWER(tc.nombre) = LOWER(?) ");
            params.add(categoryName);
        }

        if (minPrice != null) {
            sql.append("AND a.precio >= ? ");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sql.append("AND a.precio <= ? ");
            params.add(maxPrice);
        }

        if (stateId != null) {
            sql.append("AND a.id_estado_articulo = ? ");
            params.add(stateId);
        }

        sql.append("ORDER BY a.id_articulo DESC");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            rs = executeQuery(sql.toString(), params.toArray());
            conn = rs.getStatement().getConnection();
            stmt = (PreparedStatement) rs.getStatement();

            while (rs.next()) {
                Article article = mapResultSetToArticle(rs);
                List<String> categorias = getCategoriesByArticleId(article.getIdArticulo());
                article.setCategorias(categorias);
                articles.add(article);
            }
        } catch (SQLException e) {
            System.err.println("Error al filtrar por múltiples criterios: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return articles;
    }

    // Actualiza solo el stock de un artículo
    public boolean updateStock(int idArticulo, int newStock) {
        String sql = "UPDATE Articulo SET stock = ? WHERE id_articulo = ?";
        return executeUpdate(sql, newStock, idArticulo) > 0;
    }

    // Decrementa el stock de un artículo
    public boolean decrementStock(int idArticulo, int quantity) {
        String sql = "UPDATE Articulo SET stock = stock - ? " +
                "WHERE id_articulo = ? AND stock >= ?";
        return executeUpdate(sql, quantity, idArticulo, quantity) > 0;
    }

    // Obtiene las categorías de un artículo
    public List<String> getCategoriesByArticleId(int idArticulo) {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT tc.nombre " +
                "FROM Categoria c " +
                "INNER JOIN Tipo_Categoria tc ON c.id_categoria_tipo = tc.id_categoria " +
                "WHERE c.id_articulo = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            rs = executeQuery(sql, idArticulo);
            conn = rs.getStatement().getConnection();
            stmt = (PreparedStatement) rs.getStatement();

            while (rs.next()) {
                categories.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return categories;
    }

    // Mapea un ResultSet a un objeto Article
    private Article mapResultSetToArticle(ResultSet rs) throws SQLException {
        Article article = new Article();
        article.setIdArticulo(rs.getInt("id_articulo"));
        article.setNombre(rs.getString("nombre"));
        article.setDescripcion(rs.getString("descripcion"));
        article.setPrecio(rs.getBigDecimal("precio"));
        article.setImagen(rs.getString("imagen"));
        article.setStock(rs.getInt("stock"));
        article.setIdEstadoArticulo(rs.getInt("id_estado_articulo"));
        article.setNombreEstado(rs.getString("nombre_estado"));
        article.setIdAccion(rs.getInt("id_accion"));
        return article;
    }
}