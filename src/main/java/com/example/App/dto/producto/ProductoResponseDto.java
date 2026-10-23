package com.example.App.dto.producto;

import com.example.App.dto.categoria.CategoriaResponseDto;
import com.example.App.model.Producto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ProductoResponseDto(
Long id,
String codigoReferencia,
String nombre,
String descripcion,
BigDecimal precio,
int stock,
LocalDateTime fechaRegistro,
boolean isActivo,
LocalDateTime fechaBaja,
Set<CategoriaResponseDto> categorias
) {
public static ProductoResponseDto fromEntity(Producto producto) {
    return new ProductoResponseDto(
            producto.getId(),
            producto.getCodigoReferencia(),
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getPrecio(),
            producto.getStock(),
            producto.getFecha(),
            producto.isActivo(),
            producto.getFechaBaja(),
            producto.getCategorias().stream()
            .map(CategoriaResponseDto::fromEntity).collect(Collectors.toSet())
    );
}
}
