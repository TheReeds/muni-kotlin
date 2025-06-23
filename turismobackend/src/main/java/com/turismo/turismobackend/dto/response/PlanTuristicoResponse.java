package com.turismo.turismobackend.dto.response;

import com.turismo.turismobackend.model.PlanTuristico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanTuristicoResponse {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precioTotal;
    private Integer duracionDias;
    private Integer capacidadMaxima;
    private PlanTuristico.EstadoPlan estado;
    private PlanTuristico.NivelDificultad nivelDificultad;
    private String imagenPrincipalUrl;
    private String itinerario;
    private String incluye;
    private String noIncluye;
    private String recomendaciones;
    private String requisitos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private MunicipalidadBasicResponse municipalidad;
    private UsuarioBasicResponse usuarioCreador;
    private List<ServicioPlanResponse> servicios;
    private Integer totalReservas;
}