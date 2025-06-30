package com.turismo.turismobackend.service;

import com.turismo.turismobackend.dto.request.EmprendedorRequest;
import com.turismo.turismobackend.dto.response.EmprendedorResponse;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.Categoria;
import com.turismo.turismobackend.model.Emprendedor;
import com.turismo.turismobackend.model.Municipalidad;
import com.turismo.turismobackend.model.Usuario;
import com.turismo.turismobackend.repository.CategoriaRepository;
import com.turismo.turismobackend.repository.EmprendedorRepository;
import com.turismo.turismobackend.repository.MunicipalidadRepository;
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
    private final CategoriaRepository categoriaRepository;
    
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
    
    public List<EmprendedorResponse> getEmprendedoresByCategoria(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", categoriaId));
        
        return emprendedorRepository.findByCategoria(categoria).stream()
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
        
        // Buscar la categoría si se proporcionó un ID
        Categoria categoria = null;
        if (request.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", request.getCategoriaId()));
        }
        
        // Crear nuevo emprendedor con ubicación
        Emprendedor emprendedor = Emprendedor.builder()
                .nombreEmpresa(request.getNombreEmpresa())
                .rubro(request.getRubro())
                .direccion(request.getDireccion())
                .latitud(request.getLatitud())  // NUEVO CAMPO
                .longitud(request.getLongitud()) // NUEVO CAMPO
                .direccionCompleta(request.getDireccionCompleta()) // NUEVO CAMPO
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .sitioWeb(request.getSitioWeb())
                .descripcion(request.getDescripcion())
                .productos(request.getProductos())
                .servicios(request.getServicios())
                .municipalidad(municipalidad)
                .categoria(categoria)
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
        
        // Buscar la municipalidad, si se va a cambiar
        Municipalidad municipalidad = null;
        if (!emprendedor.getMunicipalidad().getId().equals(request.getMunicipalidadId())) {
            municipalidad = municipalidadRepository.findById(request.getMunicipalidadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Municipalidad", "id", request.getMunicipalidadId()));
        } else {
            municipalidad = emprendedor.getMunicipalidad();
        }
        
        // Buscar la categoría, si se proporcionó un ID
        Categoria categoria = null;
        if (request.getCategoriaId() != null) {
            // Si se proporciona un ID de categoría diferente al actual o el actual es nulo
            if (emprendedor.getCategoria() == null || 
                    !emprendedor.getCategoria().getId().equals(request.getCategoriaId())) {
                categoria = categoriaRepository.findById(request.getCategoriaId())
                        .orElseThrow(() -> new ResourceNotFoundException("Categoría", "id", request.getCategoriaId()));
            } else {
                categoria = emprendedor.getCategoria();
            }
        }
        
        // Actualizar los datos
        emprendedor.setNombreEmpresa(request.getNombreEmpresa());
        emprendedor.setRubro(request.getRubro());
        emprendedor.setDireccion(request.getDireccion());
        emprendedor.setLatitud(request.getLatitud());      // NUEVO CAMPO
        emprendedor.setLongitud(request.getLongitud());    // NUEVO CAMPO
        emprendedor.setDireccionCompleta(request.getDireccionCompleta()); // NUEVO CAMPO
        emprendedor.setTelefono(request.getTelefono());
        emprendedor.setEmail(request.getEmail());
        emprendedor.setSitioWeb(request.getSitioWeb());
        emprendedor.setDescripcion(request.getDescripcion());
        emprendedor.setProductos(request.getProductos());
        emprendedor.setServicios(request.getServicios());
        emprendedor.setMunicipalidad(municipalidad);
        emprendedor.setCategoria(categoria);
        
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

        // Mapear categoría resumida (si existe)
        EmprendedorResponse.CategoriaResumen categoriaResumen = null;
        if (emprendedor.getCategoria() != null) {
            categoriaResumen = EmprendedorResponse.CategoriaResumen.builder()
                    .id(emprendedor.getCategoria().getId())
                    .nombre(emprendedor.getCategoria().getNombre())
                    .build();
        }

        // Construir respuesta
        return EmprendedorResponse.builder()
                .id(emprendedor.getId())
                .nombreEmpresa(emprendedor.getNombreEmpresa())
                .rubro(emprendedor.getRubro())
                .direccion(emprendedor.getDireccion())
                .latitud(emprendedor.getLatitud())
                .longitud(emprendedor.getLongitud())
                .direccionCompleta(emprendedor.getDireccionCompleta())
                .telefono(emprendedor.getTelefono())
                .email(emprendedor.getEmail())
                .sitioWeb(emprendedor.getSitioWeb())
                .descripcion(emprendedor.getDescripcion())
                .productos(emprendedor.getProductos())
                .servicios(emprendedor.getServicios())
                .usuarioId(emprendedor.getUsuario() != null ? emprendedor.getUsuario().getId() : null) // ✅ Cambio clave
                .municipalidad(municipalidadResumen)
                .categoria(categoriaResumen)
                .build();
    }

    // NUEVOS MÉTODOS para ubicación
    public List<EmprendedorResponse> getEmprendedoresCercanos(Double latitud, Double longitud, Double radioKm) {
        return emprendedorRepository.findAll().stream()
                .filter(Emprendedor::tieneUbicacionValida)
                .filter(emp -> calcularDistancia(latitud, longitud, emp.getLatitud(), emp.getLongitud()) <= radioKm)
                .map(this::mapToEmprendedorResponse)
                .collect(Collectors.toList());
    }
    
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}