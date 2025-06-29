package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    
    List<CarritoItem> findByCarritoId(Long carritoId);
    
    List<CarritoItem> findByCarritoUsuarioId(Long usuarioId);
    
    Optional<CarritoItem> findByCarritoIdAndServicioIdAndFechaServicio(
            Long carritoId, Long servicioId, LocalDate fechaServicio);
    
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.carrito.usuario.id = :usuarioId AND ci.servicio.id = :servicioId")
    List<CarritoItem> findByUsuarioIdAndServicioId(@Param("usuarioId") Long usuarioId, 
                                                   @Param("servicioId") Long servicioId);
    
    @Query("SELECT COUNT(ci) FROM CarritoItem ci WHERE ci.carrito.usuario.id = :usuarioId")
    Long countByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    void deleteByCarritoId(Long carritoId);
}