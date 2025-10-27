package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.entity.Sancion;
import com.cunoc.commerce.config.BaseDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository 
public class SancionDAO extends BaseDAO {

    // Crear nueva sanción
    public boolean insert(Sancion sancion) {
        if (sancion == null || sancion.getMotivo() == null || sancion.getMotivo().trim().isEmpty()) {
            return false;
        }

        String sql = """
                INSERT INTO Sancion (motivo, fecha_hora, id_usuario)
                VALUES (?, ?, ?)
                """;

        try {
            LocalDateTime fechaHora = sancion.getFechaHora() != null ? 
                sancion.getFechaHora() : LocalDateTime.now();

            int rowsAffected = executeUpdate(sql,
                    sancion.getMotivo(),
                    Timestamp.valueOf(fechaHora),
                    sancion.getIdUsuario());

            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Error al insertar sanción: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Obtener todas las sanciones con información de usuarios
    public List<Sancion> findAll() {
        String sql = """
                SELECT s.*, u.nombre, u.apellido, u.email
                FROM Sancion s
                INNER JOIN Usuario u ON s.id_usuario = u.id_usuario
                ORDER BY s.fecha_hora DESC
                """;

        List<Sancion> sanciones = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Sancion sancion = mapResultSetToSancion(rs);
                sanciones.add(sancion);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener todas las sanciones: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return sanciones;
    }


    // mapear ResultSet a Sancion
    private Sancion mapResultSetToSancion(ResultSet rs) throws SQLException {
        Sancion sancion = new Sancion(
                rs.getInt("id_sancion"),
                rs.getString("motivo"),
                rs.getTimestamp("fecha_hora").toLocalDateTime(),
                rs.getInt("id_usuario")
        );

        // Agregar información del usuario si está disponible
        try {
            String nombre = rs.getString("nombre");
            String apellido = rs.getString("apellido");
            sancion.setNombreUsuario(nombre + " " + apellido);
            sancion.setEmailUsuario(rs.getString("email"));
        } catch (SQLException e) {
            // No hay problema si no existen estas columnas
        }

        return sancion;
    }
}