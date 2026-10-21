package com.example.App.controller;

import com.example.App.dto.producto.ProductoResponse;
import com.example.App.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
