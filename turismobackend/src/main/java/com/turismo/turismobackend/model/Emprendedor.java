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
    
    // NUEVOS CAMPOS DE UBICACIÓN
    @Column(name = "latitud")
    private Double latitud;
    
    @Column(name = "longitud")
    private Double longitud;
    
    @Column(name = "direccion_completa")
    private String direccionCompleta;
    
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
    
    // RELACIÓN CON MENSAJES DE CHAT
    @Builder.Default
    @OneToMany(mappedBy = "emprendedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMensaje> mensajes = new ArrayList<>();
    
    // MÉTODO PARA VALIDAR COORDENADAS
    public boolean tieneUbicacionValida() {
        return latitud != null && longitud != null && 
               latitud >= -90 && latitud <= 90 && 
               longitud >= -180 && longitud <= 180;
    }
}