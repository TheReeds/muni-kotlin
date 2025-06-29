package com.turismo.turismobackend.service;

import com.turismo.turismobackend.dto.request.UbicacionRequest;
import com.turismo.turismobackend.dto.response.*;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UbicacionService {
    
    private final EmprendedorRepository emprendedorRepository;
    private final ServicioTuristicoRepository servicioRepository;
    private final MunicipalidadRepository municipalidadRepository;
    
    public List<EmprendedorResponse> obtenerEmprendedoresConUbicacion(
            Long municipalidadId, Double latitud, Double longitud, Double radio) {
        
        List<Emprendedor> emprendedores;
        
        if (municipalidadId != null) {
            emprendedores = emprendedorRepository.findByMunicipalidadId(municipalidadId);
        } else {
            emprendedores = emprendedorRepository.findAll();
        }
        
        return emprendedores.stream()
                .filter(Emprendedor::tieneUbicacionValida)
                .filter(emprendedor -> {
                    if (latitud != null && longitud != null && radio != null) {
                        return calcularDistancia(latitud, longitud, 
                                emprendedor.getLatitud(), emprendedor.getLongitud()) <= radio;
                    }
                    return true;
                })
                .map(this::convertEmprendedorToResponse)
                .collect(Collectors.toList());
    }
    
    public List<ServicioTuristicoResponse> obtenerServiciosConUbicacion(
            String tipoServicio, Long municipalidadId, Double latitud, Double longitud, Double radio) {
        
        List<ServicioTuristico> servicios;
        
        if (municipalidadId != null && tipoServicio != null) {
            servicios = servicioRepository.findByEmprendedorMunicipalidadId(municipalidadId)
                    .stream()
                    .filter(s -> s.getTipo().name().equals(tipoServicio.toUpperCase()))
                    .collect(Collectors.toList());
        } else if (municipalidadId != null) {
            servicios = servicioRepository.findByEmprendedorMunicipalidadId(municipalidadId);
        } else if (tipoServicio != null) {
            try {
                ServicioTuristico.TipoServicio tipo = ServicioTuristico.TipoServicio.valueOf(tipoServicio.toUpperCase());
                servicios = servicioRepository.findByTipo(tipo);
            } catch (IllegalArgumentException e) {
                servicios = List.of();
            }
        } else {
            servicios = servicioRepository.findAll();
        }
        
        return servicios.stream()
                .filter(ServicioTuristico::tieneUbicacionValida)
                .filter(servicio -> {
                    if (latitud != null && longitud != null && radio != null) {
                        return calcularDistancia(latitud, longitud, 
                                servicio.getLatitud(), servicio.getLongitud()) <= radio;
                    }
                    return true;
                })
                .map(this::convertServicioToResponse)
                .collect(Collectors.toList());
    }
    
    public UbicacionResponse actualizarUbicacionEmprendedor(Long emprendedorId, UbicacionRequest request) {
        Emprendedor emprendedor = emprendedorRepository.findById(emprendedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Emprendedor", "id", emprendedorId));
        
        // Verificar permisos
        Usuario usuario = getCurrentUser();
        if (!emprendedor.getUsuario().getId().equals(usuario.getId()) && !hasRole("ROLE_ADMIN")) {
            throw new RuntimeException("No tiene permisos para actualizar esta ubicación");
        }
        
        emprendedor.setLatitud(request.getLatitud());
        emprendedor.setLongitud(request.getLongitud());
        emprendedor.setDireccionCompleta(request.getDireccionCompleta());
        
        Emprendedor saved = emprendedorRepository.save(emprendedor);
        return convertEmprendedorToUbicacionResponse(saved);
    }
    
    public UbicacionResponse actualizarUbicacionServicio(Long servicioId, UbicacionRequest request) {
        ServicioTuristico servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", servicioId));
        
        // Verificar permisos
        Usuario usuario = getCurrentUser();
        if (!servicio.getEmprendedor().getUsuario().getId().equals(usuario.getId()) && !hasRole("ROLE_ADMIN")) {
            throw new RuntimeException("No tiene permisos para actualizar esta ubicación");
        }
        
        servicio.setLatitud(request.getLatitud());
        servicio.setLongitud(request.getLongitud());
        
        ServicioTuristico saved = servicioRepository.save(servicio);
        return convertServicioToUbicacionResponse(saved);
    }
    
    public boolean validarCoordenadas(Double latitud, Double longitud) {
        return latitud != null && longitud != null && 
               latitud >= -90 && latitud <= 90 && 
               longitud >= -180 && longitud <= 180;
    }
    
    public double calcularDistanciaEntre(double lat1, double lng1, double lat2, double lng2) {
        return calcularDistancia(lat1, lng1, lat2, lng2);
    }
    
    public List<UbicacionResponse> obtenerUbicacionesCercanas(Double latitud, Double longitud, Double radio, String tipo) {
        if ("emprendedores".equals(tipo)) {
            return obtenerEmprendedoresConUbicacion(null, latitud, longitud, radio)
                    .stream()
                    .map(emp -> UbicacionResponse.builder()
                            .latitud(emp.getLatitud())
                            .longitud(emp.getLongitud())
                            .direccionCompleta(emp.getDireccionCompleta())
                            .tieneUbicacionValida(emp.getLatitud() != null && emp.getLongitud() != null)
                            .build())
                    .collect(Collectors.toList());
        } else if ("servicios".equals(tipo)) {
            return obtenerServiciosConUbicacion(null, null, latitud, longitud, radio)
                    .stream()
                    .map(serv -> UbicacionResponse.builder()
                            .latitud(serv.getLatitud())
                            .longitud(serv.getLongitud())
                            .direccionCompleta(serv.getUbicacion())
                            .tieneUbicacionValida(serv.getLatitud() != null && serv.getLongitud() != null)
                            .build())
                    .collect(Collectors.toList());
        }
        return List.of();
    }
    
    // Métodos de conversión privados
    private EmprendedorResponse convertEmprendedorToResponse(Emprendedor emprendedor) {
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
                .usuarioId(emprendedor.getUsuario().getId())
                .municipalidad(EmprendedorResponse.MunicipalidadResumen.builder()
                        .id(emprendedor.getMunicipalidad().getId())
                        .nombre(emprendedor.getMunicipalidad().getNombre())
                        .distrito(emprendedor.getMunicipalidad().getDistrito())
                        .build())
                .categoria(emprendedor.getCategoria() != null ? 
                        EmprendedorResponse.CategoriaResumen.builder()
                                .id(emprendedor.getCategoria().getId())
                                .nombre(emprendedor.getCategoria().getNombre())
                                .build() : null)
                .build();
    }
    
    private ServicioTuristicoResponse convertServicioToResponse(ServicioTuristico servicio) {
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
                .latitud(servicio.getLatitud())
                .longitud(servicio.getLongitud())
                .requisitos(servicio.getRequisitos())
                .incluye(servicio.getIncluye())
                .noIncluye(servicio.getNoIncluye())
                .imagenUrl(servicio.getImagenUrl())
                .emprendedor(EmprendedorBasicResponse.builder()
                        .id(servicio.getEmprendedor().getId())
                        .nombreEmpresa(servicio.getEmprendedor().getNombreEmpresa())
                        .rubro(servicio.getEmprendedor().getRubro())
                        .telefono(servicio.getEmprendedor().getTelefono())
                        .email(servicio.getEmprendedor().getEmail())
                        .municipalidad(MunicipalidadBasicResponse.builder()
                                .id(servicio.getEmprendedor().getMunicipalidad().getId())
                                .nombre(servicio.getEmprendedor().getMunicipalidad().getNombre())
                                .departamento(servicio.getEmprendedor().getMunicipalidad().getDepartamento())
                                .provincia(servicio.getEmprendedor().getMunicipalidad().getProvincia())
                                .distrito(servicio.getEmprendedor().getMunicipalidad().getDistrito())
                                .build())
                        .build())
                .build();
    }
    
    private UbicacionResponse convertEmprendedorToUbicacionResponse(Emprendedor emprendedor) {
        return UbicacionResponse.builder()
                .latitud(emprendedor.getLatitud())
                .longitud(emprendedor.getLongitud())
                .direccionCompleta(emprendedor.getDireccionCompleta())
                .tieneUbicacionValida(emprendedor.tieneUbicacionValida())
                .build();
    }
    
    private UbicacionResponse convertServicioToUbicacionResponse(ServicioTuristico servicio) {
        return UbicacionResponse.builder()
                .latitud(servicio.getLatitud())
                .longitud(servicio.getLongitud())
                .direccionCompleta(servicio.getUbicacion())
                .tieneUbicacionValida(servicio.tieneUbicacionValida())
                .build();
    }
    
    // Fórmula de Haversine para calcular distancia entre dos puntos
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
    
    private boolean hasRole(String role) {
        return getCurrentUser().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}
