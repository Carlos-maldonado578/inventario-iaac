package com.tienda.inventario.repository;

import com.tienda.inventario.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Cliente.
 * Hereda operaciones CRUD: findAll, findById, save, deleteById.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
