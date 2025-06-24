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
public class UsuarioResponse {
    
    private Long id;
    private String nombre;
    private String apellido;
    private String username;
    private String email;
    private List<String> roles;
    private EmprendedorResumen emprendedor;
    
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