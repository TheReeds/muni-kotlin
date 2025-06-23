package com.turismo.turismobackend.dto.request;

import com.turismo.turismobackend.model.Pago;
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
public class PagoRequest {
    
    @NotNull(message = "El ID de la reserva es obligatorio")
    private Long reservaId;
    
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal monto;
    
    @NotNull(message = "El tipo de pago es obligatorio")
    private Pago.TipoPago tipo;
    
    @NotNull(message = "El método de pago es obligatorio")
    private Pago.MetodoPago metodoPago;
    
    private String numeroTransaccion;
    
    private String numeroAutorizacion;
    
    private String observaciones;
}