package com.tienda.inventario.service;

import com.tienda.inventario.model.Cliente;
import com.tienda.inventario.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que encapsula la logica de negocio para la gestion de clientes.
 */
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    /** Obtiene todos los clientes registrados. */
    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    /** Obtiene un cliente por su identificador. */
    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    /** Registra o actualiza un cliente. */
    public Cliente guardar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    /** Elimina un cliente por su identificador. */
    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }
}
