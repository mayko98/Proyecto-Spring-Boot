package com.example.App.controller;

import com.example.App.dto.pedido.DetallePedidoRequestDto;
import com.example.App.dto.pedido.DetallePedidoResponseDto;
import com.example.App.dto.pedido.PedidoRequestDto;
import com.example.App.dto.pedido.PedidoResponseDto;
import com.example.App.exception.BadRequestException;
import com.example.App.exception.ResourceNotFoundException;
import com.example.App.model.EstadoPedido;
import com.example.App.service.PedidoService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PedidoService pedidoService;

    private PedidoResponseDto sampleResponseDto;
    private PedidoRequestDto sampleRequestDto;

    @BeforeEach
    void setUp() {
        DetallePedidoResponseDto detalleDto = new DetallePedidoResponseDto(
                1L,
                1L,
                "Teclado Mecanico",
                2,
                new BigDecimal("50.00"),
                new BigDecimal("100.00")
        );

        sampleResponseDto = new PedidoResponseDto(
                1L,
                1L,
                1L,
                LocalDateTime.of(2026, 11, 20, 10, 0),
                List.of(detalleDto),
                EstadoPedido.PENDIENTE,
                new BigDecimal("100.00")
        );

        DetallePedidoRequestDto requestLinea = new DetallePedidoRequestDto(1L, 2);
        sampleRequestDto = new PedidoRequestDto(1L, 1L, List.of(requestLinea));
    }

    @Test
    void findAll_CuandoExistenPedidos_DebeRetornar200YLista() throws Exception {
        when(pedidoService.getPedidos()).thenReturn(List.of(sampleResponseDto));

        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].total").value(100.00))
                .andExpect(jsonPath("$[0].estadoPedido").value("PENDIENTE"));

        verify(pedidoService, times(1)).getPedidos();
    }

    @Test
    void getPedido_CuandoExisteId_DebeRetornar200YJson() throws Exception {
        Long id = 1L;
        when(pedidoService.getPedidoById(id)).thenReturn(sampleResponseDto);

        mockMvc.perform(get("/api/v1/pedidos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.direccionEnvioId").value(1))
                .andExpect(jsonPath("$.total").value(100.00))
                .andExpect(jsonPath("$.estadoPedido").value("PENDIENTE"))
                .andExpect(jsonPath("$.detallePedidos.length()").value(1))
                .andExpect(jsonPath("$.detallePedidos[0].productoNombre").value("Teclado Mecanico"));

        verify(pedidoService, times(1)).getPedidoById(id);
    }

    @Test
    void getPedido_CuandoNoExisteId_DebeRetornar404YErrorJson() throws Exception {
        Long id = 99L;
        when(pedidoService.getPedidoById(id))
                .thenThrow(new ResourceNotFoundException("Pedido no encontrado con el id " + id));

        mockMvc.perform(get("/api/v1/pedidos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Pedido no encontrado con el id " + id));

        verify(pedidoService, times(1)).getPedidoById(id);
    }

    @Test
    void getPedido_CuandoIdInvalido_DebeRetornar400() throws Exception {
        mockMvc.perform(get("/api/v1/pedidos/{id}", -1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(pedidoService, never()).getPedidoById(any());
    }

    @Test
    void findAllByUsuarioId_CuandoExisteUsuario_DebeRetornar200YLista() throws Exception {
        Long usuarioId = 1L;
        when(pedidoService.getPedidosByUsuarioId(usuarioId)).thenReturn(List.of(sampleResponseDto));

        mockMvc.perform(get("/api/v1/pedidos/usuario/{id}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].usuarioId").value(1));

        verify(pedidoService, times(1)).getPedidosByUsuarioId(usuarioId);
    }

    @Test
    void findAllByUsuarioId_CuandoNoExisteUsuario_DebeRetornar404YErrorJson() throws Exception {
        Long usuarioId = 99L;
        when(pedidoService.getPedidosByUsuarioId(usuarioId))
                .thenThrow(new ResourceNotFoundException("Usuario no encontrado con el id " + usuarioId));

        mockMvc.perform(get("/api/v1/pedidos/usuario/{id}", usuarioId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(pedidoService, times(1)).getPedidosByUsuarioId(usuarioId);
    }

    @Test
    void findAllByUsuarioId_CuandoIdInvalido_DebeRetornar400() throws Exception {
        mockMvc.perform(get("/api/v1/pedidos/usuario/{id}", 0))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(pedidoService, never()).getPedidosByUsuarioId(any());
    }

    @Test
    void createPedido_CuandoDatosValidos_DebeRetornar201YJson() throws Exception {
        when(pedidoService.savePedido(any(PedidoRequestDto.class))).thenReturn(sampleResponseDto);

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.total").value(100.00))
                .andExpect(jsonPath("$.estadoPedido").value("PENDIENTE"));

        verify(pedidoService, times(1)).savePedido(any(PedidoRequestDto.class));
    }

    @Test
    void createPedido_CuandoUsuarioIdNulo_DebeRetornar400() throws Exception {
        PedidoRequestDto dtoInvalido = new PedidoRequestDto(null, 1L, sampleRequestDto.lineasPrdoductos());

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errores.usuarioId").exists());

        verify(pedidoService, never()).savePedido(any());
    }

    @Test
    void createPedido_CuandoDireccionIdNulo_DebeRetornar400() throws Exception {
        PedidoRequestDto dtoInvalido = new PedidoRequestDto(1L, null, sampleRequestDto.lineasPrdoductos());

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errores.direccionEnvioId").exists());

        verify(pedidoService, never()).savePedido(any());
    }

    @Test
    void createPedido_CuandoLineasVacias_DebeRetornar400() throws Exception {
        PedidoRequestDto dtoInvalido = new PedidoRequestDto(1L, 1L, List.of());

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errores.lineasPrdoductos").exists());

        verify(pedidoService, never()).savePedido(any());
    }

    @Test
    void createPedido_CuandoCantidadInvalidaEnLinea_DebeRetornar400() throws Exception {
        DetallePedidoRequestDto lineaInvalida = new DetallePedidoRequestDto(1L, -5);
        PedidoRequestDto dtoInvalido = new PedidoRequestDto(1L, 1L, List.of(lineaInvalida));

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(pedidoService, never()).savePedido(any());
    }

    @Test
    void createPedido_CuandoStockInsuficiente_DebeRetornar400YMensaje() throws Exception {
        when(pedidoService.savePedido(any(PedidoRequestDto.class)))
                .thenThrow(new BadRequestException("Stock insuficiente para: Teclado Mecanico"));

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Stock insuficiente para: Teclado Mecanico"));

        verify(pedidoService, times(1)).savePedido(any(PedidoRequestDto.class));
    }

    @Test
    void createPedido_CuandoDireccionAjena_DebeRetornar400YMensaje() throws Exception {
        when(pedidoService.savePedido(any(PedidoRequestDto.class)))
                .thenThrow(new BadRequestException("La direccion de envio no pertenece al usuario"));

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("La direccion de envio no pertenece al usuario"));

        verify(pedidoService, times(1)).savePedido(any(PedidoRequestDto.class));
    }

    @Test
    void updatePedido_CuandoIdValido_DebeRetornar200YJson() throws Exception {
        Long id = 1L;
        PedidoResponseDto pedidoCanceladoDto = new PedidoResponseDto(
                1L,
                1L,
                1L,
                sampleResponseDto.fechaPedido(),
                sampleResponseDto.detallePedidos(),
                EstadoPedido.CANCELADO,
                sampleResponseDto.total()
        );
        when(pedidoService.cancelarPedido(id)).thenReturn(pedidoCanceladoDto);

        mockMvc.perform(patch("/api/v1/pedidos/{id}/cancelar", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estadoPedido").value("CANCELADO"));

        verify(pedidoService, times(1)).cancelarPedido(id);
    }

    @Test
    void updatePedido_CuandoNoExistePedido_DebeRetornar404YErrorJson() throws Exception {
        Long id = 99L;
        when(pedidoService.cancelarPedido(id))
                .thenThrow(new ResourceNotFoundException("Pedido no encontrado con el id " + id));

        mockMvc.perform(patch("/api/v1/pedidos/{id}/cancelar", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Pedido no encontrado con el id " + id));

        verify(pedidoService, times(1)).cancelarPedido(id);
    }

    @Test
    void updatePedido_CuandoPedidoYaCancelado_DebeRetornar400YMensaje() throws Exception {
        Long id = 1L;
        when(pedidoService.cancelarPedido(id))
                .thenThrow(new BadRequestException("Este pedido no puede ser cancelado"));

        mockMvc.perform(patch("/api/v1/pedidos/{id}/cancelar", id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Este pedido no puede ser cancelado"));

        verify(pedidoService, times(1)).cancelarPedido(id);
    }

    @Test
    void updatePedido_CuandoIdInvalido_DebeRetornar400() throws Exception {
        mockMvc.perform(patch("/api/v1/pedidos/{id}/cancelar", -1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(pedidoService, never()).cancelarPedido(any());
    }
}
