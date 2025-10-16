package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.CardDAO;
import com.cunoc.commerce.entity.Card;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/card")
public class CardController {

    private final CardDAO cardDAO = new CardDAO();

    // Crear nueva tarjeta
    @PostMapping
    public ResponseEntity<Card> crearTarjeta(@RequestBody Card card) {
        boolean insertada = cardDAO.insert(card);

        if (insertada) {
            return ResponseEntity.status(201).body(card);
        }

        return ResponseEntity.badRequest().build();
    }

    // Obtener todas las tarjetas de un usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Card>> obtenerTarjetasUsuario(@PathVariable int idUsuario) {
        List<Card> tarjetas = cardDAO.findByUsuario(idUsuario);
        return ResponseEntity.ok(tarjetas);
    }

    // Reducir saldo de tarjeta
    @PostMapping("/{numero}/reducir-saldo")
    public ResponseEntity<Map<String, Object>> reducirSaldo(
            @PathVariable String numero,
            @RequestParam BigDecimal monto) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            response.put("success", false);
            response.put("message", "El monto debe ser mayor a cero");
            return ResponseEntity.badRequest().body(response);
        }

        boolean reducido = cardDAO.reducirSaldo(numero, monto);

        if (reducido) {
            response.put("success", true);
            response.put("message", "Pago realizado exitosamente");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Saldo insuficiente o tarjeta no encontrada");
            return ResponseEntity.badRequest().body(response);
        }
    }


    // Eliminar tarjeta
    @DeleteMapping("/{numero}")
    public ResponseEntity<Void> eliminarTarjeta(@PathVariable String numero) {
        boolean eliminado = cardDAO.delete(numero);

        if (eliminado) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}