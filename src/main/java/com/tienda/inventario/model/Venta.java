package com.tienda.inventario.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa una venta registrada en el sistema.
 * Incluye la logica de negocio para aplicar descuentos segun tipo de compra:
 * - Compra EN_LINEA con mas de 3 productos: 15% de descuento.
 * - Compra DIRECTA o en linea con 3 o menos productos: sin descuento.
 */
@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del cliente que realiza la compra */
    private String nombreCliente;

    /** Tipo de compra: EN_LINEA o DIRECTO */
    @Enumerated(EnumType.STRING)
    private TipoCompra tipoCompra;

    /** Codigo del producto vendido (referencia al codigo en tabla productos) */
    private String codigoProducto;

    /** Numero de unidades compradas en esta transaccion */
    private int cantidadProductos;

    /** Precio por unidad al momento de realizar la venta */
    private double precioUnitario;

    /** Porcentaje de descuento aplicado: 0.0 o 15.0 */
    private double descuentoPorcentaje;

    /** Monto total final a pagar despues de aplicar el descuento */
    private double total;
}
