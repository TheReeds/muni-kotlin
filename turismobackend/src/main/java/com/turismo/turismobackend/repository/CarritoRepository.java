package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.Carrito;
import com.turismo.turismobackend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    
    Optional<Carrito> findByUsuario(Usuario usuario);
    
    Optional<Carrito> findByUsuarioId(Long usuarioId);
    
    @Query("SELECT c FROM Carrito c LEFT JOIN FETCH c.items WHERE c.usuario.id = :usuarioId")
    Optional<Carrito> findByUsuarioIdWithItems(@Param("usuarioId") Long usuarioId);
    
    boolean existsByUsuarioId(Long usuarioId);
}