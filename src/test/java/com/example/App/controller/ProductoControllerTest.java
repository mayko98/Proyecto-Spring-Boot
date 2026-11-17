package com.example.App.controller;

import com.example.App.dto.categoria.CategoriaResponseDto;
import com.example.App.dto.producto.ProductoRequestDto;
import com.example.App.dto.producto.ProductoResponseDto;
import com.example.App.exception.DuplicateResourceException;
import com.example.App.exception.ResourceNotFoundException;
import com.example.App.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductoService productoService;

    private ProductoResponseDto sampleResponseDto;
    private ProductoRequestDto sampleRequestDto;

    @BeforeEach
    void setUp() {
        CategoriaResponseDto categoriaDto = new CategoriaResponseDto(1L, "Electronica", "Gadgets y tecnologia");

        sampleResponseDto = new ProductoResponseDto(
                1L,
                "PROD-ABCD1234",
                "Laptop Gamer",
                "Portatil de alta gama para desarrollo y gaming",
                new BigDecimal("1299.99"),
                10,
                LocalDateTime.now(),
                true,
                null,
                Set.of(categoriaDto)
        );

        sampleRequestDto = new ProductoRequestDto(
                "Laptop Gamer",
                "Portatil de alta gama para desarrollo y gaming",
                new BigDecimal("1299.99"),
                10,
                "PROD-ABCD1234",
                Set.of(1L)
        );
    }

    @Test
    void getProducto_CuandoExisteId_DebeRetornar200YJson() throws Exception {
        Long id = 1L;
        when(productoService.getProducto(id)).thenReturn(sampleResponseDto);

        mockMvc.perform(get("/api/v1/productos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Laptop Gamer"))
                .andExpect(jsonPath("$.codigoReferencia").value("PROD-ABCD1234"))
                .andExpect(jsonPath("$.precio").value(1299.99))
                .andExpect(jsonPath("$.stock").value(10));

        verify(productoService, times(1)).getProducto(id);
    }

    @Test
    void getProducto_CuandoNoExisteId_DebeRetornar404YErrorJson() throws Exception {
        Long id = 99L;
        when(productoService.getProducto(id))
                .thenThrow(new ResourceNotFoundException("Recurso no encontrado " + id));

        mockMvc.perform(get("/api/v1/productos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Recurso no encontrado " + id));

        verify(productoService, times(1)).getProducto(id);
    }

    @Test
    void getProducto_CuandoIdEsInvalido_DebeRetornar400YErrorJson() throws Exception {
        Long idInvalido = -1L;

        mockMvc.perform(get("/api/v1/productos/{id}", idInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(productoService, never()).getProducto(any());
    }

    @Test
    void getProductos_CuandoExistenProductos_DebeRetornar200YListaJson() throws Exception {
        List<ProductoResponseDto> productos = List.of(sampleResponseDto);
        when(productoService.getProductos()).thenReturn(productos);

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Laptop Gamer"))
                .andExpect(jsonPath("$[0].codigoReferencia").value("PROD-ABCD1234"));

        verify(productoService, times(1)).getProductos();
    }

    @Test
    void getProductos_CuandoNoExistenProductos_DebeRetornar200YListaVacia() throws Exception {
        when(productoService.getProductos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));

        verify(productoService, times(1)).getProductos();
    }

    @Test
    void getProductoByCodRef_CuandoExisteCodigo_DebeRetornar200YJson() throws Exception {
        String codigo = "PROD-ABCD1234";
        when(productoService.getProductoByCodReferece(codigo)).thenReturn(sampleResponseDto);

        mockMvc.perform(get("/api/v1/productos/codigo/{codigoReferencia}", codigo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoReferencia").value(codigo))
                .andExpect(jsonPath("$.nombre").value("Laptop Gamer"));

        verify(productoService, times(1)).getProductoByCodReferece(codigo);
    }

    @Test
    void getProductoByCodRef_CuandoNoExisteCodigo_DebeRetornar404YErrorJson() throws Exception {
        String codigoInexistente = "PROD-99999999";
        when(productoService.getProductoByCodReferece(codigoInexistente))
                .thenThrow(new ResourceNotFoundException("Recurso no encontrado " + codigoInexistente));

        mockMvc.perform(get("/api/v1/productos/codigo/{codigoReferencia}", codigoInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Recurso no encontrado " + codigoInexistente));

        verify(productoService, times(1)).getProductoByCodReferece(codigoInexistente);
    }

    @Test
    void crearProducto_CuandoDatosValidos_DebeRetornar201YJson() throws Exception {
        when(productoService.crearProducto(any(ProductoRequestDto.class))).thenReturn(sampleResponseDto);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Laptop Gamer"))
                .andExpect(jsonPath("$.codigoReferencia").value("PROD-ABCD1234"))
                .andExpect(jsonPath("$.precio").value(1299.99));

        verify(productoService, times(1)).crearProducto(any(ProductoRequestDto.class));
    }

    @Test
    void crearProducto_CuandoNombreVacio_DebeRetornar400YErrores() throws Exception {
        ProductoRequestDto dtoInvalido = new ProductoRequestDto(
                "",
                "Descripcion valida",
                new BigDecimal("100.00"),
                5,
                null,
                Set.of(1L)
        );

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errores.nombre").exists());

        verify(productoService, never()).crearProducto(any());
    }

    @Test
    void crearProducto_CuandoPrecioNuloONegativo_DebeRetornar400YErrores() throws Exception {
        ProductoRequestDto dtoInvalido = new ProductoRequestDto(
                "Teclado Mecanico",
                "Teclado RGB",
                new BigDecimal("-10.00"),
                5,
                null,
                Set.of(1L)
        );

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errores.precio").exists());

        verify(productoService, never()).crearProducto(any());
    }

    @Test
    void crearProducto_CuandoCategoriasVacias_DebeRetornar400YErrores() throws Exception {
        ProductoRequestDto dtoInvalido = new ProductoRequestDto(
                "Raton Inalambrico",
                "Raton optico ergonómico",
                new BigDecimal("29.99"),
                10,
                null,
                Collections.emptySet()
        );

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errores.categoriasIds").exists());

        verify(productoService, never()).crearProducto(any());
    }

    @Test
    void crearProducto_CuandoCodigoYaExiste_DebeRetornar409YErrorJson() throws Exception {
        String mensajeError = "El código de referencia 'PROD-ABCD1234' ya está en uso";
        when(productoService.crearProducto(any(ProductoRequestDto.class)))
                .thenThrow(new DuplicateResourceException(mensajeError));

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(mensajeError));

        verify(productoService, times(1)).crearProducto(any(ProductoRequestDto.class));
    }

    @Test
    void crearProducto_CuandoCategoriaNoExiste_DebeRetornar404YErrorJson() throws Exception {
        String mensajeError = "Una o más categorías especificadas no existen";
        when(productoService.crearProducto(any(ProductoRequestDto.class)))
                .thenThrow(new ResourceNotFoundException(mensajeError));

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(mensajeError));

        verify(productoService, times(1)).crearProducto(any(ProductoRequestDto.class));
    }

    @Test
    void actualizarProducto_CuandoDatosValidos_DebeRetornar200YJson() throws Exception {
        Long id = 1L;
        when(productoService.productoPut(any(ProductoRequestDto.class), eq(id))).thenReturn(sampleResponseDto);

        mockMvc.perform(put("/api/v1/productos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Laptop Gamer"))
                .andExpect(jsonPath("$.precio").value(1299.99));

        verify(productoService, times(1)).productoPut(any(ProductoRequestDto.class), eq(id));
    }

    @Test
    void actualizarProducto_CuandoIdNoExiste_DebeRetornar404YErrorJson() throws Exception {
        Long id = 99L;
        when(productoService.productoPut(any(ProductoRequestDto.class), eq(id)))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado con id " + id));

        mockMvc.perform(put("/api/v1/productos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(productoService, times(1)).productoPut(any(ProductoRequestDto.class), eq(id));
    }

    @Test
    void actualizarProducto_CuandoIdInvalido_DebeRetornar400YErrorJson() throws Exception {
        Long idInvalido = 0L;

        mockMvc.perform(put("/api/v1/productos/{id}", idInvalido)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(productoService, never()).productoPut(any(), any());
    }

    @Test
    void deleteProducto_CuandoExiste_DebeRetornar204NoContent() throws Exception {
        Long id = 1L;
        doNothing().when(productoService).productoDelete(id);

        mockMvc.perform(delete("/api/v1/productos/{id}", id))
                .andExpect(status().isNoContent());

        verify(productoService, times(1)).productoDelete(id);
    }

    @Test
    void deleteProducto_CuandoIdNoExiste_DebeRetornar404YErrorJson() throws Exception {
        Long id = 99L;
        doThrow(new ResourceNotFoundException("Producto no encontrado con id " + id))
                .when(productoService).productoDelete(id);

        mockMvc.perform(delete("/api/v1/productos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(productoService, times(1)).productoDelete(id);
    }

    @Test
    void deleteProducto_CuandoIdInvalido_DebeRetornar400YErrorJson() throws Exception {
        Long idInvalido = -5L;

        mockMvc.perform(delete("/api/v1/productos/{id}", idInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(productoService, never()).productoDelete(any());
    }
}
