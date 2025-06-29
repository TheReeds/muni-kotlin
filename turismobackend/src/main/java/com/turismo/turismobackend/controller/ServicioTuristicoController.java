package com.turismo.turismobackend.controller;

import com.turismo.turismobackend.dto.request.ServicioTuristicoRequest;
import com.turismo.turismobackend.dto.response.ServicioTuristicoResponse;
import com.turismo.turismobackend.model.ServicioTuristico;
import com.turismo.turismobackend.service.ServicioTuristicoService;
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
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
@Tag(name = "Servicios Turísticos", description = "API para gestión de servicios turísticos")
public class ServicioTuristicoController {
    
    private final ServicioTuristicoService servicioService;
    
    @GetMapping
    @Operation(summary = "Obtener todos los servicios turísticos")
    public ResponseEntity<List<ServicioTuristicoResponse>> getAllServicios() {
        List<ServicioTuristicoResponse> servicios = servicioService.getAllServicios();
        return ResponseEntity.ok(servicios);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener servicio turístico por ID")
    public ResponseEntity<ServicioTuristicoResponse> getServicioById(
            @Parameter(description = "ID del servicio turístico") @PathVariable Long id) {
        ServicioTuristicoResponse servicio = servicioService.getServicioById(id);
        return ResponseEntity.ok(servicio);
    }
    
    @GetMapping("/emprendedor/{emprendedorId}")
    @Operation(summary = "Obtener servicios por emprendedor")
    public ResponseEntity<List<ServicioTuristicoResponse>> getServiciosByEmprendedor(
            @Parameter(description = "ID del emprendedor") @PathVariable Long emprendedorId) {
        List<ServicioTuristicoResponse> servicios = servicioService.getServiciosByEmprendedor(emprendedorId);
        return ResponseEntity.ok(servicios);
    }
    
    @GetMapping("/municipalidad/{municipalidadId}")
    @Operation(summary = "Obtener servicios por municipalidad")
    public ResponseEntity<List<ServicioTuristicoResponse>> getServiciosByMunicipalidad(
            @Parameter(description = "ID de la municipalidad") @PathVariable Long municipalidadId) {
        List<ServicioTuristicoResponse> servicios = servicioService.getServiciosByMunicipalidad(municipalidadId);
        return ResponseEntity.ok(servicios);
    }
    
    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Obtener servicios por tipo")
    public ResponseEntity<List<ServicioTuristicoResponse>> getServiciosByTipo(
            @Parameter(description = "Tipo de servicio") @PathVariable ServicioTuristico.TipoServicio tipo) {
        List<ServicioTuristicoResponse> servicios = servicioService.getServiciosByTipo(tipo);
        return ResponseEntity.ok(servicios);
    }
    
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener servicios por estado")
    public ResponseEntity<List<ServicioTuristicoResponse>> getServiciosByEstado(
            @Parameter(description = "Estado del servicio") @PathVariable ServicioTuristico.EstadoServicio estado) {
        List<ServicioTuristicoResponse> servicios = servicioService.getServiciosByEstado(estado);
        return ResponseEntity.ok(servicios);
    }
    
    @GetMapping("/precio")
    @Operation(summary = "Obtener servicios por rango de precio")
    public ResponseEntity<List<ServicioTuristicoResponse>> getServiciosByPrecio(
            @Parameter(description = "Precio mínimo") @RequestParam BigDecimal precioMin,
            @Parameter(description = "Precio máximo") @RequestParam BigDecimal precioMax) {
        List<ServicioTuristicoResponse> servicios = servicioService.getServiciosByPrecio(precioMin, precioMax);
        return ResponseEntity.ok(servicios);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Buscar servicios por nombre o descripción")
    public ResponseEntity<List<ServicioTuristicoResponse>> searchServicios(
            @Parameter(description = "Término de búsqueda") @RequestParam String termino) {
        List<ServicioTuristicoResponse> servicios = servicioService.searchServicios(termino);
        return ResponseEntity.ok(servicios);
    }
    
    @GetMapping("/mis-servicios")
    @Operation(summary = "Obtener mis servicios (emprendedor autenticado)")
    @PreAuthorize("hasRole('ROLE_EMPRENDEDOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ServicioTuristicoResponse>> getMisServicios() {
        List<ServicioTuristicoResponse> servicios = servicioService.getMisServicios();
        return ResponseEntity.ok(servicios);
    }
    
    @PostMapping
    @Operation(summary = "Crear nuevo servicio turístico")
    @PreAuthorize("hasRole('ROLE_EMPRENDEDOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ServicioTuristicoResponse> createServicio(
            @Valid @RequestBody ServicioTuristicoRequest request) {
        ServicioTuristicoResponse servicio = servicioService.createServicio(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar servicio turístico")
    @PreAuthorize("hasRole('ROLE_EMPRENDEDOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ServicioTuristicoResponse> updateServicio(
            @Parameter(description = "ID del servicio") @PathVariable Long id,
            @Valid @RequestBody ServicioTuristicoRequest request) {
        ServicioTuristicoResponse servicio = servicioService.updateServicio(id, request);
        return ResponseEntity.ok(servicio);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar servicio turístico")
    @PreAuthorize("hasRole('ROLE_EMPRENDEDOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteServicio(
            @Parameter(description = "ID del servicio") @PathVariable Long id) {
        servicioService.deleteServicio(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado del servicio")
    @PreAuthorize("hasRole('ROLE_EMPRENDEDOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ServicioTuristicoResponse> cambiarEstado(
            @Parameter(description = "ID del servicio") @PathVariable Long id,
            @Parameter(description = "Nuevo estado") @RequestParam ServicioTuristico.EstadoServicio estado) {
        ServicioTuristicoResponse servicio = servicioService.cambiarEstado(id, estado);
        return ResponseEntity.ok(servicio);
    }
    @GetMapping("/cercanos")
    @Operation(summary = "Obtener servicios cercanos a una ubicación")
    public ResponseEntity<List<ServicioTuristicoResponse>> getServiciosCercanos(
            @Parameter(description = "Latitud") @RequestParam Double latitud,
            @Parameter(description = "Longitud") @RequestParam Double longitud,
            @Parameter(description = "Radio en km") @RequestParam(defaultValue = "5.0") Double radio) {
        List<ServicioTuristicoResponse> servicios = servicioService.getServiciosCercanos(latitud, longitud, radio);
        return ResponseEntity.ok(servicios);
    }
}