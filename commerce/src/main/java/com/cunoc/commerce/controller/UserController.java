package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.UsuarioDAO;
import com.cunoc.commerce.entity.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Obtener empleados
    @GetMapping("/empleados")
    public ResponseEntity<List<Usuario>> obtenerEmpleados() {
        try {
            return ResponseEntity.ok(usuarioDAO.findEmpleados());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Obtener usuarios paginados
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerUsuariosPaginados(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            int totalUsuarios = usuarioDAO.countTotal();
            Map<String, Object> response = new HashMap<>();
            response.put("usuarios", usuarioDAO.findAllPaginated(page, pageSize));
            response.put("currentPage", page);
            response.put("pageSize", pageSize);
            response.put("totalUsuarios", totalUsuarios);
            response.put("totalPages", (int) Math.ceil((double) totalUsuarios / pageSize));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Login de usuario
    @GetMapping("/login")
    public ResponseEntity<Usuario> login(@RequestParam String email, @RequestParam String password) {
        Usuario usuario = usuarioDAO.login(email, password);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.status(401).build();
    }

    // Crear nuevo usuario
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        return usuarioDAO.insert(usuario) ? ResponseEntity.status(201).body(usuario) : ResponseEntity.badRequest().build();
    }

    // Buscar usuario por email
    @GetMapping("/buscar")
    public ResponseEntity<Usuario> buscarUsuario(@RequestParam String email) {
        Usuario usuario = usuarioDAO.findByEmailOrId(email);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }

    // Modificar usuario
    @PutMapping
    public ResponseEntity<Usuario> modificarUsuario(@RequestBody Usuario usuario) {
        return usuarioDAO.update(usuario) ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }
}