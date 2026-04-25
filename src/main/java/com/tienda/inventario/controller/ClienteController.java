package com.tienda.inventario.controller;

import com.tienda.inventario.model.Cliente;
import com.tienda.inventario.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST para la gestion de clientes.
 * Expone los endpoints CRUD del recurso /api/clientes.
 */
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;

    /** GET /api/clientes - Retorna todos los clientes registrados. */
    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerTodos() {
        return ResponseEntity.ok(clienteService.obtenerTodos());
    }

    /** GET /api/clientes/{id} - Retorna un cliente por su ID. */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        return clienteService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /** POST /api/clientes - Registra un nuevo cliente. */
    @PostMapping
    public ResponseEntity<Cliente> crear(@RequestBody Cliente cliente) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(clienteService.guardar(cliente));
    }

    /** PUT /api/clientes/{id} - Actualiza un cliente existente. */
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id,
                                              @RequestBody Cliente cliente) {
        return clienteService.obtenerPorId(id)
            .map(existente -> {
                cliente.setId(id);
                return ResponseEntity.ok(clienteService.guardar(cliente));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /api/clientes/{id} - Elimina un cliente por su ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
