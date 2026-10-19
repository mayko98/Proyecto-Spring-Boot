# 🛒 E-Commerce REST API

[![Java 21](https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x%20%2F%204.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data-JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-data-jpa)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Swagger](https://img.shields.io/badge/OpenAPI-Swagger%20UI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](http://localhost:8080/swagger-ui.html)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

API RESTful backend para la gestión integral de una plataforma de comercio electrónico (**E-Commerce**), desarrollada con **Java 21 LTS** y el ecosistema **Spring Boot**.

Implementa una arquitectura limpia por capas (`Controller` $\rightarrow$ `Service` $\rightarrow$ `Repository`), persistencia relacional con PostgreSQL, control transaccional de pedidos y stock, DTOs inmutables con `record`, validaciones de entrada (`Bean Validation`) y documentación interactiva OpenAPI 3.0.

---

## 🏛️ Modelo de Dominio (Entity-Relationship)

```mermaid
erDiagram
    USUARIO ||--o{ PEDIDO : realiza
    USUARIO ||--o{ DIRECCION : posee
    PEDIDO ||--|{ DETALLE_PEDIDO : contiene
    PRODUCTO ||--o{ DETALLE_PEDIDO : incluye
    PRODUCTO }|--|{ CATEGORIA : clasificado_en
    DIRECCION ||--o{ PEDIDO : entrega_en

    USUARIO {
        bigint id PK
        string nombre
        string apellido
        string email UK
        string password
        string tipo
        date fecha_nacimiento
    }

    PRODUCTO {
        bigint id PK
        string codigo_referencia UK
        string nombre
        string descripcion
        numeric precio
        int stock
        date fecha_registro
    }

    CATEGORIA {
        bigint id PK
        string nombre
        string descripcion
    }

    PEDIDO {
        bigint id PK
        bigint id_usuario FK
        bigint id_direccion_envio FK
        timestamp fecha_pedido
        string estado_pedido
        numeric total
    }

    DETALLE_PEDIDO {
        bigint id PK
        bigint id_pedido FK
        bigint id_producto FK
        int cantidad
        numeric precio_unitario
    }

    DIRECCION {
        bigint id PK
        bigint id_usuario FK
        string calle
        string ciudad
        string provincia
        string codigo_postal
        string pais
    }
```

---

## 🚀 Puesta en Marcha en Local

### Requisitos Previos
* **Java 21 JDK** instalado y configurado en el `PATH`.
* **Docker** y **Docker Compose** en ejecución.

---

### 1. Iniciar Base de Datos (PostgreSQL)
El proyecto incluye un `docker-compose.yaml` listo para desplegar PostgreSQL 16 Alpine:

```bash
docker compose up -d
```

* **Host**: `localhost:5432`
* **Base de datos**: `tienda-electronica`
* **Usuario**: `admin`
* **Password**: `admin`

---

### 2. Ejecutar la Aplicación con Maven Wrapper
No necesitas tener Maven instalado en el sistema:

```bash
./mvnw spring-boot:run
```

La aplicación arrancará por defecto en `http://localhost:8080`.

---

## 📖 Documentación OpenAPI / Swagger UI

La API cuenta con documentación viva generada automáticamente conforme a la especificación OpenAPI 3.0:

* **Swagger UI interactivo**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **Especificación OpenAPI (JSON)**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 📌 Resumen de Endpoints de la API

### Productos (`/api/v1/productos`)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/v1/productos` | Listar todos los productos del catálogo |
| `GET` | `/api/v1/productos/{id}` | Obtener detalle de un producto por ID |
| `GET` | `/api/v1/productos/codigo/{codigo}` | Buscar producto por código de referencia |
| `POST` | `/api/v1/productos` | Registrar un nuevo producto con validaciones |
| `PUT` | `/api/v1/productos/{id}` | Actualizar datos de un producto |
| `DELETE` | `/api/v1/productos/{id}` | Eliminar un producto del catálogo |

### Categorías (`/api/v1/categorias`)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/v1/categorias` | Listar todas las categorías disponibles |
| `GET` | `/api/v1/categorias/{id}` | Obtener una categoría por ID |
| `POST` | `/api/v1/categorias` | Crear una nueva categoría |
| `PUT` | `/api/v1/categorias/{id}` | Modificar información de una categoría |
| `DELETE` | `/api/v1/categorias/{id}` | Eliminar categoría |

### Pedidos (`/api/v1/pedidos`)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/v1/pedidos` | Listar pedidos (filtrable por usuario) |
| `GET` | `/api/v1/pedidos/{id}` | Consultar detalle de un pedido y sus líneas |
| `POST` | `/api/v1/pedidos` | Crear pedido con control transaccional de stock |
| `PATCH` | `/api/v1/pedidos/{id}/estado` | Actualizar estado del pedido (PAGADO, ENVIADO, etc.) |

---

## 🧪 Ejecución de Pruebas Automatizadas

Para ejecutar la suite de pruebas unitarias y de integración:

```bash
./mvnw clean test
```
