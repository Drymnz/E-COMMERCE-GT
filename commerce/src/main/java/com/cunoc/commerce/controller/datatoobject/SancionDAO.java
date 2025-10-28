package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.entity.Sancion;
import com.cunoc.commerce.config.BaseDAO;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository 
public class SancionDAO extends BaseDAO {

    // Insertar nueva sanción
    public boolean insert(Sancion sancion) {
        if (sancion == null || sancion.getMotivo() == null || sancion.getMotivo().trim().isEmpty()) {
            return false;
        }

        try {
            LocalDateTime fechaHora = sancion.getFechaHora() != null ? sancion.getFechaHora() : LocalDateTime.now();
            return executeUpdate("INSERT INTO Sancion (motivo, fecha_hora, id_usuario) VALUES (?, ?, ?)",
                sancion.getMotivo(), Timestamp.valueOf(fechaHora), sancion.getIdUsuario()) > 0;
        } catch (Exception e) {
            System.err.println("Error al insertar sanción: " + e.getMessage());
            return false;
        }
    }

    // Obtener todas las sanciones
    public List<Sancion> findAll() {
        String sql = "SELECT s.*, u.nombre, u.apellido, u.email FROM Sancion s " +
                "INNER JOIN Usuario u ON s.id_usuario = u.id_usuario ORDER BY s.fecha_hora DESC";

        List<Sancion> sanciones = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Sancion sancion = new Sancion(rs.getInt("id_sancion"), rs.getString("motivo"),
                    rs.getTimestamp("fecha_hora").toLocalDateTime(), rs.getInt("id_usuario"));
                
                try {
                    sancion.setNombreUsuario(rs.getString("nombre") + " " + rs.getString("apellido"));
                    sancion.setEmailUsuario(rs.getString("email"));
                } catch (SQLException e) {
                }
                sanciones.add(sancion);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las sanciones: " + e.getMessage());
        }
        return sanciones;
    }
}