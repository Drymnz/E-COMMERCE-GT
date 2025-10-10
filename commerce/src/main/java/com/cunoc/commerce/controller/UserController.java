package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.UsuarioDAO;
import com.cunoc.commerce.entity.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

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

    /**
     * @param email del usuario a buscar
     */
    @GetMapping("/buscar")
    public ResponseEntity<Usuario> buscarUsuario(@RequestParam String email) {
        Usuario usuario = usuarioDAO.findByEmailOrId(email);

        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }

        return ResponseEntity.notFound().build();
    }

    /* modificar usuario existente */
    @PutMapping
    public ResponseEntity<Usuario> modificarUsuario(@RequestBody Usuario usuario) {
        boolean actualizado = usuarioDAO.update(usuario);

        if (actualizado) {
            return ResponseEntity.ok(usuario);
        }

        return ResponseEntity.notFound().build();
    }
}