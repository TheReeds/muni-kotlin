package com.turismo.turismobackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservas_servicios")
public class ReservaServicio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_plan_id", nullable = false)
    private ServicioPlan servicioPlan;
    
    @Column(nullable = false)
    private Boolean incluido;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal precioPersonalizado;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoServicioReserva estado;
    
    public enum EstadoServicioReserva {
        INCLUIDO,
        EXCLUIDO,
        PERSONALIZADO,
        PENDIENTE_CONFIRMACION
    }
}