package com.example.App.dto.pedido;

import com.example.App.model.EstadoPedido;
import com.example.App.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record PedidoResponseDto(
        Long id,
        Long usuarioId,
        Long direccionEnvioId,
        LocalDateTime fechaPedido,
        List<DetallePedidoResponseDto> detallePedidos,
        EstadoPedido estadoPedido,
        BigDecimal total
) {
    public static PedidoResponseDto fromEntity(Pedido pedido) {
        return new PedidoResponseDto(
          pedido.getId(),
          pedido.getUsuario().getId(),
          pedido.getDireccionEnvio().getId(),
          pedido.getFechaPedido(),
          pedido.getDetallePedidoList().stream().map(DetallePedidoResponseDto::fromEntity).collect(Collectors.toList()),
          pedido.getEstadoPedido(),
          pedido.getTotal()
        );
    }
}
