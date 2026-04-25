package com.tienda.inventario.controller;

import com.tienda.inventario.model.Venta;
import com.tienda.inventario.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST para el registro y consulta de ventas.
 * Aplica las reglas de negocio de facturacion (descuentos en linea).
 *
 * Endpoints disponibles:
 * GET  /api/ventas  - Historial completo de ventas
 * POST /api/ventas  - Registrar nueva venta (aplica descuento automaticamente)
 */
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VentaController {

    private final VentaService ventaService;

    /** GET /api/ventas - Retorna el historial completo de ventas registradas. */
    @GetMapping
    public ResponseEntity<List<Venta>> obtenerTodas() {
        return ResponseEntity.ok(ventaService.obtenerTodas());
    }

    /**
     * POST /api/ventas - Registra una nueva venta.
     * Calcula y aplica descuento segun tipo de compra y cantidad.
     * Actualiza automaticamente el stock del producto en el inventario.
     * Retorna 400 Bad Request si no hay stock suficiente.
     */
    @PostMapping
    public ResponseEntity<?> registrarVenta(@RequestBody Venta venta) {
        try {
            Venta nueva = ventaService.registrarVenta(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
