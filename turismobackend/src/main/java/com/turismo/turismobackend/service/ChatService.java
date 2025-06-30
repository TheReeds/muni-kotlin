package com.turismo.turismobackend.service;

import com.turismo.turismobackend.dto.request.ChatMensajeRequest;
import com.turismo.turismobackend.dto.response.*;
import com.turismo.turismobackend.exception.ResourceNotFoundException;
import com.turismo.turismobackend.model.*;
import com.turismo.turismobackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    
    private final ChatConversacionRepository conversacionRepository;
    private final ChatMensajeRepository mensajeRepository;
    private final EmprendedorRepository emprendedorRepository;
    private final ReservaRepository reservaRepository;
    private final ReservaCarritoRepository reservaCarritoRepository;
    
    public List<ChatConversacionResponse> obtenerConversaciones() {
        Usuario usuario = getCurrentUser();
        
        List<ChatConversacion> conversaciones;
        
        if (hasRole("ROLE_EMPRENDEDOR")) {
            // Si es emprendedor, buscar por emprendedor
            Emprendedor emprendedor = emprendedorRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Emprendedor", "usuario_id", usuario.getId()));
            conversaciones = conversacionRepository.findByEmprendedorIdAndEstado(
                    emprendedor.getId(), ChatConversacion.EstadoConversacion.ACTIVA);
        } else {
            // Si es usuario normal, buscar por usuario
            conversaciones = conversacionRepository.findByUsuarioIdAndEstado(
                    usuario.getId(), ChatConversacion.EstadoConversacion.ACTIVA);
        }
        
        return conversaciones.stream()
                .map(this::convertToConversacionResponse)
                .collect(Collectors.toList());
    }
    
    public ChatConversacionResponse iniciarConversacionConReservaCarrito(Long emprendedorId, Long reservaCarritoId) {
        Usuario usuario = getCurrentUser();
        
        Emprendedor emprendedor = emprendedorRepository.findById(emprendedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Emprendedor", "id", emprendedorId));
        
        // Verificar si ya existe una conversación
        ChatConversacion conversacion = conversacionRepository
                .findByUsuarioAndEmprendedor(usuario, emprendedor)
                .orElseGet(() -> {
                    ChatConversacion nueva = ChatConversacion.builder()
                            .usuario(usuario)
                            .emprendedor(emprendedor)
                            .estado(ChatConversacion.EstadoConversacion.ACTIVA)
                            .build();
                    
                    // Asignar reserva carrito si se proporciona
                    if (reservaCarritoId != null) {
                        ReservaCarrito reservaCarrito = reservaCarritoRepository.findById(reservaCarritoId)
                                .orElseThrow(() -> new ResourceNotFoundException("ReservaCarrito", "id", reservaCarritoId));
                        nueva.setReservaCarrito(reservaCarrito);
                    }
                    
                    return conversacionRepository.save(nueva);
                });
        
        // Crear mensaje de sistema si es nueva conversación
        if (conversacion.getMensajes().isEmpty()) {
            String mensajeInicial = "Conversación iniciada";
            if (reservaCarritoId != null) {
                mensajeInicial += " - Reserva: " + conversacion.getCodigoReservaAsociada();
            }
            
            ChatMensaje mensajeSistema = ChatMensaje.builder()
                    .conversacion(conversacion)
                    .mensaje(mensajeInicial)
                    .tipo(ChatMensaje.TipoMensaje.SISTEMA)
                    .leido(true)
                    .build();
            mensajeRepository.save(mensajeSistema);
        }
        
        return convertToConversacionResponse(conversacion);
    }
    
    public ChatMensajeResponse enviarMensaje(ChatMensajeRequest request) {
        Usuario usuario = getCurrentUser();
        
        ChatConversacion conversacion = conversacionRepository.findById(request.getConversacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversación", "id", request.getConversacionId()));
        
        // Verificar que el usuario puede participar en esta conversación
        boolean puedeParticipar = false;
        boolean esEmprendedor = false;
        
        if (conversacion.getUsuario().getId().equals(usuario.getId())) {
            puedeParticipar = true;
        } else if (hasRole("ROLE_EMPRENDEDOR")) {
            Emprendedor emprendedor = emprendedorRepository.findByUsuarioId(usuario.getId())
                    .orElse(null);
            if (emprendedor != null && conversacion.getEmprendedor().getId().equals(emprendedor.getId())) {
                puedeParticipar = true;
                esEmprendedor = true;
            }
        }
        
        if (!puedeParticipar) {
            throw new RuntimeException("No tiene permisos para participar en esta conversación");
        }
        
        // Crear mensaje
        ChatMensaje mensaje = ChatMensaje.builder()
                .conversacion(conversacion)
                .mensaje(request.getMensaje())
                .tipo(request.getTipo() != null ? request.getTipo() : ChatMensaje.TipoMensaje.TEXTO)
                .archivoUrl(request.getArchivoUrl())
                .archivoNombre(request.getArchivoNombre())
                .archivoTipo(request.getArchivoTipo())
                .build();
        
        if (esEmprendedor) {
            mensaje.setEmprendedor(emprendedorRepository.findByUsuarioId(usuario.getId()).get());
        } else {
            mensaje.setUsuario(usuario);
        }
        
        ChatMensaje savedMensaje = mensajeRepository.save(mensaje);
        
        // Actualizar fecha del último mensaje en la conversación
        conversacion.setFechaUltimoMensaje(LocalDateTime.now());
        conversacionRepository.save(conversacion);
        
        return convertToMensajeResponse(savedMensaje);
    }
    
    public List<ChatMensajeResponse> obtenerMensajes(Long conversacionId, int pagina, int tamaño) {
        Usuario usuario = getCurrentUser();
        
        ChatConversacion conversacion = conversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversación", "id", conversacionId));
        
        // Verificar permisos
        verificarPermisoConversacion(usuario, conversacion);
        
        Pageable pageable = PageRequest.of(pagina, tamaño);
        return mensajeRepository.findByConversacionIdOrderByFechaEnvioDesc(conversacionId, pageable)
                .getContent()
                .stream()
                .map(this::convertToMensajeResponse)
                .collect(Collectors.toList());
    }
    
    public void marcarMensajesComoLeidos(Long conversacionId) {
        Usuario usuario = getCurrentUser();
        
        ChatConversacion conversacion = conversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversación", "id", conversacionId));
        
        verificarPermisoConversacion(usuario, conversacion);
        
        if (hasRole("ROLE_EMPRENDEDOR")) {
            // Marcar mensajes del usuario como leídos
            mensajeRepository.marcarMensajesDeUsuarioComoLeidos(conversacionId);
        } else {
            // Marcar mensajes del emprendedor como leídos
            mensajeRepository.marcarMensajesDeEmprendedorComoLeidos(conversacionId);
        }
    }
    
    public Long contarMensajesNoLeidos() {
        Usuario usuario = getCurrentUser();
        
        if (hasRole("ROLE_EMPRENDEDOR")) {
            Emprendedor emprendedor = emprendedorRepository.findByUsuarioId(usuario.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Emprendedor", "usuario_id", usuario.getId()));
            return mensajeRepository.countMensajesNoLeidosParaEmprendedor(emprendedor.getId());
        } else {
            return mensajeRepository.countMensajesNoLeidosParaUsuario(usuario.getId());
        }
    }
    
    public void cerrarConversacion(Long conversacionId) {
        Usuario usuario = getCurrentUser();
        
        ChatConversacion conversacion = conversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversación", "id", conversacionId));
        
        verificarPermisoConversacion(usuario, conversacion);
        
        conversacion.setEstado(ChatConversacion.EstadoConversacion.CERRADA);
        conversacionRepository.save(conversacion);
    }
    
    private void verificarPermisoConversacion(Usuario usuario, ChatConversacion conversacion) {
        boolean tienePermiso = false;
        
        if (conversacion.getUsuario().getId().equals(usuario.getId())) {
            tienePermiso = true;
        } else if (hasRole("ROLE_EMPRENDEDOR")) {
            Emprendedor emprendedor = emprendedorRepository.findByUsuarioId(usuario.getId())
                    .orElse(null);
            if (emprendedor != null && conversacion.getEmprendedor().getId().equals(emprendedor.getId())) {
                tienePermiso = true;
            }
        }
        
        if (!tienePermiso) {
            throw new RuntimeException("No tiene permisos para acceder a esta conversación");
        }
    }
    
    public  ChatConversacionResponse convertToConversacionResponse(ChatConversacion conversacion) {
        Usuario currentUser = getCurrentUser();
        
        // Obtener último mensaje
        ChatMensajeResponse ultimoMensaje = null;
        if (!conversacion.getMensajes().isEmpty()) {
            ChatMensaje ultimo = conversacion.getMensajes().get(conversacion.getMensajes().size() - 1);
            ultimoMensaje = convertToMensajeResponse(ultimo);
        }
        
        // Contar mensajes no leídos
        Long mensajesNoLeidos = 0L;
        if (hasRole("ROLE_EMPRENDEDOR")) {
            mensajesNoLeidos = mensajeRepository.countMensajesNoLeidosDeUsuario(conversacion.getId());
        } else {
            mensajesNoLeidos = mensajeRepository.countMensajesNoLeidosDeEmprendedor(conversacion.getId());
        }
        
        return ChatConversacionResponse.builder()
                .id(conversacion.getId())
                .usuarioId(conversacion.getUsuario().getId())
                .emprendedorId(conversacion.getEmprendedor().getId())
                .reservaId(conversacion.getReserva() != null ? conversacion.getReserva().getId() : null)
                .reservaCarritoId(conversacion.getReservaCarrito() != null ? conversacion.getReservaCarrito().getId() : null)  // NUEVO
                .codigoReservaAsociada(conversacion.getCodigoReservaAsociada())  // NUEVO
                .fechaCreacion(conversacion.getFechaCreacion())
                .fechaUltimoMensaje(conversacion.getFechaUltimoMensaje())
                .estado(conversacion.getEstado())
                .usuario(UsuarioBasicResponse.builder()
                        .id(conversacion.getUsuario().getId())
                        .nombre(conversacion.getUsuario().getNombre())
                        .apellido(conversacion.getUsuario().getApellido())
                        .username(conversacion.getUsuario().getUsername())
                        .email(conversacion.getUsuario().getEmail())
                        .build())
                .emprendedor(EmprendedorBasicResponse.builder()
                        .id(conversacion.getEmprendedor().getId())
                        .nombreEmpresa(conversacion.getEmprendedor().getNombreEmpresa())
                        .rubro(conversacion.getEmprendedor().getRubro())
                        .telefono(conversacion.getEmprendedor().getTelefono())
                        .email(conversacion.getEmprendedor().getEmail())
                        .build())
                .ultimoMensaje(ultimoMensaje)
                .mensajesNoLeidos(mensajesNoLeidos)
                .build();
    }
    
    private ChatMensajeResponse convertToMensajeResponse(ChatMensaje mensaje) {
        boolean esDeEmprendedor = mensaje.esDeEmprendedor();
        
        // Manejar casos especiales para mensajes de sistema
        Long remitenteId = null;
        String remitenteNombre = "Sistema";
        
        if (mensaje.getTipo() == ChatMensaje.TipoMensaje.SISTEMA) {
            // Para mensajes de sistema, usar valores por defecto
            remitenteId = null;
            remitenteNombre = "Sistema";
        } else if (esDeEmprendedor && mensaje.getEmprendedor() != null) {
            remitenteId = mensaje.getEmprendedor().getId();
            remitenteNombre = mensaje.getEmprendedor().getNombreEmpresa();
        } else if (!esDeEmprendedor && mensaje.getUsuario() != null) {
            remitenteId = mensaje.getUsuario().getId();
            remitenteNombre = mensaje.getUsuario().getNombre() + " " + mensaje.getUsuario().getApellido();
        }
        
        return ChatMensajeResponse.builder()
                .id(mensaje.getId())
                .conversacionId(mensaje.getConversacion().getId())
                .mensaje(mensaje.getMensaje())
                .tipo(mensaje.getTipo())
                .fechaEnvio(mensaje.getFechaEnvio())
                .leido(mensaje.getLeido())
                .esDeEmprendedor(esDeEmprendedor)
                .remitenteId(remitenteId)
                .remitenteNombre(remitenteNombre)
                .archivoUrl(mensaje.getArchivoUrl())
                .archivoNombre(mensaje.getArchivoNombre())
                .archivoTipo(mensaje.getArchivoTipo())
                .build();
    }
    
    private Usuario getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Usuario)) {
            throw new RuntimeException("Usuario no autenticado correctamente");
        }
        return (Usuario) principal;
    }
    
    private boolean hasRole(String role) {
            return getCurrentUser().getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(role));
        }
        public ChatConversacionResponse obtenerConversacionPorId(Long conversacionId) {
        Usuario usuario = getCurrentUser();
        
        ChatConversacion conversacion = conversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversación", "id", conversacionId));
        
        verificarPermisoConversacion(usuario, conversacion);
        
        return convertToConversacionResponse(conversacion);
    }

    public void archivarConversacion(Long conversacionId) {
        Usuario usuario = getCurrentUser();
        
        ChatConversacion conversacion = conversacionRepository.findById(conversacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversación", "id", conversacionId));
        
        verificarPermisoConversacion(usuario, conversacion);
        
        conversacion.setEstado(ChatConversacion.EstadoConversacion.ARCHIVADA);
        conversacionRepository.save(conversacion);
    }
    public List<ChatConversacionResponse> obtenerConversacionesPorReservaCarrito(Long reservaCarritoId) {
        // Verificar permisos básicos
        Usuario usuario = getCurrentUser();
        
        List<ChatConversacion> conversaciones = conversacionRepository.findByReservaCarritoId(reservaCarritoId);
        
        // Filtrar solo las conversaciones donde el usuario tiene acceso
        return conversaciones.stream()
                .filter(conv -> {
                    // Usuario puede ver si es el dueño de la reserva o es el emprendedor
                    boolean esUsuarioDeReserva = conv.getUsuario().getId().equals(usuario.getId());
                    boolean esEmprendedor = hasRole("ROLE_EMPRENDEDOR") && 
                        conv.getEmprendedor().getUsuario().getId().equals(usuario.getId());
                    return esUsuarioDeReserva || esEmprendedor || hasRole("ROLE_ADMIN");
                })
                .map(this::convertToConversacionResponse)
                .collect(Collectors.toList());
    }
    public List<ChatMensajeResponse> enviarMensajeRapidoAReserva(Long reservaCarritoId, String mensaje) {
        List<ChatConversacion> conversaciones = conversacionRepository.findByReservaCarritoId(reservaCarritoId);
        List<ChatMensajeResponse> mensajesEnviados = new ArrayList<>();
        
        for (ChatConversacion conversacion : conversaciones) {
            ChatMensajeRequest request = ChatMensajeRequest.builder()
                    .conversacionId(conversacion.getId())
                    .mensaje(mensaje)
                    .tipo(ChatMensaje.TipoMensaje.TEXTO)
                    .build();
            
            try {
                ChatMensajeResponse mensajeEnviado = enviarMensaje(request);
                mensajesEnviados.add(mensajeEnviado);
            } catch (Exception e) {
                // Log error pero continuar con otras conversaciones
                System.err.println("Error enviando mensaje a conversación " + conversacion.getId() + ": " + e.getMessage());
            }
        }
        
        return mensajesEnviados;
    }
    public ChatConversacionResponse iniciarConversacion(Long emprendedorId, Long reservaId) {
        Usuario usuario = getCurrentUser();
        
        Emprendedor emprendedor = emprendedorRepository.findById(emprendedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Emprendedor", "id", emprendedorId));
        
        // Verificar si ya existe una conversación
        ChatConversacion conversacion = conversacionRepository
                .findByUsuarioAndEmprendedor(usuario, emprendedor)
                .orElseGet(() -> {
                    ChatConversacion nueva = ChatConversacion.builder()
                            .usuario(usuario)
                            .emprendedor(emprendedor)
                            .estado(ChatConversacion.EstadoConversacion.ACTIVA)
                            .build();
                    
                    // Asignar reserva si se proporciona
                    if (reservaId != null) {
                        Reserva reserva = reservaRepository.findById(reservaId)
                                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", reservaId));
                        nueva.setReserva(reserva);
                    }
                    
                    return conversacionRepository.save(nueva);
                });
        
        // Crear mensaje de sistema si es nueva conversación
        if (conversacion.getMensajes().isEmpty()) {
            String mensajeInicial = "Conversación iniciada";
            if (reservaId != null) {
                mensajeInicial += " - Reserva: " + conversacion.getCodigoReservaAsociada();
            }
            
            ChatMensaje mensajeSistema = ChatMensaje.builder()
                    .conversacion(conversacion)
                    .mensaje(mensajeInicial)
                    .tipo(ChatMensaje.TipoMensaje.SISTEMA)
                    .leido(true)
                    .build();
            mensajeRepository.save(mensajeSistema);
        }
        
        return convertToConversacionResponse(conversacion);
    }
    




}