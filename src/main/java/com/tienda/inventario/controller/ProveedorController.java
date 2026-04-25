package com.tienda.inventario.controller;

import com.tienda.inventario.model.Proveedor;
import com.tienda.inventario.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST para la gestion de proveedores.
 * Expone los endpoints CRUD del recurso /api/proveedores.
 */
@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProveedorController {

    private final ProveedorService proveedorService;

    /** GET /api/proveedores - Retorna todos los proveedores registrados. */
    @GetMapping
    public ResponseEntity<List<Proveedor>> obtenerTodos() {
        return ResponseEntity.ok(proveedorService.obtenerTodos());
    }

    /** GET /api/proveedores/{id} - Retorna un proveedor por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtenerPorId(@PathVariable Long id) {
        return proveedorService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /** POST /api/proveedores - Registra un nuevo proveedor. */
    @PostMapping
    public ResponseEntity<Proveedor> crear(@RequestBody Proveedor proveedor) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(proveedorService.guardar(proveedor));
    }

    /** PUT /api/proveedores/{id} - Actualiza un proveedor existente. */
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(@PathVariable Long id,
                                                @RequestBody Proveedor proveedor) {
        return proveedorService.obtenerPorId(id)
            .map(existente -> {
                proveedor.setId(id);
                return ResponseEntity.ok(proveedorService.guardar(proveedor));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /api/proveedores/{id} - Elimina un proveedor por su ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proveedorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
