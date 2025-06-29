package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.ReservaCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaCarritoRepository extends JpaRepository<ReservaCarrito, Long> {
    
    Optional<ReservaCarrito> findByCodigoReserva(String codigoReserva);
    
    List<ReservaCarrito> findByUsuarioId(Long usuarioId);
    
    List<ReservaCarrito> findByEstado(ReservaCarrito.EstadoReservaCarrito estado);
    
    List<ReservaCarrito> findByUsuarioIdAndEstado(Long usuarioId, ReservaCarrito.EstadoReservaCarrito estado);
    
    List<ReservaCarrito> findByFechaReservaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("SELECT r FROM ReservaCarrito r WHERE r.usuario.id = :usuarioId ORDER BY r.fechaReserva DESC")
    List<ReservaCarrito> findByUsuarioIdOrderByFechaReservaDesc(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT DISTINCT r FROM ReservaCarrito r JOIN r.items i WHERE i.servicio.emprendedor.id = :emprendedorId")
    List<ReservaCarrito> findByEmprendedorId(@Param("emprendedorId") Long emprendedorId);
    
    @Query("SELECT DISTINCT r FROM ReservaCarrito r JOIN r.items i WHERE i.servicio.emprendedor.id = :emprendedorId AND r.estado = :estado")
    List<ReservaCarrito> findByEmprendedorIdAndEstado(@Param("emprendedorId") Long emprendedorId, 
                                                      @Param("estado") ReservaCarrito.EstadoReservaCarrito estado);
    
    @Query("SELECT COUNT(r) FROM ReservaCarrito r WHERE r.usuario.id = :usuarioId AND r.estado NOT IN ('CANCELADA')")
    Long countReservasActivasByUsuario(@Param("usuarioId") Long usuarioId);
}