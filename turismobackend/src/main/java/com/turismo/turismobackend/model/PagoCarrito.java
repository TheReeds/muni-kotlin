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
@Table(name = "pagos_carrito")
public class PagoCarrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String codigoPago;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_carrito_id", nullable = false)
    private ReservaCarrito reservaCarrito;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Pago.TipoPago tipo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Pago.EstadoPago estado;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Pago.MetodoPago metodoPago;
    
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
        return "PC-" + System.currentTimeMillis();
    }
}