package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.ModeratorDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/moderator")
@CrossOrigin(origins = "*")
public class ModeratorController {

    private final ModeratorDAO moderatorDAO = new ModeratorDAO();

    // Agregar este método en la clase ModeratorController
    @GetMapping("/sanciones")
    public ResponseEntity<Map<String, Object>> getSancionesPaginadas(
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "10") int tamanoPagina) {

        if (pagina < 1)
            pagina = 1;
        if (tamanoPagina < 1 || tamanoPagina > 100)
            tamanoPagina = 10;

        Map<String, Object> resultado = moderatorDAO.getSancionesPaginadas(pagina, tamanoPagina);
        return ResponseEntity.ok(resultado);
    }

    // Obtiene artículos pendientes con paginación
    @GetMapping("/pendientes")
    public ResponseEntity<Map<String, Object>> getArticulosPendientes(
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "5") int tamanoPagina) {

        if (pagina < 1)
            pagina = 1;
        if (tamanoPagina < 1 || tamanoPagina > 50)
            tamanoPagina = 5;

        Map<String, Object> resultado = moderatorDAO.getArticulosPendientesPaginados(pagina, tamanoPagina);
        return ResponseEntity.ok(resultado);
    }

    // Aprueba un artículo
    @PutMapping("/aprobar/{idArticulo}")
    public ResponseEntity<Map<String, Object>> aprobarArticulo(@PathVariable int idArticulo) {
        boolean exito = moderatorDAO.aprobarArticulo(idArticulo);

        if (exito) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Artículo aprobado exitosamente"));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error al aprobar el artículo"));
        }
    }

    // Rechaza un artículo
    @PutMapping("/rechazar/{idArticulo}")
    public ResponseEntity<Map<String, Object>> rechazarArticulo(@PathVariable int idArticulo) {
        boolean exito = moderatorDAO.rechazarArticulo(idArticulo);

        if (exito) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Artículo rechazado exitosamente"));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error al rechazar el artículo"));
        }
    }
}