package com.turismo.turismobackend.dto.response;

import com.turismo.turismobackend.model.Reserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponse {
    
    private Long id;
    private String codigoReserva;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer numeroPersonas;
    private BigDecimal montoTotal;
    private BigDecimal montoDescuento;
    private BigDecimal montoFinal;
    private Reserva.EstadoReserva estado;
    private Reserva.MetodoPago metodoPago;
    private String observaciones;
    private String solicitudesEspeciales;
    private String contactoEmergencia;
    private String telefonoEmergencia;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaConfirmacion;
    private LocalDateTime fechaCancelacion;
    private String motivoCancelacion;
    private PlanTuristicoBasicResponse plan;
    private UsuarioBasicResponse usuario;
    private List<ReservaServicioResponse> serviciosPersonalizados;
    private List<PagoResponse> pagos;
}