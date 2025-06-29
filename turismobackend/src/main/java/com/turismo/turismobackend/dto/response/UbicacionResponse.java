package com.turismo.turismobackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionResponse {
    
    private Double latitud;
    private Double longitud;
    private String direccionCompleta;
    private boolean tieneUbicacionValida;
}