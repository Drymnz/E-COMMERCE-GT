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

    /**
     * Top 10 productos más vendidos en un intervalo de tiempo
     * @param fechaInicio formato: yyyy-MM-dd'T'HH:mm:ss
     * @param fechaFin formato: yyyy-MM-dd'T'HH:mm:ss
     */
    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<List<Article>> getTopProductosVendidos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        
        try {
            List<Article> productos = reportDAO.getTopProductosMasVendidos(fechaInicio, fechaFin);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            System.err.println("Error en getTopProductosVendidos: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Top 5 clientes que más ganancias por compras han generado
     * @param fechaInicio formato: yyyy-MM-dd'T'HH:mm:ss
     * @param fechaFin formato: yyyy-MM-dd'T'HH:mm:ss
     */
    @GetMapping("/clientes-mejores-compradores")
    public ResponseEntity<List<Usuario>> getTopClientesCompradores(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        
        try {
            List<Usuario> clientes = reportDAO.getTopClientesPorGanancias(fechaInicio, fechaFin);
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            System.err.println("Error en getTopClientesCompradores: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Top 5 clientes que más productos han vendido
     * @param fechaInicio formato: yyyy-MM-dd'T'HH:mm:ss
     * @param fechaFin formato: yyyy-MM-dd'T'HH:mm:ss
     */
    @GetMapping("/clientes-mejores-vendedores")
    public ResponseEntity<List<Usuario>> getTopClientesVendedores(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        
        try {
            List<Usuario> clientes = reportDAO.getTopClientesPorVentas(fechaInicio, fechaFin);
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            System.err.println("Error en getTopClientesVendedores: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Top 10 clientes que más pedidos han realizado
     * @param fechaInicio formato: yyyy-MM-dd'T'HH:mm:ss
     * @param fechaFin formato: yyyy-MM-dd'T'HH:mm:ss
     */
    @GetMapping("/clientes-mas-pedidos")
    public ResponseEntity<List<Usuario>> getTopClientesPedidos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        
        try {
            List<Usuario> clientes = reportDAO.getTopClientesPorPedidos(fechaInicio, fechaFin);
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            System.err.println("Error en getTopClientesPedidos: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    //Top 10 clientes que más productos tienen a la venta
    @GetMapping("/clientes-mas-productos-venta")
    public ResponseEntity<List<Usuario>> getTopClientesProductosVenta() {
        try {
            List<Usuario> clientes = reportDAO.getTopClientesConMasProductosEnVenta();
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            System.err.println("Error en getTopClientesProductosVenta: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}