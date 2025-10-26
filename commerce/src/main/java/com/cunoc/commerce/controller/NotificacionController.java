package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.NotificacionDAO;
import com.cunoc.commerce.entity.Notificacion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    private final NotificacionDAO notificacionDAO = new NotificacionDAO();

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