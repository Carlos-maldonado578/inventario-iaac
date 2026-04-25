package com.tienda.inventario.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa a un proveedor de productos de la tienda.
 * Almacena los datos de contacto y codigo de identificacion del proveedor.
 */
@Entity
@Table(name = "proveedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Codigo unico del proveedor, por ejemplo: PROV-001 */
    @Column(unique = true)
    private String codigo;

    /** Razon social o nombre comercial del proveedor */
    private String nombre;

    /** Direccion comercial del proveedor */
    private String direccion;

    /** Correo electronico de contacto */
    private String correo;

    /** Numero de telefono de contacto */
    private String telefono;
}
