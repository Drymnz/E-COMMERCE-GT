package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.SancionDAO;
import com.cunoc.commerce.entity.Sancion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/sancion")
public class SancionController {

    private final SancionDAO sancionDAO = new SancionDAO();

    // Crear nueva sanción
    @PostMapping
    public ResponseEntity<Sancion> crearSancion(@RequestBody Sancion sancion) {
        return sancionDAO.insert(sancion) 
            ? ResponseEntity.status(201).body(sancion) 
            : ResponseEntity.badRequest().build();
    }

    // Obtener todas las sanciones
    @GetMapping
    public ResponseEntity<List<Sancion>> obtenerTodasSanciones() {
        return ResponseEntity.ok(sancionDAO.findAll());
    }
}