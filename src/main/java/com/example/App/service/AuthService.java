package com.example.App.service;

import com.example.App.dto.auth.AuthResponseDto;
import com.example.App.dto.auth.LoginRequestDto;
import com.example.App.dto.auth.RegistroRequestDto;
import com.example.App.exception.DuplicateResourceException;
import com.example.App.exception.ResourceNotFoundException;
import com.example.App.model.Tipo;
import com.example.App.model.Usuario;
import com.example.App.repository.UsuarioRepository;
import com.example.App.security.CustomUserDetailsService;
import com.example.App.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService{

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Transactional
    public AuthResponseDto registro(RegistroRequestDto request){
        if (usuarioRepository.findByEmail(request.email()).isPresent()){
            throw new DuplicateResourceException("El email ya está registrado: " + request.email());
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setFechaNacimiento(request.fechaNacimiento());
        usuario.setTipo(Tipo.USUARIO);
        Usuario savedUsuario = usuarioRepository.save(usuario);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(savedUsuario.getEmail());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponseDto(
                token,
                "Bearer",
                savedUsuario.getNombre(),
                savedUsuario.getApellido(),
                savedUsuario.getEmail(),
                savedUsuario.getTipo().name()
        );
    }


    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + request.email()));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponseDto(
                token,
                "Bearer",
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTipo().name()
        );


    }
}
