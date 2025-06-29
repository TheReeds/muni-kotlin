package com.turismo.turismobackend.controller;

import com.turismo.turismobackend.dto.request.EmprendedorRequest;
import com.turismo.turismobackend.dto.response.EmprendedorResponse;
import com.turismo.turismobackend.service.EmprendedorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emprendedores")
@RequiredArgsConstructor
public class EmprendedorController {
    
    private final EmprendedorService emprendedorService;
    
    @GetMapping
    public ResponseEntity<List<EmprendedorResponse>> getAllEmprendedores() {
        return ResponseEntity.ok(emprendedorService.getAllEmprendedores());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EmprendedorResponse> getEmprendedorById(@PathVariable Long id) {
        return ResponseEntity.ok(emprendedorService.getEmprendedorById(id));
    }
    
    @GetMapping("/municipalidad/{municipalidadId}")
    public ResponseEntity<List<EmprendedorResponse>> getEmprendedoresByMunicipalidad(@PathVariable Long municipalidadId) {
        return ResponseEntity.ok(emprendedorService.getEmprendedoresByMunicipalidad(municipalidadId));
    }
    
    @GetMapping("/rubro/{rubro}")
    public ResponseEntity<List<EmprendedorResponse>> getEmprendedoresByRubro(@PathVariable String rubro) {
        return ResponseEntity.ok(emprendedorService.getEmprendedoresByRubro(rubro));
    }
    
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<EmprendedorResponse>> getEmprendedoresByCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(emprendedorService.getEmprendedoresByCategoria(categoriaId));
    }
    
    @GetMapping("/mi-emprendedor")
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<EmprendedorResponse> getMiEmprendedor() {
        return ResponseEntity.ok(emprendedorService.getEmprendedorByUsuario());
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<EmprendedorResponse> createEmprendedor(@Valid @RequestBody EmprendedorRequest emprendedorRequest) {
        return ResponseEntity.ok(emprendedorService.createEmprendedor(emprendedorRequest));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<EmprendedorResponse> updateEmprendedor(
            @PathVariable Long id,
            @Valid @RequestBody EmprendedorRequest emprendedorRequest) {
        return ResponseEntity.ok(emprendedorService.updateEmprendedor(id, emprendedorRequest));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteEmprendedor(@PathVariable Long id) {
        emprendedorService.deleteEmprendedor(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/cercanos")
    @Operation(summary = "Obtener emprendedores cercanos a una ubicación")
    public ResponseEntity<List<EmprendedorResponse>> getEmprendedoresCercanos(
            @Parameter(description = "Latitud") @RequestParam Double latitud,
            @Parameter(description = "Longitud") @RequestParam Double longitud,
            @Parameter(description = "Radio en km") @RequestParam(defaultValue = "5.0") Double radio) {
        List<EmprendedorResponse> emprendedores = emprendedorService.getEmprendedoresCercanos(latitud, longitud, radio);
        return ResponseEntity.ok(emprendedores);
    }
}