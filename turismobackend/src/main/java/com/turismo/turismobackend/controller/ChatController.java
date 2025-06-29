package com.turismo.turismobackend.controller;

import com.turismo.turismobackend.dto.request.ChatMensajeRequest;
import com.turismo.turismobackend.dto.response.ChatConversacionResponse;
import com.turismo.turismobackend.dto.response.ChatMensajeResponse;
import com.turismo.turismobackend.model.ChatConversacion;
import com.turismo.turismobackend.model.ChatMensaje;
import com.turismo.turismobackend.repository.ChatConversacionRepository;
import com.turismo.turismobackend.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "API para sistema de chat entre usuarios y emprendedores")
@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_EMPRENDEDOR', 'ROLE_MUNICIPALIDAD', 'ROLE_ADMIN')")
public class ChatController {
    
    private final ChatService chatService;
    private final ChatConversacionRepository conversacionRepository;
    
    @GetMapping("/conversaciones")
    @Operation(summary = "Obtener conversaciones del usuario autenticado")
    public ResponseEntity<List<ChatConversacionResponse>> obtenerConversaciones() {
        List<ChatConversacionResponse> conversaciones = chatService.obtenerConversaciones();
        return ResponseEntity.ok(conversaciones);
    }
    
    @PostMapping("/conversacion/iniciar")
    @Operation(summary = "Iniciar conversación con emprendedor")
    public ResponseEntity<ChatConversacionResponse> iniciarConversacion(
            @Parameter(description = "ID del emprendedor") @RequestParam Long emprendedorId,
            @Parameter(description = "ID de la reserva (opcional)") @RequestParam(required = false) Long reservaId) {
        ChatConversacionResponse conversacion = chatService.iniciarConversacion(emprendedorId, reservaId);
        return ResponseEntity.ok(conversacion);
    }
    
    @PostMapping("/mensaje")
    @Operation(summary = "Enviar mensaje en conversación")
    public ResponseEntity<ChatMensajeResponse> enviarMensaje(
            @Valid @RequestBody ChatMensajeRequest request) {
        ChatMensajeResponse mensaje = chatService.enviarMensaje(request);
        return ResponseEntity.ok(mensaje);
    }
    
    @GetMapping("/conversacion/{conversacionId}/mensajes")
    @Operation(summary = "Obtener mensajes de una conversación con paginación")
    public ResponseEntity<List<ChatMensajeResponse>> obtenerMensajes(
            @Parameter(description = "ID de la conversación") @PathVariable Long conversacionId,
            @Parameter(description = "Número de página (0 basado)") @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int tamaño) {
        List<ChatMensajeResponse> mensajes = chatService.obtenerMensajes(conversacionId, pagina, tamaño);
        return ResponseEntity.ok(mensajes);
    }
    
    @PatchMapping("/conversacion/{conversacionId}/marcar-leidos")
    @Operation(summary = "Marcar mensajes como leídos")
    public ResponseEntity<Void> marcarMensajesComoLeidos(
            @Parameter(description = "ID de la conversación") @PathVariable Long conversacionId) {
        chatService.marcarMensajesComoLeidos(conversacionId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/mensajes-no-leidos")
    @Operation(summary = "Contar mensajes no leídos del usuario")
    public ResponseEntity<Long> contarMensajesNoLeidos() {
        Long cantidad = chatService.contarMensajesNoLeidos();
        return ResponseEntity.ok(cantidad);
    }
    
    @PatchMapping("/conversacion/{conversacionId}/cerrar")
    @Operation(summary = "Cerrar conversación")
    public ResponseEntity<Void> cerrarConversacion(
            @Parameter(description = "ID de la conversación") @PathVariable Long conversacionId) {
        chatService.cerrarConversacion(conversacionId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/conversacion/{conversacionId}")
    @Operation(summary = "Obtener detalles de una conversación específica")
    public ResponseEntity<ChatConversacionResponse> obtenerConversacion(
            @Parameter(description = "ID de la conversación") @PathVariable Long conversacionId) {
        ChatConversacionResponse conversacion = chatService.obtenerConversacionPorId(conversacionId);
        return ResponseEntity.ok(conversacion);
    }
    
    @PatchMapping("/conversacion/{conversacionId}/archivar")
    @Operation(summary = "Archivar conversación")
    public ResponseEntity<Void> archivarConversacion(
            @Parameter(description = "ID de la conversación") @PathVariable Long conversacionId) {
        chatService.archivarConversacion(conversacionId);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/conversacion/iniciar-carrito")
    @Operation(summary = "Iniciar conversación con emprendedor desde reserva carrito")
    public ResponseEntity<ChatConversacionResponse> iniciarConversacionConReservaCarrito(
            @Parameter(description = "ID del emprendedor") @RequestParam Long emprendedorId,
            @Parameter(description = "ID de la reserva carrito (opcional)") @RequestParam(required = false) Long reservaCarritoId) {
        ChatConversacionResponse conversacion = chatService.iniciarConversacionConReservaCarrito(emprendedorId, reservaCarritoId);
        return ResponseEntity.ok(conversacion);
    }

    @GetMapping("/reserva-carrito/{reservaCarritoId}/conversaciones")
    @Operation(summary = "Obtener conversaciones por reserva carrito")
    public ResponseEntity<List<ChatConversacionResponse>> obtenerConversacionesPorReservaCarrito(
            @Parameter(description = "ID de la reserva carrito") @PathVariable Long reservaCarritoId) {
        
        List<ChatConversacionResponse> response = chatService.obtenerConversacionesPorReservaCarrito(reservaCarritoId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reserva-carrito/{reservaCarritoId}/mensaje-rapido")
    @Operation(summary = "Enviar mensaje rápido a todos los emprendedores de una reserva")
    public ResponseEntity<List<ChatMensajeResponse>> enviarMensajeRapidoAReserva(
            @Parameter(description = "ID de la reserva carrito") @PathVariable Long reservaCarritoId,
            @Parameter(description = "Mensaje a enviar") @RequestParam String mensaje) {
        
        List<ChatMensajeResponse> mensajesEnviados = chatService.enviarMensajeRapidoAReserva(reservaCarritoId, mensaje);
        return ResponseEntity.ok(mensajesEnviados);
    }
}