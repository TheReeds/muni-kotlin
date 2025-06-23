package com.turismo.turismobackend.controller;

import com.turismo.turismobackend.dto.request.PagoRequest;
import com.turismo.turismobackend.dto.response.PagoResponse;
import com.turismo.turismobackend.service.PagoService;
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
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "API para gestión de pagos de reservas")
public class PagoController {
    
    private final PagoService pagoService;
    
    @GetMapping
    @Operation(summary = "Obtener todos los pagos (solo admin)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PagoResponse>> getAllPagos() {
        List<PagoResponse> pagos = pagoService.getAllPagos();
        return ResponseEntity.ok(pagos);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener pago por ID")
    public ResponseEntity<PagoResponse> getPagoById(
            @Parameter(description = "ID del pago") @PathVariable Long id) {
        PagoResponse pago = pagoService.getPagoById(id);
        return ResponseEntity.ok(pago);
    }
    
    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Obtener pago por código")
    public ResponseEntity<PagoResponse> getPagoByCodigo(
            @Parameter(description = "Código del pago") @PathVariable String codigo) {
        PagoResponse pago = pagoService.getPagoByCodigo(codigo);
        return ResponseEntity.ok(pago);
    }
    
    @GetMapping("/reserva/{reservaId}")
    @Operation(summary = "Obtener pagos por reserva")
    public ResponseEntity<List<PagoResponse>> getPagosByReserva(
            @Parameter(description = "ID de la reserva") @PathVariable Long reservaId) {
        List<PagoResponse> pagos = pagoService.getPagosByReserva(reservaId);
        return ResponseEntity.ok(pagos);
    }
    
    @GetMapping("/mis-pagos")
    @Operation(summary = "Obtener mis pagos (usuario autenticado)")
    @PreAuthorize("hasRole('USER') or hasRole('EMPRENDEDOR') or hasRole('MUNICIPALIDAD')")
    public ResponseEntity<List<PagoResponse>> getMisPagos() {
        List<PagoResponse> pagos = pagoService.getMisPagos();
        return ResponseEntity.ok(pagos);
    }
    
    @GetMapping("/municipalidad/{municipalidadId}")
    @Operation(summary = "Obtener pagos por municipalidad")
    @PreAuthorize("hasRole('MUNICIPALIDAD') or hasRole('ADMIN')")
    public ResponseEntity<List<PagoResponse>> getPagosByMunicipalidad(
            @Parameter(description = "ID de la municipalidad") @PathVariable Long municipalidadId) {
        List<PagoResponse> pagos = pagoService.getPagosByMunicipalidad(municipalidadId);
        return ResponseEntity.ok(pagos);
    }
    
    @PostMapping
    @Operation(summary = "Registrar nuevo pago")
    @PreAuthorize("hasRole('USER') or hasRole('EMPRENDEDOR') or hasRole('MUNICIPALIDAD') or hasRole('ADMIN')")
    public ResponseEntity<PagoResponse> registrarPago(
            @Valid @RequestBody PagoRequest request) {
        PagoResponse pago = pagoService.registrarPago(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pago);
    }
    
    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar pago")
    @PreAuthorize("hasRole('MUNICIPALIDAD') or hasRole('ADMIN')")
    public ResponseEntity<PagoResponse> confirmarPago(
            @Parameter(description = "ID del pago") @PathVariable Long id) {
        PagoResponse pago = pagoService.confirmarPago(id);
        return ResponseEntity.ok(pago);
    }
    
    @PatchMapping("/{id}/rechazar")
    @Operation(summary = "Rechazar pago")
    @PreAuthorize("hasRole('MUNICIPALIDAD') or hasRole('ADMIN')")
    public ResponseEntity<PagoResponse> rechazarPago(
            @Parameter(description = "ID del pago") @PathVariable Long id,
            @Parameter(description = "Motivo del rechazo") @RequestParam String motivo) {
        PagoResponse pago = pagoService.rechazarPago(id, motivo);
        return ResponseEntity.ok(pago);
    }
}