package com.turismo.turismobackend.controller;

import com.turismo.turismobackend.dto.request.PlanTuristicoRequest;
import com.turismo.turismobackend.dto.response.PlanTuristicoResponse;
import com.turismo.turismobackend.model.PlanTuristico;
import com.turismo.turismobackend.service.PlanTuristicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/planes")
@RequiredArgsConstructor
@Tag(name = "Planes Turísticos", description = "API para gestión de planes turísticos")
public class PlanTuristicoController {
    
    private final PlanTuristicoService planService;
    
    @GetMapping
    @Operation(summary = "Obtener todos los planes turísticos")
    public ResponseEntity<List<PlanTuristicoResponse>> getAllPlanes() {
        List<PlanTuristicoResponse> planes = planService.getAllPlanes();
        return ResponseEntity.ok(planes);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener plan turístico por ID")
    public ResponseEntity<PlanTuristicoResponse> getPlanById(
            @Parameter(description = "ID del plan turístico") @PathVariable Long id) {
        PlanTuristicoResponse plan = planService.getPlanById(id);
        return ResponseEntity.ok(plan);
    }
    
    @GetMapping("/municipalidad/{municipalidadId}")
    @Operation(summary = "Obtener planes por municipalidad")
    public ResponseEntity<List<PlanTuristicoResponse>> getPlanesByMunicipalidad(
            @Parameter(description = "ID de la municipalidad") @PathVariable Long municipalidadId) {
        List<PlanTuristicoResponse> planes = planService.getPlanesByMunicipalidad(municipalidadId);
        return ResponseEntity.ok(planes);
    }
    
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener planes por estado")
    public ResponseEntity<List<PlanTuristicoResponse>> getPlanesByEstado(
            @Parameter(description = "Estado del plan") @PathVariable PlanTuristico.EstadoPlan estado) {
        List<PlanTuristicoResponse> planes = planService.getPlanesByEstado(estado);
        return ResponseEntity.ok(planes);
    }
    
    @GetMapping("/dificultad/{nivel}")
    @Operation(summary = "Obtener planes por nivel de dificultad")
    public ResponseEntity<List<PlanTuristicoResponse>> getPlanesByNivelDificultad(
            @Parameter(description = "Nivel de dificultad") @PathVariable PlanTuristico.NivelDificultad nivel) {
        List<PlanTuristicoResponse> planes = planService.getPlanesByNivelDificultad(nivel);
        return ResponseEntity.ok(planes);
    }
    
    @GetMapping("/duracion")
    @Operation(summary = "Obtener planes por rango de duración")
    public ResponseEntity<List<PlanTuristicoResponse>> getPlanesByDuracion(
            @Parameter(description = "Duración mínima en días") @RequestParam Integer duracionMin,
            @Parameter(description = "Duración máxima en días") @RequestParam Integer duracionMax) {
        List<PlanTuristicoResponse> planes = planService.getPlanesByDuracion(duracionMin, duracionMax);
        return ResponseEntity.ok(planes);
    }
    
    @GetMapping("/precio")
    @Operation(summary = "Obtener planes por rango de precio")
    public ResponseEntity<List<PlanTuristicoResponse>> getPlanesByPrecio(
            @Parameter(description = "Precio mínimo") @RequestParam BigDecimal precioMin,
            @Parameter(description = "Precio máximo") @RequestParam BigDecimal precioMax) {
        List<PlanTuristicoResponse> planes = planService.getPlanesByPrecio(precioMin, precioMax);
        return ResponseEntity.ok(planes);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Buscar planes por nombre o descripción")
    public ResponseEntity<List<PlanTuristicoResponse>> searchPlanes(
            @Parameter(description = "Término de búsqueda") @RequestParam String termino) {
        List<PlanTuristicoResponse> planes = planService.searchPlanes(termino);
        return ResponseEntity.ok(planes);
    }
    
    @GetMapping("/mis-planes")
    @Operation(summary = "Obtener mis planes (usuario autenticado)")
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<PlanTuristicoResponse>> getMisPlanes() {
        List<PlanTuristicoResponse> planes = planService.getMisPlanes();
        return ResponseEntity.ok(planes);
    }
    
    @GetMapping("/populares")
    @Operation(summary = "Obtener planes más populares")
    public ResponseEntity<List<PlanTuristicoResponse>> getPlanesMasPopulares() {
        List<PlanTuristicoResponse> planes = planService.getPlanesMasPopulares();
        return ResponseEntity.ok(planes);
    }
    
    @PostMapping
    @Operation(summary = "Crear nuevo plan turístico")
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_ADMIN', 'ROLE_MUNICIPALIDAD')")
    public ResponseEntity<PlanTuristicoResponse> createPlan(
            @Valid @RequestBody PlanTuristicoRequest request) {
        PlanTuristicoResponse plan = planService.createPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(plan);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar plan turístico")
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_ADMIN', 'ROLE_MUNICIPALIDAD')")
    public ResponseEntity<PlanTuristicoResponse> updatePlan(
            @Parameter(description = "ID del plan") @PathVariable Long id,
            @Valid @RequestBody PlanTuristicoRequest request) {
        PlanTuristicoResponse plan = planService.updatePlan(id, request);
        return ResponseEntity.ok(plan);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar plan turístico")
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_ADMIN', 'ROLE_MUNICIPALIDAD')")
    public ResponseEntity<Void> deletePlan(
            @Parameter(description = "ID del plan") @PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado del plan")
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_ADMIN', 'ROLE_MUNICIPALIDAD')")
    public ResponseEntity<PlanTuristicoResponse> cambiarEstado(
            @Parameter(description = "ID del plan") @PathVariable Long id,
            @Parameter(description = "Nuevo estado") @RequestParam PlanTuristico.EstadoPlan estado) {
        PlanTuristicoResponse plan = planService.cambiarEstado(id, estado);
        return ResponseEntity.ok(plan);
    }
}