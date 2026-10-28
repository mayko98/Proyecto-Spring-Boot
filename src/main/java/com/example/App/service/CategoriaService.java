package com.example.App.service;

import com.example.App.dto.categoria.CategoriaRequestDto;
import com.example.App.dto.categoria.CategoriaResponseDto;
import com.example.App.exception.DuplicateResourceException;
import com.example.App.exception.ResourceNotFoundException;
import com.example.App.model.Categoria;
import com.example.App.model.Producto;
import com.example.App.repository.CategoriaRepository;
import jakarta.validation.Valid;
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
        return CategoriaResponseDto.fromEntity(categoriaRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Categoria no encontrada "+id)));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDto> getCategorias() {
        return categoriaRepository.findAll().stream().map(CategoriaResponseDto::fromEntity).toList();
    }

    @Transactional
    public CategoriaResponseDto crearCategoria(@Valid CategoriaRequestDto categoria) {


        if (categoriaRepository.existsByNombreIgnoreCase(categoria.nombre().trim())) {
            throw new DuplicateResourceException("Ya existe una categoría con el nombre: " + categoria.nombre().trim());
        }

        Categoria categoria1 = new Categoria();
        categoria1.setNombre(categoria.nombre());
        categoria1.setDescripcion(categoria.descripcion());

        return CategoriaResponseDto.fromEntity(categoriaRepository.save(categoria1));
    }

    @Transactional
    public CategoriaResponseDto categoriaPut(@Valid CategoriaRequestDto categoria, Long id) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrado con id " + id));

        if (!categoriaExistente.getNombre().equalsIgnoreCase(categoria.nombre().trim())) {
            if (categoriaRepository.existsByNombreIgnoreCase(categoria.nombre().trim())) {
                throw new DuplicateResourceException("Ya existe otra categoría con el nombre: " + categoria.nombre().
                        trim());
            }
        }


        categoriaExistente.setNombre(categoria.nombre());
        categoriaExistente.setDescripcion(categoria.descripcion());

        Categoria categoriaUpdate = categoriaRepository.save(categoriaExistente);

        return CategoriaResponseDto.fromEntity(categoriaUpdate);
    }

    @Transactional
    public void categoriaDelete(Long id) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrado con id " + id));

        if (!categoriaExistente.getProductos().isEmpty()){
            throw new DuplicateResourceException("No se puede eliminar la categoría porque tiene productos asociados");
        }
        categoriaRepository.delete(categoriaExistente);

    }
}
