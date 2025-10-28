package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.entity.Card;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository 
public class CardDAO extends BaseDAO {

    // Insertar nueva tarjeta
    public boolean insert(Card card) {
        return executeUpdate("INSERT INTO Tarjeta_de_Credito (numero, cvv, fecha_vencimiento, saldo, id_usuario) VALUES (?, ?, ?, ?, ?)", 
            card.getNumero(), card.getCvv(), Date.valueOf(card.getFechaVencimiento()), card.getSaldo(), card.getIdUsuario()) > 0;
    }

    // Buscar tarjetas por ID de usuario
    public List<Card> findByUsuario(int idUsuario) {
        List<Card> tarjetas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            rs = executeQuery("SELECT * FROM Tarjeta_de_Credito WHERE id_usuario = ?", idUsuario);
            while (rs.next()) tarjetas.add(mapResultSetToCard(rs));
        } catch (SQLException e) {
            System.err.println("Error al obtener tarjetas del usuario: " + e.getMessage());
        } finally {
            closeResources(conn, stmt, rs);
        }
        return tarjetas;
    }

    // Reducir saldo de tarjeta
    public boolean reducirSaldo(String numero, BigDecimal monto) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            stmt = conn.prepareStatement("SELECT saldo FROM Tarjeta_de_Credito WHERE numero = ? FOR UPDATE");
            stmt.setString(1, numero);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            BigDecimal saldoActual = rs.getBigDecimal("saldo");
            if (saldoActual.compareTo(monto) < 0) {
                conn.rollback();
                return false;
            }

            stmt.close();
            stmt = conn.prepareStatement("UPDATE Tarjeta_de_Credito SET saldo = ? WHERE numero = ?");
            stmt.setBigDecimal(1, saldoActual.subtract(monto));
            stmt.setString(2, numero);

            if (stmt.executeUpdate() > 0) {
                conn.commit();
                return true;
            }
            conn.rollback();
            return false;
        } catch (SQLException e) {
            System.err.println("Error al reducir saldo: " + e.getMessage());
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { }
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) { }
            closeResources(conn, stmt, rs);
        }
    }

    // Eliminar tarjeta
    public boolean delete(String numero) {
        return executeUpdate("DELETE FROM Tarjeta_de_Credito WHERE numero = ?", numero) > 0;
    }

    private Card mapResultSetToCard(ResultSet rs) throws SQLException {
        return new Card(rs.getString("numero"), rs.getString("cvv"), rs.getDate("fecha_vencimiento").toLocalDate(), 
                rs.getBigDecimal("saldo"), rs.getInt("id_usuario"));
    }
}