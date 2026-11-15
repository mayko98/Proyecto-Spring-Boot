package com.example.App.service;


import com.example.App.dto.producto.ProductoRequestDto;
import com.example.App.dto.producto.ProductoResponseDto;
import com.example.App.exception.DuplicateResourceException;
import com.example.App.exception.ResourceNotFoundException;
import com.example.App.model.Categoria;
import com.example.App.model.Producto;
import com.example.App.repository.CategoriaRepository;
import com.example.App.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @InjectMocks
    private ProductoService productoService;

    private Categoria categoria;
    private Producto producto;

    @BeforeEach
    void setUp() {

        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electronica");
        categoria.setDescripcion("Gadgets");


        producto = Producto.builder()
                .id(1L)
                .nombre("Portatil")
                .descripcion("Portatil potente")
                .precio(new BigDecimal("999.99"))
                .stock(10)
                .codigoReferencia("PROD-12345678")
                .categorias(new HashSet<>(Set.of(categoria)))
                .activo(true)
                .build();


    }

    @Test
    void getProducto_CuandoExisteId_DebeRetornarProductoResponseDto() {
        Long id = 1L;
        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));

        ProductoResponseDto resultado = productoService.getProducto(id);

        assertNotNull(resultado);

        assertEquals(id,resultado.id(),"No tiene en el mismo id ");
        assertEquals(producto.getNombre(),resultado.nombre(),"No tiene el mismo nombre");
        assertEquals(producto.getDescripcion(),resultado.descripcion(),"No tiene el mismo descripcion");
        assertEquals(producto.getPrecio(),resultado.precio(),"No tiene el mismo precio");
        assertEquals(producto.getStock(),resultado.stock(),"No tiene el mismo stock");
        assertEquals(producto.getCodigoReferencia(),resultado.codigoReferencia(),"No tiene el mismo codigo");
        assertEquals(producto.getCategorias().size(),resultado.categorias().size());
        assertEquals(producto.isActivo(),resultado.isActivo(),"No tiene el mismo activo");

        verify(productoRepository, times(1)).findById(id);

    }

    @Test
    void getProducto_CuandoNoExisteId_DebeLanzarResourceNotFoundException() {

        Long id = 1L;
        when(productoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,()-> productoService.getProducto(id));


        verify(productoRepository, times(1)).findById(id);
        verify(productoRepository, never()).save(any(Producto.class));

    }

    @Test
    void getProductos_CuandoExistenProductos_DebeRetornarListaDtos() {
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<ProductoResponseDto> resultado = productoService.getProductos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(producto.getId(), resultado.get(0).id());
        assertEquals(producto.getNombre(), resultado.get(0).nombre());
        assertEquals(producto.getPrecio(), resultado.get(0).precio());

        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void getProductos_CuandoNoExistenProductos_DebeRetornarListaVacia() {
        when(productoRepository.findAll()).thenReturn(List.of());

        List<ProductoResponseDto> resultado = productoService.getProductos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void getProductoByCodReferece_CuandoExisteCodigo_DebeRetornarProductoResponseDto() {
        String codigo = "PROD-12345678";
        when(productoRepository.findByCodigoReferencia(codigo)).thenReturn(Optional.of(producto));

        ProductoResponseDto resultado = productoService.getProductoByCodReferece(codigo);

        assertNotNull(resultado);
        assertEquals(codigo, resultado.codigoReferencia());
        assertEquals(producto.getNombre(), resultado.nombre());

        verify(productoRepository, times(1)).findByCodigoReferencia(codigo);
    }

    @Test
    void getProductoByCodReferece_CuandoNoExisteCodigo_DebeLanzarResourceNotFoundException() {
        String codigo = "INEXISTENTE";
        when(productoRepository.findByCodigoReferencia(codigo)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productoService.getProductoByCodReferece(codigo));

        verify(productoRepository, times(1)).findByCodigoReferencia(codigo);
    }

    @Test
    void crearProducto_CuandoAlgunaCategoriaNoExiste_DebeLanzarResourceNotFoundException() {
        ProductoRequestDto requestDto = new ProductoRequestDto(
                "Raton", "Raton gaming", new BigDecimal("49.99"), 5, "PROD-RATON", Set.of(1L, 99L)
        );

        when(categoriaRepository.findAllById(requestDto.categoriasIds())).thenReturn(List.of(categoria));

        assertThrows(ResourceNotFoundException.class, () -> productoService.crearProducto(requestDto));

        verify(categoriaRepository, times(1)).findAllById(requestDto.categoriasIds());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void crearProducto_CuandoCodigoReferenciaYaExiste_DebeLanzarDuplicateResourceException() {
        ProductoRequestDto requestDto = new ProductoRequestDto(
                "Raton", "Raton gaming", new BigDecimal("49.99"), 5, "PROD-RATON", Set.of(1L)
        );

        when(categoriaRepository.findAllById(requestDto.categoriasIds())).thenReturn(List.of(categoria));
        when(productoRepository.existsByCodigoReferencia("PROD-RATON")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> productoService.crearProducto(requestDto));

        verify(categoriaRepository, times(1)).findAllById(requestDto.categoriasIds());
        verify(productoRepository, times(1)).existsByCodigoReferencia("PROD-RATON");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void crearProducto_CuandoDatosValidosConCodigoManual_DebeGuardarYRetornarDto() {
        ProductoRequestDto requestDto = new ProductoRequestDto(
                "Portatil", "Portatil potente", new BigDecimal("999.99"), 10, "PROD-12345678", Set.of(1L)
        );

        when(categoriaRepository.findAllById(requestDto.categoriasIds())).thenReturn(List.of(categoria));
        when(productoRepository.existsByCodigoReferencia("PROD-12345678")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoResponseDto resultado = productoService.crearProducto(requestDto);

        assertNotNull(resultado);
        assertEquals(producto.getId(), resultado.id());
        assertEquals(producto.getNombre(), resultado.nombre());
        assertEquals(producto.getCodigoReferencia(), resultado.codigoReferencia());

        verify(categoriaRepository, times(1)).findAllById(requestDto.categoriasIds());
        verify(productoRepository, times(1)).existsByCodigoReferencia("PROD-12345678");
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void crearProducto_CuandoCodigoEsNulo_DebeGenerarCodigoAutomaticoYGuardar() {
        ProductoRequestDto requestDto = new ProductoRequestDto(
                "Portatil", "Portatil potente", new BigDecimal("999.99"), 10, null, Set.of(1L)
        );

        when(categoriaRepository.findAllById(requestDto.categoriasIds())).thenReturn(List.of(categoria));
        when(productoRepository.existsByCodigoReferencia(anyString())).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoResponseDto resultado = productoService.crearProducto(requestDto);

        assertNotNull(resultado);
        verify(categoriaRepository, times(1)).findAllById(requestDto.categoriasIds());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void productoPut_CuandoNoExisteId_DebeLanzarResourceNotFoundException() {
        Long id = 99L;
        ProductoRequestDto requestDto = new ProductoRequestDto(
                "Portatil", "Portatil potente", new BigDecimal("999.99"), 10, "PROD-12345678", Set.of(1L)
        );

        when(productoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productoService.productoPut(requestDto, id));

        verify(productoRepository, times(1)).findById(id);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void productoPut_CuandoAlgunaCategoriaNoExiste_DebeLanzarResourceNotFoundException() {
        Long id = 1L;
        ProductoRequestDto requestDto = new ProductoRequestDto(
                "Portatil", "Portatil potente", new BigDecimal("999.99"), 10, "PROD-12345678", Set.of(1L, 99L)
        );

        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findAllById(requestDto.categoriasIds())).thenReturn(List.of(categoria));

        assertThrows(ResourceNotFoundException.class, () -> productoService.productoPut(requestDto, id));

        verify(productoRepository, times(1)).findById(id);
        verify(categoriaRepository, times(1)).findAllById(requestDto.categoriasIds());
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void productoPut_CuandoDatosValidos_DebeActualizarYRetornarDto() {
        Long id = 1L;
        ProductoRequestDto requestDto = new ProductoRequestDto(
                "Portatil Pro", "Nueva descripcion", new BigDecimal("1299.99"), 15, "PROD-12345678", Set.of(1L)
        );

        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findAllById(requestDto.categoriasIds())).thenReturn(List.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        ProductoResponseDto resultado = productoService.productoPut(requestDto, id);

        assertNotNull(resultado);
        verify(productoRepository, times(1)).findById(id);
        verify(categoriaRepository, times(1)).findAllById(requestDto.categoriasIds());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void productoDelete_CuandoNoExisteId_DebeLanzarResourceNotFoundException() {
        Long id = 99L;
        when(productoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productoService.productoDelete(id));

        verify(productoRepository, times(1)).findById(id);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void productoDelete_CuandoExisteId_DebeRealizarBorradoLogico() {
        Long id = 1L;
        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));

        productoService.productoDelete(id);

        assertFalse(producto.isActivo());
        assertNotNull(producto.getFechaBaja());
        verify(productoRepository, times(1)).findById(id);
        verify(productoRepository, times(1)).save(producto);
    }

}
