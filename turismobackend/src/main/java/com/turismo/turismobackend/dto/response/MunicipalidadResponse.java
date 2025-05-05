package com.turismo.turismobackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MunicipalidadResponse {
    private Long id;
    private String nombre;
    private String departamento;
    private String provincia;
    private String distrito;
    private String direccion;
    private String telefono;
    private String sitioWeb;
    private String descripcion;
    private Long usuarioId;
    private List<EmprendedorResumen> emprendedores;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmprendedorResumen {
        private Long id;
        private String nombreEmpresa;
        private String rubro;
    }
}