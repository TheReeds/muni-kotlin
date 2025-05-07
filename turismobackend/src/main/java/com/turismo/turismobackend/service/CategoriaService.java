package com.turismo.turismobackend.service;

import com.turismo.turismobackend.dto.request.CategoriaRequest;
import com.turismo.turismobackend.dto.response.CategoriaResponse;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.Categoria;
import com.turismo.turismobackend.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    
    private final CategoriaRepository categoriaRepository;
    
    public List<CategoriaResponse> getAllCategorias() {
        return categoriaRepository.findAll().stream()
                .map(this::mapToCategoriaResponse)
                .collect(Collectors.toList());
    }
    
    public CategoriaResponse getCategoriaById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", id));
        
        return mapToCategoriaResponse(categoria);
    }
    
    public CategoriaResponse createCategoria(CategoriaRequest request) {
        // Verificar si ya existe una categoría con el mismo nombre
        if (categoriaRepository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + request.getNombre());
        }
        
        // Crear nueva categoría
        Categoria categoria = Categoria.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .build();
        
        categoriaRepository.save(categoria);
        
        return mapToCategoriaResponse(categoria);
    }
    
    public CategoriaResponse updateCategoria(Long id, CategoriaRequest request) {
        // Buscar la categoría
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", id));
        
        // Verificar que no exista otra categoría con el mismo nombre
        if (!categoria.getNombre().equals(request.getNombre()) && 
                categoriaRepository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con el nombre: " + request.getNombre());
        }
        
        // Actualizar los datos
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        
        categoriaRepository.save(categoria);
        
        return mapToCategoriaResponse(categoria);
    }
    
    public void deleteCategoria(Long id) {
        // Buscar la categoría
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", id));
        
        // Verificar si tiene emprendedores asociados
        if (!categoria.getEmprendedores().isEmpty()) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene emprendedores asociados");
        }
        
        categoriaRepository.delete(categoria);
    }
    
    private CategoriaResponse mapToCategoriaResponse(Categoria categoria) {
        return CategoriaResponse.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .cantidadEmprendedores(categoria.getEmprendedores().size())
                .build();
    }
}