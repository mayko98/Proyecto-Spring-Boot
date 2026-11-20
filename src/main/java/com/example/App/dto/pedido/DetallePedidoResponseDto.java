package com.example.App.dto.pedido;

import com.example.App.model.DetallePedido;

import java.math.BigDecimal;

public record DetallePedidoResponseDto(
        Long id,
        Long productoId,
        String productoNombre,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
    public static DetallePedidoResponseDto fromEntity(DetallePedido detallepedido) {
        return new DetallePedidoResponseDto(
                detallepedido.getId(),
                detallepedido.getProducto().getId(),
                detallepedido.getProducto().getNombre(),
                detallepedido.getCantidad(),
                detallepedido.getPrecioUnitario(),
                detallepedido.getPrecioUnitario().multiply(BigDecimal.valueOf(detallepedido.getCantidad()))
        );
    }
}
