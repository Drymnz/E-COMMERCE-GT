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

    //Obtener usuarios paginados
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerUsuariosPaginados(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        try {
            List<Usuario> usuarios = usuarioDAO.findAllPaginated(page, pageSize);
            int totalUsuarios = usuarioDAO.countTotal();
            int totalPages = (int) Math.ceil((double) totalUsuarios / pageSize);
            
            Map<String, Object> response = new HashMap<>();
            response.put("usuarios", usuarios);
            response.put("currentPage", page);
            response.put("pageSize", pageSize);
            response.put("totalUsuarios", totalUsuarios);
            response.put("totalPages", totalPages);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * @param email    del usuario
     * @param password en texto plano
     */
    @GetMapping("/login")
    public ResponseEntity<Usuario> login(
            @RequestParam String email,
            @RequestParam String password) {
        Usuario usuario = usuarioDAO.login(email, password);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.status(401).build();
    }

    /* Crear nuevo usuario */
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        boolean insertado = usuarioDAO.insert(usuario);
        if (insertado) {
            return ResponseEntity.status(201).body(usuario);
        }
        return ResponseEntity.badRequest().build();
    }

    //@param email del usuario a buscar
    @GetMapping("/buscar")
    public ResponseEntity<Usuario> buscarUsuario(@RequestParam String email) {
        Usuario usuario = usuarioDAO.findByEmailOrId(email);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }

    //modificar usuario existente
    @PutMapping
    public ResponseEntity<Usuario> modificarUsuario(@RequestBody Usuario usuario) {
        boolean actualizado = usuarioDAO.update(usuario);
        if (actualizado) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }
}