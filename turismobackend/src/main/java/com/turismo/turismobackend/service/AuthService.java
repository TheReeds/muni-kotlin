package com.turismo.turismobackend.service;

import com.turismo.turismobackend.dto.request.LoginRequest;
import com.turismo.turismobackend.dto.request.RegisterRequest;
import com.turismo.turismobackend.dto.response.AuthResponse;
import com.turismo.turismobackend.model.Rol;
import com.turismo.turismobackend.model.Usuario;
import com.turismo.turismobackend.repository.RolRepository;
import com.turismo.turismobackend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthResponse register(RegisterRequest request) {
        // Crear un nuevo usuario
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        
        // Asignar roles
        Set<Rol> roles = new HashSet<>();
        
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            // Si no se especifican roles, asignar ROLE_USER por defecto
            Rol userRole = rolRepository.findByNombre(Rol.RolNombre.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado"));
            roles.add(userRole);
        } else {
            request.getRoles().forEach(rolStr -> {
                switch (rolStr) {
                    case "admin":
                        Rol adminRole = rolRepository.findByNombre(Rol.RolNombre.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado"));
                        roles.add(adminRole);
                        break;
                    case "municipalidad":
                        Rol muniRole = rolRepository.findByNombre(Rol.RolNombre.ROLE_MUNICIPALIDAD)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado"));
                        roles.add(muniRole);
                        break;
                    case "emprendedor":
                        Rol emprRole = rolRepository.findByNombre(Rol.RolNombre.ROLE_EMPRENDEDOR)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado"));
                        roles.add(emprRole);
                        break;
                    default:
                        Rol userRole = rolRepository.findByNombre(Rol.RolNombre.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado"));
                        roles.add(userRole);
                }
            });
        }
        
        usuario.setRoles(roles);
        usuarioRepository.save(usuario);
        
        // Generar token JWT
        String jwtToken = jwtService.generateToken(usuario);
        
        // Obtener la lista de nombres de roles
        List<String> roleNames = usuario.getRoles().stream()
                .map(rol -> rol.getNombre().name())
                .toList();
        
        return AuthResponse.builder()
                .token(jwtToken)
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .roles(roleNames)
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        // Autenticar usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Obtener usuario autenticado
        Usuario usuario = (Usuario) authentication.getPrincipal();
        
        // Generar token JWT
        String jwtToken = jwtService.generateToken(usuario);
        
        // Obtener la lista de nombres de roles
        List<String> roleNames = usuario.getRoles().stream()
                .map(rol -> rol.getNombre().name())
                .toList();
        
        return AuthResponse.builder()
                .token(jwtToken)
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .roles(roleNames)
                .build();
    }
    
    public void initRoles() {
        // Crear roles si no existen
        if (!rolRepository.existsByNombre(Rol.RolNombre.ROLE_ADMIN)) {
            Rol adminRole = new Rol();
            adminRole.setNombre(Rol.RolNombre.ROLE_ADMIN);
            rolRepository.save(adminRole);
        }
        
        if (!rolRepository.existsByNombre(Rol.RolNombre.ROLE_MUNICIPALIDAD)) {
            Rol muniRole = new Rol();
            muniRole.setNombre(Rol.RolNombre.ROLE_MUNICIPALIDAD);
            rolRepository.save(muniRole);
        }
        
        if (!rolRepository.existsByNombre(Rol.RolNombre.ROLE_EMPRENDEDOR)) {
            Rol emprRole = new Rol();
            emprRole.setNombre(Rol.RolNombre.ROLE_EMPRENDEDOR);
            rolRepository.save(emprRole);
        }
        
        if (!rolRepository.existsByNombre(Rol.RolNombre.ROLE_USER)) {
            Rol userRole = new Rol();
            userRole.setNombre(Rol.RolNombre.ROLE_USER);
            rolRepository.save(userRole);
        }
    }
}