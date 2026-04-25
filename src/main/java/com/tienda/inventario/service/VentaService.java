package com.tienda.inventario.service;

import com.tienda.inventario.model.TipoCompra;
import com.tienda.inventario.model.Venta;
import com.tienda.inventario.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Servicio que gestiona el registro de ventas y la aplicacion de descuentos.
 * Contiene las reglas de negocio de facturacion de la tienda electronica.
 *
 * Regla principal:
 * - Compra EN_LINEA con MAS de 3 productos: aplica 15% de descuento.
 * - Cualquier otro caso: sin descuento (0%).
 */
@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoService productoService;

    /** Porcentaje de descuento para compras en linea con cantidad mayor al minimo */
    private static final double DESCUENTO_EN_LINEA = 15.0;

    /** Minimo de productos requeridos para acceder al descuento en linea */
    private static final int MINIMO_PRODUCTOS_DESCUENTO = 3;

    /**
     * Registra una venta completa:
     * 1. Calcula el descuento segun tipo de compra y cantidad.
     * 2. Calcula el total final con operaciones aritmeticas.
     * 3. Actualiza el stock del producto en el inventario.
     * 4. Guarda la venta en la base de datos.
     *
     * @param venta objeto con los datos de la venta ingresados por el usuario
     * @return venta registrada con total y descuento calculados
     */
    public Venta registrarVenta(Venta venta) {

        double porcentajeDescuento = 0.0;
        if (venta.getTipoCompra() == TipoCompra.EN_LINEA
                && venta.getCantidadProductos() > MINIMO_PRODUCTOS_DESCUENTO) {
            porcentajeDescuento = DESCUENTO_EN_LINEA;
        }

        double subtotal       = venta.getPrecioUnitario() * venta.getCantidadProductos();
        double montoDescuento = subtotal * (porcentajeDescuento / 100.0);
        double totalFinal     = subtotal - montoDescuento;

        venta.setDescuentoPorcentaje(porcentajeDescuento);
        venta.setTotal(totalFinal);

        productoService.actualizarStock(venta.getCodigoProducto(), venta.getCantidadProductos());

        return ventaRepository.save(venta);
    }

    /**
     * Obtiene el historial completo de ventas registradas.
     * Operacion de LECTURA - equivale a SELECT * FROM ventas.
     */
    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }
}
