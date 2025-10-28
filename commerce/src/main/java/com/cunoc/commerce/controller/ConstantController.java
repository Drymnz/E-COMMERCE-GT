package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.ConstantDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/constant")
public class ConstantController {
    
    private final ConstantDAO constantDAO = new ConstantDAO();

    // Obtener estados de moderación
    @GetMapping("/estados-moderacion")
    public ResponseEntity<List<String>> getEstadosModeracion() {
        return ResponseEntity.ok(constantDAO.getEstadosModeracion());
    }

    // Obtener estados de pedido
    @GetMapping("/estados-pedido")
    public ResponseEntity<List<String>> getEstadosPedido() {
        return ResponseEntity.ok(constantDAO.getEstadosPedido());
    }

    // Obtener estados de usuario
    @GetMapping("/estados-usuario")
    public ResponseEntity<List<String>> getEstadosUsuario() {
        return ResponseEntity.ok(constantDAO.getEstadosUsuario());
    }

    // Obtener roles de usuario
    @GetMapping("/roles")
    public ResponseEntity<List<String>> getRoles() {
        return ResponseEntity.ok(constantDAO.getRoles());
    }

    // Obtener tipos de categorías
    @GetMapping("/tipos-categorias")
    public ResponseEntity<List<String>> getTiposCategorias() {
        return ResponseEntity.ok(constantDAO.getTiposCategorias());
    }

    // Obtener estados de artículo
    @GetMapping("/estados-articulo")
    public ResponseEntity<List<String>> getEstadosArticulo() {
        return ResponseEntity.ok(constantDAO.getEstadosArticulo());
    }

}