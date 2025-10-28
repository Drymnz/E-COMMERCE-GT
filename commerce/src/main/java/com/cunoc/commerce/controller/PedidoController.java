package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.PedidoDAO;
import com.cunoc.commerce.entity.Pedido;
import com.cunoc.commerce.service.NotificacionPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedido")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private NotificacionPedidoService notificacionPedidoService;
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    // Obtener pedidos por usuario
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Pedido>> obtenerPedidosUsuario(@PathVariable int idUsuario) {
        try {
            return ResponseEntity.ok(pedidoDAO.findByUsuario(idUsuario));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Obtener pedidos en curso
    @GetMapping("/en-curso")
    public ResponseEntity<List<Pedido>> obtenerPedidosEnCurso() {
        try {
            return ResponseEntity.ok(pedidoDAO.findPedidosEnCurso());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Obtener pedido por ID
    @GetMapping("/{idPedido}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable int idPedido) {
        Pedido pedido = pedidoDAO.findById(idPedido);
        return pedido != null ? ResponseEntity.ok(pedido) : ResponseEntity.notFound().build();
    }

    // Actualizar fecha de entrega
    @PutMapping("/{idPedido}/fecha-entrega")
    public ResponseEntity<Void> actualizarFechaEntrega(
            @PathVariable int idPedido, @RequestBody Map<String, String> body) {
        try {
            LocalDateTime nuevaFecha = LocalDateTime.parse(body.get("fecha_hora_entrega"));
            boolean actualizado = pedidoDAO.updateFechaEntrega(idPedido, nuevaFecha);
            if (actualizado) notificacionPedidoService.notificarCambioFechaEntrega(idPedido, nuevaFecha);
            return actualizado ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Actualizar estado del pedido
    @PutMapping("/{idPedido}/estado")
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable int idPedido, @RequestBody Map<String, Integer> body) {
        try {
            Integer idEstado = body.get("id_estado_pedido");
            if (idEstado == null) return ResponseEntity.badRequest().build();
            
            boolean actualizado = pedidoDAO.actualizarEstado(idPedido, idEstado);
            if (actualizado) notificacionPedidoService.notificarCambioEstado(idPedido, idEstado);
            return actualizado ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}