package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.VentaDAO;
import com.cunoc.commerce.entity.VentaTotal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ventas")
@CrossOrigin(origins = "*")
public class VentaController {

    private final VentaDAO ventaDAO = new VentaDAO();

    // Obtener total de ventas por artículo
    @GetMapping("/total")
    public ResponseEntity<List<VentaTotal>> obtenerTotalVentas() {
        try {
            return ResponseEntity.ok(ventaDAO.obtenerTotalVentas());
        } catch (Exception e) {
            System.err.println("Error en obtenerTotalVentas: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // Obtener total general de ventas
    @GetMapping("/total-general")
    public ResponseEntity<Map<String, Object>> obtenerTotalGeneral() {
        try {
            return ResponseEntity.ok(Map.of("total_general", ventaDAO.obtenerTotalGeneral()));
        } catch (Exception e) {
            System.err.println("Error en obtenerTotalGeneral: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}