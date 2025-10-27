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

import org.springframework.stereotype.Repository;

@Repository
public class UsuarioDAO extends BaseDAO {

    private final EncryptionService encryptionService = new EncryptionService();

    /**
     * Obtiene el comprador/usuario que realizó un pedido
     */
    public Usuario findByPedidoId(int idPedido) {
        String sql = """
                SELECT u.*
                FROM Usuario u
                INNER JOIN Pedido p ON u.id_usuario = p.id_comprador
                WHERE p.id_pedido = ?
                LIMIT 1
                """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idPedido);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setApellido(rs.getString("apellido"));
                usuario.setEmail(rs.getString("email"));
                usuario.setPassword(rs.getString("password"));
                usuario.setIdEstado(rs.getInt("id_estado"));
                usuario.setIdRol(rs.getInt("id_rol"));
                return usuario;
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por pedido: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return null;
    }

    /**
     * Obtiene el vendedor/usuario que publicó un artículo
     */
    public Usuario findByArticuloId(int idArticulo) {
        String sql = """
                SELECT u.*
                FROM Usuario u
                INNER JOIN Publicacion p ON u.id_usuario = p.id_usuario
                WHERE p.id_articulo = ?
                LIMIT 1
                """;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idArticulo);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setApellido(rs.getString("apellido"));
                usuario.setEmail(rs.getString("email"));
                usuario.setPassword(rs.getString("password"));
                usuario.setIdEstado(rs.getInt("id_estado"));
                usuario.setIdRol(rs.getInt("id_rol"));
                return usuario;
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por artículo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return null;
    }

    // HIstorial de empleados
    public List<Usuario> findEmpleados() {
        String sql = """
                SELECT *
                FROM Usuario
                WHERE id_rol != 1
                ORDER BY id_usuario
                """;

        List<Usuario> empleados = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getInt("id_estado"),
                        rs.getInt("id_rol"));
                empleados.add(usuario);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener empleados: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return empleados;
    }

    // Contar total de usuarios
    public int countTotal() {
        String sql = "SELECT COUNT(*) as total FROM usuario";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public List<Usuario> findAllPaginated(int page, int pageSize) {
        int offset = (page - 1) * pageSize;

        String sql = """
                SELECT *
                FROM Usuario
                ORDER BY id_usuario
                LIMIT ? OFFSET ?
                """;

        List<Usuario> usuarios = new ArrayList<>();
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
                Usuario usuario = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getInt("id_estado"),
                        rs.getInt("id_rol"));
                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios paginados: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }

        return usuarios;
    }

    // @param search email o id del usuario
    public Usuario findByEmailOrId(String search) {
        if (search == null || search.trim().isEmpty()) {
            return null;
        }

        String sql = """
                SELECT *
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
                return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getInt("id_estado"),
                        rs.getInt("id_rol"));
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
     * @param email    del usuario
     * @param password en texto plano
     */
    public Usuario login(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }

        String sql = """
                SELECT *
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
                int idEstado = rs.getInt("id_estado");

                // Verificar que el usuario esté activo (estado = 2)
                if (idEstado != 2) {
                    System.out.println("Acceso denegado: Cuenta sancionada  " + email);
                    return null;
                }

                String hashedPassword = rs.getString("password");

                if (encryptionService.verify(password, hashedPassword)) {
                    Usuario usuario = new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            idEstado,
                            rs.getInt("id_rol"));

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
                    usuario.getIdEstado() != 0 ? usuario.getIdEstado() : 2,
                    usuario.getIdRol() != 0 ? usuario.getIdRol() : 1);

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
                    usuario.getIdUsuario());

            return rowsAffected > 0;

        } catch (Exception e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

}