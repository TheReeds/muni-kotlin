package com.turismo.turismobackend.dto.response;

import com.turismo.turismobackend.model.ServicioTuristico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicioTuristicoResponse {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer duracionHoras;
    private Integer capacidadMaxima;
    private ServicioTuristico.TipoServicio tipo;
    private ServicioTuristico.EstadoServicio estado;
    private String ubicacion;
    private String requisitos;
    private String incluye;
    private String noIncluye;
    private String imagenUrl;
    private EmprendedorBasicResponse emprendedor;
}