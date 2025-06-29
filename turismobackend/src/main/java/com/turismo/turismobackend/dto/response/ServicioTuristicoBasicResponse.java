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
public class ServicioTuristicoBasicResponse {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer duracionHoras;
    private ServicioTuristico.TipoServicio tipo;
    private String imagenUrl;
    private UbicacionResponse ubicacion;
    private EmprendedorBasicResponse emprendedor;
}