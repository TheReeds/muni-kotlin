package com.turismo.turismobackend.dto.request;

import com.turismo.turismobackend.model.ServicioTuristico;
import jakarta.validation.constraints.NotBlank;
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
public class ServicioTuristicoRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal precio;
    
    @NotNull(message = "La duración en horas es obligatoria")
    @Positive(message = "La duración debe ser positiva")
    private Integer duracionHoras;
    
    @NotNull(message = "La capacidad máxima es obligatoria")
    @Positive(message = "La capacidad máxima debe ser positiva")
    private Integer capacidadMaxima;
    
    @NotNull(message = "El tipo de servicio es obligatorio")
    private ServicioTuristico.TipoServicio tipo;
    
    private String ubicacion;
    
    private String requisitos;
    
    private String incluye;
    
    private String noIncluye;
    
    private String imagenUrl;
}