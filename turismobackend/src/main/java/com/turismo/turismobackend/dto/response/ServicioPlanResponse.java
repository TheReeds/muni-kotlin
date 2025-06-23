package com.turismo.turismobackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicioPlanResponse {
    
    private Long id;
    private Integer diaDelPlan;
    private Integer ordenEnElDia;
    private String horaInicio;
    private String horaFin;
    private BigDecimal precioEspecial;
    private String notas;
    private Boolean esOpcional;
    private Boolean esPersonalizable;
    private ServicioTuristicoResponse servicio;
}