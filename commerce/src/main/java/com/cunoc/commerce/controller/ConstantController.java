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

    @GetMapping("/estados-pedido")
    public ResponseEntity<List<String>> getEstadosPedido() {
        return ResponseEntity.ok(constantDAO.getEstadosPedido());
    }

    @GetMapping("/estados-usuario")
    public ResponseEntity<List<String>> getEstadosUsuario() {
        return ResponseEntity.ok(constantDAO.getEstadosUsuario());
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getRoles() {
        return ResponseEntity.ok(constantDAO.getRoles());
    }

    @GetMapping("/tipos-categorias")
    public ResponseEntity<List<String>> getTiposCategorias() {
        return ResponseEntity.ok(constantDAO.getTiposCategorias());
    }

    @GetMapping("/estados-articulo")
    public ResponseEntity<List<String>> getEstadosArticulo() {
        return ResponseEntity.ok(constantDAO.getEstadosArticulo());
    }

}