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
        boolean insertado = sancionDAO.insert(sancion);

        if (insertado) {
            return ResponseEntity.status(201).body(sancion);
        }

        return ResponseEntity.badRequest().build();
    }

    // Obtener todas las sanciones
    @GetMapping
    public ResponseEntity<List<Sancion>> obtenerTodasSanciones() {
        List<Sancion> sanciones = sancionDAO.findAll();
        return ResponseEntity.ok(sanciones);
    }

}