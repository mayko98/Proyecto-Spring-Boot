package com.example.App.service;

import com.example.App.exception.ResourceNotFoundException;
import com.example.App.dto.producto.ProductoResponse;
import com.example.App.model.Producto;
import com.example.App.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;


    @Transactional(readOnly = true)
    public ProductoResponse getProducto(Long id){

        Producto producto = productoRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Recurso no encontrado "+id));
        return ProductoResponse.fromEntity(producto);
    }


}
