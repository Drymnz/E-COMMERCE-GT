package com.cunoc.commerce.controller.datatoobject;


import com.cunoc.commerce.entity.Usuario;
import com.cunoc.commerce.config.BaseDAO;
import com.cunoc.commerce.config.EncryptionService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO extends BaseDAO {

    private final EncryptionService encryptionService = new EncryptionService();

    public List<Usuario> getAllUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre, apellido, email, id_estado, id_rol FROM Usuario LIMIT 100";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return usuarios;
    }

    /**
     * @param search email o id del usuario
     */
    public Usuario findByEmailOrId(String search) {
        if (search == null || search.trim().isEmpty()) {
            return null;
        }

        String sql = """
            SELECT id_usuario, nombre, apellido, email, id_estado, id_rol
            FROM Usuario
            WHERE email = ? OR CAST(id_usuario AS TEXT) = ?
            """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, search.trim());
            stmt.setString(2, search.trim());

            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUsuario(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return null;
    }

    /**
     * @param email del usuario
     * @param password en texto plano
     */
    public Usuario login(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }

        String sql = """
            SELECT id_usuario, nombre, apellido, email, password, id_estado, id_rol
            FROM Usuario
            WHERE email = ?
            """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email.trim());

            rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                
                if (encryptionService.verify(password, hashedPassword)) {
                    Usuario usuario = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getString("id_estado"),
                        rs.getString("id_rol")
                    );
                    
                    System.out.println("Login exitoso para: " + usuario.getEmail());
                    return usuario;
                } else {
                    System.out.println("Contraseña incorrecta");
                }
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

    public boolean insert(Usuario usuario) {
        if (usuario == null) {
            return false;
        }

        if (findByEmailOrId(usuario.getEmail()) != null) {
            System.out.println("El usuario ya existe: " + usuario.getEmail());
            return false;
        }

        String passwordEncriptada = encryptionService.encrypt(usuario.getPassword());

        String sql = """
            INSERT INTO Usuario (nombre, apellido, email, password, id_estado, id_rol)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try {
            int rowsAffected = executeUpdate(sql,
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                passwordEncriptada,
                usuario.getIdEstado() != null ? usuario.getIdEstado() : "2",
                usuario.getIdRol() != null ? usuario.getIdRol() : "1"
            );

            if (rowsAffected > 0) {
                Usuario insertado = findByEmailOrId(usuario.getEmail());
                if (insertado != null) {
                    usuario.setIdUsuario(insertado.getIdUsuario());
                }
                return true;
            }

        } catch (Exception e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() <= 0) {
            return false;
        }

        String sql = """
            UPDATE Usuario 
            SET nombre = ?, apellido = ?, email = ?, id_estado = ?, id_rol = ?
            WHERE id_usuario = ?
            """;

        try {
            int rowsAffected = executeUpdate(sql,
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getIdEstado(),
                usuario.getIdRol(),
                usuario.getIdUsuario()
            );

            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int idUsuario) {
        String sql = "UPDATE Usuario SET id_estado = '1' WHERE id_usuario = ?";
        
        try {
            return executeUpdate(sql, idUsuario) > 0;
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id_usuario"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("email"),
            rs.getString("id_estado"),
            rs.getString("id_rol")
        );
    }
}