package com.turismo.turismobackend.service;

import com.turismo.turismobackend.dto.response.UsuarioResponse;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.Emprendedor;
import com.turismo.turismobackend.model.Rol;
import com.turismo.turismobackend.model.Usuario;
import com.turismo.turismobackend.repository.EmprendedorRepository;
import com.turismo.turismobackend.repository.RolRepository;
import com.turismo.turismobackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final EmprendedorRepository emprendedorRepository;
    private final RolRepository rolRepository;
    
    public List<UsuarioResponse> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToUsuarioResponse)
                .collect(Collectors.toList());
    }
    
    public UsuarioResponse getUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return mapToUsuarioResponse(usuario);
    }
    
    public List<UsuarioResponse> getUsuariosSinEmprendedor() {
        return usuarioRepository.findUsuariosSinEmprendedor().stream()
                .map(this::mapToUsuarioResponse)
                .collect(Collectors.toList());
    }
    
    public List<UsuarioResponse> getUsuariosPorRol(String rolNombre) {
        Rol.RolNombre rolEnum;
        try {
            rolEnum = Rol.RolNombre.valueOf(rolNombre.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol no válido: " + rolNombre);
        }
        
        Rol rol = rolRepository.findByNombre(rolEnum)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "nombre", rolNombre));
        
        return usuarioRepository.findByRolesContaining(rol).stream()
                .map(this::mapToUsuarioResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void asignarUsuarioAEmprendedor(Long usuarioId, Long emprendedorId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));
        
        Emprendedor emprendedor = emprendedorRepository.findById(emprendedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Emprendedor", "id", emprendedorId));
        
        // Verificar si el emprendedor ya tiene un usuario asignado
        if (emprendedor.getUsuario() != null) {
            throw new RuntimeException("El emprendedor ya tiene un usuario asignado");
        }
        
        // Verificar si el usuario ya tiene un emprendedor asignado
        Optional<Emprendedor> emprendedorExistente = emprendedorRepository.findByUsuario(usuario);
        if (emprendedorExistente.isPresent()) {
            throw new RuntimeException("El usuario ya tiene un emprendedor asignado");
        }
        
        // Asignar usuario al emprendedor
        emprendedor.setUsuario(usuario);
        emprendedorRepository.save(emprendedor);
    }
    
    @Transactional
    public void cambiarUsuarioDeEmprendedor(Long usuarioId, Long emprendedorId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));
        
        Emprendedor nuevoEmprendedor = emprendedorRepository.findById(emprendedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Emprendedor", "id", emprendedorId));
        
        // Verificar si el nuevo emprendedor ya tiene un usuario asignado
        if (nuevoEmprendedor.getUsuario() != null && !nuevoEmprendedor.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("El emprendedor destino ya tiene otro usuario asignado");
        }
        
        // Buscar si el usuario ya tiene un emprendedor asignado
        Optional<Emprendedor> emprendedorAnterior = emprendedorRepository.findByUsuario(usuario);
        
        // Desasignar del emprendedor anterior si existe
        if (emprendedorAnterior.isPresent()) {
            emprendedorAnterior.get().setUsuario(null);
            emprendedorRepository.save(emprendedorAnterior.get());
        }
        
        // Asignar al nuevo emprendedor
        nuevoEmprendedor.setUsuario(usuario);
        emprendedorRepository.save(nuevoEmprendedor);
    }
    
    @Transactional
    public void desasignarUsuarioDeEmprendedor(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));
        
        Optional<Emprendedor> emprendedor = emprendedorRepository.findByUsuario(usuario);
        
        if (emprendedor.isEmpty()) {
            throw new RuntimeException("El usuario no tiene un emprendedor asignado");
        }
        
        // Desasignar usuario del emprendedor
        emprendedor.get().setUsuario(null);
        emprendedorRepository.save(emprendedor.get());
    }
    
    @Transactional
    public void asignarRolAUsuario(Long usuarioId, String rolNombre) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));
        
        // Convertir el nombre del rol a enum
        Rol.RolNombre rolEnum;
        try {
            rolEnum = Rol.RolNombre.valueOf(rolNombre.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol no válido: " + rolNombre);
        }
        
        // Buscar el rol en la base de datos
        Rol rol = rolRepository.findByNombre(rolEnum)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "nombre", rolNombre));
        
        // Verificar si el usuario ya tiene este rol
        if (usuario.getRoles().contains(rol)) {
            throw new RuntimeException("El usuario ya tiene el rol: " + rolNombre);
        }
        
        // Asignar el rol
        usuario.getRoles().add(rol);
        usuarioRepository.save(usuario);
    }
    
    @Transactional
    public void quitarRolAUsuario(Long usuarioId, String rolNombre) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));
        
        // Convertir el nombre del rol a enum
        Rol.RolNombre rolEnum;
        try {
            rolEnum = Rol.RolNombre.valueOf(rolNombre.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol no válido: " + rolNombre);
        }
        
        // Buscar el rol en la base de datos
        Rol rol = rolRepository.findByNombre(rolEnum)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "nombre", rolNombre));
        
        // Verificar si el usuario tiene este rol
        if (!usuario.getRoles().contains(rol)) {
            throw new RuntimeException("El usuario no tiene el rol: " + rolNombre);
        }
        
        // No permitir quitar el último rol si no es ROLE_USER
        if (usuario.getRoles().size() == 1 && !rolEnum.equals(Rol.RolNombre.ROLE_USER)) {
            // Asignar ROLE_USER antes de quitar el último rol
            Rol userRole = rolRepository.findByNombre(Rol.RolNombre.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Rol ROLE_USER no encontrado"));
            usuario.getRoles().add(userRole);
        }
        
        // Quitar el rol
        usuario.getRoles().remove(rol);
        usuarioRepository.save(usuario);
    }
    
    @Transactional
    public void resetearRolesAUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));
        
        // Buscar el rol ROLE_USER
        Rol userRole = rolRepository.findByNombre(Rol.RolNombre.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Rol ROLE_USER no encontrado"));
        
        // Limpiar todos los roles y asignar solo ROLE_USER
        usuario.getRoles().clear();
        usuario.getRoles().add(userRole);
        usuarioRepository.save(usuario);
    }
    
    private UsuarioResponse mapToUsuarioResponse(Usuario usuario) {
        // Obtener el emprendedor asociado si existe
        Optional<Emprendedor> emprendedor = emprendedorRepository.findByUsuario(usuario);
        
        List<String> roleNames = usuario.getRoles().stream()
                .map(rol -> rol.getNombre().name())
                .collect(Collectors.toList());
        
        UsuarioResponse.EmprendedorResumen emprendedorResumen = null;
        if (emprendedor.isPresent()) {
            emprendedorResumen = UsuarioResponse.EmprendedorResumen.builder()
                    .id(emprendedor.get().getId())
                    .nombreEmpresa(emprendedor.get().getNombreEmpresa())
                    .rubro(emprendedor.get().getRubro())
                    .build();
        }
        
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .roles(roleNames)
                .emprendedor(emprendedorResumen)
                .build();
    }
}