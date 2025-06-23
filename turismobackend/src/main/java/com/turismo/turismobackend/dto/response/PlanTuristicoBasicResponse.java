package com.turismo.turismobackend.dto.response;

import com.turismo.turismobackend.model.PlanTuristico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanTuristicoBasicResponse {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precioTotal;
    private Integer duracionDias;
    private Integer capacidadMaxima;
    private PlanTuristico.EstadoPlan estado;
    private PlanTuristico.NivelDificultad nivelDificultad;
    private String imagenPrincipalUrl;
    private MunicipalidadBasicResponse municipalidad;
}