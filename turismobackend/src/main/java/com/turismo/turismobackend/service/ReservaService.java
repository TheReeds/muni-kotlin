package com.turismo.turismobackend.service;

import com.turismo.turismobackend.dto.request.ReservaRequest;
import com.turismo.turismobackend.dto.response.*;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService {
    
    private final ReservaRepository reservaRepository;
    private final PlanTuristicoRepository planRepository;
    private final ServicioPlanRepository servicioPlanRepository;
    private final ReservaServicioRepository reservaServicioRepository;
    
    public List<ReservaResponse> getAllReservas() {
        // Solo admin puede ver todas las reservas
        if (!hasRole("ROLE_ADMIN")) {
            throw new RuntimeException("No tiene permisos para ver todas las reservas");
        }
        return reservaRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public ReservaResponse getReservaById(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", id));
        
        // Verificar permisos
        Usuario usuario = getCurrentUser();
        if (!reserva.getUsuario().getId().equals(usuario.getId()) && 
            !hasRole("ROLE_ADMIN") && 
            !esPropietarioDelPlan(reserva.getPlan(), usuario)) {
            throw new RuntimeException("No tiene permisos para ver esta reserva");
        }
        
        return convertToResponse(reserva);
    }
    
    public ReservaResponse getReservaByCodigo(String codigoReserva) {
        Reserva reserva = reservaRepository.findByCodigoReserva(codigoReserva)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "codigo", codigoReserva));
        
        // Verificar permisos
        Usuario usuario = getCurrentUser();
        if (!reserva.getUsuario().getId().equals(usuario.getId()) && 
            !hasRole("ROLE_ADMIN") && 
            !esPropietarioDelPlan(reserva.getPlan(), usuario)) {
            throw new RuntimeException("No tiene permisos para ver esta reserva");
        }
        
        return convertToResponse(reserva);
    }
    
    public List<ReservaResponse> getMisReservas() {
        Usuario usuario = getCurrentUser();
        return reservaRepository.findByUsuarioIdOrderByFechaReservaDesc(usuario.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<ReservaResponse> getReservasByPlan(Long planId) {
        // Verificar que el usuario es propietario del plan o admin
        PlanTuristico plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", planId));
        
        Usuario usuario = getCurrentUser();
        if (!esPropietarioDelPlan(plan, usuario) && !hasRole("ROLE_ADMIN")) {
            throw new RuntimeException("No tiene permisos para ver las reservas de este plan");
        }
        
        return reservaRepository.findByPlanId(planId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<ReservaResponse> getReservasByMunicipalidad(Long municipalidadId) {
        // Verificar que el usuario pertenece a la municipalidad o es admin
        Usuario usuario = getCurrentUser();
        if (!hasRole("ROLE_ADMIN") && !perteneceAMunicipalidad(usuario, municipalidadId)) {
            throw new RuntimeException("No tiene permisos para ver las reservas de esta municipalidad");
        }
        
        return reservaRepository.findByMunicipalidadId(municipalidadId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public ReservaResponse createReserva(ReservaRequest request) {
        Usuario usuario = getCurrentUser();
        
        PlanTuristico plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", request.getPlanId()));
        
        // Verificar que el plan está activo
        if (plan.getEstado() != PlanTuristico.EstadoPlan.ACTIVO) {
            throw new RuntimeException("El plan no está disponible para reservas");
        }
        
        // Verificar disponibilidad
        LocalDate fechaFin = request.getFechaInicio().plusDays(plan.getDuracionDias() - 1);
        verificarDisponibilidad(plan, request.getFechaInicio(), request.getNumeroPersonas());
        
        // Calcular precio total
        BigDecimal montoTotal = calcularMontoTotal(plan, request);
        
        Reserva reserva = Reserva.builder()
                .plan(plan)
                .usuario(usuario)
                .fechaInicio(request.getFechaInicio())
                .fechaFin(fechaFin)
                .numeroPersonas(request.getNumeroPersonas())
                .montoTotal(montoTotal)
                .montoDescuento(BigDecimal.ZERO)
                .montoFinal(montoTotal)
                .estado(Reserva.EstadoReserva.PENDIENTE)
                .metodoPago(request.getMetodoPago())
                .observaciones(request.getObservaciones())
                .solicitudesEspeciales(request.getSolicitudesEspeciales())
                .contactoEmergencia(request.getContactoEmergencia())
                .telefonoEmergencia(request.getTelefonoEmergencia())
                .build();
        
        Reserva savedReserva = reservaRepository.save(reserva);
        
        // Crear servicios personalizados si se especificaron
        if (request.getServiciosPersonalizados() != null) {
            for (var servicioRequest : request.getServiciosPersonalizados()) {
                ServicioPlan servicioPlan = servicioPlanRepository.findById(servicioRequest.getServicioPlanId())
                        .orElseThrow(() -> new ResourceNotFoundException("ServicioPlan", "id", servicioRequest.getServicioPlanId()));
                
                ReservaServicio reservaServicio = ReservaServicio.builder()
                        .reserva(savedReserva)
                        .servicioPlan(servicioPlan)
                        .incluido(servicioRequest.getIncluido())
                        .precioPersonalizado(servicioRequest.getPrecioPersonalizado())
                        .observaciones(servicioRequest.getObservaciones())
                        .estado(servicioRequest.getEstado())
                        .build();
                
                reservaServicioRepository.save(reservaServicio);
            }
        }
        
        return convertToResponse(savedReserva);
    }
    
    public ReservaResponse confirmarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", id));
        
        // Solo el propietario del plan o admin puede confirmar
        Usuario usuario = getCurrentUser();
        if (!esPropietarioDelPlan(reserva.getPlan(), usuario) && !hasRole("ROLE_ADMIN")) {
            throw new RuntimeException("No tiene permisos para confirmar esta reserva");
        }
        
        if (reserva.getEstado() != Reserva.EstadoReserva.PENDIENTE) {
            throw new RuntimeException("Solo se pueden confirmar reservas pendientes");
        }
        
        reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);
        reserva.setFechaConfirmacion(LocalDateTime.now());
        
        Reserva updatedReserva = reservaRepository.save(reserva);
        return convertToResponse(updatedReserva);
    }
    
    public ReservaResponse cancelarReserva(Long id, String motivo) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", id));
        
        // El usuario puede cancelar su propia reserva o admin/propietario pueden cancelar cualquiera
        Usuario usuario = getCurrentUser();
        if (!reserva.getUsuario().getId().equals(usuario.getId()) && 
            !esPropietarioDelPlan(reserva.getPlan(), usuario) && 
            !hasRole("ROLE_ADMIN")) {
            throw new RuntimeException("No tiene permisos para cancelar esta reserva");
        }
        
        if (reserva.getEstado() == Reserva.EstadoReserva.CANCELADA || 
            reserva.getEstado() == Reserva.EstadoReserva.COMPLETADA) {
            throw new RuntimeException("No se puede cancelar una reserva en estado " + reserva.getEstado());
        }
        
        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        reserva.setFechaCancelacion(LocalDateTime.now());
        reserva.setMotivoCancelacion(motivo);
        
        Reserva updatedReserva = reservaRepository.save(reserva);
        return convertToResponse(updatedReserva);
    }
    
    public ReservaResponse completarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", id));
        
        // Solo el propietario del plan o admin puede completar
        Usuario usuario = getCurrentUser();
        if (!esPropietarioDelPlan(reserva.getPlan(), usuario) && !hasRole("ROLE_ADMIN")) {
            throw new RuntimeException("No tiene permisos para completar esta reserva");
        }
        
        if (reserva.getEstado() != Reserva.EstadoReserva.EN_PROCESO) {
            throw new RuntimeException("Solo se pueden completar reservas en proceso");
        }
        
        reserva.setEstado(Reserva.EstadoReserva.COMPLETADA);
        
        Reserva updatedReserva = reservaRepository.save(reserva);
        return convertToResponse(updatedReserva);
    }
    
    private void verificarDisponibilidad(PlanTuristico plan, LocalDate fecha, Integer numeroPersonas) {
        Long personasReservadas = reservaRepository.countPersonasByPlanAndDate(plan.getId(), fecha);
        if (personasReservadas == null) personasReservadas = 0L;
        
        if (personasReservadas + numeroPersonas > plan.getCapacidadMaxima()) {
            throw new RuntimeException("No hay suficiente capacidad disponible para la fecha seleccionada");
        }
    }
    
    private BigDecimal calcularMontoTotal(PlanTuristico plan, ReservaRequest request) {
        BigDecimal total = plan.getPrecioTotal().multiply(BigDecimal.valueOf(request.getNumeroPersonas()));
        
        // Aquí se podrían aplicar descuentos o recargos adicionales
        
        return total;
    }
    
    private ReservaResponse convertToResponse(Reserva reserva) {
        return ReservaResponse.builder()
                .id(reserva.getId())
                .codigoReserva(reserva.getCodigoReserva())
                .fechaInicio(reserva.getFechaInicio())
                .fechaFin(reserva.getFechaFin())
                .numeroPersonas(reserva.getNumeroPersonas())
                .montoTotal(reserva.getMontoTotal())
                .montoDescuento(reserva.getMontoDescuento())
                .montoFinal(reserva.getMontoFinal())
                .estado(reserva.getEstado())
                .metodoPago(reserva.getMetodoPago())
                .observaciones(reserva.getObservaciones())
                .solicitudesEspeciales(reserva.getSolicitudesEspeciales())
                .contactoEmergencia(reserva.getContactoEmergencia())
                .telefonoEmergencia(reserva.getTelefonoEmergencia())
                .fechaReserva(reserva.getFechaReserva())
                .fechaConfirmacion(reserva.getFechaConfirmacion())
                .fechaCancelacion(reserva.getFechaCancelacion())
                .motivoCancelacion(reserva.getMotivoCancelacion())
                .plan(convertToPlanBasicResponse(reserva.getPlan()))
                .usuario(convertToUsuarioBasicResponse(reserva.getUsuario()))
                .build();
    }
    
    private PlanTuristicoBasicResponse convertToPlanBasicResponse(PlanTuristico plan) {
        return PlanTuristicoBasicResponse.builder()
                .id(plan.getId())
                .nombre(plan.getNombre())
                .descripcion(plan.getDescripcion())
                .precioTotal(plan.getPrecioTotal())
                .duracionDias(plan.getDuracionDias())
                .capacidadMaxima(plan.getCapacidadMaxima())
                .estado(plan.getEstado())
                .nivelDificultad(plan.getNivelDificultad())
                .imagenPrincipalUrl(plan.getImagenPrincipalUrl())
                .municipalidad(MunicipalidadBasicResponse.builder()
                        .id(plan.getMunicipalidad().getId())
                        .nombre(plan.getMunicipalidad().getNombre())
                        .departamento(plan.getMunicipalidad().getDepartamento())
                        .provincia(plan.getMunicipalidad().getProvincia())
                        .distrito(plan.getMunicipalidad().getDistrito())
                        .build())
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
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    private boolean hasRole(String role) {
        return getCurrentUser().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
    
    private boolean esPropietarioDelPlan(PlanTuristico plan, Usuario usuario) {
        return plan.getUsuarioCreador().getId().equals(usuario.getId()) ||
               (plan.getMunicipalidad().getUsuario() != null && 
                plan.getMunicipalidad().getUsuario().getId().equals(usuario.getId()));
    }
    
    private boolean perteneceAMunicipalidad(Usuario usuario, Long municipalidadId) {
        // Implementar lógica para verificar si el usuario pertenece a la municipalidad
        return false; // Placeholder
    }
}