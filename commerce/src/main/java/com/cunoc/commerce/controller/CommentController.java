package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.CommentDAO;
import com.cunoc.commerce.entity.Comentario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/commet")
@CrossOrigin(origins = "*")
public class CommentController {
    private final CommentDAO commentDAO = new CommentDAO();
    
    // Obtener comentarios por artículo
    @GetMapping("/articulo/{idArticulo}")
    public ResponseEntity<List<Comentario>> getComentariosByArticulo(@PathVariable int idArticulo) {
        try {
            return ResponseEntity.ok(commentDAO.getComentariosByArticulo(idArticulo));
        } catch (Exception e) {
            System.err.println("Error al obtener comentarios por artículo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Crear nuevo comentario
    @PostMapping
    public ResponseEntity<Comentario> crearComentario(@RequestBody Comentario comentario) {
        try {
            if (comentario.getPuntuacion() < 1 || comentario.getPuntuacion() > 5) return ResponseEntity.badRequest().build();
            if (comentario.getDescripcion() == null || comentario.getDescripcion().trim().isEmpty()) return ResponseEntity.badRequest().build();
            
            Comentario nuevo = commentDAO.insertarComentario(comentario);
            return nuevo != null ? ResponseEntity.status(HttpStatus.CREATED).body(nuevo) 
                    : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            System.err.println("Error al crear comentario: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}