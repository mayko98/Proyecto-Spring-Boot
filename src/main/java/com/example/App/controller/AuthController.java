package com.example.App.controller;

import com.example.App.dto.auth.AuthResponseDto;
import com.example.App.dto.auth.LoginRequestDto;
import com.example.App.dto.auth.RegistroRequestDto;
import com.example.App.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<AuthResponseDto> registrarUsuario(@Valid @RequestBody RegistroRequestDto usuario) {
        return new ResponseEntity<>(authService.registro(usuario), HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto usuario) {
        return new ResponseEntity<>(authService.login(usuario), HttpStatus.OK);
    }
}
