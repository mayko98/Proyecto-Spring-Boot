package com.example.App.dto.producto;

import com.example.App.dto.categoria.CategoriaResponse;
import com.example.App.model.Producto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ProductoResponse(
Long id,
String codigoReferencia,
String nombre,
String descripcion,
BigDecimal precio,
int stock,
LocalDateTime fechaRegistro,
Set<CategoriaResponse> categorias
) {
public static ProductoResponse fromEntity(Producto producto) {
    return new ProductoResponse(
            producto.getId(),
            producto.getCodigoReferencia(),
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getPrecio(),
            producto.getStock(),
            producto.getFecha(),
            producto.getCategorias().stream()
            .map(CategoriaResponse::fromEntity).collect(Collectors.toSet())
    );
}
}
