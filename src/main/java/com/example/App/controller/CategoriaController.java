package com.example.App.controller;

import com.example.App.dto.categoria.CategoriaResponseDto;
import com.example.App.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDto> getCategoria(@PathVariable Long id){
        return new ResponseEntity<>(categoriaService.getCategoria(id), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<CategoriaResponseDto>> getCategorias(){
        return new ResponseEntity<>(categoriaService.getCategorias(),HttpStatus.OK);
    }
}
