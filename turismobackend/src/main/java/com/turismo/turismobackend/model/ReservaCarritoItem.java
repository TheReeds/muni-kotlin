package com.turismo.turismobackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reserva_carrito_items")
public class ReservaCarritoItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_carrito_id", nullable = false)
    private ReservaCarrito reservaCarrito;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private ServicioTuristico servicio;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(nullable = false)
    private LocalDate fechaServicio;
    
    @Column(columnDefinition = "TEXT")
    private String notasEspeciales;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoItemReserva estado;
    
    // MÉTODO PARA CALCULAR SUBTOTAL
    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
    
    public enum EstadoItemReserva {
        PENDIENTE,
        CONFIRMADO,
        COMPLETADO,
        CANCELADO
    }
}