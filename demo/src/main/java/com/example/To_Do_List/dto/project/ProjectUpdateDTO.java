package com.example.To_Do_List.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectUpdateDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        String name,

        @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
        String description,

        @Size(max = 7, message = "El color debe ser un código hexadecimal válido (ej: #FF5733)")
        String color
) {
}
