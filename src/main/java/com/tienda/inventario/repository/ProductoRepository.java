package com.tienda.inventario.repository;

import com.tienda.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Producto.
 * Hereda operaciones CRUD completas de JpaRepository (herencia en POO).
 * Spring Data JPA genera la implementacion automaticamente en tiempo de ejecucion.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca un producto por su codigo unico.
     * Spring Data genera la consulta SQL automaticamente por el nombre del metodo.
     * @param codigo el codigo unico del producto (ej: ELEC-001)
     * @return Optional con el producto si existe, o vacio si no se encuentra
     */
    Optional<Producto> findByCodigo(String codigo);
}
