package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.entity.Notificacion;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository 
public class NotificacionDAO extends BaseDAO {

    // Obtener notificaciones paginadas
    public List<Notificacion> findAllPaginated(int page, int pageSize) {
        String sql = "SELECT id_notificacion, mensaje, fecha_hora, id_usuario " +
                "FROM Notificacion ORDER BY fecha_hora DESC LIMIT ? OFFSET ?";
        
        List<Notificacion> notificaciones = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pageSize);
            stmt.setInt(2, (page - 1) * pageSize);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notificaciones.add(new Notificacion(
                        rs.getInt("id_notificacion"),
                        rs.getString("mensaje"),
                        rs.getTimestamp("fecha_hora").toLocalDateTime(),
                        rs.getInt("id_usuario")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener notificaciones paginadas: " + e.getMessage());
            e.printStackTrace();
        }
        
        return notificaciones;
    }

    // Contar total de notificaciones
    public int countTotal() {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as total FROM Notificacion");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            System.err.println("Error al contar notificaciones: " + e.getMessage());
            return 0;
        }
    }

    // Insertar nueva notificación
    public boolean insert(Notificacion notificacion) {
        if (notificacion == null || notificacion.getMensaje() == null) return false;
        
        try {
            return executeUpdate(
                "INSERT INTO Notificacion (mensaje, fecha_hora, id_usuario) VALUES (?, ?, ?)",
                notificacion.getMensaje(),
                Timestamp.valueOf(notificacion.getFechaHora()),
                notificacion.getIdUsuario()) > 0;
        } catch (Exception e) {
            System.err.println("Error al insertar notificacion: " + e.getMessage());
            return false;
        }
    }
}