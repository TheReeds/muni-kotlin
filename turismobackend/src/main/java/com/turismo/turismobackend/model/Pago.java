package com.turismo.turismobackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pagos")
public class Pago {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String codigoPago;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPago tipo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estado;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodoPago;
    
    private String numeroTransaccion;
    
    private String numeroAutorizacion;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(nullable = false)
    private LocalDateTime fechaPago;
    
    private LocalDateTime fechaConfirmacion;
    
    @PrePersist
    protected void onCreate() {
        fechaPago = LocalDateTime.now();
        if (codigoPago == null) {
            codigoPago = generarCodigoPago();
        }
    }
    
    private String generarCodigoPago() {
        return "PAG-" + System.currentTimeMillis();
    }
    
    public enum TipoPago {
        SEÑA,
        PAGO_COMPLETO,
        PAGO_PARCIAL,
        SALDO_PENDIENTE
    }
    
    public enum EstadoPago {
        PENDIENTE,
        PROCESANDO,
        CONFIRMADO,
        FALLIDO,
        REEMBOLSADO,
        CANCELADO
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