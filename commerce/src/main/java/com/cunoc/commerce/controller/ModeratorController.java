package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.ModeratorDAO;
import com.cunoc.commerce.entity.NotificacionArticuloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/moderator")
@CrossOrigin(origins = "*")
public class ModeratorController {

    @Autowired
    private NotificacionArticuloService notificacionArticuloService;
    private final ModeratorDAO moderatorDAO = new ModeratorDAO();

    // Obtener sanciones paginadas
    @GetMapping("/sanciones")
    public ResponseEntity<Map<String, Object>> getSancionesPaginadas(
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "10") int tamanoPagina) {
        return ResponseEntity.ok(moderatorDAO.getSancionesPaginadas(
            Math.max(1, pagina), 
            (tamanoPagina < 1 || tamanoPagina > 100) ? 10 : tamanoPagina));
    }

    // Obtener artículos pendientes paginados
    @GetMapping("/pendientes")
    public ResponseEntity<Map<String, Object>> getArticulosPendientes(
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "5") int tamanoPagina) {
        return ResponseEntity.ok(moderatorDAO.getArticulosPendientesPaginados(
            Math.max(1, pagina),
            (tamanoPagina < 1 || tamanoPagina > 50) ? 5 : tamanoPagina));
    }

    // Aprobar artículo
    @PutMapping("/aprobar/{idArticulo}")
    public ResponseEntity<Map<String, Object>> aprobarArticulo(
            @PathVariable int idArticulo,
            @RequestParam(required = false, defaultValue = "0") int idModerador) {
        boolean exito = moderatorDAO.aprobarArticulo(idArticulo);
        if (exito) notificacionArticuloService.notificarAprobacion(idArticulo, idModerador);
        return ResponseEntity.status(exito ? 200 : 400).body(Map.of(
            "success", exito,
            "message", exito ? "Artículo aprobado exitosamente" : "Error al aprobar el artículo"));
    }

    // Rechazar artículo
    @PutMapping("/rechazar/{idArticulo}")
    public ResponseEntity<Map<String, Object>> rechazarArticulo(
            @PathVariable int idArticulo,
            @RequestParam(required = false, defaultValue = "0") int idModerador,
            @RequestBody(required = false) Map<String, String> body) {
        boolean exito = moderatorDAO.rechazarArticulo(idArticulo);
        if (exito) notificacionArticuloService.notificarRechazo(idArticulo, idModerador, 
            body != null ? body.get("motivo") : null);
        return ResponseEntity.status(exito ? 200 : 400).body(Map.of(
            "success", exito,
            "message", exito ? "Artículo rechazado exitosamente" : "Error al rechazar el artículo"));
    }
}