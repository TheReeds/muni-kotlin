package com.turismo.turismobackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmprendedorResponse {
    private Long id;
    private String nombreEmpresa;
    private String rubro;
    private String direccion;
    
    // NUEVOS CAMPOS DE UBICACIÓN
    private Double latitud;
    private Double longitud;
    private String direccionCompleta;
    
    private String telefono;
    private String email;
    private String sitioWeb;
    private String descripcion;
    private String productos;
    private String servicios;
    private Long usuarioId;
    private MunicipalidadResumen municipalidad;
    private CategoriaResumen categoria;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MunicipalidadResumen {
        private Long id;
        private String nombre;
        private String distrito;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoriaResumen {
        private Long id;
        private String nombre;
    }
}