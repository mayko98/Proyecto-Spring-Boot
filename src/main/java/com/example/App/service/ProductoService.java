package com.example.App.service;

import com.example.App.exception.ResourceNotFoundException;
import com.example.App.dto.producto.ProductoResponse;
import com.example.App.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;


    @Transactional(readOnly = true)
    public ProductoResponse getProducto(Long id){
        return ProductoResponse.fromEntity(productoRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Recurso no encontrado "+id)));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> getProductos() {
        return productoRepository.findAll().stream().map(ProductoResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse getProductoByCodReferece(String codigoReferencia) {

        return ProductoResponse.fromEntity(productoRepository.findByCodigoReferencia(codigoReferencia).orElseThrow(()->new ResourceNotFoundException("Recurso no encontrado "+codigoReferencia)));
    }
}
