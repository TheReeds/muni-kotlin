package com.turismo.turismobackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmprendedorBasicResponse {
    
    private Long id;
    private String nombreEmpresa;
    private String rubro;
    private String telefono;
    private String email;
    private MunicipalidadBasicResponse municipalidad;
}