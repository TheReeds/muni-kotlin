package com.turismo.turismobackend.dto.response;

import com.turismo.turismobackend.model.ReservaServicio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaServicioResponse {
    
    private Long id;
    private Boolean incluido;
    private BigDecimal precioPersonalizado;
    private String observaciones;
    private ReservaServicio.EstadoServicioReserva estado;
    private ServicioPlanResponse servicioPlan;
}