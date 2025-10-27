package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.entity.Comentario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository 
public class CommentDAO extends BaseDAO {
    
    //Obtiene todos los comentarios de un artículo 
    public List<Comentario> getComentariosByArticulo(int idArticulo) {
        List<Comentario> comentarios = new ArrayList<>();
        String sql = "SELECT id_comentario, descripcion, puntuacion, id_usuario, id_articulo " +
                     "FROM Comentario WHERE id_articulo = ? ORDER BY id_comentario DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idArticulo);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Comentario comentario = new Comentario(
                    rs.getInt("id_comentario"),
                    rs.getString("descripcion"),
                    rs.getInt("puntuacion"),
                    rs.getInt("id_usuario"),
                    rs.getInt("id_articulo")
                );
                comentarios.add(comentario);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener comentarios por artículo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return comentarios;
    }
    
    //Inserta un nuevo comentario y retorna el comentario con su ID
    public Comentario insertarComentario(Comentario comentario) {
        String sql = "INSERT INTO Comentario (descripcion, puntuacion, id_usuario, id_articulo) " +
                     "VALUES (?, ?, ?, ?) RETURNING id_comentario";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, comentario.getDescripcion());
            stmt.setInt(2, comentario.getPuntuacion());
            stmt.setInt(3, comentario.getIdUsuario());
            stmt.setInt(4, comentario.getIdArticulo());
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                int idGenerado = rs.getInt("id_comentario");
                return new Comentario(
                    idGenerado,
                    comentario.getDescripcion(),
                    comentario.getPuntuacion(),
                    comentario.getIdUsuario(),
                    comentario.getIdArticulo()
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar comentario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return null;
    }
}