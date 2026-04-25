package com.tienda.inventario.service;

import com.tienda.inventario.model.Proveedor;
import com.tienda.inventario.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que encapsula la logica de negocio para la gestion de proveedores.
 */
@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    /** Obtiene todos los proveedores registrados. */
    public List<Proveedor> obtenerTodos() {
        return proveedorRepository.findAll();
    }

    /** Obtiene un proveedor por su identificador. */
    public Optional<Proveedor> obtenerPorId(Long id) {
        return proveedorRepository.findById(id);
    }

    /** Registra o actualiza un proveedor. */
    public Proveedor guardar(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    /** Elimina un proveedor por su identificador. */
    public void eliminar(Long id) {
        proveedorRepository.deleteById(id);
    }
}
