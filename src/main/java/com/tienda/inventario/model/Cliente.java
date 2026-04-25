package com.tienda.inventario.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa a un cliente de la tienda electronica.
 * Almacena los datos de contacto necesarios para la facturacion y despacho.
 */
@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre completo del cliente */
    @Column(nullable = false)
    private String nombre;

    /** Direccion de domicilio o despacho */
    private String direccion;

    /** Numero de telefono de contacto */
    private String telefono;
}
