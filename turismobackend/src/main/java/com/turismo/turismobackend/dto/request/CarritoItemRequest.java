package com.turismo.turismobackend.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItemRequest {
    
    @NotNull(message = "El ID del servicio es obligatorio")
    private Long servicioId;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser positiva")
    private Integer cantidad;
    
    @NotNull(message = "La fecha del servicio es obligatoria")
    @Future(message = "La fecha del servicio debe ser futura")
    private LocalDate fechaServicio;
    
    private String notasEspeciales;
}