package com.turismo.turismobackend.service;

import com.turismo.turismobackend.dto.request.PlanTuristicoRequest;
import com.turismo.turismobackend.dto.request.ServicioPlanRequest;
import com.turismo.turismobackend.dto.response.*;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.*;
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
public class PlanTuristicoService {
    
    private final PlanTuristicoRepository planRepository;
    private final ServicioTuristicoRepository servicioRepository;
    private final ServicioPlanRepository servicioPlanRepository;
    private final MunicipalidadRepository municipalidadRepository;
    
    public List<PlanTuristicoResponse> getAllPlanes() {
        return planRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public PlanTuristicoResponse getPlanById(Long id) {
        PlanTuristico plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan turístico", "id", id));
        return convertToResponse(plan);
    }
    
    public List<PlanTuristicoResponse> getPlanesByMunicipalidad(Long municipalidadId) {
        return planRepository.findByMunicipalidadId(municipalidadId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<PlanTuristicoResponse> getPlanesByEstado(PlanTuristico.EstadoPlan estado) {
        return planRepository.findByEstado(estado).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<PlanTuristicoResponse> getPlanesByNivelDificultad(PlanTuristico.NivelDificultad nivel) {
        return planRepository.findByNivelDificultad(nivel).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<PlanTuristicoResponse> getPlanesByDuracion(Integer duracionMin, Integer duracionMax) {
        return planRepository.findByDuracionDiasBetween(duracionMin, duracionMax).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<PlanTuristicoResponse> getPlanesByPrecio(BigDecimal precioMin, BigDecimal precioMax) {
        return planRepository.findByPrecioTotalBetween(precioMin, precioMax).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<PlanTuristicoResponse> searchPlanes(String termino) {
        return planRepository.findByNombreOrDescripcionContaining(termino, termino).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<PlanTuristicoResponse> getMisPlanes() {
        Usuario usuario = getCurrentUser();
        return planRepository.findByUsuarioCreadorId(usuario.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<PlanTuristicoResponse> getPlanesMasPopulares() {
        return planRepository.findMostPopular().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public PlanTuristicoResponse createPlan(PlanTuristicoRequest request) {
        Usuario usuario = getCurrentUser();
        
        // Obtener municipalidad del usuario (si es municipalidad) o buscar por ID
        Municipalidad municipalidad;
        if (hasRole("ROLE_MUNICIPALIDAD")) {
            municipalidad = municipalidadRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Municipalidad", "usuario_id", usuario.getId()));
        } else if (hasRole("ROLE_ADMIN")) {
            if (request.getMunicipalidadId() == null) {
                throw new RuntimeException("Admin debe especificar la municipalidad en el request");
            }
            municipalidad = municipalidadRepository.findById(request.getMunicipalidadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Municipalidad", "id", request.getMunicipalidadId()));
        } else {
            throw new RuntimeException("No tiene permisos para crear planes turísticos");
        }
        
        // Calcular precio total ANTES de crear el plan
        BigDecimal precioTotal = BigDecimal.ZERO;
        for (ServicioPlanRequest servicioRequest : request.getServicios()) {
            ServicioTuristico servicio = servicioRepository.findById(servicioRequest.getServicioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", servicioRequest.getServicioId()));
            
            // Calcular precio del servicio
            BigDecimal precioServicio = servicioRequest.getPrecioEspecial() != null 
                    ? servicioRequest.getPrecioEspecial() 
                    : servicio.getPrecio();
            
            if (!servicioRequest.getEsOpcional()) {
                precioTotal = precioTotal.add(precioServicio);
            }
        }
        
        // Crear plan con el precio total ya calculado
        PlanTuristico plan = PlanTuristico.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .duracionDias(request.getDuracionDias())
                .capacidadMaxima(request.getCapacidadMaxima())
                .nivelDificultad(request.getNivelDificultad())
                .imagenPrincipalUrl(request.getImagenPrincipalUrl())
                .itinerario(request.getItinerario())
                .incluye(request.getIncluye())
                .noIncluye(request.getNoIncluye())
                .recomendaciones(request.getRecomendaciones())
                .requisitos(request.getRequisitos())
                .estado(PlanTuristico.EstadoPlan.BORRADOR)
                .precioTotal(precioTotal)
                .municipalidad(municipalidad)
                .usuarioCreador(usuario)
                .build();
        
        PlanTuristico savedPlan = planRepository.save(plan);
        
        // Agregar servicios al plan
        for (ServicioPlanRequest servicioRequest : request.getServicios()) {
            ServicioTuristico servicio = servicioRepository.findById(servicioRequest.getServicioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", servicioRequest.getServicioId()));
            
            ServicioPlan servicioPlan = ServicioPlan.builder()
                    .plan(savedPlan)
                    .servicio(servicio)
                    .diaDelPlan(servicioRequest.getDiaDelPlan())
                    .ordenEnElDia(servicioRequest.getOrdenEnElDia())
                    .horaInicio(servicioRequest.getHoraInicio())
                    .horaFin(servicioRequest.getHoraFin())
                    .precioEspecial(servicioRequest.getPrecioEspecial())
                    .notas(servicioRequest.getNotas())
                    .esOpcional(servicioRequest.getEsOpcional())
                    .esPersonalizable(servicioRequest.getEsPersonalizable())
                    .build();
            
            servicioPlanRepository.save(servicioPlan);
        }
        
        return convertToResponse(savedPlan);
    }
    
    public PlanTuristicoResponse updatePlan(Long id, PlanTuristicoRequest request) {
        PlanTuristico plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan turístico", "id", id));
        
        // Verificar permisos
        Usuario usuario = getCurrentUser();
        if (!plan.getUsuarioCreador().getId().equals(usuario.getId()) && !hasRole("ROLE_ADMIN")) {
            throw new RuntimeException("No tiene permisos para modificar este plan");
        }
        
        plan.setNombre(request.getNombre());
        plan.setDescripcion(request.getDescripcion());
        plan.setDuracionDias(request.getDuracionDias());
        plan.setCapacidadMaxima(request.getCapacidadMaxima());
        plan.setNivelDificultad(request.getNivelDificultad());
        plan.setImagenPrincipalUrl(request.getImagenPrincipalUrl());
        plan.setItinerario(request.getItinerario());
        plan.setIncluye(request.getIncluye());
        plan.setNoIncluye(request.getNoIncluye());
        plan.setRecomendaciones(request.getRecomendaciones());
        plan.setRequisitos(request.getRequisitos());
        
        // Eliminar servicios existentes del plan
        servicioPlanRepository.deleteByPlanId(id);
        
        // Agregar nuevos servicios y recalcular precio
        BigDecimal precioTotal = BigDecimal.ZERO;
        for (ServicioPlanRequest servicioRequest : request.getServicios()) {
            ServicioTuristico servicio = servicioRepository.findById(servicioRequest.getServicioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", servicioRequest.getServicioId()));
            
            ServicioPlan servicioPlan = ServicioPlan.builder()
                    .plan(plan)
                    .servicio(servicio)
                    .diaDelPlan(servicioRequest.getDiaDelPlan())
                    .ordenEnElDia(servicioRequest.getOrdenEnElDia())
                    .horaInicio(servicioRequest.getHoraInicio())
                    .horaFin(servicioRequest.getHoraFin())
                    .precioEspecial(servicioRequest.getPrecioEspecial())
                    .notas(servicioRequest.getNotas())
                    .esOpcional(servicioRequest.getEsOpcional())
                    .esPersonalizable(servicioRequest.getEsPersonalizable())
                    .build();
            
            servicioPlanRepository.save(servicioPlan);
            
            // Calcular precio total
            BigDecimal precioServicio = servicioRequest.getPrecioEspecial() != null 
                    ? servicioRequest.getPrecioEspecial() 
                    : servicio.getPrecio();
            
            if (!servicioRequest.getEsOpcional()) {
                precioTotal = precioTotal.add(precioServicio);
            }
        }
        
        plan.setPrecioTotal(precioTotal);
        PlanTuristico updatedPlan = planRepository.save(plan);
        return convertToResponse(updatedPlan);
    }
    
    public void deletePlan(Long id) {
        PlanTuristico plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan turístico", "id", id));
        
        // Verificar permisos
        Usuario usuario = getCurrentUser();
        if (!plan.getUsuarioCreador().getId().equals(usuario.getId()) && !hasRole("ROLE_ADMIN")) {
            throw new RuntimeException("No tiene permisos para eliminar este plan");
        }
        
        // Verificar que no tenga reservas activas
        boolean tieneReservasActivas = plan.getReservas().stream()
                .anyMatch(reserva -> reserva.getEstado() != Reserva.EstadoReserva.CANCELADA);
        
        if (tieneReservasActivas) {
            throw new RuntimeException("No se puede eliminar un plan con reservas activas");
        }
        
        planRepository.delete(plan);
    }
    
    public PlanTuristicoResponse cambiarEstado(Long id, PlanTuristico.EstadoPlan nuevoEstado) {
        PlanTuristico plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan turístico", "id", id));
        
        // Verificar permisos
        Usuario usuario = getCurrentUser();
        if (!plan.getUsuarioCreador().getId().equals(usuario.getId()) && !hasRole("ROLE_ADMIN")) {
            throw new RuntimeException("No tiene permisos para modificar este plan");
        }
        
        plan.setEstado(nuevoEstado);
        PlanTuristico updatedPlan = planRepository.save(plan);
        return convertToResponse(updatedPlan);
    }
    
    private PlanTuristicoResponse convertToResponse(PlanTuristico plan) {
        return PlanTuristicoResponse.builder()
                .id(plan.getId())
                .nombre(plan.getNombre())
                .descripcion(plan.getDescripcion())
                .precioTotal(plan.getPrecioTotal())
                .duracionDias(plan.getDuracionDias())
                .capacidadMaxima(plan.getCapacidadMaxima())
                .estado(plan.getEstado())
                .nivelDificultad(plan.getNivelDificultad())
                .imagenPrincipalUrl(plan.getImagenPrincipalUrl())
                .itinerario(plan.getItinerario())
                .incluye(plan.getIncluye())
                .noIncluye(plan.getNoIncluye())
                .recomendaciones(plan.getRecomendaciones())
                .requisitos(plan.getRequisitos())
                .fechaCreacion(plan.getFechaCreacion())
                .fechaActualizacion(plan.getFechaActualizacion())
                .municipalidad(convertToMunicipalidadBasicResponse(plan.getMunicipalidad()))
                .usuarioCreador(convertToUsuarioBasicResponse(plan.getUsuarioCreador()))
                .servicios(plan.getServicios().stream()
                        .map(this::convertToServicioPlanResponse)
                        .collect(Collectors.toList()))
                .totalReservas(plan.getReservas().size())
                .build();
    }
    
    private ServicioPlanResponse convertToServicioPlanResponse(ServicioPlan servicioPlan) {
        return ServicioPlanResponse.builder()
                .id(servicioPlan.getId())
                .diaDelPlan(servicioPlan.getDiaDelPlan())
                .ordenEnElDia(servicioPlan.getOrdenEnElDia())
                .horaInicio(servicioPlan.getHoraInicio())
                .horaFin(servicioPlan.getHoraFin())
                .precioEspecial(servicioPlan.getPrecioEspecial())
                .notas(servicioPlan.getNotas())
                .esOpcional(servicioPlan.getEsOpcional())
                .esPersonalizable(servicioPlan.getEsPersonalizable())
                .servicio(convertToServicioResponse(servicioPlan.getServicio()))
                .build();
    }
    
    private ServicioTuristicoResponse convertToServicioResponse(ServicioTuristico servicio) {
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
                .requisitos(servicio.getRequisitos())
                .incluye(servicio.getIncluye())
                .noIncluye(servicio.getNoIncluye())
                .imagenUrl(servicio.getImagenUrl())
                .build();
    }
    
    private MunicipalidadBasicResponse convertToMunicipalidadBasicResponse(Municipalidad municipalidad) {
        return MunicipalidadBasicResponse.builder()
                .id(municipalidad.getId())
                .nombre(municipalidad.getNombre())
                .departamento(municipalidad.getDepartamento())
                .provincia(municipalidad.getProvincia())
                .distrito(municipalidad.getDistrito())
                .build();
    }
    
    private UsuarioBasicResponse convertToUsuarioBasicResponse(Usuario usuario) {
        return UsuarioBasicResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .build();
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