package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.ReportDAO;
import com.cunoc.commerce.entity.Article;
import com.cunoc.commerce.entity.Usuario;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportDAO reportDAO = new ReportDAO();

    // Obtener productos más vendidos
    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<List<Article>> getTopProductosVendidos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            return ResponseEntity.ok(reportDAO.getTopProductosMasVendidos(fechaInicio, fechaFin));
        } catch (Exception e) {
            System.err.println("Error en getTopProductosVendidos: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // Obtener mejores clientes compradores
    @GetMapping("/clientes-mejores-compradores")
    public ResponseEntity<List<Usuario>> getTopClientesCompradores(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            return ResponseEntity.ok(reportDAO.getTopClientesPorGanancias(fechaInicio, fechaFin));
        } catch (Exception e) {
            System.err.println("Error en getTopClientesCompradores: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // Obtener mejores clientes vendedores
    @GetMapping("/clientes-mejores-vendedores")
    public ResponseEntity<List<Usuario>> getTopClientesVendedores(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            return ResponseEntity.ok(reportDAO.getTopClientesPorVentas(fechaInicio, fechaFin));
        } catch (Exception e) {
            System.err.println("Error en getTopClientesVendedores: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // Obtener clientes con más pedidos
    @GetMapping("/clientes-mas-pedidos")
    public ResponseEntity<List<Usuario>> getTopClientesPedidos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            return ResponseEntity.ok(reportDAO.getTopClientesPorPedidos(fechaInicio, fechaFin));
        } catch (Exception e) {
            System.err.println("Error en getTopClientesPedidos: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // Obtener clientes con más productos en venta
    @GetMapping("/clientes-mas-productos-venta")
    public ResponseEntity<List<Usuario>> getTopClientesProductosVenta() {
        try {
            return ResponseEntity.ok(reportDAO.getTopClientesConMasProductosEnVenta());
        } catch (Exception e) {
            System.err.println("Error en getTopClientesProductosVenta: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}