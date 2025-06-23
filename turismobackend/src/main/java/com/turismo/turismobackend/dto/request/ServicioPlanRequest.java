package com.turismo.turismobackend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicioPlanRequest {
    
    @NotNull(message = "El ID del servicio es obligatorio")
    private Long servicioId;
    
    @NotNull(message = "El día del plan es obligatorio")
    @Positive(message = "El día del plan debe ser positivo")
    private Integer diaDelPlan;
    
    @NotNull(message = "El orden en el día es obligatorio")
    @Positive(message = "El orden en el día debe ser positivo")
    private Integer ordenEnElDia;
    
    private String horaInicio;
    
    private String horaFin;
    
    private BigDecimal precioEspecial;
    
    private String notas;
    
    @NotNull(message = "Debe especificar si es opcional")
    private Boolean esOpcional;
    
    @NotNull(message = "Debe especificar si es personalizable")
    private Boolean esPersonalizable;
}