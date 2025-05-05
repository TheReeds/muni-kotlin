package com.turismo.turismobackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "emprendedores")
public class Emprendedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombreEmpresa;
    
    @Column(nullable = false)
    private String rubro;
    
    private String direccion;
    
    private String telefono;
    
    private String email;
    
    private String sitioWeb;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(columnDefinition = "TEXT")
    private String productos;
    
    @Column(columnDefinition = "TEXT")
    private String servicios;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipalidad_id")
    private Municipalidad municipalidad;
    
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;
}