package com.turismo.turismobackend.controller;

import com.turismo.turismobackend.dto.request.ChatMensajeRequest;
import com.turismo.turismobackend.dto.response.ChatMensajeResponse;
import com.turismo.turismobackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    
    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    
    @MessageMapping("/chat.enviarMensaje")
    public void enviarMensaje(ChatMensajeRequest mensaje) {
        try {
            ChatMensajeResponse respuesta = chatService.enviarMensaje(mensaje);
            
            // Enviar mensaje a todos los suscriptores de la conversación
            simpMessagingTemplate.convertAndSend(
                    "/topic/conversacion/" + mensaje.getConversacionId(), 
                    respuesta
            );
            
        } catch (Exception e) {
            // Manejar errores y enviar notificación de error
            simpMessagingTemplate.convertAndSend(
                    "/topic/conversacion/" + mensaje.getConversacionId() + "/error",
                    "Error al enviar mensaje: " + e.getMessage()
            );
        }
    }
    
    @MessageMapping("/chat.escribiendo/{conversacionId}")
    @SendTo("/topic/conversacion/{conversacionId}/escribiendo")
    public String usuarioEscribiendo(@DestinationVariable Long conversacionId, String usuario) {
        return usuario + " está escribiendo...";
    }
    
    @MessageMapping("/chat.dejoDeEscribir/{conversacionId}")
    @SendTo("/topic/conversacion/{conversacionId}/escribiendo")
    public String usuarioDejoDeEscribir(@DestinationVariable Long conversacionId, String usuario) {
        return "";
    }
    
    @MessageMapping("/chat.unirse/{conversacionId}")
    public void unirseAConversacion(@DestinationVariable Long conversacionId, String usuario) {
        simpMessagingTemplate.convertAndSend(
                "/topic/conversacion/" + conversacionId + "/usuarios",
                usuario + " se unió a la conversación"
        );
    }
    
    @MessageMapping("/chat.salir/{conversacionId}")
    public void salirDeConversacion(@DestinationVariable Long conversacionId, String usuario) {
        simpMessagingTemplate.convertAndSend(
                "/topic/conversacion/" + conversacionId + "/usuarios",
                usuario + " salió de la conversación"
        );
    }
}