package com.turismo.turismobackend.dto.request;

import com.turismo.turismobackend.model.ReservaServicio;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaServicioRequest {
    
    @NotNull(message = "El ID del servicio del plan es obligatorio")
    private Long servicioPlanId;
    
    @NotNull(message = "Debe especificar si está incluido")
    private Boolean incluido;
    
    private BigDecimal precioPersonalizado;
    
    private String observaciones;
    
    @NotNull(message = "El estado es obligatorio")
    private ReservaServicio.EstadoServicioReserva estado;
}