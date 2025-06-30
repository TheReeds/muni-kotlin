package com.turismo.turismobackend.controller;

import com.turismo.turismobackend.dto.response.UsuarioResponse;
import com.turismo.turismobackend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Map<String, String>> asignarUsuarioAEmprendedor(
            @PathVariable Long usuarioId,
            @PathVariable Long emprendedorId) {

        usuarioService.asignarUsuarioAEmprendedor(usuarioId, emprendedorId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario asignado al emprendedor correctamente");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{usuarioId}/cambiar-emprendedor/{emprendedorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> cambiarUsuarioDeEmprendedor(
            @PathVariable Long usuarioId,
            @PathVariable Long emprendedorId) {

        usuarioService.cambiarUsuarioDeEmprendedor(usuarioId, emprendedorId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario cambiado de emprendedor correctamente");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{usuarioId}/desasignar-emprendedor")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> desasignarUsuarioDeEmprendedor(
            @PathVariable Long usuarioId) {

        usuarioService.desasignarUsuarioDeEmprendedor(usuarioId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario desasignado del emprendedor correctamente");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{usuarioId}/asignar-rol/{rol}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> asignarRolAUsuario(
            @PathVariable Long usuarioId,
            @PathVariable String rol) {

        usuarioService.asignarRolAUsuario(usuarioId, rol);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Rol asignado al usuario correctamente");

        return ResponseEntity.ok(response);
    }

    
    @PutMapping("/{usuarioId}/quitar-rol/{rol}")
    @PreAuthorize("hasAnyRole('ROLE_EMPRENDEDOR', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> quitarRolAUsuario(
            @PathVariable Long usuarioId,
            @PathVariable String rol) {
        
        usuarioService.quitarRolAUsuario(usuarioId, rol);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Rol quitado al usuario correctamente");

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{usuarioId}/resetear-roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> resetearRolesUsuario(@PathVariable Long usuarioId) {

        usuarioService.resetearRolesAUsuario(usuarioId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Roles del usuario reseteados a ROLE_USER");

        return ResponseEntity.ok(response);
    }

}