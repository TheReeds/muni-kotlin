package com.turismo.turismobackend.controller;

import com.turismo.turismobackend.dto.request.ReservaRequest;
import com.turismo.turismobackend.dto.response.ReservaResponse;
import com.turismo.turismobackend.service.ReservaService;
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
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "API para gestión de reservas de planes turísticos")
public class ReservaController {
    
    private final ReservaService reservaService;
    
    @GetMapping
    @Operation(summary = "Obtener todas las reservas (solo admin)")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ReservaResponse>> getAllReservas() {
        List<ReservaResponse> reservas = reservaService.getAllReservas();
        return ResponseEntity.ok(reservas);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID")
    public ResponseEntity<ReservaResponse> getReservaById(
            @Parameter(description = "ID de la reserva") @PathVariable Long id) {
        ReservaResponse reserva = reservaService.getReservaById(id);
        return ResponseEntity.ok(reserva);
    }
    
    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Obtener reserva por código")
    public ResponseEntity<ReservaResponse> getReservaByCodigo(
            @Parameter(description = "Código de la reserva") @PathVariable String codigo) {
        ReservaResponse reserva = reservaService.getReservaByCodigo(codigo);
        return ResponseEntity.ok(reserva);
    }
    
    @GetMapping("/mis-reservas")
    @Operation(summary = "Obtener mis reservas (usuario autenticado)")
    public ResponseEntity<List<ReservaResponse>> getMisReservas() {
        List<ReservaResponse> reservas = reservaService.getMisReservas();
        return ResponseEntity.ok(reservas);
    }
    
    @GetMapping("/plan/{planId}")
    @Operation(summary = "Obtener reservas por plan turístico")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<List<ReservaResponse>> getReservasByPlan(
            @Parameter(description = "ID del plan turístico") @PathVariable Long planId) {
        List<ReservaResponse> reservas = reservaService.getReservasByPlan(planId);
        return ResponseEntity.ok(reservas);
    }
    
    @GetMapping("/municipalidad/{municipalidadId}")
    @Operation(summary = "Obtener reservas por municipalidad")
    @PreAuthorize("hasRole('ROLE_MUNICIPALIDAD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ReservaResponse>> getReservasByMunicipalidad(
            @Parameter(description = "ID de la municipalidad") @PathVariable Long municipalidadId) {
        List<ReservaResponse> reservas = reservaService.getReservasByMunicipalidad(municipalidadId);
        return ResponseEntity.ok(reservas);
    }
    
    @PostMapping
    @Operation(summary = "Crear nueva reserva")
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_ADMIN', 'ROLE_MUNICIPALIDAD', 'ROLE_USER')")
    public ResponseEntity<ReservaResponse> createReserva(
            @Valid @RequestBody ReservaRequest request) {
        ReservaResponse reserva = reservaService.createReserva(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }
    
    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar reserva")
    @PreAuthorize("hasRole('ROLE_MUNICIPALIDAD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ReservaResponse> confirmarReserva(
            @Parameter(description = "ID de la reserva") @PathVariable Long id) {
        ReservaResponse reserva = reservaService.confirmarReserva(id);
        return ResponseEntity.ok(reserva);
    }
    
    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar reserva")
    public ResponseEntity<ReservaResponse> cancelarReserva(
            @Parameter(description = "ID de la reserva") @PathVariable Long id,
            @Parameter(description = "Motivo de cancelación") @RequestParam String motivo) {
        ReservaResponse reserva = reservaService.cancelarReserva(id, motivo);
        return ResponseEntity.ok(reserva);
    }
    
    @PatchMapping("/{id}/completar")
    @Operation(summary = "Completar reserva")
    @PreAuthorize("hasRole('ROLE_MUNICIPALIDAD') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ReservaResponse> completarReserva(
            @Parameter(description = "ID de la reserva") @PathVariable Long id) {
        ReservaResponse reserva = reservaService.completarReserva(id);
        return ResponseEntity.ok(reserva);
    }
}