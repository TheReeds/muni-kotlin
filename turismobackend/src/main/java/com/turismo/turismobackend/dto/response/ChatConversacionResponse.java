package com.turismo.turismobackend.dto.response;

import com.turismo.turismobackend.model.ChatConversacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversacionResponse {
    
    private Long id;
    private Long usuarioId;
    private Long emprendedorId;
    private Long reservaId;
    private Long reservaCarritoId;  // NUEVO CAMPO
    private String codigoReservaAsociada;  // NUEVO CAMPO
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimoMensaje;
    private ChatConversacion.EstadoConversacion estado;
    private UsuarioBasicResponse usuario;
    private EmprendedorBasicResponse emprendedor;
    private ChatMensajeResponse ultimoMensaje;
    private Long mensajesNoLeidos;
    private List<ChatMensajeResponse> mensajesRecientes;
}