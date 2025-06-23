package com.turismo.turismobackend.dto.response;

import com.turismo.turismobackend.model.Pago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponse {
    
    private Long id;
    private String codigoPago;
    private BigDecimal monto;
    private Pago.TipoPago tipo;
    private Pago.EstadoPago estado;
    private Pago.MetodoPago metodoPago;
    private String numeroTransaccion;
    private String numeroAutorizacion;
    private String observaciones;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaConfirmacion;
}