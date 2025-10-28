package com.cunoc.commerce.controller.datatoobject;

import com.cunoc.commerce.entity.Usuario;
import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.config.EncryptionService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioDAO extends BaseDAO {

    private final EncryptionService encryptionService = new EncryptionService();

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id_usuario"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("email"),
            rs.getInt("id_estado"),
            rs.getInt("id_rol")
        );
    }

    private Usuario ejecutarConsultaUnica(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            rs = stmt.executeQuery();
            return rs.next() ? mapearUsuario(rs) : null;
        } catch (SQLException e) {
            System.err.println("Error en consulta: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    private List<Usuario> ejecutarConsultaLista(String sql, Object... params) {
        List<Usuario> usuarios = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            rs = stmt.executeQuery();
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta lista: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        return usuarios;
    }

    // Buscar usuario por ID de pedido
    public Usuario findByPedidoId(int idPedido) {
        String sql = "SELECT u.* FROM Usuario u INNER JOIN Pedido p ON u.id_usuario = p.id_comprador WHERE p.id_pedido = ? LIMIT 1";
        return ejecutarConsultaUnica(sql, idPedido);
    }

    // Buscar usuario por ID de artículo
    public Usuario findByArticuloId(int idArticulo) {
        String sql = "SELECT u.* FROM Usuario u INNER JOIN Publicacion p ON u.id_usuario = p.id_usuario WHERE p.id_articulo = ? LIMIT 1";
        return ejecutarConsultaUnica(sql, idArticulo);
    }

    // Buscar empleados
    public List<Usuario> findEmpleados() {
        String sql = "SELECT * FROM Usuario WHERE id_rol != 1 ORDER BY id_usuario";
        return ejecutarConsultaLista(sql);
    }

    // Contar total de usuarios
    public int countTotal() {
        String sql = "SELECT COUNT(*) as total FROM usuario";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Obtener usuarios paginados
    public List<Usuario> findAllPaginated(int page, int pageSize) {
        String sql = "SELECT * FROM Usuario ORDER BY id_usuario LIMIT ? OFFSET ?";
        return ejecutarConsultaLista(sql, pageSize, (page - 1) * pageSize);
    }

    // Buscar usuario por email o ID
    public Usuario findByEmailOrId(String search) {
        if (search == null || search.trim().isEmpty()) return null;
        String sql = "SELECT * FROM Usuario WHERE email = ? OR CAST(id_usuario AS TEXT) = ?";
        return ejecutarConsultaUnica(sql, search.trim(), search.trim());
    }

    // Login de usuario
    public Usuario login(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) return null;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM Usuario WHERE email = ?");
            stmt.setString(1, email.trim());
            rs = stmt.executeQuery();

            if (rs.next()) {
                int idEstado = rs.getInt("id_estado");
                if (idEstado != 2) {
                    System.out.println("Acceso denegado: Cuenta sancionada " + email);
                    return null;
                }
                
                if (encryptionService.verify(password, rs.getString("password"))) {
                    System.out.println("Login exitoso para: " + email);
                    return mapearUsuario(rs);
                }
                System.out.println("Contraseña incorrecta");
            } else {
                System.out.println("Usuario no encontrado: " + email);
            }
        } catch (SQLException e) {
            System.err.println("Error en login: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        return null;
    }

    // Insertar nuevo usuario
    public boolean insert(Usuario usuario) {
        if (usuario == null) return false;
        if (findByEmailOrId(usuario.getEmail()) != null) {
            System.out.println("El usuario ya existe: " + usuario.getEmail());
            return false;
        }

        String sql = "INSERT INTO Usuario (nombre, apellido, email, password, id_estado, id_rol) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            int rowsAffected = executeUpdate(sql,
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                encryptionService.encrypt(usuario.getPassword()),
                usuario.getIdEstado() != 0 ? usuario.getIdEstado() : 2,
                usuario.getIdRol() != 0 ? usuario.getIdRol() : 1
            );

            if (rowsAffected > 0) {
                Usuario insertado = findByEmailOrId(usuario.getEmail());
                if (insertado != null) usuario.setIdUsuario(insertado.getIdUsuario());
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Actualizar usuario
    public boolean update(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() <= 0) return false;

        String sql = "UPDATE Usuario SET nombre = ?, apellido = ?, email = ?, id_estado = ?, id_rol = ? WHERE id_usuario = ?";
        try {
            return executeUpdate(sql,
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getIdEstado(),
                usuario.getIdRol(),
                usuario.getIdUsuario()
            ) > 0;
        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}