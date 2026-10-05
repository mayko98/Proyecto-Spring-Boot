# 🛒 E-Commerce REST API

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![Swagger](https://img.shields.io/badge/OpenAPI-Swagger%20UI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](http://localhost:8080/swagger-ui.html)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

API RESTful para la gestión del backend de una tienda online (**E-Commerce**), desarrollada con **Java 21 LTS** y **Spring Boot 4.x**. 

El proyecto implementa una arquitectura limpia por capas, control estricto de reglas de negocio (gestión de stock y transacciones atómicas), validación de datos en entrada, manejo centralizado de excepciones y documentación viva interactiva.

---

## 🎯 Objetivos y Retos de Negocio

* 📦 **Catálogo de Productos y Categorías**: Gestión estructurada con DTOs inmutables (`record`) y validaciones de datos (`Bean Validation`).
* 🛒 **Pedidos y Control Atómico de Stock**: Lógica de compra transaccional (`@Transactional`) garantizando que nunca se venda producto sin disponibilidad en almacén.
* 🛡️ **Seguridad y Control de Acceso**: Diferenciación de permisos entre Clientes (`ROLE_USER`) y Administradores (`ROLE_ADMIN`).
* 📖 **Documentación Viva**: Especificación OpenAPI 3.0 accesible desde Swagger UI (`/swagger-ui.html`).
* 🧪 **Testing Automatizado**: Cobertura con pruebas unitarias (Mockito) y de integración (`MockMvc`).

---

## 🚀 Estado Actual

* **Fase Activa**: `Fase 0 - Diseño del Modelo de Dominio y Reglas de Negocio`.
* **Base Tecnológica**: Inicializada y verificada con Maven Wrapper (`BUILD SUCCESS`).
