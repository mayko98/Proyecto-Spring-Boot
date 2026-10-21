package com.example.App.dto.categoria;

import com.example.App.model.Categoria;

public record CategoriaResponse(
        Long id,
        String nombre
) {
    public static  CategoriaResponse fromEntity(Categoria categoria){
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre()
        );
    }
}
