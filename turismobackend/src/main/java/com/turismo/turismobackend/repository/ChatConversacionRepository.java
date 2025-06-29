package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.ChatConversacion;
import com.turismo.turismobackend.model.Emprendedor;
import com.turismo.turismobackend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatConversacionRepository extends JpaRepository<ChatConversacion, Long> {
    
    List<ChatConversacion> findByUsuarioId(Long usuarioId);
    
    List<ChatConversacion> findByEmprendedorId(Long emprendedorId);
    
    Optional<ChatConversacion> findByUsuarioAndEmprendedor(Usuario usuario, Emprendedor emprendedor);
    
    Optional<ChatConversacion> findByUsuarioIdAndEmprendedorId(Long usuarioId, Long emprendedorId);
    
    List<ChatConversacion> findByReservaId(Long reservaId);
    
    @Query("SELECT c FROM ChatConversacion c WHERE c.usuario.id = :usuarioId AND c.estado = :estado ORDER BY c.fechaUltimoMensaje DESC")
    List<ChatConversacion> findByUsuarioIdAndEstado(@Param("usuarioId") Long usuarioId, 
                                                    @Param("estado") ChatConversacion.EstadoConversacion estado);
    
    @Query("SELECT c FROM ChatConversacion c WHERE c.emprendedor.id = :emprendedorId AND c.estado = :estado ORDER BY c.fechaUltimoMensaje DESC")
    List<ChatConversacion> findByEmprendedorIdAndEstado(@Param("emprendedorId") Long emprendedorId, 
                                                        @Param("estado") ChatConversacion.EstadoConversacion estado);
    
    @Query("SELECT c FROM ChatConversacion c WHERE (c.usuario.id = :usuarioId OR c.emprendedor.id = :emprendedorId) ORDER BY c.fechaUltimoMensaje DESC")
    List<ChatConversacion> findConversacionesByParticipante(@Param("usuarioId") Long usuarioId, 
                                                            @Param("emprendedorId") Long emprendedorId);
    
    @Query("SELECT c FROM ChatConversacion c LEFT JOIN FETCH c.mensajes WHERE c.id = :conversacionId")
    Optional<ChatConversacion> findByIdWithMensajes(@Param("conversacionId") Long conversacionId);

    @Query("SELECT c FROM ChatConversacion c WHERE c.reservaCarrito.id = :reservaCarritoId")
    List<ChatConversacion> findByReservaCarritoId(@Param("reservaCarritoId") Long reservaCarritoId);

    Optional<ChatConversacion> findByUsuarioIdAndEmprendedorIdAndReservaCarritoId(
        Long usuarioId, Long emprendedorId, Long reservaCarritoId);

    @Query("SELECT c FROM ChatConversacion c WHERE c.reservaCarrito.id = :reservaCarritoId")
    List<ChatConversacion> findConversacionesByReservaCarrito(@Param("reservaCarritoId") Long reservaCarritoId);

    
}