package com.example.App.controller;

import com.example.App.dto.producto.ProductoCreated;
import com.example.App.dto.producto.ProductoResponse;
import com.example.App.model.Producto;
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
    public ResponseEntity<ProductoResponse> getProducto(@PathVariable Long id){
        return new ResponseEntity<>(productoService.getProducto(id), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<ProductoResponse>> getProductos(){
        return new ResponseEntity<>(productoService.getProductos(),HttpStatus.OK);
    }
    @GetMapping("/codigo/{codigoReferencia}")
    public ResponseEntity<ProductoResponse> getProductoByCodReferet(@PathVariable String codigoReferencia){
        return new ResponseEntity<>(productoService.getProductoByCodReferece(codigoReferencia), HttpStatus.OK);
    }


    @PostMapping("")
    public ResponseEntity<ProductoResponse> crearProducto(@Valid @RequestBody ProductoCreated producto){
        return new ResponseEntity<>(productoService.crearProducto(producto),HttpStatus.CREATED);
    }


}
