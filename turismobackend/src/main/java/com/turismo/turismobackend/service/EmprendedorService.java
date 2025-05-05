package com.turismo.turismobackend.service;

import com.turismo.turismobackend.dto.request.EmprendedorRequest;
import com.turismo.turismobackend.dto.response.EmprendedorResponse;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.Emprendedor;
import com.turismo.turismobackend.model.Municipalidad;
import com.turismo.turismobackend.model.Usuario;
import com.turismo.turismobackend.repository.EmprendedorRepository;
import com.turismo.turismobackend.repository.MunicipalidadRepository;
import com.turismo.turismobackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmprendedorService {
    
    private final EmprendedorRepository emprendedorRepository;
    private final MunicipalidadRepository municipalidadRepository;
    private final UsuarioRepository usuarioRepository;
    
    public List<EmprendedorResponse> getAllEmprendedores() {
        return emprendedorRepository.findAll().stream()
                .map(this::mapToEmprendedorResponse)
                .collect(Collectors.toList());
    }
    
    public EmprendedorResponse getEmprendedorById(Long id) {
        Emprendedor emprendedor = emprendedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emprendedor", "id", id));
        
        return mapToEmprendedorResponse(emprendedor);
    }
    
    public List<EmprendedorResponse> getEmprendedoresByMunicipalidad(Long municipalidadId) {
        Municipalidad municipalidad = municipalidadRepository.findById(municipalidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Municipalidad", "id", municipalidadId));
        
        return emprendedorRepository.findByMunicipalidad(municipalidad).stream()
                .map(this::mapToEmprendedorResponse)
                .collect(Collectors.toList());
    }
    
    public List<EmprendedorResponse> getEmprendedoresByRubro(String rubro) {
        return emprendedorRepository.findByRubro(rubro).stream()
                .map(this::mapToEmprendedorResponse)
                .collect(Collectors.toList());
    }
    
    public EmprendedorResponse createEmprendedor(EmprendedorRequest request) {
        // Obtener el usuario autenticado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Verificar si el usuario ya tiene un emprendedor asignado
        if (emprendedorRepository.findByUsuario(usuario).isPresent()) {
            throw new RuntimeException("El usuario ya tiene un emprendedor asignado");
        }
        
        // Buscar la municipalidad a la que pertenecerá el emprendedor
        Municipalidad municipalidad = municipalidadRepository.findById(request.getMunicipalidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Municipalidad", "id", request.getMunicipalidadId()));
        
        // Crear nuevo emprendedor
        Emprendedor emprendedor = Emprendedor.builder()
                .nombreEmpresa(request.getNombreEmpresa())
                .rubro(request.getRubro())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .sitioWeb(request.getSitioWeb())
                .descripcion(request.getDescripcion())
                .productos(request.getProductos())
                .servicios(request.getServicios())
                .municipalidad(municipalidad)
                .usuario(usuario)
                .build();
        
        emprendedorRepository.save(emprendedor);
        
        return mapToEmprendedorResponse(emprendedor);
    }
    
    public EmprendedorResponse updateEmprendedor(Long id, EmprendedorRequest request) {
        // Buscar el emprendedor
        Emprendedor emprendedor = emprendedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emprendedor", "id", id));
        
        // Obtener el usuario autenticado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Verificar si el usuario es el propietario del emprendedor
        /*if (!emprendedor.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para actualizar este emprendedor");
        }*/
        
        // Buscar la municipalidad, si se va a cambiar
        Municipalidad municipalidad = null;
        if (!emprendedor.getMunicipalidad().getId().equals(request.getMunicipalidadId())) {
            municipalidad = municipalidadRepository.findById(request.getMunicipalidadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Municipalidad", "id", request.getMunicipalidadId()));
        } else {
            municipalidad = emprendedor.getMunicipalidad();
        }
        
        // Actualizar los datos
        emprendedor.setNombreEmpresa(request.getNombreEmpresa());
        emprendedor.setRubro(request.getRubro());
        emprendedor.setDireccion(request.getDireccion());
        emprendedor.setTelefono(request.getTelefono());
        emprendedor.setEmail(request.getEmail());
        emprendedor.setSitioWeb(request.getSitioWeb());
        emprendedor.setDescripcion(request.getDescripcion());
        emprendedor.setProductos(request.getProductos());
        emprendedor.setServicios(request.getServicios());
        emprendedor.setMunicipalidad(municipalidad);
        
        emprendedorRepository.save(emprendedor);
        
        return mapToEmprendedorResponse(emprendedor);
    }
    
    public void deleteEmprendedor(Long id) {
        // Buscar el emprendedor
        Emprendedor emprendedor = emprendedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emprendedor", "id", id));
        
        // Obtener el usuario autenticado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Verificar si el usuario es el propietario del emprendedor o un administrador
        if (!emprendedor.getUsuario().getId().equals(usuario.getId()) && 
                usuario.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("No tienes permiso para eliminar este emprendedor");
        }
        
        emprendedorRepository.delete(emprendedor);
    }
    
    public EmprendedorResponse getEmprendedorByUsuario() {
        // Obtener el usuario autenticado
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Buscar emprendedor por usuario
        Emprendedor emprendedor = emprendedorRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Emprendedor", "usuario_id", usuario.getId()));
        
        return mapToEmprendedorResponse(emprendedor);
    }
    
    private EmprendedorResponse mapToEmprendedorResponse(Emprendedor emprendedor) {
        // Mapear municipalidad resumida
        EmprendedorResponse.MunicipalidadResumen municipalidadResumen = EmprendedorResponse.MunicipalidadResumen.builder()
                .id(emprendedor.getMunicipalidad().getId())
                .nombre(emprendedor.getMunicipalidad().getNombre())
                .distrito(emprendedor.getMunicipalidad().getDistrito())
                .build();
        
        // Construir respuesta
        return EmprendedorResponse.builder()
                .id(emprendedor.getId())
                .nombreEmpresa(emprendedor.getNombreEmpresa())
                .rubro(emprendedor.getRubro())
                .direccion(emprendedor.getDireccion())
                .telefono(emprendedor.getTelefono())
                .email(emprendedor.getEmail())
                .sitioWeb(emprendedor.getSitioWeb())
                .descripcion(emprendedor.getDescripcion())
                .productos(emprendedor.getProductos())
                .servicios(emprendedor.getServicios())
                .usuarioId(emprendedor.getUsuario().getId())
                .municipalidad(municipalidadResumen)
                .build();
    }
}