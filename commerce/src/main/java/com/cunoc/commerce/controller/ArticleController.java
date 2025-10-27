package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.ArticleDAO;
import com.cunoc.commerce.entity.Article;
import com.cunoc.commerce.entity.Publicacion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/article")
@CrossOrigin(origins = "*")
public class ArticleController {

    private final ArticleDAO articleDAO = new ArticleDAO();

    // Crea una nueva publicación con su artículo
    @PostMapping
    public ResponseEntity<?> createPublicacion(@RequestBody Publicacion publicacion) {
        try {
            // Validar que existe el objeto publicacion
            if (publicacion == null) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Validación fallida", "La publicación no puede ser nula"));
            }

            // Validar id_usuario
            if (publicacion.getIdUsuario() == null) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Validación fallida", "El ID del usuario es obligatorio"));
            }

            // Validar que existe el artículo
            Article articulo = publicacion.getArticulo();
            if (articulo == null) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Validación fallida", "El artículo es obligatorio"));
            }

            // Validaciones del artículo
            if (articulo.getNombre() == null || articulo.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Validación fallida", "El nombre del artículo es obligatorio"));
            }
            if (articulo.getPrecio() == null || articulo.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Validación fallida", "El precio debe ser mayor o igual a 0"));
            }
            if (articulo.getStock() == null || articulo.getStock() < 0) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Validación fallida", "El stock debe ser mayor o igual a 0"));
            }
            if (articulo.getIdEstadoArticulo() == null) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Validación fallida", "El estado del artículo es obligatorio"));
            }

            // Validación de categorías (OPCIONAL)
            if (articulo.getCategorias() == null || articulo.getCategorias().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Validación fallida", "Debe seleccionar al menos una categoría"));
            }

            int newId = articleDAO.create(publicacion);
            if (newId > 0) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(createSuccessResponse("Publicación creada exitosamente", publicacion));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("Error al crear publicación", "No se pudo insertar la publicación"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al crear publicación", e.getMessage()));
        }
    }

    // Lista todos los artículos disponibles (con stock > 0)
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableArticles() {
        try {
            List<Article> articles = articleDAO.findAllAvailable();
            return ResponseEntity.ok(createSuccessResponse("Artículos disponibles obtenidos", articles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al obtener artículos disponibles", e.getMessage()));
        }
    }

    // Lista todos los artículos de un usuario
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getArticlesByUserId(@PathVariable int id) {
        try {
            List<Article> articles = articleDAO.findByUserId(id);
            return ResponseEntity.ok(createSuccessResponse("Artículos del usuario obtenidos", articles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al obtener artículos del usuario", e.getMessage()));
        }
    }

    // Actualiza un artículo existente
    @PutMapping("/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable int id, @RequestBody Article article) {
        try {
            // Verificar que el artículo existe
            Article existingArticle = articleDAO.findById(id);
            if (existingArticle == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Artículo no encontrado", "No existe artículo con ID: " + id));
            }

            // Validaciones básicas
            if (article.getNombre() == null || article.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Validación fallida", "El nombre es obligatorio"));
            }
            if (article.getPrecio() == null || article.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Validación fallida", "El precio debe ser mayor o igual a 0"));
            }
            if (article.getStock() == null || article.getStock() < 0) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Validación fallida", "El stock debe ser mayor o igual a 0"));
            }

            article.setIdArticulo(id);
            boolean updated = articleDAO.update(article);

            if (updated) {
                Article updatedArticle = articleDAO.findById(id);
                return ResponseEntity.ok(createSuccessResponse("Artículo actualizado exitosamente", updatedArticle));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("Error al actualizar", "No se pudo actualizar el artículo"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al actualizar artículo", e.getMessage()));
        }
    }

    // Obtener articulo solo si el usuario es el creado
    @GetMapping("/user/{idUsuario}/article/{idArticulo}")
    public ResponseEntity<?> getArticleByUserAndId(
            @PathVariable int idUsuario,
            @PathVariable int idArticulo) {
        try {
            Article article = articleDAO.findByIdAndUserId(idArticulo, idUsuario);

            if (article == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Artículo no encontrado",
                                "No existe el artículo o no pertenece al usuario"));
            }

            return ResponseEntity.ok(createSuccessResponse("Artículo obtenido exitosamente", article));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al obtener artículo", e.getMessage()));
        }
    }

    // Elimina un artículo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable int id) {
        try {
            Article existingArticle = articleDAO.findById(id);
            if (existingArticle == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Artículo no encontrado", "No existe artículo con ID: " + id));
            }

            boolean deleted = articleDAO.delete(id);
            if (deleted) {
                return ResponseEntity.ok(createSuccessResponse("Artículo eliminado exitosamente", null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("Error al eliminar", "No se pudo eliminar el artículo"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al eliminar artículo", e.getMessage()));
        }
    }

    // Busca artículos por id (usando /details/{id} para evitar conflictos)
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getArticle(@PathVariable int id) {
        try {
            if (id <= 0) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Parámetro inválido", "El ID del artículo debe ser mayor a 0"));
            }

            Article article = articleDAO.findById(id);

            if (article != null) {
                return ResponseEntity.ok(createSuccessResponse("Artículo encontrado exitosamente", article));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Artículo no encontrado", "No existe un artículo con el ID: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al buscar el artículo", e.getMessage()));
        }
    }

    // Actualiza solo el stock de un artículo
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> updateStock(@PathVariable int id, @RequestBody Map<String, Integer> body) {
        try {
            if (!body.containsKey("stock")) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Parámetro faltante", "Se requiere el campo 'stock'"));
            }

            int newStock = body.get("stock");
            if (newStock < 0) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Validación fallida", "El stock debe ser mayor o igual a 0"));
            }

            Article existingArticle = articleDAO.findById(id);
            if (existingArticle == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Artículo no encontrado", "No existe artículo con ID: " + id));
            }

            boolean updated = articleDAO.updateStock(id, newStock);
            if (updated) {
                Article updatedArticle = articleDAO.findById(id);
                return ResponseEntity.ok(createSuccessResponse("Stock actualizado exitosamente", updatedArticle));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("Error al actualizar stock", "No se pudo actualizar el stock"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al actualizar stock", e.getMessage()));
        }
    }

    // Actualizando el estado
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateArticleStatus(
            @PathVariable int id,
            @RequestBody Map<String, Integer> body) {
        try {
            // Validar que existe el campo id_estado_articulo
            if (!body.containsKey("id_estado_articulo")) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("Validación fallida", "El campo id_estado_articulo es obligatorio"));
            }

            int idEstadoArticulo = body.get("id_estado_articulo");

            // Verificar que el artículo existe
            Article existingArticle = articleDAO.findById(id);
            if (existingArticle == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Artículo no encontrado", "No existe artículo con ID: " + id));
            }

            // Actualizar el estado
            boolean updated = articleDAO.updateStatus(id, idEstadoArticulo);
            if (updated) {
                return ResponseEntity.ok(createSuccessResponse("Estado actualizado exitosamente", null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("Error al actualizar estado",
                                "No se pudo actualizar el estado del artículo"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al actualizar estado", e.getMessage()));
        }
    }

    // Crea una respuesta exitosa estandarizada
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    // Crea una respuesta de error estandarizada
    private Map<String, Object> createErrorResponse(String message, String detail) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("error", detail);
        return response;
    }
}