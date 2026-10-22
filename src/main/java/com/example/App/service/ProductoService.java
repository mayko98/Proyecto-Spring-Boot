package com.example.App.service;

import com.example.App.exception.DuplicateResourceException;
import com.example.App.dto.producto.ProductoCreated;
import com.example.App.exception.ResourceNotFoundException;
import com.example.App.dto.producto.ProductoResponse;
import com.example.App.model.Categoria;
import com.example.App.model.Producto;
import com.example.App.repository.CategoriaRepository;
import com.example.App.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;


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

    @Transactional
    public ProductoResponse crearProducto(ProductoCreated producto) {

        List<Categoria> si = categoriaRepository.findAllById(producto.categoriasIds());
        if (si.size() != producto.categoriasIds().size()) {
            throw new ResourceNotFoundException("Una o más categorías especificadas no existen");
        }

        Producto p = Producto.builder()
                .nombre(producto.nombre())
                .descripcion(producto.descripcion())
                .precio(producto.precio())
                .stock(producto.stock())
                .codigoReferencia(comprobarreferencia(producto.codigoReferencia()))
                .categorias(new HashSet<>(si)).build()
                ;

        Producto  p1 = productoRepository.save(p);

        return ProductoResponse.fromEntity(p1);
    }

    private String comprobarreferencia( String codigo) {

        if(codigo == null || codigo.isBlank()) {
            return generarCodigoReferencia();
        }

        // Si el usuario lo envió, lo limpiamos de espacios y lo pasamos a mayúsculas
        String codigoLimpio = codigo.trim().toUpperCase();

        // Comprobamos si está repetido
        if (productoRepository.existsByCodigoReferencia(codigoLimpio)) {
            throw new DuplicateResourceException("El código de referencia '" + codigoLimpio + "' ya está en uso");
        }

        return codigoLimpio;
    }

    private String generarCodigoReferencia() {
        String codigo;
        do {
            // Genera un prefijo + 8 caracteres hexadecimales aleatorios
            codigo = "PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (productoRepository.existsByCodigoReferencia(codigo)); // Asegura que nunca colisione

        return codigo;
    }




}
