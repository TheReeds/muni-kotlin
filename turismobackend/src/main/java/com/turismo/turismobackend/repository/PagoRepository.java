package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    
    Optional<Pago> findByCodigoPago(String codigoPago);
    
    List<Pago> findByReservaId(Long reservaId);
    
    List<Pago> findByEstado(Pago.EstadoPago estado);
    
    List<Pago> findByTipo(Pago.TipoPago tipo);
    
    List<Pago> findByMetodoPago(Pago.MetodoPago metodoPago);
    
    List<Pago> findByReservaIdAndEstado(Long reservaId, Pago.EstadoPago estado);
    
    List<Pago> findByFechaPagoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("SELECT p FROM Pago p WHERE p.reserva.usuario.id = :usuarioId")
    List<Pago> findByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT p FROM Pago p WHERE p.reserva.plan.municipalidad.id = :municipalidadId")
    List<Pago> findByMunicipalidadId(@Param("municipalidadId") Long municipalidadId);
    
    @Query("SELECT p FROM Pago p WHERE p.reserva.id = :reservaId AND p.estado = 'CONFIRMADO'")
    List<Pago> findPagosConfirmadosByReserva(@Param("reservaId") Long reservaId);
    
    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.reserva.id = :reservaId AND p.estado = 'CONFIRMADO'")
    Long getTotalPagadoByReserva(@Param("reservaId") Long reservaId);
}