package com.turismo.turismobackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MunicipalidadRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El departamento es obligatorio")
    private String departamento;
    
    @NotBlank(message = "La provincia es obligatoria")
    private String provincia;
    
    @NotBlank(message = "El distrito es obligatorio")
    private String distrito;
    
    private String direccion;
    
    private String telefono;
    
    private String sitioWeb;
    
    private String descripcion;
}