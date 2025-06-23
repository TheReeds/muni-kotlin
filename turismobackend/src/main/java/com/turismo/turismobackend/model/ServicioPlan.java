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
@Table(name = "servicios_planes")
public class ServicioPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private PlanTuristico plan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private ServicioTuristico servicio;
    
    @Column(nullable = false)
    private Integer diaDelPlan;
    
    @Column(nullable = false)
    private Integer ordenEnElDia;
    
    private String horaInicio;
    
    private String horaFin;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal precioEspecial;
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @Column(nullable = false)
    private Boolean esOpcional;
    
    @Column(nullable = false)
    private Boolean esPersonalizable;
}