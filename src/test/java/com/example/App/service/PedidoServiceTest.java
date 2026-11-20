package com.example.App.service;

import com.example.App.dto.pedido.DetallePedidoRequestDto;
import com.example.App.dto.pedido.PedidoRequestDto;
import com.example.App.dto.pedido.PedidoResponseDto;
import com.example.App.exception.BadRequestException;
import com.example.App.exception.ResourceNotFoundException;
import com.example.App.model.*;
import com.example.App.repository.DireccionRepository;
import com.example.App.repository.PedidoRepository;
import com.example.App.repository.ProductoRepository;
import com.example.App.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario usuario;
    private Direccion direccion;
    private Producto producto;
    private Pedido pedido;
    private DetallePedido detallePedido;
    private PedidoRequestDto pedidoRequestDto;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@example.com")
                .password("hash123")
                .tipo(Tipo.USUARIO)
                .pedidos(new ArrayList<>())
                .build();

        direccion = new Direccion();
        direccion.setId(1L);
        direccion.setCalle("Calle Mayor 1");
        direccion.setCiudad("Valencia");
        direccion.setProvincia("Valencia");
        direccion.setCodigoPostal("46001");
        direccion.setPais("España");
        direccion.setUsuario(usuario);

        producto = Producto.builder()
                .id(1L)
                .nombre("Teclado Mecanico")
                .descripcion("Teclado RGB con switches blue")
                .precio(new BigDecimal("50.00"))
                .stock(10)
                .codigoReferencia("PROD-TEC1234")
                .activo(true)
                .build();

        detallePedido = DetallePedido.builder()
                .id(1L)
                .producto(producto)
                .cantidad(2)
                .precioUnitario(new BigDecimal("50.00"))
                .build();

        pedido = Pedido.builder()
                .id(1L)
                .usuario(usuario)
                .direccionEnvio(direccion)
                .fechaPedido(LocalDateTime.now())
                .detallePedidoList(new ArrayList<>(List.of(detallePedido)))
                .estadoPedido(EstadoPedido.PENDIENTE)
                .total(new BigDecimal("100.00"))
                .build();
        detallePedido.setPedido(pedido);

        DetallePedidoRequestDto detalleDto = new DetallePedidoRequestDto(1L, 2);
        pedidoRequestDto = new PedidoRequestDto(1L, 1L, List.of(detalleDto));
    }

    @Test
    void savePedido_CuandoDatosValidos_DebeCrearPedidoDescontarStockYCalcularTotal() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        PedidoResponseDto resultado = pedidoService.savePedido(pedidoRequestDto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals(1L, resultado.usuarioId());
        assertEquals(1L, resultado.direccionEnvioId());
        assertEquals(EstadoPedido.PENDIENTE, resultado.estadoPedido());
        assertEquals(new BigDecimal("100.00"), resultado.total());
        assertEquals(8, producto.getStock());
        assertEquals(1, resultado.detallePedidos().size());
        assertEquals(new BigDecimal("100.00"), resultado.detallePedidos().get(0).subtotal());

        verify(usuarioRepository, times(1)).findById(1L);
        verify(direccionRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void savePedido_CuandoUsuarioNoExiste_DebeLanzarResourceNotFoundException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.savePedido(pedidoRequestDto));

        verify(usuarioRepository, times(1)).findById(1L);
        verify(direccionRepository, never()).findById(any());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void savePedido_CuandoDireccionNoExiste_DebeLanzarResourceNotFoundException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(direccionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.savePedido(pedidoRequestDto));

        verify(usuarioRepository, times(1)).findById(1L);
        verify(direccionRepository, times(1)).findById(1L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void savePedido_CuandoDireccionNoPerteneceAUsuario_DebeLanzarBadRequestException() {
        Usuario otroUsuario = Usuario.builder().id(2L).build();
        Direccion direccionAjena = new Direccion();
        direccionAjena.setId(1L);
        direccionAjena.setUsuario(otroUsuario);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccionAjena));

        assertThrows(BadRequestException.class, () -> pedidoService.savePedido(pedidoRequestDto));

        verify(usuarioRepository, times(1)).findById(1L);
        verify(direccionRepository, times(1)).findById(1L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void savePedido_CuandoProductoNoExiste_DebeLanzarResourceNotFoundException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion));
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.savePedido(pedidoRequestDto));

        verify(productoRepository, times(1)).findById(1L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void savePedido_CuandoProductoNoEstaActivo_DebeLanzarBadRequestException() {
        producto.setActivo(false);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(BadRequestException.class, () -> pedidoService.savePedido(pedidoRequestDto));

        verify(productoRepository, times(1)).findById(1L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void savePedido_CuandoStockInsuficiente_DebeLanzarBadRequestException() {
        producto.setStock(1);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(BadRequestException.class, () -> pedidoService.savePedido(pedidoRequestDto));

        verify(productoRepository, times(1)).findById(1L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void cancelarPedido_CuandoEstadoPendiente_DebeCancelarYRestaurarStock() {
        producto.setStock(8);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        PedidoResponseDto resultado = pedidoService.cancelarPedido(1L);

        assertNotNull(resultado);
        assertEquals(EstadoPedido.CANCELADO, resultado.estadoPedido());
        assertEquals(10, producto.getStock());

        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void cancelarPedido_CuandoEstadoPagado_DebeCancelarYRestaurarStock() {
        pedido.setEstadoPedido(EstadoPedido.PAGADO);
        producto.setStock(8);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        PedidoResponseDto resultado = pedidoService.cancelarPedido(1L);

        assertNotNull(resultado);
        assertEquals(EstadoPedido.CANCELADO, resultado.estadoPedido());
        assertEquals(10, producto.getStock());

        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void cancelarPedido_CuandoPedidoNoExiste_DebeLanzarResourceNotFoundException() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.cancelarPedido(1L));

        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void cancelarPedido_CuandoPedidoYaCancelado_DebeLanzarBadRequestException() {
        pedido.setEstadoPedido(EstadoPedido.CANCELADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BadRequestException.class, () -> pedidoService.cancelarPedido(1L));

        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void cancelarPedido_CuandoPedidoEnviado_DebeLanzarBadRequestException() {
        pedido.setEstadoPedido(EstadoPedido.ENVIADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BadRequestException.class, () -> pedidoService.cancelarPedido(1L));

        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void cancelarPedido_CuandoPedidoEntregado_DebeLanzarBadRequestException() {
        pedido.setEstadoPedido(EstadoPedido.ENTREGADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(BadRequestException.class, () -> pedidoService.cancelarPedido(1L));

        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void getPedidoById_CuandoExisteId_DebeRetornarPedidoResponseDto() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        PedidoResponseDto resultado = pedidoService.getPedidoById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals(1L, resultado.usuarioId());
        assertEquals(new BigDecimal("100.00"), resultado.total());

        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void getPedidoById_CuandoNoExisteId_DebeLanzarResourceNotFoundException() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.getPedidoById(1L));

        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void getPedidos_CuandoExistenPedidos_DebeRetornarLista() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

        List<PedidoResponseDto> resultado = pedidoService.getPedidos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).id());

        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void getPedidosByUsuarioId_CuandoUsuarioExiste_DebeRetornarLista() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(pedidoRepository.findByUsuarioId(1L)).thenReturn(List.of(pedido));

        List<PedidoResponseDto> resultado = pedidoService.getPedidosByUsuarioId(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).id());

        verify(usuarioRepository, times(1)).existsById(1L);
        verify(pedidoRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void getPedidosByUsuarioId_CuandoUsuarioNoExiste_DebeLanzarResourceNotFoundException() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.getPedidosByUsuarioId(1L));

        verify(usuarioRepository, times(1)).existsById(1L);
        verify(pedidoRepository, never()).findByUsuarioId(any());
    }
}
