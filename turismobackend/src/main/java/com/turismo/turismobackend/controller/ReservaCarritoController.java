package com.turismo.turismobackend.controller;

import com.turismo.turismobackend.dto.request.ReservaCarritoRequest;
import com.turismo.turismobackend.dto.response.ChatConversacionResponse;
import com.turismo.turismobackend.dto.response.ReservaCarritoResponse;
import com.turismo.turismobackend.service.ReservaCarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas-carrito")
@RequiredArgsConstructor
@Tag(name = "Reservas desde Carrito", description = "API para gestión de reservas creadas desde el carrito")
@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_EMPRENDEDOR', 'ROLE_MUNICIPALIDAD', 'ROLE_ADMIN')")
public class ReservaCarritoController {
    
    private final ReservaCarritoService reservaCarritoService;
    
    @PostMapping("/crear")
    @Operation(summary = "Crear reserva desde carrito")
    public ResponseEntity<ReservaCarritoResponse> crearReservaDesdeCarrito(
            @Valid @RequestBody ReservaCarritoRequest request) {
        ReservaCarritoResponse reserva = reservaCarritoService.crearReservaDesdeCarrito(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }
    
    @GetMapping("/mis-reservas")
    @Operation(summary = "Obtener mis reservas ordenadas por fecha")
    public ResponseEntity<List<ReservaCarritoResponse>> obtenerMisReservas() {
        List<ReservaCarritoResponse> reservas = reservaCarritoService.obtenerMisReservas();
        return ResponseEntity.ok(reservas);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID")
    public ResponseEntity<ReservaCarritoResponse> obtenerReservaPorId(
            @Parameter(description = "ID de la reserva") @PathVariable Long id) {
        ReservaCarritoResponse reserva = reservaCarritoService.obtenerReservaPorId(id);
        return ResponseEntity.ok(reserva);
    }
    
    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Obtener reserva por código único")
    public ResponseEntity<ReservaCarritoResponse> obtenerReservaPorCodigo(
            @Parameter(description = "Código de la reserva") @PathVariable String codigo) {
        ReservaCarritoResponse reserva = reservaCarritoService.obtenerReservaPorCodigo(codigo);
        return ResponseEntity.ok(reserva);
    }
    
    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar reserva")
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_MUNICIPALIDAD', 'ROLE_ADMIN')")
    public ResponseEntity<ReservaCarritoResponse> confirmarReserva(
            @Parameter(description = "ID de la reserva") @PathVariable Long id) {
        ReservaCarritoResponse reserva = reservaCarritoService.confirmarReserva(id);
        return ResponseEntity.ok(reserva);
    }
    
    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar reserva con motivo")
    public ResponseEntity<ReservaCarritoResponse> cancelarReserva(
            @Parameter(description = "ID de la reserva") @PathVariable Long id,
            @Parameter(description = "Motivo de cancelación") @RequestParam String motivo) {
        ReservaCarritoResponse reserva = reservaCarritoService.cancelarReserva(id, motivo);
        return ResponseEntity.ok(reserva);
    }
    
    @PatchMapping("/{id}/completar")
    @Operation(summary = "Marcar reserva como completada")
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_MUNICIPALIDAD', 'ROLE_ADMIN')")
    public ResponseEntity<ReservaCarritoResponse> completarReserva(
            @Parameter(description = "ID de la reserva") @PathVariable Long id) {
        ReservaCarritoResponse reserva = reservaCarritoService.completarReserva(id);
        return ResponseEntity.ok(reserva);
    }
    
    @GetMapping("/emprendedor/reservas")
    @Operation(summary = "Obtener reservas para emprendedor autenticado")
    @PreAuthorize("hasRole('ROLE_EMPRENDEDOR')")
    public ResponseEntity<List<ReservaCarritoResponse>> obtenerReservasPorEmprendedor() {
        List<ReservaCarritoResponse> reservas = reservaCarritoService.obtenerReservasPorEmprendedor();
        return ResponseEntity.ok(reservas);
    }
    
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Filtrar mis reservas por estado")
    public ResponseEntity<List<ReservaCarritoResponse>> obtenerReservasPorEstado(
            @Parameter(description = "Estado de la reserva") @PathVariable String estado) {
        List<ReservaCarritoResponse> reservas = reservaCarritoService.obtenerReservasPorEstado(estado);
        return ResponseEntity.ok(reservas);
    }
    
    @GetMapping("/estadisticas")
    @Operation(summary = "Obtener estadísticas de reservas del usuario")
    public ResponseEntity<?> obtenerEstadisticasReservas() {
        // Implementar estadísticas si es necesario
        return ResponseEntity.ok("Estadísticas en desarrollo");
    }
    @GetMapping("/{id}/conversaciones")
    @Operation(summary = "Obtener conversaciones de chat de una reserva")
    public ResponseEntity<List<ChatConversacionResponse>> obtenerConversacionesDeReserva(
            @Parameter(description = "ID de la reserva") @PathVariable Long id) {
        List<ChatConversacionResponse> conversaciones = reservaCarritoService.obtenerConversacionesDeReserva(id);
        return ResponseEntity.ok(conversaciones);
    }
}