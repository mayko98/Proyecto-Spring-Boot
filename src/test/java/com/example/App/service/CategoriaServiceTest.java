package com.example.App.service;

import com.example.App.dto.categoria.CategoriaRequestDto;
import com.example.App.dto.categoria.CategoriaResponseDto;
import com.example.App.exception.DuplicateResourceException;
import com.example.App.exception.ResourceNotFoundException;
import com.example.App.model.Categoria;
import com.example.App.model.Producto;
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

    @Test
    void crearCategoria_CuandoNombreNoExiste_DebeGuardarYRetornarDto(){
        CategoriaRequestDto requestDto = new CategoriaRequestDto(
                categoria.getNombre(),categoria.getDescripcion()
        );

        when(categoriaRepository.existsByNombreIgnoreCase(requestDto.nombre().trim())).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaResponseDto resultado = categoriaService.crearCategoria(requestDto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals(requestDto.nombre(), resultado.nombre());
        assertEquals(requestDto.descripcion(), resultado.descripcion());

        verify(categoriaRepository, times(1)).existsByNombreIgnoreCase(requestDto.nombre().trim());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void crearCategoria_CuandoNombreYaExiste_DebeLanzarDuplicateResourceException() {

        CategoriaRequestDto requestDto = new CategoriaRequestDto(
                categoria.getNombre(),categoria.getDescripcion()
        );

        when(categoriaRepository.existsByNombreIgnoreCase(requestDto.nombre())).thenReturn(true);

        assertThrows(DuplicateResourceException.class,()-> categoriaService.crearCategoria(requestDto));


        verify(categoriaRepository, times(1)).existsByNombreIgnoreCase(requestDto.nombre());
        verify(categoriaRepository,never()).save(any(Categoria.class));
    }

    @Test
    void categoriaPut_CuandoNoExisteId_DebeLanzarResourceNotFoundException() {

        long id = 1L;
        CategoriaRequestDto requestDto = new CategoriaRequestDto(
                categoria.getNombre(),categoria.getDescripcion()
        );
        when(categoriaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()->categoriaService.categoriaPut(requestDto,id));

        verify(categoriaRepository,times(1)).findById(1L);
        verify(categoriaRepository, never()).save(any(Categoria.class));


    }

    @Test
    void categoriaPut_CuandoNombreCambiaYExiste_DebeLanzarDuplicateResourceException() {
        long id = 1L;
        CategoriaRequestDto requestDto = new CategoriaRequestDto(
                "Ropa","Prenda varias"
        );

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNombreIgnoreCase(requestDto.nombre().trim())).thenReturn(true);
        assertThrows(DuplicateResourceException.class,()->categoriaService.categoriaPut(requestDto,id));


        verify(categoriaRepository,times(1)).findById(id);
        verify(categoriaRepository,times(1)).existsByNombreIgnoreCase(requestDto.nombre().trim());
        verify(categoriaRepository,never()).save(any(Categoria.class));

    }

    @Test
    void categoriaPut_CuandoDatosValidos_DebeActualizarYRetornarDto() {
        long id = 1L;
        CategoriaRequestDto requestDto = new CategoriaRequestDto(
                "Ropa","Prenda varias"
        );

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNombreIgnoreCase(requestDto.nombre().trim())).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaResponseDto resultado = categoriaService.categoriaPut(requestDto,id);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals(requestDto.nombre(), resultado.nombre());
        assertEquals(requestDto.descripcion(), resultado.descripcion());
        verify(categoriaRepository, times(1)).findById(id);
        verify(categoriaRepository, times(1)).existsByNombreIgnoreCase(requestDto.nombre().trim());
        verify(categoriaRepository,times(1)).save(any(Categoria.class));


    }
    @Test
    void categoriaDelete_CuandoNoExisteId_DebeLanzarResourceNotFoundException() {

        Long id = 1L;
        when(categoriaRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,()->categoriaService.categoriaDelete(id));

        verify(categoriaRepository,times(1)).findById(id);
        verify(categoriaRepository,never()).delete(any(Categoria.class));
    }


    @Test
    void categoriaDelete_CuandoTieneProductosAsociados_DebeLanzarDuplicateResourceException() {
        Long id = 1L;
        categoria.getProductos().add(new Producto());

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));


        assertThrows(DuplicateResourceException.class,()->categoriaService.categoriaDelete(id));


        verify(categoriaRepository,times(1)).findById(id);
        verify(categoriaRepository,never()).delete(any(Categoria.class));



    }

    @Test
    void categoriaDelete_CuandoExisteYSinProductos_DebeEliminar() {
        Long id = 1L;

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
        categoriaService.categoriaDelete(id);

        verify(categoriaRepository,times(1)).findById(id);
        verify(categoriaRepository,times(1)).delete(any(Categoria.class));
    }

}
