package com.turismo.turismobackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_conversaciones")
public class ChatConversacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendedor_id", nullable = false)
    private Emprendedor emprendedor;
    
    // REFERENCIA OPCIONAL A RESERVA TRADICIONAL
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;
    
    // REFERENCIA OPCIONAL A RESERVA DESDE CARRITO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_carrito_id")
    private ReservaCarrito reservaCarrito;
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    
    private LocalDateTime fechaUltimoMensaje;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoConversacion estado;
    
    @Builder.Default
    @OneToMany(mappedBy = "conversacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMensaje> mensajes = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaUltimoMensaje = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoConversacion.ACTIVA;
        }
    }
    
    // MÉTODO PARA OBTENER LA RESERVA ASOCIADA (CUALQUIER TIPO)
    public Object getReservaAsociada() {
        if (reserva != null) {
            return reserva;
        } else if (reservaCarrito != null) {
            return reservaCarrito;
        }
        return null;
    }
    
    public String getCodigoReservaAsociada() {
        if (reserva != null) {
            return reserva.getCodigoReserva();
        } else if (reservaCarrito != null) {
            return reservaCarrito.getCodigoReserva();
        }
        return null;
    }
    
    public enum EstadoConversacion {
        ACTIVA,
        CERRADA,
        ARCHIVADA
    }
}