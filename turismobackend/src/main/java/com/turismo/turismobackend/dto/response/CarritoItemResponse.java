package com.turismo.turismobackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItemResponse {
    
    private Long id;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private LocalDate fechaServicio;
    private String notasEspeciales;
    private LocalDateTime fechaAgregado;
    private ServicioTuristicoBasicResponse servicio;
}