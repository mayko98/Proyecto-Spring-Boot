package com.example.App.dto.pedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PedidoRequestDto(
        @NotNull(message = "El ID del usuario es obligatorio")
        @Positive(message = "El ID del usuario debe ser mayor que 0")
        Long usuarioId,

        @NotNull(message = "El ID de la dirección de envío es obligatorio")
        @Positive(message = "El ID de la dirección debe ser mayor que 0")
        Long direccionEnvioId,

        @NotEmpty(message = "El pedido debe contener al menos un producto")
        List<@Valid DetallePedidoRequestDto> lineasPrdoductos
) {
}
