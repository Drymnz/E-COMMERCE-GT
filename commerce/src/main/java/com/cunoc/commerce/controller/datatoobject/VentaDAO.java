package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.entity.VentaTotal;
import com.cunoc.commerce.config.BaseDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class VentaDAO extends BaseDAO {

    // Obtener total de ventas por artículo
    public List<VentaTotal> obtenerTotalVentas() {
        System.out.println("=== Ejecutando obtenerTotalVentas ===");
        String sql = "SELECT a.id_articulo, a.nombre as nombre_articulo, SUM(p.cantidad) as cantidad_vendida, " +
                     "SUM(p.cantidad * a.precio) as total_ventas FROM Producto p " +
                     "INNER JOIN Articulo a ON p.id_articulo = a.id_articulo " +
                     "GROUP BY a.id_articulo, a.nombre ORDER BY total_ventas DESC";

        List<VentaTotal> ventas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                VentaTotal venta = new VentaTotal(
                    rs.getInt("id_articulo"),
                    rs.getString("nombre_articulo"),
                    rs.getInt("cantidad_vendida"),
                    rs.getBigDecimal("total_ventas")
                );
                ventas.add(venta);
                System.out.println("Venta agregada: " + venta);
            }
            
            System.out.println("Total de ventas encontradas: " + ventas.size());
        } catch (SQLException e) {
            System.err.println("Error al obtener total de ventas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        return ventas;
    }

    // Obtener total general de ventas
    public double obtenerTotalGeneral() {
        String sql = "SELECT COALESCE(SUM(p.cantidad * a.precio), 0) as total_general FROM Producto p " +
                     "INNER JOIN Articulo a ON p.id_articulo = a.id_articulo";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getDouble("total_general") : 0.0;
        } catch (SQLException e) {
            System.err.println("Error al obtener total general: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }
}