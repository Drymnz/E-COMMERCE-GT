package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.entity.Notificacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificacionDAO extends BaseDAO {

    //Obtener todas las notificaciones del sistema paginadas
    public List<Notificacion> findAllPaginated(int page, int pageSize) {
        int offset = (page - 1) * pageSize;

        String sql = """
                SELECT 
                    id_notificacion,
                    mensaje,
                    fecha_hora,
                    id_usuario
                FROM Notificacion
                ORDER BY fecha_hora DESC
                LIMIT ? OFFSET ?
                """;

        List<Notificacion> notificaciones = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Notificacion notificacion = new Notificacion(
                    rs.getInt("id_notificacion"),
                    rs.getString("mensaje"),
                    rs.getTimestamp("fecha_hora").toLocalDateTime(),
                    rs.getInt("id_usuario")
                );
                notificaciones.add(notificacion);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener notificaciones paginadas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return notificaciones;
    }

    //Contar total de notificaciones en el sistema
    public int countTotal() {
        String sql = "SELECT COUNT(*) as total FROM Notificacion";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al contar notificaciones: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
}