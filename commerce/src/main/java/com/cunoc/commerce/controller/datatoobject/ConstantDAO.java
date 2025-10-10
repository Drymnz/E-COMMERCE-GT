package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.config.BaseDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConstantDAO extends BaseDAO {

    public List<String> getEstadosPedido() {
        return getConstantes("SELECT nombre FROM Estado_Pedido");
    }

    public List<String> getEstadosUsuario() {
        return getConstantes("SELECT nombre FROM Estado_Usuario");
    }

    public List<String> getRoles() {
        return getConstantes("SELECT nombre FROM Rol");
    }

    public List<String> getTiposCategorias() {
        return getConstantes("SELECT nombre FROM Tipo_Categoria");
    }

    public List<String> getEstadosArticulo() {
        return getConstantes("SELECT nombre FROM Estado_Articulo");
    }

    private List<String> getConstantes(String sql) {
        List<String> constantes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                constantes.add(rs.getString("nombre"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener constantes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return constantes;
    }
}