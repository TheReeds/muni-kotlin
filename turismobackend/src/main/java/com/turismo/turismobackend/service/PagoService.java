package com.turismo.turismobackend.service;

import com.turismo.turismobackend.dto.request.PagoRequest;
import com.turismo.turismobackend.dto.response.PagoResponse;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.Pago;
import com.turismo.turismobackend.model.Reserva;
import com.turismo.turismobackend.model.Usuario;
import com.turismo.turismobackend.repository.PagoRepository;
import com.turismo.turismobackend.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PagoService {
    
    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    
    public List<PagoResponse> getAllPagos() {
        // Solo admin puede ver todos los pagos
        if (!hasRole("ROLE_ADMIN")) {
            throw new RuntimeException("No tiene permisos para ver todos los pagos");
        }
        return pagoRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public PagoResponse getPagoById(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", "id", id));
        
        // Verificar permisos
        Usuario usuario = getCurrentUser();
        if (!pago.getReserva().getUsuario().getId().equals(usuario.getId()) && 
            !hasRole("ROLE_ADMIN") && 
            !esPropietarioDelPlan(pago.getReserva(), usuario)) {
            throw new RuntimeException("No tiene permisos para ver este pago");
        }
        
        return convertToResponse(pago);
    }
    
    public PagoResponse getPagoByCodigo(String codigoPago) {
        Pago pago = pagoRepository.findByCodigoPago(codigoPago)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", "codigo", codigoPago));
        
        // Verificar permisos
        Usuario usuario = getCurrentUser();
        if (!pago.getReserva().getUsuario().getId().equals(usuario.getId()) && 
            !hasRole("ROLE_ADMIN") && 
            !esPropietarioDelPlan(pago.getReserva(), usuario)) {
            throw new RuntimeException("No tiene permisos para ver este pago");
        }
        
        return convertToResponse(pago);
    }
    
    public List<PagoResponse> getPagosByReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + reservaId));
        
        // Verificar permisos
        Usuario usuario = getCurrentUser();
        if (!reserva.getUsuario().getId().equals(usuario.getId()) && 
            !hasRole("ROLE_ADMIN") && 
            !esPropietarioDelPlan(reserva, usuario)) {
            throw new RuntimeException("No tiene permisos para ver los pagos de esta reserva");
        }
        
        return pagoRepository.findByReservaId(reservaId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<PagoResponse> getMisPagos() {
        Usuario usuario = getCurrentUser();
        return pagoRepository.findByUsuarioId(usuario.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<PagoResponse> getPagosByMunicipalidad(Long municipalidadId) {
        // Verificar que el usuario pertenece a la municipalidad o es admin
        Usuario usuario = getCurrentUser();
        if (!hasRole("ROLE_ADMIN") && !perteneceAMunicipalidad(usuario, municipalidadId)) {
            throw new RuntimeException("No tiene permisos para ver los pagos de esta municipalidad");
        }
        
        return pagoRepository.findByMunicipalidadId(municipalidadId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public PagoResponse registrarPago(PagoRequest request) {
        Reserva reserva = reservaRepository.findById(request.getReservaId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + request.getReservaId()));
        
        // Verificar que el usuario puede registrar pagos para esta reserva
        Usuario usuario = getCurrentUser();
        if (!reserva.getUsuario().getId().equals(usuario.getId()) && 
            !hasRole("ROLE_ADMIN") && 
            !esPropietarioDelPlan(reserva, usuario)) {
            throw new RuntimeException("No tiene permisos para registrar pagos para esta reserva");
        }
        
        // Verificar que la reserva está en estado adecuado para recibir pagos
        if (reserva.getEstado() == Reserva.EstadoReserva.CANCELADA) {
            throw new RuntimeException("No se pueden registrar pagos para una reserva cancelada");
        }
        
        Pago pago = Pago.builder()
                .reserva(reserva)
                .monto(request.getMonto())
                .tipo(request.getTipo())
                .estado(Pago.EstadoPago.PENDIENTE)
                .metodoPago(request.getMetodoPago())
                .numeroTransaccion(request.getNumeroTransaccion())
                .numeroAutorizacion(request.getNumeroAutorizacion())
                .observaciones(request.getObservaciones())
                .build();
        
        Pago savedPago = pagoRepository.save(pago);
        
        // Actualizar estado de reserva si corresponde
        actualizarEstadoReservaPorPago(reserva);
        
        return convertToResponse(savedPago);
    }
    
    public PagoResponse confirmarPago(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", "id", id));
        
        // Solo admin o propietario del plan pueden confirmar pagos
        Usuario usuario = getCurrentUser();
        if (!hasRole("ROLE_ADMIN") && !esPropietarioDelPlan(pago.getReserva(), usuario)) {
            throw new RuntimeException("No tiene permisos para confirmar este pago");
        }
        
        if (pago.getEstado() != Pago.EstadoPago.PENDIENTE && pago.getEstado() != Pago.EstadoPago.PROCESANDO) {
            throw new RuntimeException("Solo se pueden confirmar pagos pendientes o en procesamiento");
        }
        
        pago.setEstado(Pago.EstadoPago.CONFIRMADO);
        pago.setFechaConfirmacion(LocalDateTime.now());
        
        Pago updatedPago = pagoRepository.save(pago);
        
        // Actualizar estado de reserva
        actualizarEstadoReservaPorPago(pago.getReserva());
        
        return convertToResponse(updatedPago);
    }
    
    public PagoResponse rechazarPago(Long id, String motivo) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", "id", id));
        
        // Solo admin o propietario del plan pueden rechazar pagos
        Usuario usuario = getCurrentUser();
        if (!hasRole("ROLE_ADMIN") && !esPropietarioDelPlan(pago.getReserva(), usuario)) {
            throw new RuntimeException("No tiene permisos para rechazar este pago");
        }
        
        if (pago.getEstado() != Pago.EstadoPago.PENDIENTE && pago.getEstado() != Pago.EstadoPago.PROCESANDO) {
            throw new RuntimeException("Solo se pueden rechazar pagos pendientes o en procesamiento");
        }
        
        pago.setEstado(Pago.EstadoPago.FALLIDO);
        pago.setObservaciones(pago.getObservaciones() + " | Motivo rechazo: " + motivo);
        
        Pago updatedPago = pagoRepository.save(pago);
        return convertToResponse(updatedPago);
    }
    
    private void actualizarEstadoReservaPorPago(Reserva reserva) {
        Long totalPagado = pagoRepository.getTotalPagadoByReserva(reserva.getId());
        if (totalPagado == null) totalPagado = 0L;
        
        if (totalPagado >= reserva.getMontoFinal().longValue()) {
            reserva.setEstado(Reserva.EstadoReserva.PAGADA);
        } else if (totalPagado > 0) {
            // Tiene pagos parciales, mantener confirmada si ya lo estaba
            if (reserva.getEstado() == Reserva.EstadoReserva.PENDIENTE) {
                reserva.setEstado(Reserva.EstadoReserva.CONFIRMADA);
            }
        }
        
        reservaRepository.save(reserva);
    }
    
    private PagoResponse convertToResponse(Pago pago) {
        return PagoResponse.builder()
                .id(pago.getId())
                .codigoPago(pago.getCodigoPago())
                .monto(pago.getMonto())
                .tipo(pago.getTipo())
                .estado(pago.getEstado())
                .metodoPago(pago.getMetodoPago())
                .numeroTransaccion(pago.getNumeroTransaccion())
                .numeroAutorizacion(pago.getNumeroAutorizacion())
                .observaciones(pago.getObservaciones())
                .fechaPago(pago.getFechaPago())
                .fechaConfirmacion(pago.getFechaConfirmacion())
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
    
    private boolean esPropietarioDelPlan(Reserva reserva, Usuario usuario) {
        return reserva.getPlan().getUsuarioCreador().getId().equals(usuario.getId()) ||
               (reserva.getPlan().getMunicipalidad().getUsuario() != null && 
                reserva.getPlan().getMunicipalidad().getUsuario().getId().equals(usuario.getId()));
    }
    
    private boolean perteneceAMunicipalidad(Usuario usuario, Long municipalidadId) {
        // Implementar lógica para verificar si el usuario pertenece a la municipalidad
        return false; // Placeholder
    }
}