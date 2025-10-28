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
        return cardDAO.insert(card) ? ResponseEntity.status(201).body(card) : ResponseEntity.badRequest().build();
    }

    // Obtener tarjetas por usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Card>> obtenerTarjetasUsuario(@PathVariable int idUsuario) {
        return ResponseEntity.ok(cardDAO.findByUsuario(idUsuario));
    }

    // Reducir saldo de tarjeta
    @PostMapping("/{numero}/reducir-saldo")
    public ResponseEntity<Map<String, Object>> reducirSaldo(@PathVariable String numero, @RequestParam BigDecimal monto) {
        Map<String, Object> response = new HashMap<>();
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            response.put("success", false);
            response.put("message", "El monto debe ser mayor a cero");
            return ResponseEntity.badRequest().body(response);
        }
        boolean reducido = cardDAO.reducirSaldo(numero, monto);
        response.put("success", reducido);
        response.put("message", reducido ? "Pago realizado exitosamente" : "Saldo insuficiente o tarjeta no encontrada");
        return reducido ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    // Eliminar tarjeta
    @DeleteMapping("/{numero}")
    public ResponseEntity<Void> eliminarTarjeta(@PathVariable String numero) {
        return cardDAO.delete(numero) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}