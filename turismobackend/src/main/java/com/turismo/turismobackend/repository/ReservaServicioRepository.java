package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.ReservaServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaServicioRepository extends JpaRepository<ReservaServicio, Long> {
    
    List<ReservaServicio> findByReservaId(Long reservaId);
    
    List<ReservaServicio> findByServicioPlanId(Long servicioPlanId);
    
    List<ReservaServicio> findByReservaIdAndIncluido(Long reservaId, Boolean incluido);
    
    List<ReservaServicio> findByReservaIdAndEstado(Long reservaId, ReservaServicio.EstadoServicioReserva estado);
}