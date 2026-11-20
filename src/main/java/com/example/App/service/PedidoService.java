package com.example.App.service;

import com.example.App.dto.pedido.DetallePedidoRequestDto;
import com.example.App.dto.pedido.PedidoRequestDto;
import com.example.App.dto.pedido.PedidoResponseDto;
import com.example.App.exception.BadRequestException;
import com.example.App.exception.ResourceNotFoundException;
import com.example.App.model.*;
import com.example.App.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<PedidoResponseDto> getPedidos() {
        return pedidoRepository.findAll().stream().map(PedidoResponseDto::fromEntity).toList();
    }
    @Transactional(readOnly = true)
    public PedidoResponseDto getPedidoById(Long id) {
        return  PedidoResponseDto.fromEntity(pedidoRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Pedido no encontrado con el id "+id)));
    }


    @Transactional
    public PedidoResponseDto savePedido(PedidoRequestDto pedidoRequestDto) {
        Usuario usuario = usuarioRepository.findById(pedidoRequestDto.usuarioId()).orElseThrow(()->new ResourceNotFoundException("Usuario no encontrado"));
        Direccion direccion = direccionRepository.findById(pedidoRequestDto.direccionEnvioId()).orElseThrow(()->new ResourceNotFoundException("Direccion no econtrada en la bd "));

        if (!direccion.getUsuario().getId().equals(usuario.getId())) {
            throw new BadRequestException("La direccion de envio no pertenece al usuario ");
        }

        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .direccionEnvio(direccion)
                .build();

        for (DetallePedidoRequestDto p : pedidoRequestDto.lineasPrdoductos()) {
            Producto p1 =  productoRepository.findById(p.productoId()).orElseThrow(()->new ResourceNotFoundException("Producto no encontrado"));

            if (!p1.isActivo()){
                throw new BadRequestException("El producto " + p1.getNombre() + " no está activo");
            }
            if (p1.getStock() < p.cantidad()) {
                throw new BadRequestException("Stock insuficiente para: " + p1.getNombre());
            }
            p1.setStock(p1.getStock() - p.cantidad());



            pedido.addDetalle(DetallePedido.builder().producto(p1).cantidad(p.cantidad()).precioUnitario(p1.getPrecio()).build());


        };



        pedido.calcularTotal();
        return PedidoResponseDto.fromEntity(pedidoRepository.save(pedido));

    }

    @Transactional
    public PedidoResponseDto cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(()->new  ResourceNotFoundException("Pedido no encontrado con el id "+id));

        if (pedido.getEstadoPedido() == EstadoPedido.CANCELADO) {
            throw new BadRequestException("Este pedido no puede ser cancelado");
        }
        if (pedido.getEstadoPedido() == EstadoPedido.ENVIADO || pedido.getEstadoPedido() == EstadoPedido.ENTREGADO) {
            throw new BadRequestException("No se puede cancelar un pedido que ya ha sido enviado o entregado");
        }

        for(DetallePedido detalle: pedido.getDetallePedidoList()){
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());

        }
        pedido.setEstadoPedido(EstadoPedido.CANCELADO);

        return  PedidoResponseDto.fromEntity(pedido);
    }



    @Transactional(readOnly = true)
    public List<PedidoResponseDto> getPedidosByUsuarioId(Long id) {
        if (!usuarioRepository.existsById(id)){
            throw new ResourceNotFoundException("Usuario no encontrado con el id "+ id);
        }
        return pedidoRepository.findByUsuarioId(id).stream()
                .map(PedidoResponseDto::fromEntity)
                .toList();
    }
}

