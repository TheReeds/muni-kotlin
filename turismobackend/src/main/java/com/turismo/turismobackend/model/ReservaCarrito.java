package com.turismo.turismobackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservas_carrito")
public class ReservaCarrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String codigoReserva;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal montoDescuento;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoFinal;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReservaCarrito estado;
    
    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    private String contactoEmergencia;
    
    private String telefonoEmergencia;
    
    @Column(nullable = false)
    private LocalDateTime fechaReserva;
    
    private LocalDateTime fechaConfirmacion;
    
    private LocalDateTime fechaCancelacion;
    
    @Column(columnDefinition = "TEXT")
    private String motivoCancelacion;
    
    @Builder.Default
    @OneToMany(mappedBy = "reservaCarrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservaCarritoItem> items = new ArrayList<>();
    
    // USAR EL SISTEMA DE PAGOS EXISTENTE
    @Builder.Default
    @OneToMany(mappedBy = "reservaCarrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PagoCarrito> pagosCarrito = new ArrayList<>();
    
    // RELACIÓN CON CHAT
    @Builder.Default
    @OneToMany(mappedBy = "reservaCarrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatConversacion> conversaciones = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        fechaReserva = LocalDateTime.now();
        if (codigoReserva == null) {
            codigoReserva = generarCodigoReserva();
        }
    }
    
    private String generarCodigoReserva() {
        return "RC-" + System.currentTimeMillis();
    }
    
    public enum EstadoReservaCarrito {
        PENDIENTE,
        CONFIRMADA,
        PAGADA,
        EN_PROCESO,
        COMPLETADA,
        CANCELADA
    }
    
    public enum MetodoPago {
        EFECTIVO,
        TARJETA_CREDITO,
        TARJETA_DEBITO,
        TRANSFERENCIA,
        PAGO_MOVIL,
        PAYPAL,
        OTRO
    }
}