package com.example.App.dto.pedido;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DetallePedidoRequestDto(
        @NotNull(message = "El ID del producto es obligatorio")
        @Positive(message = "El ID del producto debe ser mayor que 0")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        Integer cantidad
) {
}
