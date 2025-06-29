package com.turismo.turismobackend.service;

import com.turismo.turismobackend.dto.request.ServicioTuristicoRequest;
import com.turismo.turismobackend.dto.response.ServicioTuristicoResponse;
import com.turismo.turismobackend.dto.response.EmprendedorBasicResponse;
import com.turismo.turismobackend.dto.response.MunicipalidadBasicResponse;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.ServicioTuristico;
import com.turismo.turismobackend.model.Emprendedor;
import com.turismo.turismobackend.model.Usuario;
import com.turismo.turismobackend.repository.ServicioTuristicoRepository;
import com.turismo.turismobackend.repository.EmprendedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicioTuristicoService {
    
    private final ServicioTuristicoRepository servicioRepository;
    private final EmprendedorRepository emprendedorRepository;
    
    public List<ServicioTuristicoResponse> getAllServicios() {
        return servicioRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public ServicioTuristicoResponse getServicioById(Long id) {
        ServicioTuristico servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio turístico", "id", id));
        return convertToResponse(servicio);
    }
    
    public List<ServicioTuristicoResponse> getServiciosByEmprendedor(Long emprendedorId) {
        return servicioRepository.findByEmprendedorId(emprendedorId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<ServicioTuristicoResponse> getServiciosByMunicipalidad(Long municipalidadId) {
        return servicioRepository.findByEmprendedorMunicipalidadId(municipalidadId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<ServicioTuristicoResponse> getServiciosByTipo(ServicioTuristico.TipoServicio tipo) {
        return servicioRepository.findByTipo(tipo).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<ServicioTuristicoResponse> getServiciosByEstado(ServicioTuristico.EstadoServicio estado) {
        return servicioRepository.findByEstado(estado).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<ServicioTuristicoResponse> getServiciosByPrecio(BigDecimal precioMin, BigDecimal precioMax) {
        return servicioRepository.findByPrecioBetween(precioMin, precioMax).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<ServicioTuristicoResponse> searchServicios(String termino) {
        return servicioRepository.findByNombreOrDescripcionContaining(termino, termino).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<ServicioTuristicoResponse> getMisServicios() {
        Usuario usuario = getCurrentUser();
        Emprendedor emprendedor = emprendedorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Emprendedor", "usuario_id", usuario.getId()));
        
        return servicioRepository.findByEmprendedorId(emprendedor.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public ServicioTuristicoResponse createServicio(ServicioTuristicoRequest request) {
        Usuario usuario = getCurrentUser();
        Emprendedor emprendedor = emprendedorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Emprendedor", "usuario_id", usuario.getId()));
        
        ServicioTuristico servicio = ServicioTuristico.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .duracionHoras(request.getDuracionHoras())
                .capacidadMaxima(request.getCapacidadMaxima())
                .tipo(request.getTipo())
                .estado(ServicioTuristico.EstadoServicio.ACTIVO)
                .ubicacion(request.getUbicacion())
                .latitud(request.getLatitud())      // NUEVO CAMPO
                .longitud(request.getLongitud())    // NUEVO CAMPO
                .requisitos(request.getRequisitos())
                .incluye(request.getIncluye())
                .noIncluye(request.getNoIncluye())
                .imagenUrl(request.getImagenUrl())
                .emprendedor(emprendedor)
                .build();
        
        ServicioTuristico savedServicio = servicioRepository.save(servicio);
        return convertToResponse(savedServicio);
    }
    
    public ServicioTuristicoResponse updateServicio(Long id, ServicioTuristicoRequest request) {
        ServicioTuristico servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio turístico", "id", id));
        
        // Verificar que el servicio pertenece al emprendedor actual
        Usuario usuario = getCurrentUser();
        if (!servicio.getEmprendedor().getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tiene permisos para modificar este servicio");
        }
        
        servicio.setNombre(request.getNombre());
        servicio.setDescripcion(request.getDescripcion());
        servicio.setPrecio(request.getPrecio());
        servicio.setDuracionHoras(request.getDuracionHoras());
        servicio.setCapacidadMaxima(request.getCapacidadMaxima());
        servicio.setTipo(request.getTipo());
        servicio.setUbicacion(request.getUbicacion());
        servicio.setLatitud(request.getLatitud());      // NUEVO CAMPO
        servicio.setLongitud(request.getLongitud());    // NUEVO CAMPO
        servicio.setRequisitos(request.getRequisitos());
        servicio.setIncluye(request.getIncluye());
        servicio.setNoIncluye(request.getNoIncluye());
        servicio.setImagenUrl(request.getImagenUrl());
        
        ServicioTuristico updatedServicio = servicioRepository.save(servicio);
        return convertToResponse(updatedServicio);
    }
    
    public void deleteServicio(Long id) {
        ServicioTuristico servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio turístico", "id", id));
        
        // Verificar que el servicio pertenece al emprendedor actual
        Usuario usuario = getCurrentUser();
        if (!servicio.getEmprendedor().getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tiene permisos para eliminar este servicio");
        }
        
        servicioRepository.delete(servicio);
    }
    
    public ServicioTuristicoResponse cambiarEstado(Long id, ServicioTuristico.EstadoServicio nuevoEstado) {
        ServicioTuristico servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio turístico", "id", id));
        
        // Verificar que el servicio pertenece al emprendedor actual
        Usuario usuario = getCurrentUser();
        if (!servicio.getEmprendedor().getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tiene permisos para modificar este servicio");
        }
        
        servicio.setEstado(nuevoEstado);
        ServicioTuristico updatedServicio = servicioRepository.save(servicio);
        return convertToResponse(updatedServicio);
    }
    
    private ServicioTuristicoResponse convertToResponse(ServicioTuristico servicio) {
        return ServicioTuristicoResponse.builder()
                .id(servicio.getId())
                .nombre(servicio.getNombre())
                .descripcion(servicio.getDescripcion())
                .precio(servicio.getPrecio())
                .duracionHoras(servicio.getDuracionHoras())
                .capacidadMaxima(servicio.getCapacidadMaxima())
                .tipo(servicio.getTipo())
                .estado(servicio.getEstado())
                .ubicacion(servicio.getUbicacion())
                .latitud(servicio.getLatitud())     // NUEVO CAMPO
                .longitud(servicio.getLongitud())   // NUEVO CAMPO
                .requisitos(servicio.getRequisitos())
                .incluye(servicio.getIncluye())
                .noIncluye(servicio.getNoIncluye())
                .imagenUrl(servicio.getImagenUrl())
                .emprendedor(convertToEmprendedorBasicResponse(servicio.getEmprendedor()))
                .build();
    }
    
    private EmprendedorBasicResponse convertToEmprendedorBasicResponse(Emprendedor emprendedor) {
        return EmprendedorBasicResponse.builder()
                .id(emprendedor.getId())
                .nombreEmpresa(emprendedor.getNombreEmpresa())
                .rubro(emprendedor.getRubro())
                .telefono(emprendedor.getTelefono())
                .email(emprendedor.getEmail())
                .municipalidad(MunicipalidadBasicResponse.builder()
                        .id(emprendedor.getMunicipalidad().getId())
                        .nombre(emprendedor.getMunicipalidad().getNombre())
                        .departamento(emprendedor.getMunicipalidad().getDepartamento())
                        .provincia(emprendedor.getMunicipalidad().getProvincia())
                        .distrito(emprendedor.getMunicipalidad().getDistrito())
                        .build())
                .build();
    }
    // NUEVO MÉTODO para servicios cercanos
    public List<ServicioTuristicoResponse> getServiciosCercanos(Double latitud, Double longitud, Double radioKm) {
        return servicioRepository.findAll().stream()
                .filter(ServicioTuristico::tieneUbicacionValida)
                .filter(servicio -> calcularDistancia(latitud, longitud, 
                        servicio.getLatitud(), servicio.getLongitud()) <= radioKm)
                .map(this::convertToResponse)
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

    
    private Usuario getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Usuario)) {
            throw new RuntimeException("Usuario no autenticado correctamente");
        }
        return (Usuario) principal;
    }
}