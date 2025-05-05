package com.turismo.turismobackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "municipalidades")
public class Municipalidad {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String departamento;
    
    @Column(nullable = false)
    private String provincia;
    
    @Column(nullable = false)
    private String distrito;
    
    private String direccion;
    
    private String telefono;
    
    private String sitioWeb;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;
    
    @Builder.Default
    @OneToMany(mappedBy = "municipalidad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Emprendedor> emprendedores = new ArrayList<>();
}