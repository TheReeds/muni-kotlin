package com.turismo.turismobackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_mensajes")
public class ChatMensaje {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversacion_id", nullable = false)
    private ChatConversacion conversacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendedor_id")
    private Emprendedor emprendedor;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String mensaje;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMensaje tipo;
    
    @Column(nullable = false)
    private LocalDateTime fechaEnvio;
    
    @Column(nullable = false)
    private Boolean leido;
    
    // CAMPOS OPCIONALES PARA ARCHIVOS
    private String archivoUrl;
    private String archivoNombre;
    private String archivoTipo;
    
    @PrePersist
    protected void onCreate() {
        fechaEnvio = LocalDateTime.now();
        if (leido == null) {
            leido = false;
        }
        if (tipo == null) {
            tipo = TipoMensaje.TEXTO;
        }
    }
    
    // MÉTODO PARA DETERMINAR SI EL REMITENTE ES EMPRENDEDOR
    public boolean esDeEmprendedor() {
    // Si es un mensaje de sistema, no es de emprendedor ni de usuario
        if (this.tipo == TipoMensaje.SISTEMA) {
            return false;
        }
        
        // Es de emprendedor si tiene emprendedor asignado
        return this.emprendedor != null;
    }
    
    public enum TipoMensaje {
        TEXTO,
        IMAGEN,
        ARCHIVO,
        SISTEMA  // Para mensajes automáticos del sistema
    }
}