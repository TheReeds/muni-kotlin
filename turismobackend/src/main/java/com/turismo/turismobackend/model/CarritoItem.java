package com.turismo.turismobackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "carrito_items")
public class CarritoItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;
    
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
    
    @Column(nullable = false)
    private LocalDateTime fechaAgregado;
    
    @PrePersist
    protected void onCreate() {
        fechaAgregado = LocalDateTime.now();
        if (precioUnitario == null && servicio != null) {
            precioUnitario = servicio.getPrecio();
        }
    }
    
    // MÉTODO PARA CALCULAR SUBTOTAL
    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}