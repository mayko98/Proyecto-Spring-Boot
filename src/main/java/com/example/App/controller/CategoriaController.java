package com.example.App.controller;

import com.example.App.dto.categoria.CategoriaRequestDto;
import com.example.App.dto.categoria.CategoriaResponseDto;
import com.example.App.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDto> getCategoria(@PathVariable("id") Long id){
        return new ResponseEntity<>(categoriaService.getCategoria(id), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<CategoriaResponseDto>> getCategorias(){
        return new ResponseEntity<>(categoriaService.getCategorias(),HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<CategoriaResponseDto> crearCategoria(@Valid @RequestBody CategoriaRequestDto categoria){
        return new ResponseEntity<>(categoriaService.crearCategoria(categoria),HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDto> actualizarCategoria(@Valid @RequestBody CategoriaRequestDto categoria, @PathVariable("id") Long id){
        return new ResponseEntity<>(categoriaService.categoriaPut(categoria,id),HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable("id") Long id){
        categoriaService.categoriaDelete(id);
        return ResponseEntity.noContent().build();
    }
}
