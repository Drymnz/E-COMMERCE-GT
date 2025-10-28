package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.NotificacionDAO;
import com.cunoc.commerce.entity.Notificacion;
import com.cunoc.commerce.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    private final NotificacionDAO notificacionDAO = new NotificacionDAO();
    @Autowired
    private NotificacionService notificacionService;

    // Notificar cambio de estado de pedido
    @PostMapping("/pedido/cambio-estado")
    public ResponseEntity<Map<String, Object>> notificarCambioPedido(@RequestBody Map<String, Object> req) {
        try {
            LocalDateTime fechaEstimada = req.containsKey("fecha_estimada_entrega") && req.get("fecha_estimada_entrega") != null
                ? LocalDateTime.parse((String) req.get("fecha_estimada_entrega")) : null;
            
            boolean enviado = notificacionService.notificarCambioEstadoPedido(
                (int) req.get("id_usuario"), (int) req.get("numero_pedido"),
                (String) req.get("estado_anterior"), (String) req.get("estado_nuevo"),
                (String) req.get("total_pedido"), fechaEstimada);
            
            return ResponseEntity.ok(Map.of(
                "success", enviado,
                "message", enviado ? "Notificacion enviada exitosamente" : "Error al enviar notificacion"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }
    
    // Notificar producto aprobado
    @PostMapping("/producto/aprobado")
    public ResponseEntity<Map<String, Object>> notificarProductoAprobado(@RequestBody Map<String, Object> req) {
        try {
            boolean enviado = notificacionService.notificarProductoAprobado(
                (int) req.get("id_vendedor"), (String) req.get("nombre_producto"),
                (String) req.get("precio_producto"), (int) req.get("id_moderador"));
            
            return ResponseEntity.ok(Map.of(
                "success", enviado,
                "message", enviado ? "Notificacion enviada exitosamente" : "Error al enviar notificacion"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }
    
    // Notificar producto rechazado
    @PostMapping("/producto/rechazado")
    public ResponseEntity<Map<String, Object>> notificarProductoRechazado(@RequestBody Map<String, Object> req) {
        try {
            boolean enviado = notificacionService.notificarProductoRechazado(
                (int) req.get("id_vendedor"), (String) req.get("nombre_producto"),
                (int) req.get("id_moderador"), (String) req.get("motivo_rechazo"));
            
            return ResponseEntity.ok(Map.of(
                "success", enviado,
                "message", enviado ? "Notificacion enviada exitosamente" : "Error al enviar notificacion"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }

    // Obtener notificaciones paginadas
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerNotificacionesPaginadas(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<Notificacion> notificaciones = notificacionDAO.findAllPaginated(page, pageSize);
            int totalNotificaciones = notificacionDAO.countTotal();
            
            return ResponseEntity.ok(Map.of(
                "notificaciones", notificaciones,
                "currentPage", page,
                "pageSize", pageSize,
                "totalNotificaciones", totalNotificaciones,
                "totalPages", (int) Math.ceil((double) totalNotificaciones / pageSize)));
        } catch (Exception e) {
            System.err.println("Error al obtener notificaciones: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}