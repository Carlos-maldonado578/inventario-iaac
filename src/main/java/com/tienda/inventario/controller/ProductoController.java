package com.tienda.inventario.controller;

import com.tienda.inventario.model.Producto;
import com.tienda.inventario.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST para la gestion de productos del inventario.
 * Expone los endpoints CRUD del recurso /api/productos.
 *
 * Endpoints disponibles:
 * GET    /api/productos       - Leer todos los productos
 * GET    /api/productos/{id}  - Leer producto por ID
 * POST   /api/productos       - Crear nuevo producto
 * PUT    /api/productos/{id}  - Actualizar producto existente
 * DELETE /api/productos/{id}  - Eliminar producto
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    /** GET /api/productos - Retorna la lista completa de productos del inventario. */
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    /** GET /api/productos/{id} - Retorna un producto especifico por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /** POST /api/productos - Registra un nuevo producto en el inventario. */
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        Producto nuevo = productoService.guardar(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    /** PUT /api/productos/{id} - Modifica los datos de un producto existente. */
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id,
                                               @RequestBody Producto producto) {
        return productoService.obtenerPorId(id)
            .map(existente -> {
                producto.setId(id);
                return ResponseEntity.ok(productoService.guardar(producto));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /api/productos/{id} - Remueve un producto del inventario. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
