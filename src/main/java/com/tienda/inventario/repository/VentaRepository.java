package com.tienda.inventario.repository;

import com.tienda.inventario.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Venta.
 * Hereda operaciones CRUD: findAll, findById, save, deleteById.
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
}
