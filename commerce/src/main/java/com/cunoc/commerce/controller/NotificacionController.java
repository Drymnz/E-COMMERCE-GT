package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.NotificacionDAO;
import com.cunoc.commerce.entity.Notificacion;
import com.cunoc.commerce.service.NotificacionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
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
    public ResponseEntity<Map<String, Object>> notificarCambioPedido(
            @RequestBody Map<String, Object> request) {
        
        try {
            int idUsuario = (int) request.get("id_usuario");
            int numeroPedido = (int) request.get("numero_pedido");
            String estadoAnterior = (String) request.get("estado_anterior");
            String estadoNuevo = (String) request.get("estado_nuevo");
            String totalPedido = (String) request.get("total_pedido");
            
            LocalDateTime fechaEstimada = null;
            if (request.containsKey("fecha_estimada_entrega") 
                    && request.get("fecha_estimada_entrega") != null) {
                fechaEstimada = LocalDateTime.parse((String) request.get("fecha_estimada_entrega"));
            }
            
            boolean enviado = notificacionService.notificarCambioEstadoPedido(
                    idUsuario,
                    numeroPedido,
                    estadoAnterior,
                    estadoNuevo,
                    totalPedido,
                    fechaEstimada
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", enviado);
            response.put("message", enviado 
                    ? "Notificacion enviada exitosamente" 
                    : "Error al enviar notificacion");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Notificar producto aprobado
    @PostMapping("/producto/aprobado")
    public ResponseEntity<Map<String, Object>> notificarProductoAprobado(
            @RequestBody Map<String, Object> request) {
        
        try {
            int idVendedor = (int) request.get("id_vendedor");
            String nombreProducto = (String) request.get("nombre_producto");
            String precioProducto = (String) request.get("precio_producto");
            int idModerador = (int) request.get("id_moderador");
            
            boolean enviado = notificacionService.notificarProductoAprobado(
                    idVendedor,
                    nombreProducto,
                    precioProducto,
                    idModerador
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", enviado);
            response.put("message", enviado 
                    ? "Notificacion enviada exitosamente" 
                    : "Error al enviar notificacion");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // Notificar producto rechazado
    @PostMapping("/producto/rechazado")
    public ResponseEntity<Map<String, Object>> notificarProductoRechazado(
            @RequestBody Map<String, Object> request) {
        
        try {
            int idVendedor = (int) request.get("id_vendedor");
            String nombreProducto = (String) request.get("nombre_producto");
            int idModerador = (int) request.get("id_moderador");
            String motivoRechazo = (String) request.get("motivo_rechazo");
            
            boolean enviado = notificacionService.notificarProductoRechazado(
                    idVendedor,
                    nombreProducto,
                    idModerador,
                    motivoRechazo
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", enviado);
            response.put("message", enviado 
                    ? "Notificacion enviada exitosamente" 
                    : "Error al enviar notificacion");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    //Obtener todas las notificaciones por paginas
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerNotificacionesPaginadas(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        try {
            List<Notificacion> notificaciones = notificacionDAO.findAllPaginated(page, pageSize);
            int totalNotificaciones = notificacionDAO.countTotal();
            int totalPages = (int) Math.ceil((double) totalNotificaciones / pageSize);
            
            Map<String, Object> response = new HashMap<>();
            response.put("notificaciones", notificaciones);
            response.put("currentPage", page);
            response.put("pageSize", pageSize);
            response.put("totalNotificaciones", totalNotificaciones);
            response.put("totalPages", totalPages);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error al obtener notificaciones: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}