package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.ArticleDAO;
import com.cunoc.commerce.entity.Article;
import com.cunoc.commerce.entity.Publicacion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/article")
@CrossOrigin(origins = "*")
public class ArticleController {
    private final ArticleDAO articleDAO = new ArticleDAO();

    // Crear nueva publicación
    @PostMapping
    public ResponseEntity<?> createPublicacion(@RequestBody Publicacion publicacion) {
        try {
            if (publicacion == null) return ResponseEntity.badRequest().body(error("Validación fallida", "La publicación no puede ser nula"));
            if (publicacion.getIdUsuario() == null) return ResponseEntity.badRequest().body(error("Validación fallida", "El ID del usuario es obligatorio"));
            
            Article articulo = publicacion.getArticulo();
            if (articulo == null) return ResponseEntity.badRequest().body(error("Validación fallida", "El artículo es obligatorio"));
            if (articulo.getNombre() == null || articulo.getNombre().trim().isEmpty()) return ResponseEntity.badRequest().body(error("Validación fallida", "El nombre del artículo es obligatorio"));
            if (articulo.getPrecio() == null || articulo.getPrecio().compareTo(BigDecimal.ZERO) < 0) return ResponseEntity.badRequest().body(error("Validación fallida", "El precio debe ser mayor o igual a 0"));
            if (articulo.getStock() == null || articulo.getStock() < 0) return ResponseEntity.badRequest().body(error("Validación fallida", "El stock debe ser mayor o igual a 0"));
            if (articulo.getIdEstadoArticulo() == null) return ResponseEntity.badRequest().body(error("Validación fallida", "El estado del artículo es obligatorio"));
            if (articulo.getCategorias() == null || articulo.getCategorias().isEmpty()) return ResponseEntity.badRequest().body(error("Validación fallida", "Debe seleccionar al menos una categoría"));

            int newId = articleDAO.create(publicacion);
            return newId > 0 ? ResponseEntity.status(HttpStatus.CREATED).body(success("Publicación creada exitosamente", publicacion))
                    : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Error al crear publicación", "No se pudo insertar la publicación"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Error al crear publicación", e.getMessage()));
        }
    }

    // Obtener artículos disponibles
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableArticles() {
        try {
            return ResponseEntity.ok(success("Artículos disponibles obtenidos", articleDAO.findAllAvailable()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Error al obtener artículos disponibles", e.getMessage()));
        }
    }

    // Obtener artículos por ID de usuario
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getArticlesByUserId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(success("Artículos del usuario obtenidos", articleDAO.findByUserId(id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Error al obtener artículos del usuario", e.getMessage()));
        }
    }

    // Actualizar artículo
    @PutMapping("/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable int id, @RequestBody Article article) {
        try {
            Article existing = articleDAO.findById(id);
            if (existing == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Artículo no encontrado", "No existe artículo con ID: " + id));
            if (article.getNombre() == null || article.getNombre().trim().isEmpty()) return ResponseEntity.badRequest().body(error("Validación fallida", "El nombre es obligatorio"));
            if (article.getPrecio() == null || article.getPrecio().compareTo(BigDecimal.ZERO) < 0) return ResponseEntity.badRequest().body(error("Validación fallida", "El precio debe ser mayor o igual a 0"));
            if (article.getStock() == null || article.getStock() < 0) return ResponseEntity.badRequest().body(error("Validación fallida", "El stock debe ser mayor o igual a 0"));

            article.setIdArticulo(id);
            return articleDAO.update(article) ? ResponseEntity.ok(success("Artículo actualizado exitosamente", articleDAO.findById(id)))
                    : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Error al actualizar", "No se pudo actualizar el artículo"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Error al actualizar artículo", e.getMessage()));
        }
    }

    // Obtener artículo por usuario e ID
    @GetMapping("/user/{idUsuario}/article/{idArticulo}")
    public ResponseEntity<?> getArticleByUserAndId(@PathVariable int idUsuario, @PathVariable int idArticulo) {
        try {
            Article article = articleDAO.findByIdAndUserId(idArticulo, idUsuario);
            return article == null ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Artículo no encontrado", "No existe el artículo o no pertenece al usuario"))
                    : ResponseEntity.ok(success("Artículo obtenido exitosamente", article));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Error al obtener artículo", e.getMessage()));
        }
    }

    // Eliminar artículo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable int id) {
        try {
            if (articleDAO.findById(id) == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Artículo no encontrado", "No existe artículo con ID: " + id));
            return articleDAO.delete(id) ? ResponseEntity.ok(success("Artículo eliminado exitosamente", null))
                    : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Error al eliminar", "No se pudo eliminar el artículo"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Error al eliminar artículo", e.getMessage()));
        }
    }

    // Obtener detalles de artículo
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getArticle(@PathVariable int id) {
        try {
            if (id <= 0) return ResponseEntity.badRequest().body(error("Parámetro inválido", "El ID del artículo debe ser mayor a 0"));
            Article article = articleDAO.findById(id);
            return article != null ? ResponseEntity.ok(success("Artículo encontrado exitosamente", article))
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Artículo no encontrado", "No existe un artículo con el ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Error al buscar el artículo", e.getMessage()));
        }
    }

    // Actualizar stock de artículo
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable int id, @RequestBody Map<String, Integer> body) {
        try {
            if (!body.containsKey("stock")) return ResponseEntity.badRequest().body(error("Parámetro faltante", "Se requiere el campo 'stock'"));
            int newStock = body.get("stock");
            if (newStock < 0) return ResponseEntity.badRequest().body(error("Validación fallida", "El stock debe ser mayor o igual a 0"));
            if (articleDAO.findById(id) == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Artículo no encontrado", "No existe artículo con ID: " + id));
            
            return articleDAO.updateStock(id, newStock) ? ResponseEntity.ok(success("Stock actualizado exitosamente", articleDAO.findById(id)))
                    : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Error al actualizar stock", "No se pudo actualizar el stock"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Error al actualizar stock", e.getMessage()));
        }
    }

    // Actualizar estado de artículo
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateArticleStatus(@PathVariable int id, @RequestBody Map<String, Integer> body) {
        try {
            if (!body.containsKey("id_estado_articulo")) return ResponseEntity.badRequest().body(error("Validación fallida", "El campo id_estado_articulo es obligatorio"));
            if (articleDAO.findById(id) == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error("Artículo no encontrado", "No existe artículo con ID: " + id));
            
            return articleDAO.updateStatus(id, body.get("id_estado_articulo")) ? ResponseEntity.ok(success("Estado actualizado exitosamente", null))
                    : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Error al actualizar estado", "No se pudo actualizar el estado del artículo"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Error al actualizar estado", e.getMessage()));
        }
    }

    private Map<String, Object> success(String message, Object data) {
        Map<String, Object> r = new HashMap<>();
        r.put("message", message);
        r.put("data", data);
        return r;
    }

    private Map<String, Object> error(String message, String detail) {
        Map<String, Object> r = new HashMap<>();
        r.put("message", message);
        r.put("error", detail);
        return r;
    }
}