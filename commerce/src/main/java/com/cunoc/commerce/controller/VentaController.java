package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.VentaDAO;
import com.cunoc.commerce.entity.VentaTotal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ventas")
@CrossOrigin(origins = "*")
public class VentaController {

    private final VentaDAO ventaDAO = new VentaDAO();

    /**
     * Obtiene el listado de artículos vendidos con sus totales
     */
    @GetMapping("/total")
    public ResponseEntity<List<VentaTotal>> obtenerTotalVentas() {
        try {
            List<VentaTotal> ventas = ventaDAO.obtenerTotalVentas();
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            System.err.println("Error en obtenerTotalVentas: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Obtiene el total general de todas las ventas
     */
    @GetMapping("/total-general")
    public ResponseEntity<Map<String, Object>> obtenerTotalGeneral() {
        try {
            double totalGeneral = ventaDAO.obtenerTotalGeneral();
            
            Map<String, Object> response = new HashMap<>();
            response.put("total_general", totalGeneral);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error en obtenerTotalGeneral: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}