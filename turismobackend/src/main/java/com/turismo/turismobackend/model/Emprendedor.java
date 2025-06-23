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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
    
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;
    
    @Builder.Default
    @OneToMany(mappedBy = "emprendedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServicioTuristico> serviciosTuristicos = new ArrayList<>();
}