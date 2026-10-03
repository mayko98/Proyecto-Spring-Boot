package com.example.To_Do_List.config;

import com.example.To_Do_List.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {



//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {}

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    SecurityFilterChain springWebFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz->authz
                        // 1. Las rutas de autenticación (para que la gente pueda registrarse y hacer login):
                        .requestMatchers("/api/auth/**").permitAll()

                        // 2. La documentación interactiva Swagger y OpenAPI:
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**").permitAll()

                        // 3. La consola de base de datos H2:
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll()

                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sesion->sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .headers(headers->headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();



    }







}
