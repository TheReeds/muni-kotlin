package com.turismo.turismobackend.dto.response;

import com.turismo.turismobackend.model.ChatMensaje;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMensajeResponse {
    
    private Long id;
    private Long conversacionId;
    private String mensaje;
    private ChatMensaje.TipoMensaje tipo;
    private LocalDateTime fechaEnvio;
    private Boolean leido;
    private boolean esDeEmprendedor;
    
    // Información del remitente
    private Long remitenteId;
    private String remitenteNombre;
    
    // Para archivos
    private String archivoUrl;
    private String archivoNombre;
    private String archivoTipo;
}