package com.example.To_Do_List.dto.task;

import com.example.To_Do_List.models.Prioridad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskCreateDTO(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 50, message = "El título no puede superar los 50 caracteres")
        String title,

        @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
        String description,
        Prioridad prioridad,
         Long projectId
) {
}