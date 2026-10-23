package com.example.App.service;

import com.example.App.dto.categoria.CategoriaResponseDto;
import com.example.App.exception.ResourceNotFoundException;
import com.example.App.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public CategoriaResponseDto getCategoria(Long id){
        return CategoriaResponseDto.fromEntity(categoriaRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Recurso no encontrado "+id)));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDto> getCategorias() {
        return categoriaRepository.findAll().stream().map(CategoriaResponseDto::fromEntity).toList();
    }
}
