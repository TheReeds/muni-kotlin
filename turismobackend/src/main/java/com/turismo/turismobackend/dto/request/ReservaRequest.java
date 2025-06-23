package com.turismo.turismobackend.dto.request;

import com.turismo.turismobackend.model.Reserva;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequest {
    
    @NotNull(message = "El ID del plan es obligatorio")
    private Long planId;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDate fechaInicio;
    
    @NotNull(message = "El número de personas es obligatorio")
    @Positive(message = "El número de personas debe ser positivo")
    private Integer numeroPersonas;
    
    private String observaciones;
    
    private String solicitudesEspeciales;
    
    private String contactoEmergencia;
    
    private String telefonoEmergencia;
    
    private Reserva.MetodoPago metodoPago;
    
    private List<ReservaServicioRequest> serviciosPersonalizados;
}