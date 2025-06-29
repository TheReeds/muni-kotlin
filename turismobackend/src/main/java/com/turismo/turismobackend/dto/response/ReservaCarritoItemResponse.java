package com.turismo.turismobackend.dto.response;

import com.turismo.turismobackend.model.ReservaCarritoItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaCarritoItemResponse {
    
    private Long id;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private LocalDate fechaServicio;
    private String notasEspeciales;
    private ReservaCarritoItem.EstadoItemReserva estado;
    private ServicioTuristicoBasicResponse servicio;
}