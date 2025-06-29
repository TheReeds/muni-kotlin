package com.turismo.turismobackend.dto.request;

import com.turismo.turismobackend.model.ChatMensaje;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMensajeRequest {
    
    @NotNull(message = "El ID de la conversación es obligatorio")
    private Long conversacionId;
    
    @NotBlank(message = "El mensaje no puede estar vacío")
    private String mensaje;
    
    private ChatMensaje.TipoMensaje tipo;
    
    // Para archivos
    private String archivoUrl;
    private String archivoNombre;
    private String archivoTipo;
}