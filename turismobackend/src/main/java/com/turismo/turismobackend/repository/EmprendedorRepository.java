package com.turismo.turismobackend.repository;

import com.turismo.turismobackend.model.Emprendedor;
import com.turismo.turismobackend.model.Municipalidad;
import com.turismo.turismobackend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmprendedorRepository extends JpaRepository<Emprendedor, Long> {
    Optional<Emprendedor> findByUsuario(Usuario usuario);
    List<Emprendedor> findByMunicipalidad(Municipalidad municipalidad);
    List<Emprendedor> findByRubro(String rubro);
    boolean existsByNombreEmpresa(String nombreEmpresa);
}