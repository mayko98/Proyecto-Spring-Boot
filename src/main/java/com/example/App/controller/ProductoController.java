package com.example.App.controller;

import com.example.App.dto.producto.ProductoRequestDto;
import com.example.App.dto.producto.ProductoResponseDto;
import com.example.App.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> getProducto(@PathVariable Long id){
        return new ResponseEntity<>(productoService.getProducto(id), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<ProductoResponseDto>> getProductos(){
        return new ResponseEntity<>(productoService.getProductos(),HttpStatus.OK);
    }
    @GetMapping("/codigo/{codigoReferencia}")
    public ResponseEntity<ProductoResponseDto> getProductoByCodReferet(@PathVariable String codigoReferencia){
        return new ResponseEntity<>(productoService.getProductoByCodReferece(codigoReferencia), HttpStatus.OK);
    }


    @PostMapping("")
    public ResponseEntity<ProductoResponseDto> crearProducto(@Valid @RequestBody ProductoRequestDto producto){
        return new ResponseEntity<>(productoService.crearProducto(producto),HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> actualizarProducto(@Valid @RequestBody ProductoRequestDto producto,@PathVariable Long id){
        return new ResponseEntity<>(productoService.productoPut(producto,id),HttpStatus.OK);
    }

}
