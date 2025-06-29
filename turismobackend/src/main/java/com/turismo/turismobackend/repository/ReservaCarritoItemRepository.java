package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.ReservaCarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaCarritoItemRepository extends JpaRepository<ReservaCarritoItem, Long> {
    
    List<ReservaCarritoItem> findByReservaCarritoId(Long reservaCarritoId);
    
    List<ReservaCarritoItem> findByServicioId(Long servicioId);
    
    List<ReservaCarritoItem> findByReservaCarritoIdAndEstado(Long reservaCarritoId, 
                                                            ReservaCarritoItem.EstadoItemReserva estado);
    
    @Query("SELECT ri FROM ReservaCarritoItem ri WHERE ri.servicio.emprendedor.id = :emprendedorId")
    List<ReservaCarritoItem> findByEmprendedorId(@Param("emprendedorId") Long emprendedorId);
    
    @Query("SELECT ri FROM ReservaCarritoItem ri WHERE ri.servicio.emprendedor.id = :emprendedorId AND ri.fechaServicio = :fecha")
    List<ReservaCarritoItem> findByEmprendedorIdAndFecha(@Param("emprendedorId") Long emprendedorId, 
                                                         @Param("fecha") LocalDate fecha);
    
    @Query("SELECT SUM(ri.cantidad) FROM ReservaCarritoItem ri WHERE ri.servicio.id = :servicioId AND ri.fechaServicio = :fecha AND ri.estado NOT IN ('CANCELADO')")
    Long countPersonasByServicioAndFecha(@Param("servicioId") Long servicioId, @Param("fecha") LocalDate fecha);
}