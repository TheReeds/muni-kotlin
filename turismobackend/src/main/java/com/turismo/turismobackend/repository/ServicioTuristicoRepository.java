package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.ServicioTuristico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ServicioTuristicoRepository extends JpaRepository<ServicioTuristico, Long> {
    
    List<ServicioTuristico> findByEmprendedorId(Long emprendedorId);
    
    List<ServicioTuristico> findByEmprendedorMunicipalidadId(Long municipalidadId);
    
    List<ServicioTuristico> findByTipo(ServicioTuristico.TipoServicio tipo);
    
    List<ServicioTuristico> findByEstado(ServicioTuristico.EstadoServicio estado);
    
    List<ServicioTuristico> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);
    
    List<ServicioTuristico> findByCapacidadMaximaGreaterThanEqual(Integer capacidadMinima);
    
    @Query("SELECT s FROM ServicioTuristico s WHERE s.emprendedor.municipalidad.id = :municipalidadId AND s.estado = :estado")
    List<ServicioTuristico> findByMunicipalidadAndEstado(@Param("municipalidadId") Long municipalidadId, 
                                                         @Param("estado") ServicioTuristico.EstadoServicio estado);
    
    @Query("SELECT s FROM ServicioTuristico s WHERE s.nombre LIKE %:nombre% OR s.descripcion LIKE %:descripcion%")
    List<ServicioTuristico> findByNombreOrDescripcionContaining(@Param("nombre") String nombre, 
                                                                @Param("descripcion") String descripcion);
    
    @Query("SELECT s FROM ServicioTuristico s WHERE s.emprendedor.categoria.id = :categoriaId")
    List<ServicioTuristico> findByEmprendedorCategoriaId(@Param("categoriaId") Long categoriaId);
}