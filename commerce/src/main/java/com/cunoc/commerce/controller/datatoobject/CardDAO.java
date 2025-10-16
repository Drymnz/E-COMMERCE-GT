package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.entity.Card;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardDAO extends BaseDAO {

    // Insertar nueva tarjeta
    public boolean insert(Card card) {
        String sql = "INSERT INTO Tarjeta_de_Credito (numero, cvv, fecha_vencimiento, saldo, id_usuario) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        int filasAfectadas = executeUpdate(sql, 
            card.getNumero(), 
            card.getCvv(), 
            Date.valueOf(card.getFechaVencimiento()),
            card.getSaldo(),
            card.getIdUsuario()
        );
        
        return filasAfectadas > 0;
    }

    // Obtener todas las tarjetas de un usuario
    public List<Card> findByUsuario(int idUsuario) {
        String sql = "SELECT * FROM Tarjeta_de_Credito WHERE id_usuario = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Card> tarjetas = new ArrayList<>();

        try {
            rs = executeQuery(sql, idUsuario);
            
            while (rs.next()) {
                tarjetas.add(mapResultSetToCard(rs));
            }
            
            return tarjetas;
            
        } catch (SQLException e) {
            System.err.println("Error al obtener tarjetas del usuario: " + e.getMessage());
            return tarjetas;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    // Reducir saldo de la tarjeta con transacción
    public boolean reducirSaldo(String numero, BigDecimal monto) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Verificar saldo actual
            String sqlSelect = "SELECT saldo FROM Tarjeta_de_Credito WHERE numero = ? FOR UPDATE";
            stmt = conn.prepareStatement(sqlSelect);
            stmt.setString(1, numero);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false; 
            }

            BigDecimal saldoActual = rs.getBigDecimal("saldo");

            // Verificar si hay saldo suficiente
            if (saldoActual.compareTo(monto) < 0) {
                conn.rollback();
                return false; 
            }

            // Reducir el saldo
            BigDecimal nuevoSaldo = saldoActual.subtract(monto);
            String sqlUpdate = "UPDATE Tarjeta_de_Credito SET saldo = ? WHERE numero = ?";
            
            stmt.close();
            stmt = conn.prepareStatement(sqlUpdate);
            stmt.setBigDecimal(1, nuevoSaldo);
            stmt.setString(2, numero);
            
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error al reducir saldo: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error al restablecer autoCommit: " + e.getMessage());
            }
            closeResources(conn, stmt, rs);
        }
    }

    // Eliminar tarjeta
    public boolean delete(String numero) {
        String sql = "DELETE FROM Tarjeta_de_Credito WHERE numero = ?";
        int filasAfectadas = executeUpdate(sql, numero);
        return filasAfectadas > 0;
    }

    // Mapear ResultSet a objeto Card
    private Card mapResultSetToCard(ResultSet rs) throws SQLException {
        return new Card(
            rs.getString("numero"),
            rs.getString("cvv"),
            rs.getDate("fecha_vencimiento").toLocalDate(),
            rs.getBigDecimal("saldo"),
            rs.getInt("id_usuario")
        );
    }
}