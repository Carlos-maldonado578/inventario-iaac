package com.tienda.inventario.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un producto en el inventario de la tienda.
 * Lombok genera automaticamente: getters, setters, constructores y toString.
 * La anotacion @Entity indica que esta clase se mapea a una tabla en H2.
 */
@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    /** Identificador unico generado automaticamente por H2 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Codigo unico del producto, por ejemplo: ELEC-001 */
    @Column(unique = true, nullable = false)
    private String codigo;

    /** Nombre o descripcion del articulo electronico */
    @Column(nullable = false)
    private String descripcion;

    /** Unidades disponibles en el inventario actual */
    private int cantidad;

    /** Precio unitario del producto en pesos */
    private double precio;

    /** Bodega y estante donde esta fisicamente el producto */
    private String ubicacion;
}
