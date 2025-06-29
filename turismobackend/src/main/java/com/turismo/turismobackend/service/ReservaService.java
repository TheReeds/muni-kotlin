package com.turismo.turismobackend.service;

import com.turismo.turismobackend.dto.request.ReservaRequest;
import com.turismo.turismobackend.dto.response.*;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.dto.request.ReservaServicioRequest;
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
                .orElseThrow(() -> new ResourceNotFoundException("Plan turístico", "id", request.getPlanId()));
        
        // VALIDACIONES MEJORADAS
        
        // 1. Verificar que el plan esté activo
        if (plan.getEstado() != PlanTuristico.EstadoPlan.ACTIVO) {
            throw new RuntimeException("El plan turístico no está disponible para reservas");
        }
        
        // 2. Validar fechas
        LocalDate fechaFin = request.getFechaInicio().plusDays(plan.getDuracionDias() - 1);
        
        // 3. Verificar disponibilidad de capacidad
        Long personasReservadas = reservaRepository.countPersonasByPlanAndDate(
                request.getPlanId(), request.getFechaInicio());
        
        if (personasReservadas != null && 
            personasReservadas + request.getNumeroPersonas() > plan.getCapacidadMaxima()) {
            throw new RuntimeException("No hay suficiente capacidad disponible para las fechas seleccionadas");
        }
        
        // 4. Calcular montos con descuentos aplicables
        BigDecimal montoTotal = plan.getPrecioTotal().multiply(BigDecimal.valueOf(request.getNumeroPersonas()));
        BigDecimal montoDescuento = calcularDescuentos(usuario, plan, request.getNumeroPersonas());
        BigDecimal montoFinal = montoTotal.subtract(montoDescuento);
        
        // Crear reserva
        Reserva reserva = Reserva.builder()
                .plan(plan)
                .usuario(usuario)
                .fechaInicio(request.getFechaInicio())
                .fechaFin(fechaFin)
                .numeroPersonas(request.getNumeroPersonas())
                .montoTotal(montoTotal)
                .montoDescuento(montoDescuento)
                .montoFinal(montoFinal)
                .estado(Reserva.EstadoReserva.PENDIENTE)
                .metodoPago(request.getMetodoPago())
                .observaciones(request.getObservaciones())
                .solicitudesEspeciales(request.getSolicitudesEspeciales())
                .contactoEmergencia(request.getContactoEmergencia())
                .telefonoEmergencia(request.getTelefonoEmergencia())
                .build();
        
        Reserva savedReserva = reservaRepository.save(reserva);
        
        // Crear servicios personalizados si se proporcionan
        if (request.getServiciosPersonalizados() != null) {
            for (ReservaServicioRequest servicioRequest : request.getServiciosPersonalizados()) {
                ServicioPlan servicioPlan = servicioPlanRepository.findById(servicioRequest.getServicioPlanId())
                        .orElseThrow(() -> new ResourceNotFoundException("Servicio del plan", "id", servicioRequest.getServicioPlanId()));
                
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
        
        return convertToReservaResponse(savedReserva);
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

    // NUEVO MÉTODO para calcular descuentos
    private BigDecimal calcularDescuentos(Usuario usuario, PlanTuristico plan, Integer numeroPersonas) {
        BigDecimal descuento = BigDecimal.ZERO;
        
        // Descuento por grupo grande (más de 5 personas)
        if (numeroPersonas >= 5) {
            descuento = descuento.add(plan.getPrecioTotal().multiply(BigDecimal.valueOf(0.10))); // 10% descuento
        }
        
        // Descuento por usuario frecuente (más de 3 reservas completadas)
        Long reservasCompletadas = reservaRepository.countReservasCompletadasByUsuario(usuario.getId());
        if (reservasCompletadas != null && reservasCompletadas >= 3) {
            descuento = descuento.add(plan.getPrecioTotal().multiply(BigDecimal.valueOf(0.05))); // 5% descuento
        }
        
        return descuento;
    }
    
    private boolean esPropietarioDelPlan(PlanTuristico plan, Usuario usuario) {
        return plan.getUsuarioCreador().getId().equals(usuario.getId()) ||
               (plan.getMunicipalidad().getUsuario() != null && 
                plan.getMunicipalidad().getUsuario().getId().equals(usuario.getId()));
    }

    private ReservaResponse convertToReservaResponse(Reserva reserva) {
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
                .plan(PlanTuristicoBasicResponse.builder()
                        .id(reserva.getPlan().getId())
                        .nombre(reserva.getPlan().getNombre())
                        .descripcion(reserva.getPlan().getDescripcion())
                        .precioTotal(reserva.getPlan().getPrecioTotal())
                        .duracionDias(reserva.getPlan().getDuracionDias())
                        .capacidadMaxima(reserva.getPlan().getCapacidadMaxima())
                        .estado(reserva.getPlan().getEstado())
                        .nivelDificultad(reserva.getPlan().getNivelDificultad())
                        .imagenPrincipalUrl(reserva.getPlan().getImagenPrincipalUrl())
                        .build())
                .usuario(UsuarioBasicResponse.builder()
                        .id(reserva.getUsuario().getId())
                        .nombre(reserva.getUsuario().getNombre())
                        .apellido(reserva.getUsuario().getApellido())
                        .username(reserva.getUsuario().getUsername())
                        .email(reserva.getUsuario().getEmail())
                        .build())
                .serviciosPersonalizados(reserva.getServiciosPersonalizados().stream()
                        .map(this::convertToReservaServicioResponse)
                        .collect(Collectors.toList()))
                .pagos(reserva.getPagos().stream()
                        .map(this::convertToPagoResponseFromPago)  // USAR TU MODELO EXISTENTE
                        .collect(Collectors.toList()))
                .build();
    }
    // MÉTODO PARA CONVERTIR TU MODELO PAGO EXISTENTE
    private PagoResponse convertToPagoResponseFromPago(Pago pago) {
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
    // MÉTODOS AUXILIARES FALTANTES
    private ReservaServicioResponse convertToReservaServicioResponse(ReservaServicio reservaServicio) {
        return ReservaServicioResponse.builder()
                .id(reservaServicio.getId())
                .incluido(reservaServicio.getIncluido())
                .precioPersonalizado(reservaServicio.getPrecioPersonalizado())
                .observaciones(reservaServicio.getObservaciones())
                .estado(reservaServicio.getEstado())
                .servicioPlan(convertToServicioPlanResponse(reservaServicio.getServicioPlan()))
                .build();
    }

    private PagoCarritoResponse convertToPagoCarritoResponse(PagoCarrito pago) {
        return PagoCarritoResponse.builder()
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
                // Si necesitas incluir datos de la reserva asociada:
                //.reservaId(pago.getReservaCarrito().getId())
                .build();
    }

    
    private boolean perteneceAMunicipalidad(Usuario usuario, Long municipalidadId) {
        // Implementar lógica para verificar si el usuario pertenece a la municipalidad
        return false; // Placeholder
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
                .servicio(ServicioTuristicoResponse.builder()
                        .id(servicioPlan.getServicio().getId())
                        .nombre(servicioPlan.getServicio().getNombre())
                        .descripcion(servicioPlan.getServicio().getDescripcion())
                        .precio(servicioPlan.getServicio().getPrecio())
                        .duracionHoras(servicioPlan.getServicio().getDuracionHoras())
                        .capacidadMaxima(servicioPlan.getServicio().getCapacidadMaxima())
                        .tipo(servicioPlan.getServicio().getTipo())
                        .estado(servicioPlan.getServicio().getEstado())
                        .ubicacion(servicioPlan.getServicio().getUbicacion())
                        .latitud(servicioPlan.getServicio().getLatitud())
                        .longitud(servicioPlan.getServicio().getLongitud())
                        .requisitos(servicioPlan.getServicio().getRequisitos())
                        .incluye(servicioPlan.getServicio().getIncluye())
                        .noIncluye(servicioPlan.getServicio().getNoIncluye())
                        .imagenUrl(servicioPlan.getServicio().getImagenUrl())
                        .build())
                .build();
    }
}