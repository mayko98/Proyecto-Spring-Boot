package com.example.App.service;

import com.example.App.dto.producto.ProductoRequestDto;
import com.example.App.dto.producto.ProductoResponseDto;
import com.example.App.exception.DuplicateResourceException;
import com.example.App.exception.ResourceNotFoundException;
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
    public ProductoResponseDto getProducto(Long id){
        return ProductoResponseDto.fromEntity(productoRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Recurso no encontrado "+id)));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDto> getProductos() {
        return productoRepository.findAll().stream().map(ProductoResponseDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponseDto getProductoByCodReferece(String codigoReferencia) {

        return ProductoResponseDto.fromEntity(productoRepository.findByCodigoReferencia(codigoReferencia).orElseThrow(()->new ResourceNotFoundException("Recurso no encontrado "+codigoReferencia)));
    }

    @Transactional
    public ProductoResponseDto crearProducto(ProductoRequestDto producto) {

        List<Categoria> categoriasencontradas = categoriaRepository.findAllById(producto.categoriasIds());
        if (categoriasencontradas.size() != producto.categoriasIds().size()) {
            throw new ResourceNotFoundException("Una o más categorías especificadas no existen");
        }

        Producto p = Producto.builder()
                .nombre(producto.nombre())
                .descripcion(producto.descripcion())
                .precio(producto.precio())
                .stock(producto.stock())
                .codigoReferencia(comprobarreferencia(producto.codigoReferencia()))
                .categorias(new HashSet<>(categoriasencontradas)).build()
                ;

        Producto  p1 = productoRepository.save(p);

        return ProductoResponseDto.fromEntity(p1);
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

    @Transactional
    public ProductoResponseDto productoPut(ProductoRequestDto producto,Long id) {

        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));

        List<Categoria> categoriasencontradas = categoriaRepository.findAllById(producto.categoriasIds());
        if (categoriasencontradas.size() != producto.categoriasIds().size()) {
            throw new ResourceNotFoundException("Una o más categorías especificadas no existen");
        }



        productoExistente.setNombre(producto.nombre());
        productoExistente.setDescripcion(producto.descripcion());
        productoExistente.setPrecio(producto.precio());
        productoExistente.setStock(producto.stock());
        productoExistente.setCategorias(new HashSet<>(categoriasencontradas));

        Producto  p1 = productoRepository.save(productoExistente);

        return ProductoResponseDto.fromEntity(p1);
    }
}
