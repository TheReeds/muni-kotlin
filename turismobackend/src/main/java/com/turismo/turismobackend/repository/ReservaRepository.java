package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    Optional<Reserva> findByCodigoReserva(String codigoReserva);
    
    List<Reserva> findByUsuarioId(Long usuarioId);
    
    List<Reserva> findByPlanId(Long planId);
    
    List<Reserva> findByEstado(Reserva.EstadoReserva estado);
    
    List<Reserva> findByUsuarioIdAndEstado(Long usuarioId, Reserva.EstadoReserva estado);
    
    List<Reserva> findByPlanIdAndEstado(Long planId, Reserva.EstadoReserva estado);
    
    List<Reserva> findByFechaInicioBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    List<Reserva> findByFechaReservaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("SELECT r FROM Reserva r WHERE r.plan.municipalidad.id = :municipalidadId")
    List<Reserva> findByMunicipalidadId(@Param("municipalidadId") Long municipalidadId);
    
    @Query("SELECT r FROM Reserva r WHERE r.plan.municipalidad.id = :municipalidadId AND r.estado = :estado")
    List<Reserva> findByMunicipalidadIdAndEstado(@Param("municipalidadId") Long municipalidadId, 
                                                 @Param("estado") Reserva.EstadoReserva estado);
    
    @Query("SELECT r FROM Reserva r WHERE r.fechaInicio = :fecha AND r.plan.id = :planId")
    List<Reserva> findByFechaInicioAndPlanId(@Param("fecha") LocalDate fecha, @Param("planId") Long planId);
    
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.plan.id = :planId AND r.fechaInicio = :fecha AND r.estado NOT IN ('CANCELADA')")
    Long countActiveReservasByPlanAndDate(@Param("planId") Long planId, @Param("fecha") LocalDate fecha);
    
    @Query("SELECT SUM(r.numeroPersonas) FROM Reserva r WHERE r.plan.id = :planId AND r.fechaInicio = :fecha AND r.estado NOT IN ('CANCELADA')")
    Long countPersonasByPlanAndDate(@Param("planId") Long planId, @Param("fecha") LocalDate fecha);
    
    @Query("SELECT r FROM Reserva r WHERE r.usuario.id = :usuarioId ORDER BY r.fechaReserva DESC")
    List<Reserva> findByUsuarioIdOrderByFechaReservaDesc(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.usuario.id = :usuarioId AND r.estado = 'COMPLETADA'")
    Long countReservasCompletadasByUsuario(@Param("usuarioId") Long usuarioId);
}