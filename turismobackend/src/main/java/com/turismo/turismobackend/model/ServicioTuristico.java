package com.turismo.turismobackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "servicios_turisticos")
public class ServicioTuristico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Column(nullable = false)
    private Integer duracionHoras;
    
    @Column(nullable = false)
    private Integer capacidadMaxima;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoServicio tipo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoServicio estado;
    
    private String ubicacion;
    
    // NUEVOS CAMPOS DE UBICACIÓN
    @Column(name = "latitud")
    private Double latitud;
    
    @Column(name = "longitud")
    private Double longitud;
    
    @Column(columnDefinition = "TEXT")
    private String requisitos;
    
    @Column(columnDefinition = "TEXT")
    private String incluye;
    
    @Column(columnDefinition = "TEXT")
    private String noIncluye;
    
    private String imagenUrl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprendedor_id", nullable = false)
    private Emprendedor emprendedor;
    
    @Builder.Default
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServicioPlan> servicioPlan = new ArrayList<>();
    
    // RELACIÓN CON CARRITO ITEMS
    @Builder.Default
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoItem> carritoItems = new ArrayList<>();
    
    // MÉTODO PARA VALIDAR COORDENADAS
    public boolean tieneUbicacionValida() {
        return latitud != null && longitud != null && 
               latitud >= -90 && latitud <= 90 && 
               longitud >= -180 && longitud <= 180;
    }
    
    public enum TipoServicio {
        ALOJAMIENTO,
        TRANSPORTE,
        ALIMENTACION,
        GUIA_TURISTICO,
        ACTIVIDAD_RECREATIVA,
        TOUR,
        AVENTURA,
        CULTURAL,
        GASTRONOMICO,
        WELLNESS,
        OTRO
    }
    
    public enum EstadoServicio {
        ACTIVO,
        INACTIVO,
        AGOTADO,
        MANTENIMIENTO
    }
}