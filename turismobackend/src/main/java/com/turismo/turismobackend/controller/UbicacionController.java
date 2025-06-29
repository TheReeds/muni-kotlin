package com.turismo.turismobackend.controller;

import com.turismo.turismobackend.dto.request.UbicacionRequest;
import com.turismo.turismobackend.dto.response.EmprendedorResponse;
import com.turismo.turismobackend.dto.response.ServicioTuristicoResponse;
import com.turismo.turismobackend.dto.response.UbicacionResponse;
import com.turismo.turismobackend.service.UbicacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubicaciones")
@RequiredArgsConstructor
@Tag(name = "Ubicaciones", description = "API para gestión de ubicaciones y mapas")
public class UbicacionController {
    
    private final UbicacionService ubicacionService;
    
    @GetMapping("/emprendedores")
    @Operation(summary = "Obtener ubicaciones de emprendedores para mostrar en mapa")
    public ResponseEntity<List<EmprendedorResponse>> obtenerUbicacionesEmprendedores(
            @Parameter(description = "ID de municipalidad (opcional)") @RequestParam(required = false) Long municipalidadId,
            @Parameter(description = "Latitud centro de búsqueda") @RequestParam(required = false) Double latitud,
            @Parameter(description = "Longitud centro de búsqueda") @RequestParam(required = false) Double longitud,
            @Parameter(description = "Radio de búsqueda en km") @RequestParam(required = false) Double radio) {
        List<EmprendedorResponse> emprendedores = ubicacionService.obtenerEmprendedoresConUbicacion(
                municipalidadId, latitud, longitud, radio);
        return ResponseEntity.ok(emprendedores);
    }
    
    @GetMapping("/servicios")
    @Operation(summary = "Obtener ubicaciones de servicios turísticos para mapa")
    public ResponseEntity<List<ServicioTuristicoResponse>> obtenerUbicacionesServicios(
            @Parameter(description = "Tipo de servicio (TOUR, ALOJAMIENTO, etc.)") @RequestParam(required = false) String tipoServicio,
            @Parameter(description = "ID de municipalidad (opcional)") @RequestParam(required = false) Long municipalidadId,
            @Parameter(description = "Latitud centro de búsqueda") @RequestParam(required = false) Double latitud,
            @Parameter(description = "Longitud centro de búsqueda") @RequestParam(required = false) Double longitud,
            @Parameter(description = "Radio de búsqueda en km") @RequestParam(required = false) Double radio) {
        List<ServicioTuristicoResponse> servicios = ubicacionService.obtenerServiciosConUbicacion(
                tipoServicio, municipalidadId, latitud, longitud, radio);
        return ResponseEntity.ok(servicios);
    }
    
    @PutMapping("/emprendedor/{emprendedorId}")
    @Operation(summary = "Actualizar ubicación de emprendedor")
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<UbicacionResponse> actualizarUbicacionEmprendedor(
            @Parameter(description = "ID del emprendedor") @PathVariable Long emprendedorId,
            @Valid @RequestBody UbicacionRequest request) {
        UbicacionResponse ubicacion = ubicacionService.actualizarUbicacionEmprendedor(emprendedorId, request);
        return ResponseEntity.ok(ubicacion);
    }
    
    @PutMapping("/servicio/{servicioId}")
    @Operation(summary = "Actualizar ubicación de servicio turístico")
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<UbicacionResponse> actualizarUbicacionServicio(
            @Parameter(description = "ID del servicio") @PathVariable Long servicioId,
            @Valid @RequestBody UbicacionRequest request) {
        UbicacionResponse ubicacion = ubicacionService.actualizarUbicacionServicio(servicioId, request);
        return ResponseEntity.ok(ubicacion);
    }
    
    @GetMapping("/cercanos")
    @Operation(summary = "Buscar emprendedores y servicios cercanos a una ubicación")
    public ResponseEntity<?> buscarCercanos(
            @Parameter(description = "Latitud del punto de búsqueda") @RequestParam Double latitud,
            @Parameter(description = "Longitud del punto de búsqueda") @RequestParam Double longitud,
            @Parameter(description = "Radio de búsqueda en km") @RequestParam(defaultValue = "5.0") Double radio,
            @Parameter(description = "Tipo: 'emprendedores' o 'servicios'") @RequestParam String tipo) {
        
        if ("emprendedores".equals(tipo)) {
            List<EmprendedorResponse> emprendedores = ubicacionService.obtenerEmprendedoresConUbicacion(
                    null, latitud, longitud, radio);
            return ResponseEntity.ok(emprendedores);
        } else if ("servicios".equals(tipo)) {
            List<ServicioTuristicoResponse> servicios = ubicacionService.obtenerServiciosConUbicacion(
                    null, null, latitud, longitud, radio);
            return ResponseEntity.ok(servicios);
        } else {
            return ResponseEntity.badRequest().body("Tipo debe ser 'emprendedores' o 'servicios'");
        }
    }
    
    @GetMapping("/validar-coordenadas")
    @Operation(summary = "Validar si las coordenadas son válidas")
    public ResponseEntity<Boolean> validarCoordenadas(
            @Parameter(description = "Latitud a validar") @RequestParam Double latitud,
            @Parameter(description = "Longitud a validar") @RequestParam Double longitud) {
        boolean validas = ubicacionService.validarCoordenadas(latitud, longitud);
        return ResponseEntity.ok(validas);
    }
    
    @GetMapping("/distancia")
    @Operation(summary = "Calcular distancia entre dos puntos")
    public ResponseEntity<Double> calcularDistancia(
            @Parameter(description = "Latitud punto 1") @RequestParam Double lat1,
            @Parameter(description = "Longitud punto 1") @RequestParam Double lng1,
            @Parameter(description = "Latitud punto 2") @RequestParam Double lat2,
            @Parameter(description = "Longitud punto 2") @RequestParam Double lng2) {
        double distancia = ubicacionService.calcularDistanciaEntre(lat1, lng1, lat2, lng2);
        return ResponseEntity.ok(distancia);
    }
}