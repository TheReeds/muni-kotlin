package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.PlanTuristico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PlanTuristicoRepository extends JpaRepository<PlanTuristico, Long> {
    
    List<PlanTuristico> findByMunicipalidadId(Long municipalidadId);
    
    List<PlanTuristico> findByUsuarioCreadorId(Long usuarioId);
    
    List<PlanTuristico> findByEstado(PlanTuristico.EstadoPlan estado);
    
    List<PlanTuristico> findByNivelDificultad(PlanTuristico.NivelDificultad nivelDificultad);
    
    List<PlanTuristico> findByDuracionDiasBetween(Integer duracionMin, Integer duracionMax);
    
    List<PlanTuristico> findByPrecioTotalBetween(BigDecimal precioMin, BigDecimal precioMax);
    
    List<PlanTuristico> findByCapacidadMaximaGreaterThanEqual(Integer capacidadMinima);
    
    @Query("SELECT p FROM PlanTuristico p WHERE p.nombre LIKE %:nombre% OR p.descripcion LIKE %:descripcion%")
    List<PlanTuristico> findByNombreOrDescripcionContaining(@Param("nombre") String nombre, 
                                                            @Param("descripcion") String descripcion);
    
    @Query("SELECT p FROM PlanTuristico p WHERE p.municipalidad.id = :municipalidadId AND p.estado = :estado")
    List<PlanTuristico> findByMunicipalidadAndEstado(@Param("municipalidadId") Long municipalidadId, 
                                                     @Param("estado") PlanTuristico.EstadoPlan estado);
    
    @Query("SELECT DISTINCT p FROM PlanTuristico p JOIN p.servicios sp WHERE sp.servicio.tipo = :tipoServicio")
    List<PlanTuristico> findByTipoServicio(@Param("tipoServicio") com.turismo.turismobackend.model.ServicioTuristico.TipoServicio tipoServicio);
    
    @Query("SELECT p FROM PlanTuristico p WHERE SIZE(p.reservas) > 0 ORDER BY SIZE(p.reservas) DESC")
    List<PlanTuristico> findMostPopular();
}