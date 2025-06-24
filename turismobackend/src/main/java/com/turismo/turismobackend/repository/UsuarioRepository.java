package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.Rol;
import com.turismo.turismobackend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM Usuario u WHERE u.id NOT IN (SELECT e.usuario.id FROM Emprendedor e WHERE e.usuario IS NOT NULL)")
    List<Usuario> findUsuariosSinEmprendedor();
    
    List<Usuario> findByRolesContaining(Rol rol);
}