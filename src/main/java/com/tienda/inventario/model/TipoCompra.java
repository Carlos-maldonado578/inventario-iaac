package com.tienda.inventario.model;

/**
 * Enumeracion que define los tipos de compra disponibles en el sistema.
 * EN_LINEA: compra digital - aplica 15% descuento si cantidad > 3 productos.
 * DIRECTO: compra presencial en tienda - sin descuento adicional.
 */
public enum TipoCompra {
    EN_LINEA,
    DIRECTO
}
