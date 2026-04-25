package com.tienda.inventario.service;

import com.tienda.inventario.model.Producto;
import com.tienda.inventario.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que encapsula la logica de negocio para la gestion de productos.
 * Aplica encapsulamiento al separar la logica de acceso a datos del controlador REST.
 * @RequiredArgsConstructor de Lombok inyecta las dependencias por constructor automaticamente.
 */
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    /**
     * Obtiene todos los productos registrados en el inventario.
     * Operacion de LECTURA - equivale a SELECT * FROM productos.
     */
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    /**
     * Obtiene un producto especifico por su identificador unico.
     * Operacion de LECTURA - equivale a SELECT * FROM productos WHERE id = ?
     */
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    /**
     * Registra un nuevo producto o actualiza uno existente en el inventario.
     * Operacion de ESCRITURA - equivale a INSERT o UPDATE segun si existe el ID.
     */
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    /**
     * Elimina un producto del inventario por su identificador.
     * Operacion de ELIMINACION - equivale a DELETE FROM productos WHERE id = ?
     */
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    /**
     * Descuenta del stock la cantidad de productos vendidos.
     * Aplica operaciones aritmeticas para calcular inventario disponible
     * y valida disponibilidad antes de realizar la actualizacion.
     *
     * @param codigo          codigo del producto vendido
     * @param cantidadVendida unidades vendidas en la transaccion
     * @return producto con el stock actualizado
     * @throws RuntimeException si el producto no existe o no hay stock suficiente
     */
    public Producto actualizarStock(String codigo, int cantidadVendida) {
        Producto producto = productoRepository.findByCodigo(codigo)
            .orElseThrow(() -> new RuntimeException(
                "Producto no encontrado con codigo: " + codigo));

        if (producto.getCantidad() < cantidadVendida) {
            throw new RuntimeException(
                "Stock insuficiente para '" + producto.getDescripcion() + "'. " +
                "Disponible: " + producto.getCantidad() +
                " | Solicitado: " + cantidadVendida);
        }

        int stockActualizado = producto.getCantidad() - cantidadVendida;
        producto.setCantidad(stockActualizado);

        return productoRepository.save(producto);
    }
}
