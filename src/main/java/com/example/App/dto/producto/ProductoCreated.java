package com.example.App.dto.producto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Set;

public record ProductoCreated(
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombre,

        @Size(max = 500, message = "La descripcion no puede superar los 100 caracteres")
        @NotBlank(message = "La descripcion es obligatoria")
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser estrictamente mayor que cero")
        BigDecimal precio,
        @PositiveOrZero(message = "El stock no puede ser negativo")

        Integer stock,

        @Size(max = 50, message = "El código no puede superar los 50 caracteres")
        String codigoReferencia,
        @NotEmpty(message = "El producto debe pertenecer al menos a una categoría")
        Set<Long> categoriasIds
) {
    public ProductoCreated {
        if (stock == null) {
            stock = 0;
        }
    }

}
