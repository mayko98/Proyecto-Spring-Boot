package com.example.App.controller;

import com.example.App.dto.categoria.CategoriaRequestDto;
import com.example.App.dto.categoria.CategoriaResponseDto;
import com.example.App.exception.DuplicateResourceException;
import com.example.App.exception.ResourceNotFoundException;
import com.example.App.service.CategoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoriaController.class)
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CategoriaService categoriaService;

    @Test
    void getCategoria_CuandoExisteId_DebeRetornar200YJson() throws Exception {
        Long id = 1L;
        CategoriaResponseDto responseDto = new CategoriaResponseDto(id, "Electronica", "Gadgets y tecnologia");
        when(categoriaService.getCategoria(id)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/categorias/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Electronica"))
                .andExpect(jsonPath("$.descripcion").value("Gadgets y tecnologia"));

        verify(categoriaService, times(1)).getCategoria(id);
    }

    @Test
    void getCategoria_CuandoNoExisteId_DebeRetornar404YErrorJson() throws Exception {
        Long id = 99L;
        when(categoriaService.getCategoria(id))
                .thenThrow(new ResourceNotFoundException("Categoria no encontrada " + id));

        mockMvc.perform(get("/api/v1/categorias/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Categoria no encontrada " + id));

        verify(categoriaService, times(1)).getCategoria(id);
    }

    @Test
    void getCategoria_CuandoIdEsInvalido_DebeRetornar400YErrorJson() throws Exception {
        Long idInvalido = -1L;

        mockMvc.perform(get("/api/v1/categorias/{id}", idInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(categoriaService, never()).getCategoria(any());
    }

    @Test
    void getCategorias_CuandoExistenCategorias_DebeRetornar200YListaJson() throws Exception {
        List<CategoriaResponseDto> responseDtos = List.of(
                new CategoriaResponseDto(1L, "Electronica", "Gadgets y tecnologia"),
                new CategoriaResponseDto(2L, "Hogar", "Articulos para el hogar")
        );

        when(categoriaService.getCategorias()).thenReturn(responseDtos);

        mockMvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Electronica"))
                .andExpect(jsonPath("$[1].nombre").value("Hogar"));

        verify(categoriaService, times(1)).getCategorias();
    }

    @Test
    void crearCategoria_CuandoDatosValidos_DebeRetornar201YJson() throws Exception {
        CategoriaRequestDto requestDto = new CategoriaRequestDto("Hogar", "Articulos para casa");
        CategoriaResponseDto responseDto = new CategoriaResponseDto(1L, "Hogar", "Articulos para casa");

        when(categoriaService.crearCategoria(any(CategoriaRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Hogar"))
                .andExpect(jsonPath("$.descripcion").value("Articulos para casa"));

        verify(categoriaService, times(1)).crearCategoria(any(CategoriaRequestDto.class));
    }

    @Test
    void crearCategoria_CuandoNombreInvalido_DebeRetornar400YErrores() throws Exception {
        CategoriaRequestDto requestDto = new CategoriaRequestDto("", "Articulos para casa");

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errores.nombre").exists());

        verify(categoriaService, never()).crearCategoria(any());
    }

    @Test
    void crearCategoria_CuandoNombreYaExiste_DebeRetornar409YErrorJson() throws Exception {
        CategoriaRequestDto requestDto = new CategoriaRequestDto("Electronica", "Articulos varios para casa");
        String mensajeError = "Ya existe una categoría con el nombre: " + requestDto.nombre();

        when(categoriaService.crearCategoria(any(CategoriaRequestDto.class)))
                .thenThrow(new DuplicateResourceException(mensajeError));

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(mensajeError));

        verify(categoriaService, times(1)).crearCategoria(any(CategoriaRequestDto.class));
    }

    @Test
    void actualizarCategoria_CuandoDatosValidos_DebeRetornar200YJson() throws Exception {
        Long id = 1L;
        CategoriaRequestDto requestDto = new CategoriaRequestDto("Ropa Deportiva", "Prendas de alta calidad");
        CategoriaResponseDto responseDto = new CategoriaResponseDto(id, "Ropa Deportiva", "Prendas de alta calidad");

        when(categoriaService.categoriaPut(any(CategoriaRequestDto.class), eq(id))).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Ropa Deportiva"))
                .andExpect(jsonPath("$.descripcion").value("Prendas de alta calidad"));

        verify(categoriaService, times(1)).categoriaPut(any(CategoriaRequestDto.class), eq(id));
    }

    @Test
    void actualizarCategoria_CuandoIdNoExiste_DebeRetornar404YErrorJson() throws Exception {
        Long id = 99L;
        CategoriaRequestDto requestDto = new CategoriaRequestDto("Ropa Deportiva", "Prendas de alta calidad");

        when(categoriaService.categoriaPut(any(CategoriaRequestDto.class), eq(id)))
                .thenThrow(new ResourceNotFoundException("Categoria no encontrado con id " + id));

        mockMvc.perform(put("/api/v1/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(categoriaService, times(1)).categoriaPut(any(CategoriaRequestDto.class), eq(id));
    }

    @Test
    void actualizarCategoria_CuandoNombreYaExiste_DebeRetornar409YErrorJson() throws Exception {
        Long id = 1L;
        CategoriaRequestDto requestDto = new CategoriaRequestDto("Ropa", "Prendas de alta calidad");

        when(categoriaService.categoriaPut(any(CategoriaRequestDto.class), eq(id)))
                .thenThrow(new DuplicateResourceException("Ya existe otra categoría con el nombre: Ropa"));

        mockMvc.perform(put("/api/v1/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        verify(categoriaService, times(1)).categoriaPut(any(CategoriaRequestDto.class), eq(id));
    }

    @Test
    void actualizarCategoria_CuandoIdInvalido_DebeRetornar400YErrorJson() throws Exception {
        Long idInvalido = 0L;
        CategoriaRequestDto requestDto = new CategoriaRequestDto("Ropa Deportiva", "Prendas de alta calidad");

        mockMvc.perform(put("/api/v1/categorias/{id}", idInvalido)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(categoriaService, never()).categoriaPut(any(), any());
    }

    @Test
    void deleteCategoria_CuandoExisteYSinProductos_DebeRetornar204NoContent() throws Exception {
        Long id = 1L;
        doNothing().when(categoriaService).categoriaDelete(id);

        mockMvc.perform(delete("/api/v1/categorias/{id}", id))
                .andExpect(status().isNoContent());

        verify(categoriaService, times(1)).categoriaDelete(id);
    }

    @Test
    void deleteCategoria_CuandoIdNoExiste_DebeRetornar404YErrorJson() throws Exception {
        Long id = 99L;
        doThrow(new ResourceNotFoundException("Categoria no encontrado con id " + id))
                .when(categoriaService).categoriaDelete(id);

        mockMvc.perform(delete("/api/v1/categorias/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(categoriaService, times(1)).categoriaDelete(id);
    }

    @Test
    void deleteCategoria_CuandoTieneProductos_DebeRetornar409YErrorJson() throws Exception {
        Long id = 1L;
        doThrow(new DuplicateResourceException("No se puede eliminar la categoría porque tiene productos asociados"))
                .when(categoriaService).categoriaDelete(id);

        mockMvc.perform(delete("/api/v1/categorias/{id}", id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        verify(categoriaService, times(1)).categoriaDelete(id);
    }

    @Test
    void deleteCategoria_CuandoIdInvalido_DebeRetornar400YErrorJson() throws Exception {
        Long idInvalido = -1L;

        mockMvc.perform(delete("/api/v1/categorias/{id}", idInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(categoriaService, never()).categoriaDelete(any());
    }

}
