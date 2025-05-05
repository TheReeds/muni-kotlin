package com.turismo.turismobackend.controller;

import com.turismo.turismobackend.dto.request.MunicipalidadRequest;
import com.turismo.turismobackend.dto.response.MunicipalidadResponse;
import com.turismo.turismobackend.service.MunicipalidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/municipalidades")
@RequiredArgsConstructor
public class MunicipalidadController {
    
    private final MunicipalidadService municipalidadService;
    
    @GetMapping
    public ResponseEntity<List<MunicipalidadResponse>> getAllMunicipalidades() {
        return ResponseEntity.ok(municipalidadService.getAllMunicipalidades());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MunicipalidadResponse> getMunicipalidadById(@PathVariable Long id) {
        return ResponseEntity.ok(municipalidadService.getMunicipalidadById(id));
    }
    
    @GetMapping("/departamento/{departamento}")
    public ResponseEntity<List<MunicipalidadResponse>> getMunicipalidadesByDepartamento(@PathVariable String departamento) {
        return ResponseEntity.ok(municipalidadService.getMunicipalidadesByDepartamento(departamento));
    }
    
    @GetMapping("/provincia/{provincia}")
    public ResponseEntity<List<MunicipalidadResponse>> getMunicipalidadesByProvincia(@PathVariable String provincia) {
        return ResponseEntity.ok(municipalidadService.getMunicipalidadesByProvincia(provincia));
    }
    
    @GetMapping("/distrito/{distrito}")
    public ResponseEntity<List<MunicipalidadResponse>> getMunicipalidadesByDistrito(@PathVariable String distrito) {
        return ResponseEntity.ok(municipalidadService.getMunicipalidadesByDistrito(distrito));
    }
    
    @GetMapping("/mi-municipalidad")
    @PreAuthorize("hasAnyRole('ROLE_MUNICIPALIDAD', 'ROLE_ADMIN')")
    public ResponseEntity<MunicipalidadResponse> getMiMunicipalidad() {
        return ResponseEntity.ok(municipalidadService.getMunicipalidadByUsuario());
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_MUNICIPALIDAD', 'ROLE_ADMIN')")
    public ResponseEntity<MunicipalidadResponse> createMunicipalidad(@Valid @RequestBody MunicipalidadRequest municipalidadRequest) {
        return ResponseEntity.ok(municipalidadService.createMunicipalidad(municipalidadRequest));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MUNICIPALIDAD', 'ROLE_ADMIN')")
    public ResponseEntity<MunicipalidadResponse> updateMunicipalidad(
            @PathVariable Long id,
            @Valid @RequestBody MunicipalidadRequest municipalidadRequest) {
        return ResponseEntity.ok(municipalidadService.updateMunicipalidad(id, municipalidadRequest));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MUNICIPALIDAD', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteMunicipalidad(@PathVariable Long id) {
        municipalidadService.deleteMunicipalidad(id);
        return ResponseEntity.noContent().build();
    }
}