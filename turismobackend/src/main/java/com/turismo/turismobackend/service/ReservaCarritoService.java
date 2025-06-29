package com.turismo.turismobackend.service;

import com.turismo.turismobackend.dto.request.ReservaCarritoRequest;
import com.turismo.turismobackend.dto.response.*;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.turismo.turismobackend.service.ChatService;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaCarritoService {
    
    private final ReservaCarritoRepository reservaCarritoRepository;
    private final ReservaCarritoItemRepository reservaCarritoItemRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final EmprendedorRepository emprendedorRepository;
    private final ChatService chatService;
    private final ChatConversacionRepository conversacionRepository;
    
    public ReservaCarritoResponse crearReservaDesdeCarrito(ReservaCarritoRequest request) {
        Usuario usuario = getCurrentUser();
        
        // Obtener carrito del usuario
        Carrito carrito = carritoRepository.findByUsuarioIdWithItems(usuario.getId())
                .orElseThrow(() -> new RuntimeException("No hay carrito para este usuario"));
        
        if (carrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }
        
        // Validar disponibilidad de servicios
        for (CarritoItem item : carrito.getItems()) {
            if (item.getServicio().getEstado() != ServicioTuristico.EstadoServicio.ACTIVO) {
                throw new RuntimeException("El servicio " + item.getServicio().getNombre() + " no está disponible");
            }
            
            // Verificar capacidad
            Long personasReservadas = reservaCarritoItemRepository
                    .countPersonasByServicioAndFecha(item.getServicio().getId(), item.getFechaServicio());
            
            if (personasReservadas != null && 
                personasReservadas + item.getCantidad() > item.getServicio().getCapacidadMaxima()) {
                throw new RuntimeException("No hay suficiente capacidad para el servicio " + 
                        item.getServicio().getNombre() + " en la fecha seleccionada");
            }
        }
        
        // Crear reserva
        BigDecimal montoTotal = carrito.getTotalCarrito();
        BigDecimal montoDescuento = BigDecimal.ZERO; // Aquí podrías aplicar descuentos
        BigDecimal montoFinal = montoTotal.subtract(montoDescuento);
        
        ReservaCarrito reserva = ReservaCarrito.builder()
                .usuario(usuario)
                .montoTotal(montoTotal)
                .montoDescuento(montoDescuento)
                .montoFinal(montoFinal)
                .estado(ReservaCarrito.EstadoReservaCarrito.PENDIENTE)
                .metodoPago(request.getMetodoPago())
                .observaciones(request.getObservaciones())
                .contactoEmergencia(request.getContactoEmergencia())
                .telefonoEmergencia(request.getTelefonoEmergencia())
                .build();
        
        ReservaCarrito savedReserva = reservaCarritoRepository.save(reserva);
        
        // Crear items de la reserva desde el carrito
        for (CarritoItem carritoItem : carrito.getItems()) {
            ReservaCarritoItem reservaItem = ReservaCarritoItem.builder()
                    .reservaCarrito(savedReserva)
                    .servicio(carritoItem.getServicio())
                    .cantidad(carritoItem.getCantidad())
                    .precioUnitario(carritoItem.getPrecioUnitario())
                    .fechaServicio(carritoItem.getFechaServicio())
                    .notasEspeciales(carritoItem.getNotasEspeciales())
                    .estado(ReservaCarritoItem.EstadoItemReserva.PENDIENTE)
                    .build();
            
            reservaCarritoItemRepository.save(reservaItem);
        }
        
        // Limpiar carrito
        carritoItemRepository.deleteByCarritoId(carrito.getId());

        Set<Long> emprendedoresIds = new HashSet<>();
        for (CarritoItem carritoItem : carrito.getItems()) {
            emprendedoresIds.add(carritoItem.getServicio().getEmprendedor().getId());
        }
        // Iniciar conversación con cada emprendedor único
        for (Long emprendedorId : emprendedoresIds) {
            try {
                chatService.iniciarConversacionConReservaCarrito(emprendedorId, savedReserva.getId());
            } catch (Exception e) {
                // Log error pero no fallar la reserva
                System.err.println("Error al crear conversación con emprendedor " + emprendedorId + ": " + e.getMessage());
            }
        }
        
        return convertToReservaCarritoResponse(savedReserva);
    }
    
    public List<ReservaCarritoResponse> obtenerMisReservas() {
        Usuario usuario = getCurrentUser();
        
        List<ReservaCarrito> reservas = reservaCarritoRepository
                .findByUsuarioIdOrderByFechaReservaDesc(usuario.getId());
        
        return reservas.stream()
                .map(this::convertToReservaCarritoResponse)
                .collect(Collectors.toList());
    }
    
    public ReservaCarritoResponse obtenerReservaPorId(Long id) {
        ReservaCarrito reserva = reservaCarritoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", id));
        
        // Verificar permisos
        Usuario usuario = getCurrentUser();
        if (!reserva.getUsuario().getId().equals(usuario.getId()) && !hasRole("ROLE_ADMIN")) {
            throw new RuntimeException("No tiene permisos para ver esta reserva");
        }
        
        return convertToReservaCarritoResponse(reserva);
    }
    
    public ReservaCarritoResponse obtenerReservaPorCodigo(String codigo) {
        ReservaCarrito reserva = reservaCarritoRepository.findByCodigoReserva(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "codigo", codigo));
        
        return convertToReservaCarritoResponse(reserva);
    }
    
    public ReservaCarritoResponse confirmarReserva(Long id) {
        ReservaCarrito reserva = reservaCarritoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", id));
        
        if (reserva.getEstado() != ReservaCarrito.EstadoReservaCarrito.PENDIENTE) {
            throw new RuntimeException("Solo se pueden confirmar reservas pendientes");
        }
        
        reserva.setEstado(ReservaCarrito.EstadoReservaCarrito.CONFIRMADA);
        reserva.setFechaConfirmacion(LocalDateTime.now());
        
        // Confirmar items
        for (ReservaCarritoItem item : reserva.getItems()) {
            item.setEstado(ReservaCarritoItem.EstadoItemReserva.CONFIRMADO);
            reservaCarritoItemRepository.save(item);
        }
        
        return convertToReservaCarritoResponse(reservaCarritoRepository.save(reserva));
    }
    
    public ReservaCarritoResponse cancelarReserva(Long id, String motivo) {
        ReservaCarrito reserva = reservaCarritoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", id));
        
        // Verificar permisos
        Usuario usuario = getCurrentUser();
        if (!reserva.getUsuario().getId().equals(usuario.getId()) && !hasRole("ROLE_ADMIN")) {
            throw new RuntimeException("No tiene permisos para cancelar esta reserva");
        }
        
        if (reserva.getEstado() == ReservaCarrito.EstadoReservaCarrito.COMPLETADA) {
            throw new RuntimeException("No se puede cancelar una reserva completada");
        }
        
        reserva.setEstado(ReservaCarrito.EstadoReservaCarrito.CANCELADA);
        reserva.setFechaCancelacion(LocalDateTime.now());
        reserva.setMotivoCancelacion(motivo);
        
        // Cancelar items
        for (ReservaCarritoItem item : reserva.getItems()) {
            item.setEstado(ReservaCarritoItem.EstadoItemReserva.CANCELADO);
            reservaCarritoItemRepository.save(item);
        }
        
        return convertToReservaCarritoResponse(reservaCarritoRepository.save(reserva));
    }

    
    private ReservaCarritoResponse convertToReservaCarritoResponse(ReservaCarrito reserva) {
        List<ReservaCarritoItemResponse> items = reserva.getItems().stream()
                .map(this::convertToReservaCarritoItemResponse)
                .collect(Collectors.toList());
        
        List<PagoCarritoResponse> pagos = reserva.getPagosCarrito().stream()
                .map(this::convertToPagoCarritoResponse)
                .collect(Collectors.toList());
        
        return ReservaCarritoResponse.builder()
                .id(reserva.getId())
                .codigoReserva(reserva.getCodigoReserva())
                .montoTotal(reserva.getMontoTotal())
                .montoDescuento(reserva.getMontoDescuento())
                .montoFinal(reserva.getMontoFinal())
                .estado(reserva.getEstado())
                .metodoPago(reserva.getMetodoPago())
                .observaciones(reserva.getObservaciones())
                .contactoEmergencia(reserva.getContactoEmergencia())
                .telefonoEmergencia(reserva.getTelefonoEmergencia())
                .fechaReserva(reserva.getFechaReserva())
                .fechaConfirmacion(reserva.getFechaConfirmacion())
                .fechaCancelacion(reserva.getFechaCancelacion())
                .motivoCancelacion(reserva.getMotivoCancelacion())
                .usuario(UsuarioBasicResponse.builder()
                        .id(reserva.getUsuario().getId())
                        .nombre(reserva.getUsuario().getNombre())
                        .apellido(reserva.getUsuario().getApellido())
                        .username(reserva.getUsuario().getUsername())
                        .email(reserva.getUsuario().getEmail())
                        .build())
                .items(items)
                .pagos(pagos)
                .build();
    }

    // MÉTODO PARA CONVERTIR PAGO CARRITO
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
                .build();
    }

    private ReservaCarritoItemResponse convertToReservaCarritoItemResponse(ReservaCarritoItem item) {
        return ReservaCarritoItemResponse.builder()
                .id(item.getId())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.getSubtotal())
                .fechaServicio(item.getFechaServicio())
                .notasEspeciales(item.getNotasEspeciales())
                .estado(item.getEstado())
                .servicio(ServicioTuristicoBasicResponse.builder()
                        .id(item.getServicio().getId())
                        .nombre(item.getServicio().getNombre())
                        .descripcion(item.getServicio().getDescripcion())
                        .precio(item.getServicio().getPrecio())
                        .duracionHoras(item.getServicio().getDuracionHoras())
                        .tipo(item.getServicio().getTipo())
                        .imagenUrl(item.getServicio().getImagenUrl())
                        .build())
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
    public ReservaCarritoResponse completarReserva(Long id) {
        ReservaCarrito reserva = reservaCarritoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", id));
        
        if (reserva.getEstado() != ReservaCarrito.EstadoReservaCarrito.EN_PROCESO &&
            reserva.getEstado() != ReservaCarrito.EstadoReservaCarrito.CONFIRMADA) {
            throw new RuntimeException("Solo se pueden completar reservas confirmadas o en proceso");
        }
        
        reserva.setEstado(ReservaCarrito.EstadoReservaCarrito.COMPLETADA);
        
        // Completar todos los items
        for (ReservaCarritoItem item : reserva.getItems()) {
            item.setEstado(ReservaCarritoItem.EstadoItemReserva.COMPLETADO);
            reservaCarritoItemRepository.save(item);
        }
        
        return convertToReservaCarritoResponse(reservaCarritoRepository.save(reserva));
    }

    public List<ReservaCarritoResponse> obtenerReservasPorEstado(String estadoStr) {
        Usuario usuario = getCurrentUser();
        
        try {
            ReservaCarrito.EstadoReservaCarrito estado = 
                    ReservaCarrito.EstadoReservaCarrito.valueOf(estadoStr.toUpperCase());
            
            List<ReservaCarrito> reservas = reservaCarritoRepository
                    .findByUsuarioIdAndEstado(usuario.getId(), estado);
            
            return reservas.stream()
                    .map(this::convertToReservaCarritoResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado de reserva no válido: " + estadoStr);
        }
    }

    public List<ReservaCarritoResponse> obtenerReservasPorEmprendedor() {
        Usuario usuario = getCurrentUser();
        
        if (!hasRole("ROLE_EMPRENDEDOR")) {
            throw new RuntimeException("Solo los emprendedores pueden acceder a esta función");
        }
        
        // Buscar emprendedor del usuario
        Emprendedor emprendedor = emprendedorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Emprendedor", "usuario_id", usuario.getId()));
        
        List<ReservaCarrito> reservas = reservaCarritoRepository
                .findByEmprendedorId(emprendedor.getId());
        
        return reservas.stream()
                .map(this::convertToReservaCarritoResponse)
                .collect(Collectors.toList());
    }
    public List<ChatConversacionResponse> obtenerConversacionesDeReserva(Long reservaCarritoId) {
        Usuario usuario = getCurrentUser();
        
        ReservaCarrito reserva = reservaCarritoRepository.findById(reservaCarritoId)
                .orElseThrow(() -> new ResourceNotFoundException("ReservaCarrito", "id", reservaCarritoId));
        
        // Verificar permisos
        if (!reserva.getUsuario().getId().equals(usuario.getId()) && !hasRole("ROLE_ADMIN")) {
            throw new RuntimeException("No tiene permisos para ver las conversaciones de esta reserva");
        }
        
        // Delegar al ChatService para obtener las conversaciones
        return chatService.obtenerConversacionesPorReservaCarrito(reservaCarritoId);
    }
}