package com.turismo.turismobackend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmprendedorRequest {
    
    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre de la empresa debe tener entre 2 y 100 caracteres")
    private String nombreEmpresa;
    
    @NotBlank(message = "El rubro es obligatorio")
    private String rubro;
    
    private String direccion;
    
    // NUEVOS CAMPOS DE UBICACIÓN
    private Double latitud;
    private Double longitud;
    private String direccionCompleta;
    
    private String telefono;
    
    @Email(message = "Debe ingresar un email válido")
    private String email;
    
    private String sitioWeb;
    
    private String descripcion;
    
    private String productos;
    
    private String servicios;
    
    @NotNull(message = "El ID de la municipalidad es obligatorio")
    private Long municipalidadId;
    
    private Long categoriaId; // Opcional, puede ser null
}