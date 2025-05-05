package com.turismo.turismobackend.service;

import com.turismo.turismobackend.dto.request.MunicipalidadRequest;
import com.turismo.turismobackend.dto.response.MunicipalidadResponse;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.Emprendedor;
import com.turismo.turismobackend.model.Municipalidad;
import com.turismo.turismobackend.model.Usuario;
import com.turismo.turismobackend.repository.MunicipalidadRepository;
import com.turismo.turismobackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MunicipalidadService {
    
    private final MunicipalidadRepository municipalidadRepository;
    private final UsuarioRepository usuarioRepository;
    
    public List<MunicipalidadResponse> getAllMunicipalidades() {
        return municipalidadRepository.findAll().stream()
                .map(this::mapToMunicipalidadResponse)
                .collect(Collectors.toList());
    }
    
    public MunicipalidadResponse getMunicipalidadById(Long id) {
        Municipalidad municipalidad = municipalidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Municipalidad", "id", id));
        
        return mapToMunicipalidadResponse(municipalidad);
    }
    
    public MunicipalidadResponse createMunicipalidad(MunicipalidadRequest request) {
        // Obtener el usuario autenticado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Verificar si el usuario ya tiene una municipalidad asignada
        if (municipalidadRepository.findByUsuario(usuario).isPresent()) {
            throw new RuntimeException("El usuario ya tiene una municipalidad asignada");
        }
        
        // Crear nueva municipalidad
        Municipalidad municipalidad = Municipalidad.builder()
                .nombre(request.getNombre())
                .departamento(request.getDepartamento())
                .provincia(request.getProvincia())
                .distrito(request.getDistrito())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .sitioWeb(request.getSitioWeb())
                .descripcion(request.getDescripcion())
                .usuario(usuario)
                .build();
        
        municipalidadRepository.save(municipalidad);
        
        return mapToMunicipalidadResponse(municipalidad);
    }
    
    public MunicipalidadResponse updateMunicipalidad(Long id, MunicipalidadRequest request) {
        // Buscar la municipalidad
        Municipalidad municipalidad = municipalidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Municipalidad", "id", id));
        
        // Obtener el usuario autenticado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Verificar si el usuario es el propietario de la municipalidad
        /*if (!municipalidad.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para actualizar esta municipalidad");
        }*/
        
        // Actualizar los datos
        municipalidad.setNombre(request.getNombre());
        municipalidad.setDepartamento(request.getDepartamento());
        municipalidad.setProvincia(request.getProvincia());
        municipalidad.setDistrito(request.getDistrito());
        municipalidad.setDireccion(request.getDireccion());
        municipalidad.setTelefono(request.getTelefono());
        municipalidad.setSitioWeb(request.getSitioWeb());
        municipalidad.setDescripcion(request.getDescripcion());
        
        municipalidadRepository.save(municipalidad);
        
        return mapToMunicipalidadResponse(municipalidad);
    }
    
    public void deleteMunicipalidad(Long id) {
        // Buscar la municipalidad
        Municipalidad municipalidad = municipalidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Municipalidad", "id", id));
        
        // Obtener el usuario autenticado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Verificar si el usuario es el propietario de la municipalidad o un administrador
        if (!municipalidad.getUsuario().getId().equals(usuario.getId()) && 
                usuario.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("No tienes permiso para eliminar esta municipalidad");
        }
        
        municipalidadRepository.delete(municipalidad);
    }
    
    public List<MunicipalidadResponse> getMunicipalidadesByDepartamento(String departamento) {
        return municipalidadRepository.findByDepartamento(departamento).stream()
                .map(this::mapToMunicipalidadResponse)
                .collect(Collectors.toList());
    }
    
    public List<MunicipalidadResponse> getMunicipalidadesByProvincia(String provincia) {
        return municipalidadRepository.findByProvincia(provincia).stream()
                .map(this::mapToMunicipalidadResponse)
                .collect(Collectors.toList());
    }
    
    public List<MunicipalidadResponse> getMunicipalidadesByDistrito(String distrito) {
        return municipalidadRepository.findByDistrito(distrito).stream()
                .map(this::mapToMunicipalidadResponse)
                .collect(Collectors.toList());
    }
    
    public MunicipalidadResponse getMunicipalidadByUsuario() {
        // Obtener el usuario autenticado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Buscar municipalidad por usuario
        Municipalidad municipalidad = municipalidadRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Municipalidad", "usuario_id", usuario.getId()));
        
        return mapToMunicipalidadResponse(municipalidad);
    }
    
    private MunicipalidadResponse mapToMunicipalidadResponse(Municipalidad municipalidad) {
    // Verificar si la lista de emprendedores es nula, si lo es, inicializarla como una lista vacía
        List<Emprendedor> emprendedores = municipalidad.getEmprendedores();
        if (emprendedores == null) {
            emprendedores = new ArrayList<>();
        }
        
        // Mapear emprendedores a la respuesta resumida
        List<MunicipalidadResponse.EmprendedorResumen> emprendedoresResumen = emprendedores.stream()
                .map(emp -> MunicipalidadResponse.EmprendedorResumen.builder()
                        .id(emp.getId())
                        .nombreEmpresa(emp.getNombreEmpresa())
                        .rubro(emp.getRubro())
                        .build())
                .collect(Collectors.toList());
        
        // Construir respuesta
        return MunicipalidadResponse.builder()
                .id(municipalidad.getId())
                .nombre(municipalidad.getNombre())
                .departamento(municipalidad.getDepartamento())
                .provincia(municipalidad.getProvincia())
                .distrito(municipalidad.getDistrito())
                .direccion(municipalidad.getDireccion())
                .telefono(municipalidad.getTelefono())
                .sitioWeb(municipalidad.getSitioWeb())
                .descripcion(municipalidad.getDescripcion())
                .usuarioId(municipalidad.getUsuario().getId())
                .emprendedores(emprendedoresResumen)
                .build();
    }
}