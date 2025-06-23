package com.turismo.turismobackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservas")
public class Reserva {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String codigoReserva;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private PlanTuristico plan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false)
    private LocalDate fechaInicio;
    
    @Column(nullable = false)
    private LocalDate fechaFin;
    
    @Column(nullable = false)
    private Integer numeroPersonas;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal montoDescuento;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoFinal;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;
    
    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(columnDefinition = "TEXT")
    private String solicitudesEspeciales;
    
    private String contactoEmergencia;
    
    private String telefonoEmergencia;
    
    @Column(nullable = false)
    private LocalDateTime fechaReserva;
    
    private LocalDateTime fechaConfirmacion;
    
    private LocalDateTime fechaCancelacion;
    
    @Column(columnDefinition = "TEXT")
    private String motivoCancelacion;
    
    @Builder.Default
    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservaServicio> serviciosPersonalizados = new ArrayList<>();
    
    @Builder.Default
    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pago> pagos = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        fechaReserva = LocalDateTime.now();
        if (codigoReserva == null) {
            codigoReserva = generarCodigoReserva();
        }
    }
    
    private String generarCodigoReserva() {
        return "RES-" + System.currentTimeMillis();
    }
    
    public enum EstadoReserva {
        PENDIENTE,
        CONFIRMADA,
        PAGADA,
        EN_PROCESO,
        COMPLETADA,
        CANCELADA,
        NO_SHOW
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