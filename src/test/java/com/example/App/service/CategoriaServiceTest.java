package com.example.App.service;

import com.example.App.dto.categoria.CategoriaResponseDto;
import com.example.App.exception.ResourceNotFoundException;
import com.example.App.model.Categoria;
import com.example.App.repository.CategoriaRepository;
import com.example.App.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;
    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private Categoria categoria1;
    private List<Categoria> categorias;


    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electronica");
        categoria.setDescripcion("Dispositivo y gadgets");

        categoria1 = new Categoria();
        categoria1.setId(2L);
        categoria1.setNombre("Electronica2");
        categoria1.setDescripcion("Dispositivo y gadgets2");

        categorias = Arrays.asList(categoria, categoria1);
    }
    @Test
    void getCategoria_CuandoExisteId_DebeRetornarCategoriaResponseDto(){

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        CategoriaResponseDto resultado = categoriaService.getCategoria(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Electronica", resultado.nombre());
        assertEquals("Dispositivo y gadgets", resultado.descripcion());

        verify(categoriaRepository, times(1)).findById(1L);


    }
    @Test
    void getCategoria_CuandoNoExisteId_DebeLanzarResourceNotFoundException(){

        Long  id = 1L;
        when(categoriaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()-> categoriaService.getCategoria(id));

        verify(categoriaRepository, times(1)).findById(id);
    }


    @Test
    void getCategorias_CuandoExistenCategorias_DebeRetornarListaDtos(){
        when(categoriaRepository.findAll()).thenReturn(categorias);

        List<CategoriaResponseDto> resultado = categoriaService.getCategorias();

        assertNotNull(resultado);

        assertEquals(2,resultado.size());

        assertEquals("Electronica", resultado.get(0).nombre());
        assertEquals("Electronica2", resultado.get(1).nombre());
        assertEquals("Dispositivo y gadgets", resultado.get(0).descripcion());
        assertEquals("Dispositivo y gadgets2", resultado.get(1).descripcion());

        verify(categoriaRepository, times(1)).findAll();


    }

    @Test
    void getCategorias_CuandoNoExistenCategorias_DebeRetornarListaVacia(){
        when(categoriaRepository.findAll()).thenReturn(List.of());

        List<CategoriaResponseDto> resultado = categoriaService.getCategorias();
        assertNotNull(resultado);

        assertEquals(0,resultado.size());
        verify(categoriaRepository, times(1)).findAll();

    }
}
