package com.example.App.dto.categoria;

import com.example.App.model.Categoria;

public record CategoriaResponseDto(
        Long id,
        String nombre,
        String descripcion
) {
    public static CategoriaResponseDto fromEntity(Categoria categoria){
        return new CategoriaResponseDto(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }
}
