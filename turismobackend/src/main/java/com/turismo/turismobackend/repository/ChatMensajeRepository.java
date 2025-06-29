package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.ChatMensaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMensajeRepository extends JpaRepository<ChatMensaje, Long> {
    
    List<ChatMensaje> findByConversacionId(Long conversacionId);
    
    Page<ChatMensaje> findByConversacionIdOrderByFechaEnvioDesc(Long conversacionId, Pageable pageable);
    
    @Query("SELECT m FROM ChatMensaje m WHERE m.conversacion.id = :conversacionId ORDER BY m.fechaEnvio ASC")
    List<ChatMensaje> findByConversacionIdOrderByFechaEnvio(@Param("conversacionId") Long conversacionId);
    
    @Query("SELECT COUNT(m) FROM ChatMensaje m WHERE m.conversacion.id = :conversacionId AND m.leido = false AND m.usuario IS NOT NULL")
    Long countMensajesNoLeidosDeUsuario(@Param("conversacionId") Long conversacionId);
    
    @Query("SELECT COUNT(m) FROM ChatMensaje m WHERE m.conversacion.id = :conversacionId AND m.leido = false AND m.emprendedor IS NOT NULL")
    Long countMensajesNoLeidosDeEmprendedor(@Param("conversacionId") Long conversacionId);
    
    @Query("SELECT COUNT(m) FROM ChatMensaje m WHERE m.conversacion.emprendedor.id = :emprendedorId AND m.leido = false AND m.usuario IS NOT NULL")
    Long countMensajesNoLeidosParaEmprendedor(@Param("emprendedorId") Long emprendedorId);
    
    @Query("SELECT COUNT(m) FROM ChatMensaje m WHERE m.conversacion.usuario.id = :usuarioId AND m.leido = false AND m.emprendedor IS NOT NULL")
    Long countMensajesNoLeidosParaUsuario(@Param("usuarioId") Long usuarioId);
    
    @Modifying
    @Transactional
    @Query("UPDATE ChatMensaje m SET m.leido = true WHERE m.conversacion.id = :conversacionId AND m.usuario IS NOT NULL AND m.leido = false")
    int marcarMensajesDeUsuarioComoLeidos(@Param("conversacionId") Long conversacionId);
    
    @Modifying
    @Transactional
    @Query("UPDATE ChatMensaje m SET m.leido = true WHERE m.conversacion.id = :conversacionId AND m.emprendedor IS NOT NULL AND m.leido = false")
    int marcarMensajesDeEmprendedorComoLeidos(@Param("conversacionId") Long conversacionId);
    
    @Query("SELECT m FROM ChatMensaje m WHERE m.conversacion.id = :conversacionId AND m.fechaEnvio >= :fechaDesde ORDER BY m.fechaEnvio DESC")
    List<ChatMensaje> findMensajesRecientes(@Param("conversacionId") Long conversacionId, 
                                           @Param("fechaDesde") LocalDateTime fechaDesde);
}