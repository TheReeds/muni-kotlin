package com.turismo.turismobackend.dto.response;

import com.turismo.turismobackend.model.ReservaCarrito;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaCarritoResponse {
    
    private Long id;
    private String codigoReserva;
    private BigDecimal montoTotal;
    private BigDecimal montoDescuento;
    private BigDecimal montoFinal;
    private ReservaCarrito.EstadoReservaCarrito estado;
    private ReservaCarrito.MetodoPago metodoPago;
    private String observaciones;
    private String contactoEmergencia;
    private String telefonoEmergencia;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaConfirmacion;
    private LocalDateTime fechaCancelacion;
    private String motivoCancelacion;
    private UsuarioBasicResponse usuario;
    private List<ReservaCarritoItemResponse> items;
    private List<PagoCarritoResponse> pagos;
}