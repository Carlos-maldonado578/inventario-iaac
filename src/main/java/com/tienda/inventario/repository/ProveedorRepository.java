package com.tienda.inventario.repository;

import com.tienda.inventario.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Proveedor.
 * Hereda operaciones CRUD: findAll, findById, save, deleteById.
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
}
