package com.example.App.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegistroRequestDto(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 50, message = "El apellido no puede tener más de 50 caracteres")
        String apellido,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato del email no es válido")
        @Size(max = 50, message = "El email no puede tener más de 50 caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
        LocalDate fechaNacimiento
) {
}
