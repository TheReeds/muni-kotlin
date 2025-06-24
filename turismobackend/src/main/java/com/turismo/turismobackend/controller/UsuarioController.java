package com.turismo.turismobackend.controller;

import com.turismo.turismobackend.dto.response.UsuarioResponse;
import com.turismo.turismobackend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.getAllUsuarios());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UsuarioResponse> getUsuarioById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getUsuarioById(id));
    }
    
    @GetMapping("/sin-emprendedor")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> getUsuariosSinEmprendedor() {
        return ResponseEntity.ok(usuarioService.getUsuariosSinEmprendedor());
    }
    
    @GetMapping("/con-rol/{rol}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UsuarioResponse>> getUsuariosPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(usuarioService.getUsuariosPorRol(rol));
    }
    
    @PutMapping("/{usuarioId}/asignar-emprendedor/{emprendedorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> asignarUsuarioAEmprendedor(
            @PathVariable Long usuarioId,
            @PathVariable Long emprendedorId) {
        usuarioService.asignarUsuarioAEmprendedor(usuarioId, emprendedorId);
        return ResponseEntity.ok("Usuario asignado al emprendedor correctamente");
    }
    
    @PutMapping("/{usuarioId}/cambiar-emprendedor/{emprendedorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> cambiarUsuarioDeEmprendedor(
            @PathVariable Long usuarioId,
            @PathVariable Long emprendedorId) {
        usuarioService.cambiarUsuarioDeEmprendedor(usuarioId, emprendedorId);
        return ResponseEntity.ok("Usuario cambiado de emprendedor correctamente");
    }
    
    @DeleteMapping("/{usuarioId}/desasignar-emprendedor")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> desasignarUsuarioDeEmprendedor(@PathVariable Long usuarioId) {
        usuarioService.desasignarUsuarioDeEmprendedor(usuarioId);
        return ResponseEntity.ok("Usuario desasignado del emprendedor correctamente");
    }
    
    @PutMapping("/{usuarioId}/asignar-rol/{rol}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> asignarRolAUsuario(
            @PathVariable Long usuarioId,
            @PathVariable String rol) {
        usuarioService.asignarRolAUsuario(usuarioId, rol);
        return ResponseEntity.ok("Rol asignado al usuario correctamente");
    }
    
    @PutMapping("/{usuarioId}/quitar-rol/{rol}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<String> quitarRolAUsuario(
            @PathVariable Long usuarioId,
            @PathVariable String rol) {
        usuarioService.quitarRolAUsuario(usuarioId, rol);
        return ResponseEntity.ok("Rol quitado al usuario correctamente");
    }
    
    @PutMapping("/{usuarioId}/resetear-roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> resetearRolesUsuario(@PathVariable Long usuarioId) {
        usuarioService.resetearRolesAUsuario(usuarioId);
        return ResponseEntity.ok("Roles del usuario reseteados a ROLE_USER");
    }
}