package com.example.App.controller;


import com.example.App.dto.pedido.PedidoRequestDto;
import com.example.App.dto.pedido.PedidoResponseDto;
import com.example.App.service.PedidoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pedidos")
@Validated
public class PedidoController {
    private final PedidoService pedidoService;

    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<PedidoResponseDto>> findAllByUsuarioId(
            @Positive(message = "El identificador debe ser un número positivo mayor que 0")
            @PathVariable("id")
            Long id
    ){
        return new ResponseEntity<>(pedidoService.getPedidosByUsuarioId(id),HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<PedidoResponseDto>> findAll(){
        return new ResponseEntity<>(pedidoService.getPedidos(),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDto> getPedido(
            @Positive(message = "El identificador debe ser un número positivo mayor que 0")
            @PathVariable("id") Long id
    ){
        return new ResponseEntity<>(pedidoService.getPedidoById(id),HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<PedidoResponseDto> createPedido(
            @Valid  @RequestBody PedidoRequestDto pedidoRequestDto
    ){
        return new ResponseEntity<>(pedidoService.savePedido(pedidoRequestDto),HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponseDto> updatePedido(
            @Positive(message = "El identificador debe ser un número positivo mayor que 0")
            @PathVariable("id") Long id
    ){
        return new ResponseEntity<>(pedidoService.cancelarPedido(id),HttpStatus.OK);
    }

}
