package com.turismo.turismobackend.dto.request;

import com.turismo.turismobackend.model.ReservaCarrito;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaCarritoRequest {
    
    private String observaciones;
    private String contactoEmergencia;
    private String telefonoEmergencia;
    private ReservaCarrito.MetodoPago metodoPago;
}