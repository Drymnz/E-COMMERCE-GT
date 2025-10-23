package com.cunoc.commerce.controller;

import com.cunoc.commerce.controller.datatoobject.CompraDAO;
import com.cunoc.commerce.entity.CarritoCompra;
import com.cunoc.commerce.entity.RespuestaCompra;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/compra") 
@CrossOrigin(origins = "*")
public class CompraController {

    private final CompraDAO compraDAO = new CompraDAO();

    /**
     * Procesa una compra de un carrito
     * 
     * {
     *   "id_usuario": 1,
     *   "id_vendedor": 1,
     *   "items": [
     *     {
     *       "articulo": {
     *         "id_articulo": 1,
     *         "nombre": "Laptop",
     *         "precio": 8500.00,
     *         "stock": 5
     *       },
     *       "cantidad": 2
     *     }
     *   ]
     * }
     */
    @PostMapping("/procesar")
    public ResponseEntity<RespuestaCompra> procesarCompra(@RequestBody CarritoCompra carrito) {
        try {
            // Validar datos básicos
            if (carrito == null || carrito.isEmpty()) {
                RespuestaCompra respuesta = new RespuestaCompra(false, "El carrito está vacío");
                return ResponseEntity.badRequest().body(respuesta);
            }

            if (carrito.getIdUsuario() <= 0) {
                RespuestaCompra respuesta = new RespuestaCompra(false, "ID de usuario inválido");
                return ResponseEntity.badRequest().body(respuesta);
            }

            // Procesar la compra
            RespuestaCompra respuesta = compraDAO.procesarCompra(carrito);

            if (respuesta.isExitoso()) {
                return ResponseEntity.ok(respuesta);
            } else {
                return ResponseEntity.badRequest().body(respuesta);
            }

        } catch (Exception e) {
            System.err.println("Error en CompraController: " + e.getMessage());
            e.printStackTrace();
            RespuestaCompra respuesta = new RespuestaCompra(
                false, 
                "Error interno del servidor: " + e.getMessage()
            );
            return ResponseEntity.status(500).body(respuesta);
        }
    }

    //Endpoint de prueba
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Servicio de compras funcionando correctamente");
    }
}