package com.turismo.turismobackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MunicipalidadBasicResponse {
    
    private Long id;
    private String nombre;
    private String departamento;
    private String provincia;
    private String distrito;
}