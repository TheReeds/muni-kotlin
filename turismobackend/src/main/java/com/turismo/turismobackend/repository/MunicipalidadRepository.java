package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.Municipalidad;
import com.turismo.turismobackend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MunicipalidadRepository extends JpaRepository<Municipalidad, Long> {
    Optional<Municipalidad> findByUsuario(Usuario usuario);
    Optional<Municipalidad> findByUsuarioId(Long usuarioId);
    List<Municipalidad> findByDepartamento(String departamento);
    List<Municipalidad> findByProvincia(String provincia);
    List<Municipalidad> findByDistrito(String distrito);
    boolean existsByNombre(String nombre);
}