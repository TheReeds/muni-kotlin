package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.PagoCarrito;
import com.turismo.turismobackend.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoCarritoRepository extends JpaRepository<PagoCarrito, Long> {
    
    List<PagoCarrito> findByReservaCarritoId(Long reservaCarritoId);
    
    Optional<PagoCarrito> findByCodigoPago(String codigoPago);
    
    List<PagoCarrito> findByEstado(Pago.EstadoPago estado);
    
    List<PagoCarrito> findByTipo(Pago.TipoPago tipo);
    
    List<PagoCarrito> findByFechaPagoBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("SELECT p FROM PagoCarrito p WHERE p.reservaCarrito.usuario.id = :usuarioId")
    List<PagoCarrito> findByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT SUM(p.monto) FROM PagoCarrito p WHERE p.reservaCarrito.id = :reservaCarritoId AND p.estado = 'CONFIRMADO'")
    Double sumMontoConfirmadoByReservaCarrito(@Param("reservaCarritoId") Long reservaCarritoId);
    
    @Query("SELECT p FROM PagoCarrito p WHERE p.reservaCarrito.id = :reservaCarritoId AND p.estado = 'CONFIRMADO'")
    List<PagoCarrito> findPagosConfirmadosByReservaCarrito(@Param("reservaCarritoId") Long reservaCarritoId);
}