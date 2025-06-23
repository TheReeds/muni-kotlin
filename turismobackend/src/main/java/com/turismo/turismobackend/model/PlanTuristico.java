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
@Table(name = "planes_turisticos")
public class PlanTuristico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTotal;
    
    @Column(nullable = false)
    private Integer duracionDias;
    
    @Column(nullable = false)
    private Integer capacidadMaxima;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPlan estado;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelDificultad nivelDificultad;
    
    private String imagenPrincipalUrl;
    
    @Column(columnDefinition = "TEXT")
    private String itinerario;
    
    @Column(columnDefinition = "TEXT")
    private String incluye;
    
    @Column(columnDefinition = "TEXT")
    private String noIncluye;
    
    @Column(columnDefinition = "TEXT")
    private String recomendaciones;
    
    @Column(columnDefinition = "TEXT")
    private String requisitos;
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    
    private LocalDateTime fechaActualizacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipalidad_id", nullable = false)
    private Municipalidad municipalidad;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creador_id", nullable = false)
    private Usuario usuarioCreador;
    
    @Builder.Default
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServicioPlan> servicios = new ArrayList<>();
    
    @Builder.Default
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    public enum EstadoPlan {
        BORRADOR,
        ACTIVO,
        INACTIVO,
        AGOTADO,
        SUSPENDIDO
    }
    
    public enum NivelDificultad {
        FACIL,
        MODERADO,
        DIFICIL,
        EXTREMO
    }
}