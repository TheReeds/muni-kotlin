package com.turismo.turismobackend.dto.request;

import com.turismo.turismobackend.model.PlanTuristico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanTuristicoRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    private String descripcion;
    
    @NotNull(message = "La duración en días es obligatoria")
    @Positive(message = "La duración debe ser positiva")
    private Integer duracionDias;
    
    @NotNull(message = "La capacidad máxima es obligatoria")
    @Positive(message = "La capacidad máxima debe ser positiva")
    private Integer capacidadMaxima;
    
    @NotNull(message = "El nivel de dificultad es obligatorio")
    private PlanTuristico.NivelDificultad nivelDificultad;
    
    private String imagenPrincipalUrl;
    
    private String itinerario;
    
    private String incluye;
    
    private String noIncluye;
    
    private String recomendaciones;
    
    private String requisitos;
    
    // Solo requerido para ADMIN, las municipalidades usan su propia municipalidad
    private Long municipalidadId;
    
    @NotNull(message = "Los servicios son obligatorios")
    private List<ServicioPlanRequest> servicios;
}