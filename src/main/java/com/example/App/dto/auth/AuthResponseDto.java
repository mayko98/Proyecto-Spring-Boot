package com.example.App.dto.auth;

public record AuthResponseDto(
        String token,
        String tipoToken,
        String nombre,
        String apellido,
        String email,
        String rol
) {
}
