package com.example.App.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequestDto(
        @NotBlank(message = "EL nombre no puede estar vacio")
        @Size(min = 3,max = 50, message = "El tamaño del nombre no puede ser mayor a 50 o menor a 3")
        String nombre,

        @Size(min = 10, max = 200, message = "El tamaño de la descripcion no puede ser mayor a 200")
        @NotBlank(message = "La descripcion no puede estar vacia")
        String descripcion

) {
}
