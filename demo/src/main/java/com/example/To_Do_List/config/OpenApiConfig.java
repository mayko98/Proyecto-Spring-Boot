package com.example.To_Do_List.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API REST To-Do List")
                        .version("1.0.0")
                        .description("Backend para gestión de tareas To-Do List con Spring Boot, H2 y Bean Validation.")
                        .contact(new Contact()
                                .name("Mayko")
                                .email("maykotorres98@gmail.com")));
    }
}
